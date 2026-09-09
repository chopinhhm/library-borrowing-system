package com.chopinhhm.library.modulea;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserAccountRepository accounts;
    public AuthController(UserAccountRepository accounts) { this.accounts = accounts; }

    @GetMapping("/me")
    public Map<String, Object> me(Principal principal, Authentication authentication) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("username", principal.getName());
        result.put("authorities", authentication.getAuthorities());
        UserAccount account = accounts.findByUsername(principal.getName()).orElse(null);
        result.put("role", account == null ? null : account.getRole());
        result.put("readerId", account == null ? null : account.getReaderId());
        return result;
    }
}
