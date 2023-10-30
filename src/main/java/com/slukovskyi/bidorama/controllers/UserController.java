package com.slukovskyi.bidorama.controllers;

import com.slukovskyi.bidorama.dtos.UserResponseDto;
import com.slukovskyi.bidorama.mappers.UserResponseDtoMapper;
import com.slukovskyi.bidorama.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserResponseDtoMapper userResponseDtoMapper;

    @PutMapping("deposit")
    public ResponseEntity<UserResponseDto> deposit(@RequestParam("amount") Double amount) {
        UserResponseDto userResponseDto = userService.deposit(amount);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        UserResponseDto userResponseDto = userResponseDtoMapper.userToUserResponseDto(userService.getCurrentUser());
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }
}
