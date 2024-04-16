package com.bob.bankapispringapp.model;

import lombok.Data;

@Data
public class ChangePass {
    private String oldPass;
    private String newPass;
    private String reNewPass;
}
