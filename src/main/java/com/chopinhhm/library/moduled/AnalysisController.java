package com.chopinhhm.library.moduled;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/analysis")
public class AnalysisController {
    private final OverdueAnalysisService service;

    public AnalysisController(OverdueAnalysisService service) { this.service = service; }

    @GetMapping("/overdue")
    public List<OverdueAnalysis> overdue() { return service.currentReport(); }

    @GetMapping(value = "/overdue.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportOverdueCsv() {
        StringBuilder csv = new StringBuilder("\uFEFF借阅ID,读者,图书,应还日期,逾期天数,预估罚金\n");
        for (OverdueAnalysis row : service.currentReport()) {
            csv.append(row.getLoanId()).append(',').append(escape(row.getReaderName())).append(',')
                .append(escape(row.getBookTitle())).append(',').append(row.getDueAt()).append(',')
                .append(row.getOverdueDays()).append(',').append(row.getEstimatedFine()).append('\n');
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=overdue-report.csv")
            .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
            .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String escape(String value) { return "\"" + value.replace("\"", "\"\"") + "\""; }
}
