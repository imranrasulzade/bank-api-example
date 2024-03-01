package com.bob.bankapispringapp.model.requestDTO;

import com.bob.bankapispringapp.enums.AuthorityName;
import com.bob.bankapispringapp.validation.ValidBirthdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientReqDto {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String phone;

    @ValidBirthdate
    private LocalDate birthdate;

    @NotNull
    private AuthorityName authorityName;

    @NotNull
    private Integer status;
}
