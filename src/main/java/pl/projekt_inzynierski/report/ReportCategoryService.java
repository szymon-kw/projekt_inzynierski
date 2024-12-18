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
    public void addNewReportCategory(ReportCategory reportCategory) {
        reportCategoryRepository.save(reportCategory);
    }

    @Transactional
    public void deleteReportCategory(Long id) {
        ReportCategory category = reportCategoryRepository.findById(id).orElseThrow();
        reportCategoryRepository.delete(category);
    }

    public boolean isExists(String categoryName) {
        return reportCategoryRepository.existsByName(categoryName);
    }

    public Iterable<ReportCategory> getAllReportCategories() {
        return reportCategoryRepository.findAll();
    }

    public long getCountOfReportCategories() {
        return reportCategoryRepository.count();
    }

    public Optional<ReportCategory> findByName(String categoryName) {
        return reportCategoryRepository.findByName(categoryName);
    }


    public ReportCategory getReportCategoryById(Long id) {
        return reportCategoryRepository.findById(id).orElseThrow();
    }

}
