package com.slukovskyi.bidorama.services;

import com.slukovskyi.bidorama.dtos.UserRequestDto;
import com.slukovskyi.bidorama.dtos.UserResponseDto;
import com.slukovskyi.bidorama.models.User;

public interface UserService {
    UserResponseDto register(UserRequestDto userRequestDto);

    UserResponseDto login(UserRequestDto userRequestDto);

    User getCurrentUser();

    UserResponseDto deposit(Double amount);

    void returnFunds(User user, Double amount);

    UserResponseDto withdraw(Double amount);
}
