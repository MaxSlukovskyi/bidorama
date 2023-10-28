package com.slukovskyi.bidorama.dtos;

import lombok.Data;

@Data
public class UserRequestDto {
    private String username;
    private String password;
    private String name;
    private String surname;
}
