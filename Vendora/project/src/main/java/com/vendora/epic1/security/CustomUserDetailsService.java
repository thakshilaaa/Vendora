package com.vendora.epic1.security;

import com.vendora.epic1.model.User;
import com.vendora.epic1.model.User;
import com.vendora.epic1.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("User not found");
        }
        String u = username.trim();

        Optional<User> byCode = userRepository.findByUserCodeIgnoreCase(u);
        if (byCode.isPresent()) {
            return new CustomUserDetails(byCode.get());
        }
        return loadByLegacyEmailSubject(u);
    }

    /**
     * Older JWTs used email as the subject. Support those only when exactly one user has that email.
     */
    private UserDetails loadByLegacyEmailSubject(String u) {
        if (!u.contains("@")) {
            throw new UsernameNotFoundException("User not found: " + u);
        }
        List<User> users = userRepository.findAllByEmailIgnoreCase(u);
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + u);
        }
        if (users.size() > 1) {
            throw new UsernameNotFoundException(
                    "This email is shared by several accounts. Sign in again to obtain a new session (use your User ID U…).");
        }
        return new CustomUserDetails(users.get(0));
    }
}
