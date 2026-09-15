package com.chopinhhm.library.modulea;

import com.chopinhhm.library.common.BusinessException;
import java.util.List;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService implements UserDetailsService {
    private final UserAccountRepository accounts;
    private final PasswordEncoder encoder;

    public AccountService(UserAccountRepository accounts, PasswordEncoder encoder) {
        this.accounts = accounts;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = accounts.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("账号不存在"));
        return User.withUsername(account.getUsername()).password(account.getPasswordHash())
            .roles(account.getRole().name()).disabled(!account.isEnabled()).build();
    }

    public List<UserAccount> findAll() { return accounts.findAll(); }

    @Transactional
    public UserAccount create(String username, String password, UserAccount.Role role, Long readerId) {
        validateNewAccount(username, password);
        if (accounts.existsByUsername(username)) throw new BusinessException("用户名已存在");
        UserAccount account = new UserAccount();
        account.setUsername(username.trim());
        account.setPasswordHash(encoder.encode(password));
        account.setRole(normalizeRole(role));
        account.setReaderId(readerId);
        return accounts.save(account);
    }

    private void validateNewAccount(String username, String password) {
        if (username == null || username.trim().isEmpty()) throw new BusinessException("用户名不能为空");
        validatePassword(password);
    }

    @Transactional
    public UserAccount update(Long id, Boolean enabled, String password, UserAccount.Role role, Long readerId) {
        UserAccount account = accounts.findById(id).orElseThrow(() -> new BusinessException("账号不存在"));
        updateEnabled(account, enabled);
        updatePassword(account, password);
        updateRole(account, role);
        account.setReaderId(readerId);
        return accounts.save(account);
    }

    private void updateEnabled(UserAccount account, Boolean enabled) {
        if (enabled != null) account.setEnabled(enabled.booleanValue());
    }

    private void updatePassword(UserAccount account, String password) {
        if (password == null || password.trim().isEmpty()) return;
        validatePassword(password);
        account.setPasswordHash(encoder.encode(password));
    }

    private void updateRole(UserAccount account, UserAccount.Role role) {
        if (role != null) account.setRole(role);
    }

    private UserAccount.Role normalizeRole(UserAccount.Role role) {
        if (role == null) return UserAccount.Role.READER;
        return role;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) throw new BusinessException("密码至少 6 位");
    }
}
