package com.at.taskmanagement.dtos;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
}