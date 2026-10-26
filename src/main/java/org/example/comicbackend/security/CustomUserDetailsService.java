package org.example.comicbackend.security;

import org.example.comicbackend.entity.User;
import org.example.comicbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        Optional<User> userOpt = account.contains("@")
                ? userRepository.findByEmail(account)
                : userRepository.findByUsername(account);

        User user = userOpt.orElseThrow(() ->
                new UsernameNotFoundException("Không tìm thấy người dùng với tài khoản: " + account));

        return new CustomUserDetails(user);
    }
}
