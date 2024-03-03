package com.bob.bankapispringapp.controller;

import com.bob.bankapispringapp.model.LoginReq;
import com.bob.bankapispringapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @ResponseBody
    @PostMapping("/sign-in")
    public ResponseEntity<?> login(@RequestBody @Valid LoginReq loginReq)  {
        return authService.authenticate(loginReq);
    }
}
