package com.auth.services;

import com.auth.io.ProfileRequest;
import com.auth.io.ProfileResponse;

public interface ProfileService {
   ProfileResponse createProfile(ProfileRequest request);
}
