package com.bob.bankapispringapp.model.requestDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BranchReqDto {

    @NotNull(message = "name can not be null")
    private String name;

    @NotNull(message = "location can not be null")
    private Integer locationId;

    @NotNull(message = "code can not be null")
    private String code;

    @NotNull(message = "status can not be null")
    private Integer status;
}
