package com.bob.bankapispringapp.model.requestDTO;

import com.bob.bankapispringapp.enums.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountReqDto {

    @NotNull(message = " Account number can not be null")
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @NotNull(message = " Currency can not be null")
    private Currency currency;

    @NotNull(message = "amount can not be null")
    private Double amount;

    @NotNull(message = "iban can not be null")
    private String iban;

    @NotNull(message = " Client can not be null")
    private Integer clientId;

    @NotNull(message = " Branch can not be null")
    private Integer branchId;

    @NotNull(message = "Status can not be null")
    private Integer status;
}
