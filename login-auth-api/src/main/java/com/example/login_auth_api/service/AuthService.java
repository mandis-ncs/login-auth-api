package com.example.login_auth_api.service;

import com.example.login_auth_api.dto.LoginRequestDTO;
import com.example.login_auth_api.dto.RegisterRequestDTO;
import com.example.login_auth_api.dto.ResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    ResponseDTO login(LoginRequestDTO body);
    ResponseDTO register(RegisterRequestDTO body);
}
