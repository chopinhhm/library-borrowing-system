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
        if (username == null || username.trim().isEmpty()) throw new BusinessException("用户名不能为空");
        if (password == null || password.length() < 6) throw new BusinessException("密码至少 6 位");
        if (accounts.existsByUsername(username)) throw new BusinessException("用户名已存在");
        UserAccount account = new UserAccount();
        account.setUsername(username.trim());
        account.setPasswordHash(encoder.encode(password));
        account.setRole(role == null ? UserAccount.Role.READER : role);
        account.setReaderId(readerId);
        return accounts.save(account);
    }

    @Transactional
    public UserAccount update(Long id, Boolean enabled, String password, UserAccount.Role role, Long readerId) {
        UserAccount account = accounts.findById(id).orElseThrow(() -> new BusinessException("账号不存在"));
        if (enabled != null) account.setEnabled(enabled.booleanValue());
        if (password != null && !password.trim().isEmpty()) {
            if (password.length() < 6) throw new BusinessException("密码至少 6 位");
            account.setPasswordHash(encoder.encode(password));
        }
        if (role != null) account.setRole(role);
        account.setReaderId(readerId);
        return accounts.save(account);
    }
}
