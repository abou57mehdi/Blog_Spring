package com.example.demo.config;

                        import org.springframework.context.annotation.Bean;
                        import org.springframework.context.annotation.Configuration;
                        import org.springframework.security.config.annotation.web.builders.HttpSecurity;
                        import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
                        import org.springframework.security.core.userdetails.UserDetailsService;
                        import org.springframework.security.web.SecurityFilterChain;

                        @Configuration
                        @EnableWebSecurity
                        public class SecurityConfig {

                            @Bean
                            public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
                                http
                                    .userDetailsService(userDetailsService)
                                    .authorizeHttpRequests((requests) -> requests
                                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                                        .requestMatchers("/posts").permitAll()
                                        .requestMatchers("/posts/{id}").permitAll()
                                        .requestMatchers("/posts/create", "/posts/*/edit", "/posts/*/delete", "/posts/*/like", "/posts/*/comment").authenticated()
                                        .anyRequest().authenticated()
                                    )
                                    .formLogin((form) -> form
                                        .loginPage("/login")
                                        .defaultSuccessUrl("/")
                                        .permitAll()
                                    )
                                    .logout((logout) -> logout
                                        .logoutSuccessUrl("/")
                                        .permitAll()
                                    );

                                return http.build();
                            }
                        }