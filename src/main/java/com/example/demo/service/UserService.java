package com.example.demo.service;

            import com.example.demo.model.User;
            import com.example.demo.repository.UserRepository;
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.security.core.userdetails.UserDetails;
            import org.springframework.security.core.userdetails.UserDetailsService;
            import org.springframework.security.core.userdetails.UsernameNotFoundException;
            import org.springframework.security.crypto.password.PasswordEncoder;
            import org.springframework.stereotype.Service;

            @Service
            public class UserService implements UserDetailsService {

                private final UserRepository userRepository;
                private final PasswordEncoder passwordEncoder;

                @Autowired
                public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
                    this.userRepository = userRepository;
                    this.passwordEncoder = passwordEncoder;
                }

                @Override
                public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));

                    return org.springframework.security.core.userdetails.User.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .roles("USER")
                            .build();
                }

                public User findByUsername(String username) {
                    return userRepository.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));
                }

                public User registerNewUser(User user) {
                    if (userRepository.existsByUsername(user.getUsername())) {
                        throw new RuntimeException("Ce nom d'utilisateur est déjà pris");
                    }
                    if (userRepository.existsByEmail(user.getEmail())) {
                        throw new RuntimeException("Cet email est déjà utilisé");
                    }

                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return userRepository.save(user);
                }
            }