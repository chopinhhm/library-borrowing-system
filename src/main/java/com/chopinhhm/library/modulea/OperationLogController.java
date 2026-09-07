package com.chopinhhm.library.modulea;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/logs")
public class OperationLogController {
    private final OperationLogRepository repository;
    public OperationLogController(OperationLogRepository repository) { this.repository = repository; }

    @GetMapping
    public List<OperationLog> list(@RequestParam(defaultValue = "100") int limit) {
        int size = Math.max(1, Math.min(limit, 500));
        return repository.findAll(PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "operatedAt"))).getContent();
    }
}
