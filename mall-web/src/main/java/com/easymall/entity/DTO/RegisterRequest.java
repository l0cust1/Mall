package com.easymall.entity.DTO;


import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String phone;
    private String Code;

}