package com.gtalent.tutor.services;

import com.gtalent.tutor.models.User;
import com.gtalent.tutor.repositories.UserRepository;
import com.gtalent.tutor.requests.LoginRequest;
import com.gtalent.tutor.requests.RegisterRequest;
import com.gtalent.tutor.responses.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        // 1. 建立user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        userRepository.save(user);
        // 2. 產出token
        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse auth(LoginRequest request) {
        // 1. 找到對應的user
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (request.getPassword().equals(user.getPassword())) {
                // 2. 產出token
                String jwtToken = jwtService.generateToken(user);
                return new AuthResponse(jwtToken);
            }
        }
        throw new RuntimeException("無效的憑證");
    }
}
