package com.auth.services;

import com.auth.Entity.UserEntity;
import com.auth.io.ProfileRequest;
import com.auth.io.ProfileResponse;
import com.auth.repository.UserReposetory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserReposetory userReposetory;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        UserEntity userEntity = convertToUserEntity(request);
        if (!userReposetory.existsByEmail(request.getEmail())) {
            userEntity = userReposetory.save(userEntity);
            return convertToProfileResponse(userEntity);

        }

        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email Already Exist..");

    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerify(false)
                .resetOtpExpire(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();

    }

    private ProfileResponse convertToProfileResponse(UserEntity userEntity) {
        return ProfileResponse.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .userId(userEntity.getUserId())
                .isAccountVerify(userEntity.getIsAccountVerify())
                .build();

    }
}
