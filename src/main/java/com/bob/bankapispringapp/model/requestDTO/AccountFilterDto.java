package com.bob.bankapispringapp.model.requestDTO;

import com.bob.bankapispringapp.enums.Currency;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountFilterDto {
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private Currency currency;
    private Integer clientId;
    private Integer branchId;

}
