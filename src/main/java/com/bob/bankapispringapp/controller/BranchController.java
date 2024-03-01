package com.bob.bankapispringapp.controller;

import com.bob.bankapispringapp.model.requestDTO.BranchReqDto;
import com.bob.bankapispringapp.model.responseDTO.BranchRespDto;
import com.bob.bankapispringapp.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("branch")
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody @Valid BranchReqDto branchReqDto){
        branchService.add(branchReqDto);
    }

    @GetMapping
    public List<BranchRespDto> get(){
        return branchService.get();
    }

    @GetMapping("/{id}")
    public BranchRespDto getById(@PathVariable @Valid Integer id){
        return branchService.getById(id);
    }
}
