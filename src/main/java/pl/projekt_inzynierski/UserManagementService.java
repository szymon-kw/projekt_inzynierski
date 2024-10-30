package pl.projekt_inzynierski;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserManagementService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserManagementService(UserRepository userRepository,
                                 CompanyRepository companyRepository,
                                 UserRoleRepository userRoleRepository,
                                 BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user, Long companyId, Long roleId) {

        // Hashowanie hasła przed zapisem
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Pobierz obiekt Company na podstawie companyId
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid company ID: " + companyId));

        // Pobierz obiekt UserRole na podstawie roleId
        UserRole role = userRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + roleId));

        // Ustaw firmę użytkownika
        user.setCompany(company);

        // Ustaw role użytkownika
        Set<UserRole> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        // Zapisz użytkownika
        userRepository.save(user);
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
        userRepository.deleteById(userId);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
