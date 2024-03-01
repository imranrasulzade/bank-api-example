package com.bob.bankapispringapp.service;

import com.bob.bankapispringapp.model.requestDTO.BranchReqDto;
import com.bob.bankapispringapp.model.responseDTO.BranchRespDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BranchService {
    void add(BranchReqDto branchReqDto);

    List<BranchRespDto> get();

    BranchRespDto getById(Integer id);
}
