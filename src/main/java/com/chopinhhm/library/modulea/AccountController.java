package com.chopinhhm.library.modulea;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/accounts")
public class AccountController {
    private final AccountService service;
    public AccountController(AccountService service) { this.service = service; }

    @GetMapping public List<UserAccount> list() { return service.findAll(); }
    @PostMapping public UserAccount create(@RequestBody AccountRequest r) { return service.create(r.username, r.password, r.role, r.readerId); }
    @PatchMapping("/{id}") public UserAccount update(@PathVariable Long id, @RequestBody AccountRequest r) {
        return service.update(id, r.enabled, r.password, r.role, r.readerId);
    }

    public static class AccountRequest {
        public String username;
        public String password;
        public UserAccount.Role role;
        public Boolean enabled;
        public Long readerId;
    }
}
