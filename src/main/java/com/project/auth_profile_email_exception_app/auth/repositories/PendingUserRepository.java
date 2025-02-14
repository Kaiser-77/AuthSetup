package com.project.auth_profile_email_exception_app.auth.repositories;

import com.project.auth_profile_email_exception_app.auth.entities.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingUserRepository extends JpaRepository<PendingUser, Integer> {

    Optional<PendingUser> findByEmail(String email);

    Optional<PendingUser> findByOtp(String otp);

    Optional<PendingUser> findByEmailAndOtp(String email, String otp);
}
