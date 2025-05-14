package com.example.postproject.services;

import com.example.postproject.cache.SimpleCache;
import com.example.postproject.exceptions.BadRequestException;
import com.example.postproject.models.User;
import com.example.postproject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpleCache cache;

    @Mock
    private User mockUser;

    @Mock
    private User mockUpdatedData;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getUsername()).thenReturn("testuser");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getPassword()).thenReturn("password");

        when(mockUpdatedData.getEmail()).thenReturn("new@example.com");
        when(mockUpdatedData.getUsername()).thenReturn("newuser");
    }

    @Test
    void createUser() {
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.createUser(mockUser);

        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        verify(userRepository).save(mockUser);
        verify(cache).remove("all_users");
    }

    @Test
    void getAllUsers() {
        List<User> users = Arrays.asList(mockUser);
        when(cache.get("all_users")).thenReturn(Optional.empty());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
        verify(cache).put("all_users", users);
    }

    @Test
    void getUserById() {
        when(cache.get("user_1")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(mockUser.getId(), result.get().getId());
        verify(cache).put("user_1", mockUser);
    }

    @Test
    void updateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.updateUser(1L, mockUpdatedData);

        assertEquals("new@example.com", result.getEmail());
        assertEquals("newuser", result.getUsername());
        verify(cache).put("user_1", mockUser);
        verify(cache).remove("all_users");
    }

    @Test
    void deleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
        verify(cache).remove("user_1");
        verify(cache).remove("all_users");
    }
}
