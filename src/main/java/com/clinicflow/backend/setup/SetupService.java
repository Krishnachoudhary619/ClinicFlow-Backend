package com.clinicflow.backend.setup;

import com.clinicflow.backend.clinic.Clinic;
import com.clinicflow.backend.clinic.ClinicRepository;
import com.clinicflow.backend.common.ApiException;
import com.clinicflow.backend.user.User;
import com.clinicflow.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String createClinic(CreateClinicRequest request) {

        // Check duplicate email
        if (userRepository.findByEmail(request.getAdminEmail()).isPresent()) {
            throw new ApiException("Email already exists", "USER_001");
        }

        // Create clinic
        Clinic clinic = Clinic.builder()
                .name(request.getClinicName())
                .address(request.getAddress())
                .city(request.getCity())
                .phoneNumber(request.getPhoneNumber())
                .isActive(true)
                .build();

        clinicRepository.save(clinic);

        // Create admin user
        User admin = User.builder()
                .clinic(clinic)
                .name(request.getAdminName())
                .email(request.getAdminEmail())
                .phoneNumber(request.getAdminPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.ADMIN)
                .isActive(true)
                .build();

        userRepository.save(admin);

        return "Clinic and Admin user created successfully";
    }
}
