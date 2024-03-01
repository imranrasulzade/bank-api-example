package com.bob.bankapispringapp.model.responseDTO;


import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientRespDto {
    private Integer id;

    private String username;

    private String name;

    private String surname;

    private String email;

    private String phone;

    private LocalDate birthdate;

    private Integer status;
}
