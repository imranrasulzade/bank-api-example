package com.bob.bankapispringapp.service;

import com.bob.bankapispringapp.model.LoginReq;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    ResponseEntity<?> authenticate(LoginReq loginReq);
}
