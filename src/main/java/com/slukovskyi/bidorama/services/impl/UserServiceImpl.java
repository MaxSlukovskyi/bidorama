package com.slukovskyi.bidorama.services.impl;

import com.slukovskyi.bidorama.dtos.UserRequestDto;
import com.slukovskyi.bidorama.dtos.UserResponseDto;
import com.slukovskyi.bidorama.exceptions.AlreadyExistsException;
import com.slukovskyi.bidorama.exceptions.InsufficientFundsException;
import com.slukovskyi.bidorama.exceptions.NotFoundException;
import com.slukovskyi.bidorama.mappers.UserRequestDtoMapper;
import com.slukovskyi.bidorama.mappers.UserResponseDtoMapper;
import com.slukovskyi.bidorama.models.User;
import com.slukovskyi.bidorama.models.enums.Role;
import com.slukovskyi.bidorama.repositories.UserRepository;
import com.slukovskyi.bidorama.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRequestDtoMapper userRequestDtoMapper;
    private final UserResponseDtoMapper userResponseDtoMapper;

    @Override
    public UserResponseDto register(UserRequestDto userRequestDto) {
        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new AlreadyExistsException(String.format("User with username '%s' already exists",
                    userRequestDto.getUsername()));
        }

        User user = userRequestDtoMapper.userRequestDtoToUser(userRequestDto);

        String hashPassword = passwordEncoder.encode(userRequestDto.getPassword());
        user.setPassword(hashPassword);
        user.setRole(Role.USER);
        user.setBalance(0.0);

        User savedUser = userRepository.save(user);

        return userResponseDtoMapper.userToUserResponseDto(savedUser);
    }

    @Override
    public UserResponseDto login(UserRequestDto userRequestDto) throws AuthenticationException{
        String username = userRequestDto.getUsername();
        String password = userRequestDto.getPassword();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String loggedInUsername = userDetails.getUsername();

        User user = userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new NotFoundException(String.format("User with username '%s' does not exist",
                        userRequestDto.getUsername())));

        return userResponseDtoMapper.userToUserResponseDto(user);
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    @Override
    public UserResponseDto deposit(Double amount) {
        User user = this.getCurrentUser();
        user.setBalance(user.getBalance() + amount);
        User savedUser = userRepository.save(user);
        return userResponseDtoMapper.userToUserResponseDto(savedUser);
    }

    @Override
    public UserResponseDto withdraw(Double amount) {
        User user = this.getCurrentUser();

        if (user.getBalance() < amount) {
            throw new InsufficientFundsException("User has insufficient funds to perform that action");
        }

        user.setBalance(user.getBalance() - amount);
        User savedUser = userRepository.save(user);
        return userResponseDtoMapper.userToUserResponseDto(savedUser);
    }

}
