package com.auth.controller;

import com.auth.io.ProfileRequest;
import com.auth.io.ProfileResponse;
import com.auth.services.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;



@RestController

@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    // public ProfileController(ProfileService profileService) {
    // this.profileService = profileService;
    // }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request) {
        ProfileResponse profile = profileService.createProfile(request);
        // TODO : send welcome main
        return profile;
    }

    
    @GetMapping("/test")
    
    public String test(){
        return  "API is Working";
    }
}
