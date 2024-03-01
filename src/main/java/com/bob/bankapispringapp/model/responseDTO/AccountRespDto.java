package com.bob.bankapispringapp.model.responseDTO;

import com.bob.bankapispringapp.entity.Branch;
import com.bob.bankapispringapp.entity.Client;

import lombok.Data;

@Data
public class AccountRespDto {
    private Integer id;

    private String accountNumber;

    private String currency;

    private Double amount;

    private String iban;

    private Integer clientId;

    private Integer branchId;

    private Integer status;
}
