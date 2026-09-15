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
    // 构造器注入账号仓储和密码编码器。
    public AccountService(UserAccountRepository accounts, PasswordEncoder encoder) {
        this.accounts = accounts;
        this.encoder = encoder;
    }
    // 从账号仓储读取认证所需的用户信息。
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = accounts.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("账号不存在"));
        return User.withUsername(account.getUsername()).password(account.getPasswordHash())
            .roles(account.getRole().name()).disabled(!account.isEnabled()).build();
    }
    // 返回全部账号，供管理员维护页面使用。
    public List<UserAccount> findAll() { return accounts.findAll(); }
    // 创建账号前先验证用户名和密码。
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
    // 用户名唯一性由仓储层检查，校验通过后进入保存流程。
    private void validateNewAccount(String username, String password) {
        if (username == null || username.trim().isEmpty()) throw new BusinessException("用户名不能为空");
        validatePassword(password);
    }
    // 更新时只处理调用方明确传入的字段。
    @Transactional
    public UserAccount update(Long id, Boolean enabled, String password, UserAccount.Role role, Long readerId) {
        UserAccount account = accounts.findById(id).orElseThrow(() -> new BusinessException("账号不存在"));
        updateEnabled(account, enabled);
        updatePassword(account, password);
        updateRole(account, role);
        account.setReaderId(readerId);
        return accounts.save(account);
    }
    // 对可选字段分别应用更新，避免条件嵌套。
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
    // 密码长度不足六位时拒绝保存。
    private void validatePassword(String password) {
        if (password == null || password.length() < 6) throw new BusinessException("密码至少 6 位");
    }
}
