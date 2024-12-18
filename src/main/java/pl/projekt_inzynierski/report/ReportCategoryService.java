package pl.projekt_inzynierski.report;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportCategoryService {

    private final ReportCategoryRepository reportCategoryRepository;
    private final ReportService reportService;

    @Autowired
    public ReportCategoryService(ReportCategoryRepository reportCategoryRepository, ReportService reportService) {
        this.reportCategoryRepository = reportCategoryRepository;
        this.reportService = reportService;
    }

    @Transactional
    public void addNewReportCategory(ReportCategory reportCategory) {
        reportCategoryRepository.save(reportCategory);
    }

    @Transactional
    public void deleteReportCategory(Long id) {
        ReportCategory category = reportCategoryRepository.findById(id).orElseThrow();
        List<Report> reports = reportService.getReportsByCategory(category);
        for (Report report : reports) {
            report.setCategory(null);
            reportService.saveNewReport(report);
        }
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
