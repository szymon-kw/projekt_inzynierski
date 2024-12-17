package pl.projekt_inzynierski.summary;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;
import pl.projekt_inzynierski.report.Report;
import pl.projekt_inzynierski.report.ReportCategory;
import pl.projekt_inzynierski.report.ReportStatus;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChartService {

    private static final String[] POLISH_MONTHS_NAMES = {
            "styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec",
            "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"
    };

    public String generateDistributionChart(List<Report> reports, LocalDate startDate, LocalDate endDate, boolean byCategory) throws IOException {
        //Na wykresach limit maksymalnej ilosci miesiecy z ktorych statystyki moga zostać wyswietlone wynosi 8
        final LocalDate adjustedStartDate = startDate.isAfter(endDate.minusMonths(7).withDayOfMonth(1))
                ? startDate.withDayOfMonth(1)
                : endDate.minusMonths(7).withDayOfMonth(1);

        Map<LocalDate, Map<Object, Long>> groupedData = adjustedStartDate.datesUntil(endDate.withDayOfMonth(1).plusMonths(1), Period.ofMonths(1))
                .collect(Collectors.toMap(
                        date -> date,
                        date -> {
                            Map<Object, Long> dataMap = new HashMap<>();
                            Object[] values = byCategory ? ReportCategory.values() : ReportStatus.values();
                            for (Object value : values) {
                                dataMap.put(value, 0L);
                            }
                            return dataMap;
                        }
                ));

        reports.stream()
                .filter(report -> !report.getDateAdded().toLocalDate().isBefore(adjustedStartDate) &&
                        !report.getDateAdded().toLocalDate().isAfter(endDate))
                .collect(Collectors.groupingBy(
                        report -> report.getDateAdded().toLocalDate().with(TemporalAdjusters.firstDayOfMonth()),
                        Collectors.groupingBy(
                                byCategory ? Report::getCategory : Report::getStatus,
                                Collectors.counting()
                        )
                ))
                .forEach((date, dataCounts) -> {
                    Map<Object, Long> existingCounts = groupedData.get(date);
                    dataCounts.forEach((key, count) ->
                            existingCounts.put(key, existingCounts.getOrDefault(key, 0L) + count)
                    );
                });

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        groupedData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    LocalDate date = entry.getKey();
                    Map<Object, Long> dataCounts = entry.getValue();
                    int monthIndex = date.getMonthValue() - 1;
                    String monthName = POLISH_MONTHS_NAMES[monthIndex] + " " + date.getYear();
                    Object[] values = byCategory ? ReportCategory.values() : ReportStatus.values();
                    for (Object value : values) {
                        long count = dataCounts.getOrDefault(value, 0L);
                        String seriesName = byCategory ? ((ReportCategory) value).description : ((ReportStatus) value).description;
                        dataset.addValue(count, seriesName, monthName);
                    }
                });

        String title = byCategory ? "Liczba zgłoszeń według kategorii" : "Liczba zgłoszeń według statusów";
        JFreeChart chart = ChartFactory.createStackedBarChart(
                title,
                "Miesiąc",
                "Liczba zgłoszeń",
                dataset
        );

        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
        chart.getCategoryPlot().getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
        chart.getCategoryPlot().getRangeAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
        StackedBarRenderer renderer = new StackedBarRenderer();

        if (byCategory) {
            renderer.setSeriesPaint(0, new Color(244, 67, 54));  // CRITICAL - czerwony
            renderer.setSeriesPaint(1, new Color(255, 193, 7));  //MAJOR - żółty
            renderer.setSeriesPaint(2, new Color(76, 175, 80));  //MINOR - zielony
        } else {
            renderer.setSeriesPaint(0, new Color(33, 150, 243));  //PENDING - niebieski
            renderer.setSeriesPaint(1, new Color(76, 175, 80));  //IN_PROGRESS - zielony
            renderer.setSeriesPaint(2, new Color(244, 67, 54));  //COMPLETED - czerwony
        }

        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Arial", Font.BOLD, 10));
        chart.getCategoryPlot().setRenderer(renderer);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(outputStream, chart, 800, 500);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public String generateAverageTimeComparisonChart(Map<LocalDate, Double> firstReactionTimes, Map<LocalDate, Double> completionTimes,
                                                     LocalDate startDate, LocalDate endDate, boolean singleEmployee) throws IOException {
        //Maksymalnie  8 miesiecy na wykersie
        final LocalDate adjustedStartDate = startDate.isAfter(endDate.minusMonths(7).withDayOfMonth(1))
                ? startDate.withDayOfMonth(1)
                : endDate.minusMonths(7).withDayOfMonth(1);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String firstLabelName = singleEmployee
                ? "Średni czas obsługi zgłoszeń przez wszystkich pracowników (godziny)"
                : "Średni czas pierwszej reakcji na zgłoszenie (godziny)";

        LocalDate current = adjustedStartDate;
        LocalDate last = endDate.withDayOfMonth(1);

        while (!current.isAfter(last)) {
            int monthIndex = current.getMonthValue() - 1;
            String monthName = POLISH_MONTHS_NAMES[monthIndex] + " " + current.getYear();

            Double averageFirstReactionTime = firstReactionTimes.getOrDefault(current, 0.0);
            Double averageCompletionTime = completionTimes.getOrDefault(current, 0.0);

            averageFirstReactionTime = Math.round(averageFirstReactionTime * 10.0) / 10.0;
            averageCompletionTime = Math.round(averageCompletionTime * 10.0) / 10.0;

            dataset.addValue(averageFirstReactionTime, firstLabelName, monthName);
            dataset.addValue(averageCompletionTime, "Średni czas obsługi zgłoszeń pracownika (godziny)", monthName);

            current = current.plusMonths(1);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Porównanie średnich czasów obsługi zgłoszeń",
                "Miesiąc",
                "Czas (godziny)",
                dataset
        );

        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
        chart.getCategoryPlot().getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
        chart.getCategoryPlot().getRangeAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));

        BarRenderer renderer = new BarRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Arial", Font.BOLD, 10));

        renderer.setSeriesPaint(0, new Color(79, 129, 189)); //pierwsza reakcja - niebieski
        renderer.setSeriesPaint(1, new Color(192, 80, 77));  //obsługa zgloszenia - czerwony

        chart.getCategoryPlot().setRenderer(renderer);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(outputStream, chart, 800, 500);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
