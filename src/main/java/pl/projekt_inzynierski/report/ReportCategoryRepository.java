package pl.projekt_inzynierski.report;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportCategoryRepository extends CrudRepository<ReportCategory, Long> {
    Optional<ReportCategory> findById(Long id);
    Optional<ReportCategory> findByName(String name);
    boolean existsByName(String name);

}
