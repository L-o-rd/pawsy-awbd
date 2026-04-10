package com.awbd.pawsy.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.awbd.pawsy.user.model.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(final String username);
    boolean existsUserByUsername(final String username);
    boolean existsUserByEmail(final String email);
}
