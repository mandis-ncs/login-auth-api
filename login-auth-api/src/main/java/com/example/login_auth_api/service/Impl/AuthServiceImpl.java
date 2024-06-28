package com.example.login_auth_api.service.Impl;

import com.example.login_auth_api.domain.user.User;
import com.example.login_auth_api.dto.LoginRequestDTO;
import com.example.login_auth_api.dto.RegisterRequestDTO;
import com.example.login_auth_api.dto.ResponseDTO;
import com.example.login_auth_api.exceptions.InvalidPasswordException;
import com.example.login_auth_api.exceptions.UserAlreadyExistsException;
import com.example.login_auth_api.exceptions.UserNotFoundException;
import com.example.login_auth_api.infra.security.TokenService;
import com.example.login_auth_api.repositories.UserRepository;
import com.example.login_auth_api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Override
    public ResponseDTO login(LoginRequestDTO body) {
        User user = this.userRepository.findByEmail(body.email())
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (!passwordEncoder.matches(body.password(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password.");
        }

        String token = this.tokenService.generateToken(user);
        return new ResponseDTO(user.getName(), token);
    }

    @Override
    public ResponseDTO register(RegisterRequestDTO body) {
        Optional<User> user = this.userRepository.findByEmail(body.email());

        if (user.isPresent()) {
            throw new UserAlreadyExistsException("User already exists. Try login instead.");
        }

        User newUser = mapRequestToUserEntity(body);
        this.userRepository.save(newUser);
        String token = this.tokenService.generateToken(newUser);
        return new ResponseDTO(newUser.getName(), token);
    }

    private User mapRequestToUserEntity(RegisterRequestDTO body) {
        return User.builder()
                .password(passwordEncoder.encode(body.password()))
                .email(body.email())
                .name(body.name())
                .build();
    }

}
