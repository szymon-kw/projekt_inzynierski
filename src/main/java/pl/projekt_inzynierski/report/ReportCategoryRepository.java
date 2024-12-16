package pl.projekt_inzynierski.report;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportCategoryRepository extends CrudRepository<Report_Category, Long> {
    Optional<Report_Category> findById(Long id);
    Optional<Report_Category> findByName(String name);
    boolean existsByName(String name);

}
