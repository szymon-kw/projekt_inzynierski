package pl.projekt_inzynierski.report;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReportCategoryService {

    private final ReportCategoryRepository reportCategoryRepository;

    @Autowired
    public ReportCategoryService(ReportCategoryRepository reportCategoryRepository) {
        this.reportCategoryRepository = reportCategoryRepository;
    }

    @Transactional
    public void addNewReportCategory(Report_Category reportCategory) {
        reportCategoryRepository.save(reportCategory);
    }

    @Transactional
    public void deleteReportCategory(Long id) {
        Report_Category category = reportCategoryRepository.findById(id).orElseThrow();
        reportCategoryRepository.delete(category);
    }

    public boolean isExists(String categoryName) {
        return reportCategoryRepository.existsByName(categoryName);
    }

    public Iterable<Report_Category> getAllReportCategories() {
        return reportCategoryRepository.findAll();
    }

    public long getCountOfReportCategories() {
        return reportCategoryRepository.count();
    }

    public Optional<Report_Category> findByName(String categoryName) {
        return reportCategoryRepository.findByName(categoryName);
    }


    public Report_Category getReportCategoryById(Long id) {
        return reportCategoryRepository.findById(id).orElseThrow();
    }

}
