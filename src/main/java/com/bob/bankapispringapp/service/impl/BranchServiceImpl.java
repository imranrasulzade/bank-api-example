package com.bob.bankapispringapp.service.impl;

import com.bob.bankapispringapp.exception.EntityNotFoundException;
import com.bob.bankapispringapp.mapper.BranchMapper;
import com.bob.bankapispringapp.model.requestDTO.BranchReqDto;
import com.bob.bankapispringapp.model.responseDTO.BranchRespDto;
import com.bob.bankapispringapp.repository.BranchRepository;
import com.bob.bankapispringapp.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;


    @Override
    public void add(BranchReqDto branchReqDto) {
        branchRepository.save(branchMapper.mapToEntityForAdd(branchReqDto));
    }

    @Override
    public List<BranchRespDto> get(){
        return branchRepository.findAll().stream()
                .map(branchMapper::mapToDto).toList();
    }

    @Override
    public BranchRespDto getById(Integer id) {
        return branchRepository.findById(id).map(branchMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("BRANCH_NOT_FOUND"));
    }
}
