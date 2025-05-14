package com.example.postproject.services;

import com.example.postproject.cache.SimpleCache;
import com.example.postproject.exceptions.BadRequestException;
import com.example.postproject.exceptions.InternalServerErrorException;
import com.example.postproject.models.User;
import com.example.postproject.repository.UserRepository;
import com.example.postproject.services.RequestCounter;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**user service realization.*/
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final SimpleCache cache;
    private final RequestCounter requestCounter;

    /**cache for User.*/
    public UserService(UserRepository userRepository, SimpleCache cache, RequestCounter requestCounter) {
        this.userRepository = userRepository;
        this.cache = cache;
        this.requestCounter = requestCounter;
    }

    private String getUserCacheKey(Long id) {
        return "user_" + id;
    }

    private String getAllUsersCacheKey() {
        return "all_users";
    }

    /**createUser method.*/
    public User createUser(User user) {
        int requestNumber = requestCounter.increment();
        try {
            if (user == null) {
                throw new BadRequestException("User data cannot be null");
            }
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                throw new BadRequestException("Email cannot be empty");
            }
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new BadRequestException("Password cannot be empty");
            }
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                throw new BadRequestException("Username cannot be empty");
            }

            User createdUser = userRepository.save(user);
            cache.remove(getAllUsersCacheKey());
            logger.info("User created successfully: ID={}, Email={}", createdUser.getId(), createdUser.getEmail());
            return createdUser;
        } catch (BadRequestException e) {
            logger.warn("Validation error in createUser: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to create user: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to create user due to server error");
        }
    }

    /**getAllUsers method.*/
    public List<User> getAllUsers() {
        int requestNumber = requestCounter.increment();
        try {
            String cacheKey = getAllUsersCacheKey();
            Optional<Object> cachedUsers = cache.get(cacheKey);

            if (cachedUsers.isPresent()) {
                logger.debug("Retrieved users from cache");
                return (List<User>) cachedUsers.get();
            }

            List<User> users = userRepository.findAll();
            cache.put(cacheKey, users);
            logger.info("Retrieved {} users from database", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Failed to fetch users: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to fetch users");
        }
    }

    /**getUserById method.*/
    public Optional<User> getUserById(Long id) {
        int requestNumber = requestCounter.increment();
        try {
            if (id == null || id <= 0) {
                throw new BadRequestException("Invalid user ID");
            }

            String cacheKey = getUserCacheKey(id);
            Optional<Object> cachedUser = cache.get(cacheKey);

            if (cachedUser.isPresent()) {
                logger.debug("Retrieved user from cache: ID={}", id);
                return Optional.of((User) cachedUser.get());
            }

            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                cache.put(cacheKey, user.get());
                logger.info("Retrieved user from database: ID={}", id);
            } else {
                logger.warn("User not found: ID={}", id);
            }
            return user;
        } catch (BadRequestException e) {
            logger.warn("Invalid request in getUserById: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to fetch user by ID: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to fetch user");
        }
    }

    /**getUserByUsername method.*/
    public Optional<User> getUserByUsername(String username) {
        int requestNumber = requestCounter.increment();
        try {
            if (username == null || username.isEmpty()) {
                throw new BadRequestException("Username cannot be empty");
            }

            String cacheKey = "user_username_" + username;
            Optional<Object> cachedUser = cache.get(cacheKey);

            if (cachedUser.isPresent()) {
                logger.debug("Retrieved user from cache: username={}", username);
                return Optional.of((User) cachedUser.get());
            }

            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                cache.put(cacheKey, user.get());
                logger.info("Retrieved user from database: username={}", username);
            } else {
                logger.warn("User not found: username={}", username);
            }
            return user;
        } catch (BadRequestException e) {
            logger.warn("Invalid request in getUserByUsername: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to fetch user by username: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to fetch user");
        }
    }

    /**updateUser method.*/
    public User updateUser(Long id, User userDetails) {
        int requestNumber = requestCounter.increment();
        try {
            if (id == null || id <= 0) {
                throw new BadRequestException("Invalid user ID");
            }
            if (userDetails == null) {
                throw new BadRequestException("User details cannot be null");
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new BadRequestException("User not found with ID: " + id));

            if (userDetails.getEmail() != null) {
                user.setEmail(userDetails.getEmail());
            }
            if (userDetails.getPassword() != null) {
                user.setPassword(userDetails.getPassword());
            }
            if (userDetails.getUsername() != null) {
                user.setUsername(userDetails.getUsername());
            }

            User updatedUser = userRepository.save(user);
            cache.put(getUserCacheKey(id), updatedUser);
            cache.remove(getAllUsersCacheKey());
            logger.info("User updated successfully: ID={}", id);
            return updatedUser;
        } catch (BadRequestException e) {
            logger.warn("Validation error in updateUser: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to update user: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to update user");
        }
    }

    /**deleteUser method.*/
    public void deleteUser(Long id) {
        int requestNumber = requestCounter.increment();
        try {
            if (id == null || id <= 0) {
                throw new BadRequestException("Invalid user ID");
            }

            if (!userRepository.existsById(id)) {
                throw new BadRequestException("User not found with ID: " + id);
            }

            userRepository.deleteById(id);
            cache.remove(getUserCacheKey(id));
            cache.remove(getAllUsersCacheKey());
            logger.info("User deleted successfully: ID={}", id);
        } catch (BadRequestException e) {
            logger.warn("Validation error in deleteUser: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to delete user: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to delete user");
        }
    }
}