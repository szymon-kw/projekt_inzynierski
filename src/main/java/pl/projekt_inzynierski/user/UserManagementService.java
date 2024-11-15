package pl.projekt_inzynierski.user;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void saveUser(UserDto user) {

        // Pobierz obiekt Company na podstawie companyId
        Company company = companyRepository.findById(user.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid company ID: " + user.getCompanyId()));

        // Pobierz obiekt UserRole na podstawie roleId
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

        // Zapisz użytkownika
        userRepository.save(newUser);
        userPasswordChangeOrActiveService.NewVerification(newUser);

    }

    public void updateUser(Long id, User updatedUser, Long companyId, Long roleId, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));

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
            user.setPassword(passwordEncoder.encode(newPassword)); // Haszowanie nowego hasła
        }

        userRepository.save(user);
    }



    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        // Jeśli użytkownik którego chcemy usunac jest obslugujacym jakieś zgłoszenia to w nich ustawiamy
            // pole assignedUser na null
        reportRepository.findByAssignedUser(user).forEach(report -> report.setAssignedUser(null));

        // A jeśli usuwamy uzytkownika jest zgłaszającym jakieś zgłoszenia to te zgłoszenia również usuwamy
        List<Report> reportByReportingUser = reportRepository.findByReportingUser(user);
        Optional<VeryficationToken> veryficationToken = veryficationTokenRepository.findByUser(user);

        veryficationToken.ifPresent(veryficationTokenRepository::delete);
        reportRepository.deleteAll(reportByReportingUser);
        userRepository.deleteById(userId);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
