package com.sparta.passport3.auth.security;


import com.sparta.passport3.auth.client.UserServiceClient;
import com.sparta.passport3.auth.dto.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    public UserDetailsServiceImpl(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserResponseDto userResponse = userServiceClient.getUserByUsername(username).getBody();
        if (userResponse == null) {
            throw new UsernameNotFoundException("Not Found " + username);
        }

        return new UserDetailsImpl(userResponse);
    }
}