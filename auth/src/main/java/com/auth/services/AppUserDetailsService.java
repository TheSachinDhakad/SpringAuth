package com.auth.services;

import com.auth.Entity.UserEntity;
import com.auth.repository.UserReposetory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserReposetory userReposetory;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity existingUser = userReposetory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email Not Found for the email" + email));
         return new User(existingUser.getEmail() , existingUser.getPassword() , new ArrayList<>());

    }
}
