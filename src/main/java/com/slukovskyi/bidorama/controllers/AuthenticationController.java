package com.slukovskyi.bidorama.controllers;

import com.slukovskyi.bidorama.dtos.UserRequestDto;
import com.slukovskyi.bidorama.dtos.UserResponseDto;
import com.slukovskyi.bidorama.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody UserRequestDto userRequestDto) {
        userService.register(userRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto responseUserDto = userService.login(userRequestDto);
        return new ResponseEntity<>(responseUserDto, HttpStatus.OK);
    }
}
