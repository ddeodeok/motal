package com.molta.domain.playerInformation.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
    private String userId;
    private String name;
    private String nickname;
    private String password;
}
