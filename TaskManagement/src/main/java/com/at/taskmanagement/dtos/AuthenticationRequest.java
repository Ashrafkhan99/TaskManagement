package com.at.taskmanagement.dtos;

import lombok.*;

@Getter @Setter
public class AuthenticationRequest {
    private String username;
    private String password;
}
