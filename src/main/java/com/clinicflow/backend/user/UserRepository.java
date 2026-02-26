package com.clinicflow.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByClinicId(Long clinicId);

    List<User> findByClinicIdAndRole(Long clinicId, User.Role role);
}
