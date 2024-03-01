package com.bob.bankapispringapp.model.responseDTO;

import lombok.Data;

@Data
public class BranchRespDto {
    private Integer id;

    private String name;

    private Integer locationId;

    private String code;

    private Integer status;
}
