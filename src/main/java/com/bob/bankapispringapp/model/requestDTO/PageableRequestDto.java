package com.bob.bankapispringapp.model.requestDTO;

import lombok.Data;

@Data
public class PageableRequestDto {
    private int page;
    private int size;
    private String sortDirection;
}

