package com.vendora.epic1.repository;

import com.vendora.epic1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import com.vendora.epic1.model.enums.RoleType;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCaseAndPhone(String email, String phone);

    Optional<User> findByUserCodeIgnoreCase(String userCode);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByRole(RoleType role);
}