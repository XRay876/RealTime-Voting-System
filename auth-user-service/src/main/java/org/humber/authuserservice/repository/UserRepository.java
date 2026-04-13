package org.humber.authuserservice.repository;

import org.humber.authuserservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    boolean existsByEmailAndIsDeletedFalse(String email);
    Optional<User> findByIdAndIsDeletedFalse(UUID id);
}