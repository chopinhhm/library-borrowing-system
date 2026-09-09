package com.chopinhhm.library.modulea;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccessGuard {
    private final UserAccountRepository accounts;
    public AccessGuard(UserAccountRepository accounts) { this.accounts = accounts; }

    public void assertReaderAccess(Long readerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) return;
        UserAccount account = accounts.findByUsername(auth.getName()).orElseThrow(() -> new AccessDeniedException("账号不存在"));
        if (account.getReaderId() == null || !account.getReaderId().equals(readerId)) {
            throw new AccessDeniedException("不能访问其他读者的数据");
        }
    }
}
