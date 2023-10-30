package com.slukovskyi.bidorama.services.impl;

import com.slukovskyi.bidorama.dtos.UserRequestDto;
import com.slukovskyi.bidorama.dtos.UserResponseDto;
import com.slukovskyi.bidorama.exceptions.AlreadyExistsException;
import com.slukovskyi.bidorama.exceptions.InsufficientFundsException;
import com.slukovskyi.bidorama.exceptions.NotFoundException;
import com.slukovskyi.bidorama.exceptions.NullReferenceException;
import com.slukovskyi.bidorama.mappers.UserRequestDtoMapper;
import com.slukovskyi.bidorama.mappers.UserResponseDtoMapper;
import com.slukovskyi.bidorama.models.User;
import com.slukovskyi.bidorama.models.enums.Role;
import com.slukovskyi.bidorama.repositories.UserRepository;
import com.slukovskyi.bidorama.util.ResourceDataReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
@DisplayName("User service tests")
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRequestDtoMapper userRequestDtoMapper;
    @Mock
    private UserResponseDtoMapper userResponseDtoMapper;

    @Nested
    @DisplayName("#register() tests")
    class registerTests {

        @Value("classpath:data/unit-tests/dto/user-request-dto.json")
        private Resource userRequestDtoData;

        @Value("classpath:data/unit-tests/entity/user-from-dto.json")
        private Resource userFromDtoData;

        @Value("classpath:data/unit-tests/dto/user-response-dto.json")
        private Resource userResponseDtoData;

        @Test
        @DisplayName("Should throw NullReferenceException if userRequestDto is null")
        void shouldThrowExceptionIfUserDtoIsNull() {
            //Given
            UserRequestDto userRequestDto = null;

            //When - Then
            assertThrows(NullReferenceException.class, () -> userService.register(userRequestDto));
        }

        @Test
        @DisplayName("Should throw AlreadyExistsException if user already exists")
        void shouldThrowExceptionIfUserAlreadyExists() {
            //Given
            UserRequestDto userRequestDto = ResourceDataReader.asObject(userRequestDtoData, UserRequestDto.class);

            when(userRepository.existsByUsername(userRequestDto.getUsername())).thenReturn(true);

            //When - Then
            assertThrows(AlreadyExistsException.class, () -> userService.register(userRequestDto));
        }

        @Test
        @DisplayName("Should return registered userResponseDto")
        void shouldReturnRegisteredUserDto() {
            //Given
            UserRequestDto userRequestDto = ResourceDataReader.asObject(userRequestDtoData, UserRequestDto.class);
            User userFromDto = ResourceDataReader.asObject(userFromDtoData, User.class);
            String hashedPassword = "hashedPassword";
            User userToSave = ResourceDataReader.asObject(userFromDtoData, User.class);
            userToSave.setPassword(hashedPassword);
            userToSave.setRole(Role.USER);
            userToSave.setBalance(0.0);
            UserResponseDto userResponseDto = ResourceDataReader.asObject(userResponseDtoData, UserResponseDto.class);

            when(userRequestDtoMapper.userRequestDtoToUser(userRequestDto)).thenReturn(userFromDto);
            when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn(hashedPassword);
            when(userRepository.save(userToSave)).thenReturn(userToSave);
            when(userResponseDtoMapper.userToUserResponseDto(userToSave)).thenReturn(userResponseDto);

            //When
            UserResponseDto registeredUser = userService.register(userRequestDto);

            //Then
            assertNotNull(registeredUser);
            verify(userRequestDtoMapper, times(1)).userRequestDtoToUser(userRequestDto);
            verify(passwordEncoder, times(1)).encode(userRequestDto.getPassword());
            verify(userRepository, times(1)).save(userToSave);
            verify(userResponseDtoMapper, times(1)).userToUserResponseDto(userToSave);
        }
    }

    @Nested
    @DisplayName("#login() tests")
    class loginTests {

        @Value("classpath:data/unit-tests/dto/user-request-dto.json")
        private Resource userRequestDtoData;

        @Value("classpath:data/unit-tests/entity/user-from-db.json")
        private Resource userFromDBData;

        @Value("classpath:data/unit-tests/dto/user-response-dto.json")
        private Resource userResponseDtoData;

        @Test
        @DisplayName("Should throw NullReferenceException if userRequestDto is null")
        void shouldThrowExceptionIfUserDtoIsNull() {
            //Given
            UserRequestDto userRequestDto = null;

            //When - Then
            assertThrows(NullReferenceException.class, () -> userService.login(userRequestDto));
        }

        @Test
        @DisplayName("Should return userResponseDto")
        void shouldReturnUserResponseDto() {
            //Given
            UserRequestDto userRequestDto = ResourceDataReader.asObject(userRequestDtoData, UserRequestDto.class);
            User userFromDb = ResourceDataReader.asObject(userFromDBData, User.class);
            UserResponseDto userResponseDto = ResourceDataReader.asObject(userResponseDtoData, UserResponseDto.class);
            Authentication authentication = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            String username = userRequestDto.getUsername();

            try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                        .thenReturn(authentication);
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(userDetails.getUsername()).thenReturn(username);
                when(userRepository.findByUsername(username)).thenReturn(Optional.of(userFromDb));
                when(userResponseDtoMapper.userToUserResponseDto(userFromDb)).thenReturn(userResponseDto);
                securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                //When
                UserResponseDto actualUser = userService.login(userRequestDto);

                //Then
                assertNotNull(actualUser);
                verify(authenticationManager, times(1))
                        .authenticate(any(UsernamePasswordAuthenticationToken.class));
                verify(SecurityContextHolder.getContext(), times(1))
                        .setAuthentication(authentication);
                verify(userRepository, times(1)).findByUsername(username);
                verify(userResponseDtoMapper, times(1)).userToUserResponseDto(userFromDb);
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException if user does not exist")
        void shouldThrowExceptionIfUserDoesNotExist() {
            //Given
            UserRequestDto userRequestDto = ResourceDataReader.asObject(userRequestDtoData, UserRequestDto.class);
            Authentication authentication = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            String username = userRequestDto.getUsername();

            try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                        .thenReturn(authentication);
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(userDetails.getUsername()).thenReturn(username);
                when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
                securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                //When - Then
                assertThrows(NotFoundException.class, () -> userService.login(userRequestDto));
                verify(authenticationManager, times(1))
                        .authenticate(any(UsernamePasswordAuthenticationToken.class));
                verify(SecurityContextHolder.getContext(), times(1))
                        .setAuthentication(authentication);
                verify(userRepository, times(1)).findByUsername(username);
            }
        }
    }

    @Nested
    @DisplayName("#getCurrentUser() tests")
    class getCurrentUserTests {

        @Value("classpath:data/unit-tests/entity/user-from-db.json")
        private Resource userFromDBData;

        @Test
        @DisplayName("Should throw NotFoundException if user does not exist")
        void shouldThrowExceptionIfUserDoesNotExist() {
            //Given
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
                securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.empty());

                //When - Then
                assertThrows(NotFoundException.class, () -> userService.getCurrentUser());
                verify(userRepository, times(1)).findByUsername(authentication.getName());
                verify(securityContext, times(1)).getAuthentication();
            }
        }

        @Test
        @DisplayName("Should return user")
        void shouldReturnUser() {
            //Given
            User userFromDb = ResourceDataReader.asObject(userFromDBData, User.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
                securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userFromDb));

                User actualUser = userService.getCurrentUser();

                //When - Then
                assertEquals(userFromDb, actualUser);
                verify(userRepository, times(1)).findByUsername(authentication.getName());
                verify(securityContext, times(1)).getAuthentication();
            }
        }
    }

    @Nested
    @DisplayName("#deposit() tests")
    class depositTests {

        @Value("classpath:data/unit-tests/entity/user-from-db.json")
        private Resource userFromDBData;

        @Value("classpath:data/unit-tests/dto/user-response-dto.json")
        private Resource userResponseDtoData;

        @Test
        @DisplayName("Should throw IllegalArgumentException if amount is negative number")
        void shouldThrowExceptionIfAmountIsNegative() {
            //Given
            Double amount = -10.0;

            //When - Then
            assertThrows(IllegalArgumentException.class, () -> userService.deposit(amount));
        }

        @Test
        @DisplayName("Should return userResponseDto")
        void shouldReturnUserResponseDto() {
            //Given
            User userFromDb = ResourceDataReader.asObject(userFromDBData, User.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            Double amount = 10.0;
            User expectedUser = ResourceDataReader.asObject(userFromDBData, User.class);
            expectedUser.setBalance(expectedUser.getBalance() + amount);
            UserResponseDto userResponseDto = ResourceDataReader.asObject(userResponseDtoData, UserResponseDto.class);
            userResponseDto.setBalance(expectedUser.getBalance());

            try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
                securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userFromDb));
                when(userRepository.save(expectedUser)).thenReturn(expectedUser);
                when(userResponseDtoMapper.userToUserResponseDto(expectedUser)).thenReturn(userResponseDto);

                //When
                UserResponseDto actualUser = userService.deposit(amount);

                //Then
                assertEquals(userResponseDto, actualUser);
                verify(userRepository, times(1)).save(expectedUser);
                verify(userResponseDtoMapper, times(1)).userToUserResponseDto(expectedUser);
            }
        }
    }

    @Nested
    @DisplayName("#withdraw() tests")
    class withdrawTests {
        @Value("classpath:data/unit-tests/entity/user-from-db.json")
        private Resource userFromDBData;

        @Value("classpath:data/unit-tests/dto/user-response-dto.json")
        private Resource userResponseDtoData;

        @Test
        @DisplayName("Should throw InsufficientFundsException if amount is bigger than balance")
        void shouldThrowExceptionIfAmountIsBiggerThanBalance() {
            //Given
            User userFromDb = ResourceDataReader.asObject(userFromDBData, User.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            Double amount = 10.0;

            try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
                securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userFromDb));

                //When - Then
                assertThrows(InsufficientFundsException.class, () -> userService.withdraw(amount));
            }
        }

        @Test
        @DisplayName("Should return userResponseDto")
        void shouldReturnUserResponseDto() {
            //Given
            User userFromDb = ResourceDataReader.asObject(userFromDBData, User.class);
            userFromDb.setBalance(20.0);
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            Double amount = 10.0;
            User expectedUser = ResourceDataReader.asObject(userFromDBData, User.class);
            expectedUser.setBalance(10.0);
            UserResponseDto userResponseDto = ResourceDataReader.asObject(userResponseDtoData, UserResponseDto.class);
            userResponseDto.setBalance(expectedUser.getBalance());

            try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
                securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userFromDb));
                when(userRepository.save(expectedUser)).thenReturn(expectedUser);
                when(userResponseDtoMapper.userToUserResponseDto(expectedUser)).thenReturn(userResponseDto);

                //When
                UserResponseDto actualUser = userService.withdraw(amount);

                //Then
                assertEquals(userResponseDto, actualUser);
                verify(userRepository, times(1)).save(expectedUser);
                verify(userResponseDtoMapper, times(1)).userToUserResponseDto(expectedUser);
            }
        }
    }
}
