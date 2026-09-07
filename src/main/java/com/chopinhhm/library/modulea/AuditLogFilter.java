package com.chopinhhm.library.modulea;

import java.io.IOException;
import java.time.LocalDateTime;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuditLogFilter extends OncePerRequestFilter {
    private final OperationLogRepository repository;
    public AuditLogFilter(OperationLogRepository repository) { this.repository = repository; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } finally {
            if (request.getRequestURI().startsWith("/api/")) {
                OperationLog log = new OperationLog();
                log.setUsername(request.getUserPrincipal() == null ? "anonymous" : request.getUserPrincipal().getName());
                log.setMethod(request.getMethod());
                log.setPath(request.getRequestURI());
                log.setStatusCode(response.getStatus());
                log.setOperatedAt(LocalDateTime.now());
                repository.save(log);
            }
        }
    }
}
