package pl.projekt_inzynierski.user;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndIsActiveIsTrue(String email);
    List<User> findAllUserByRolesName(String roleName);

}
