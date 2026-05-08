package com.awbd.pawsy.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.awbd.pawsy.user.model.Role;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByName(final String name);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT IGNORE INTO roles (name)
        VALUES (:name)
        """, nativeQuery = true)
    void checkedInsert(@Param("name") String name);
}
