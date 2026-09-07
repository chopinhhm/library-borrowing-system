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
    @GetMapping("/me")
    public Map<String, Object> me(Principal principal, Authentication authentication) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("username", principal.getName());
        result.put("authorities", authentication.getAuthorities());
        return result;
    }
}
