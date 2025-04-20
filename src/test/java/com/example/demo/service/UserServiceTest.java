package com.example.demo.service;

    import com.example.demo.model.User;
    import com.example.demo.repository.UserRepository;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.security.crypto.password.PasswordEncoder;

    import java.util.Optional;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.ArgumentMatchers.anyString;
    import static org.mockito.Mockito.when;

    @ExtendWith(MockitoExtension.class)
    class UserServiceTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private UserService userService;

        private User testUser;

        @BeforeEach
        void setUp() {
            testUser = new User();
            testUser.setId(1L);
            testUser.setUsername("testuser");
            testUser.setEmail("test@example.com");
            testUser.setPassword("password");
        }

        @Test
        void loadUserByUsername_Success() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserDetails userDetails = userService.loadUserByUsername("testuser");

            assertNotNull(userDetails);
            assertEquals("testuser", userDetails.getUsername());
        }

        @Test
        void loadUserByUsername_UserNotFound() {
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () -> {
                userService.loadUserByUsername("unknown");
            });
        }

        @Test
        void findByUsername_Success() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            User found = userService.findByUsername("testuser");

            assertNotNull(found);
            assertEquals("testuser", found.getUsername());
        }

        @Test
        void registerNewUser_Success() {
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setEmail("new@example.com");
            newUser.setPassword("password");

            User savedUser = userService.registerNewUser(newUser);

            assertNotNull(savedUser);
        }

        @Test
        void registerNewUser_UsernameTaken() {
            when(userRepository.existsByUsername("testuser")).thenReturn(true);

            User newUser = new User();
            newUser.setUsername("testuser");
            newUser.setEmail("new@example.com");
            newUser.setPassword("password");

            assertThrows(RuntimeException.class, () -> {
                userService.registerNewUser(newUser);
            });
        }

        @Test
        void registerNewUser_EmailTaken() {
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setEmail("test@example.com");
            newUser.setPassword("password");

            assertThrows(RuntimeException.class, () -> {
                userService.registerNewUser(newUser);
            });
        }
    }