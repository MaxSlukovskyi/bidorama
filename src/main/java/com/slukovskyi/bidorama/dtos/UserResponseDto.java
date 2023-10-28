package com.slukovskyi.bidorama.dtos;

import com.slukovskyi.bidorama.models.enums.Role;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private Role role;
    private Double balance;
}
