package pl.projekt_inzynierski.user;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.projekt_inzynierski.Dto.UserDto;
import pl.projekt_inzynierski.company.Company;
import pl.projekt_inzynierski.company.CompanyRepository;
import pl.projekt_inzynierski.report.Report;
import pl.projekt_inzynierski.report.ReportRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserManagementService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ReportRepository reportRepository;
    private final UserPasswordChangeOrActiveService userPasswordChangeOrActiveService;
    private final VeryficationTokenRepository veryficationTokenRepository;

    @Autowired
    public UserManagementService(UserRepository userRepository,
                                 CompanyRepository companyRepository,
                                 UserRoleRepository userRoleRepository,
                                 BCryptPasswordEncoder passwordEncoder, ReportRepository reportRepository,
                                 UserPasswordChangeOrActiveService userPasswordChangeOrActiveService, VeryficationTokenRepository veryficationTokenRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.reportRepository = reportRepository;
        this.userPasswordChangeOrActiveService = userPasswordChangeOrActiveService;
        this.veryficationTokenRepository = veryficationTokenRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void updateAuthentication(String newEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newEmail, authentication.getCredentials(), authentication.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public void saveUser(UserDto user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Użytkownik z podanym adresem e-mail już istnieje");
        }


        Company company = companyRepository.findById(user.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid company ID: " + user.getCompanyId()));


        UserRole role = userRoleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + user.getRoleId()));

        User newUser = new User();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setCompany(company);
        newUser.setActive(false);

        Set<UserRole> roles = new HashSet<>();
        roles.add(role);
        newUser.setRoles(roles);


        userRepository.save(newUser);
        userPasswordChangeOrActiveService.NewVerification(newUser);

    }

    public void updateUserProfile(Long id, String email, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));

        user.setEmail(email);

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
    }


    public void updateUser(Long id, User updatedUser, Long companyId, Long roleId, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));


        userRepository.findByEmail(updatedUser.getEmail()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(id)) {
                throw new IllegalArgumentException("E-mail jest już przypisany do innego użytkownika.");
            }
        });

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid company ID: " + companyId));
        UserRole role = userRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + roleId));

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        user.setCompany(company);

        Set<UserRole> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
    }



    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        reportRepository.findByAssignedUser(user).forEach(report -> report.setAssignedUser(null));


        List<Report> reportByReportingUser = reportRepository.findByReportingUser(user);
        Optional<VeryficationToken> veryficationToken = veryficationTokenRepository.findByUser(user);

        veryficationToken.ifPresent(veryficationTokenRepository::delete);
        reportRepository.deleteAll(reportByReportingUser);
        userRepository.deleteById(userId);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik z tym adresem email nie może być znaleziony: " + email));
    }

    public boolean isPasswordValid(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void updateEmail(Long id, String email) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        user.setEmail(email);
        userRepository.save(user);
    }

    public void updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}



