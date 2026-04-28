package com.vendora.epic1.security;

import com.vendora.epic1.model.User;
import com.vendora.epic1.model.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CustomUserDetails implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final AtomicReference<User> user = new AtomicReference<>();

    public CustomUserDetails(User user) {
        this.user.set(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.get().getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.get().getPassword();
    }

    @Override
    public String getUsername() {
        return user.get().getUserCode();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.get().getStatus() == null || user.get().getStatus() != UserStatus.SUSPENDED;
    }

    @Override
    public boolean isEnabled() {
        if (user.get().getStatus() == null) {
            return false;
        }

        UserStatus status = user.get().getStatus();
        return status == UserStatus.ACTIVE || status == UserStatus.PENDING_VERIFICATION;
    }

    public User getUser() {
        return user.get();
    }
}