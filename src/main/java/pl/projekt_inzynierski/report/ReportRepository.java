package pl.projekt_inzynierski.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.projekt_inzynierski.user.User;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findById(long id);
    List<Report> findAllByAssignedUserIsNull();
    List<Report> findAllByAssignedUser_Email(String email);
    List<Report> findAllByReportingUser_Email(String email);
    List<Report> findByAssignedUser(User user);
    List<Report> findByReportingUser(User user);
    List<Report> findAllByStatusNot(ReportStatus status);
    List<Report> findAllByCategory(Report_Category category);
    List<Report> findAllByDateAddedIsBetween(LocalDateTime dateAdded, LocalDateTime dateAdded2);
    List<Report> findAllByReportingUserAndStatus(User user, ReportStatus status);
    List<Report> findAllByReportingUserAndStatusNot(User user, ReportStatus status);
    List<Report> findAllByAssignedUserAndStatus(User user, ReportStatus status);
    List<Report> findAllByAssignedUserAndStatusNot(User user, ReportStatus status);
    List<Report> findAllByAssignedUserIsNullAndStatusNot(ReportStatus status);
    List<Report> findAllByStatus(ReportStatus status);

    //zliczanie dla Admina
    long countAllByStatus(ReportStatus status);
    long countAllByAssignedUserIsNullAndStatusNot(ReportStatus status);
    //zliczanie dla Usera
    long countAllByReportingUserAndStatus(User user, ReportStatus status);
    //zliczanie dla Praconika
    long countAllByAssignedUserAndStatus(User user, ReportStatus status);
}
