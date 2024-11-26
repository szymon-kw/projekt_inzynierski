package pl.projekt_inzynierski.report;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.projekt_inzynierski.user.User;

import java.util.List;

public interface ReportRepository extends CrudRepository<Report, Long> {
    Report findById(long id);
    List<Report> findAllByAssignedUserIsNull();
    List<Report> findAllByAssignedUser_Email(String email);
    List<Report> findAllByReportingUser_Email(String email);
    List<Report> findByAssignedUser(User user);
    List<Report> findByReportingUser(User user);
    List<Report> findAllByStatusNot(ReportStatus status);
}
