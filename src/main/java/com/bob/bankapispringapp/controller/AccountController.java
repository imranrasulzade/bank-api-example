package com.bob.bankapispringapp.controller;

import com.bob.bankapispringapp.model.ChangePass;
import com.bob.bankapispringapp.model.requestDTO.AccountFilterDto;
import com.bob.bankapispringapp.model.requestDTO.AccountReqDto;
import com.bob.bankapispringapp.model.responseDTO.AccountRespDto;
import com.bob.bankapispringapp.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody AccountReqDto accountReqDto){
        accountService.add(accountReqDto);
    }

    @GetMapping
    public List<AccountRespDto> get(){
        return accountService.get();
    }

    @GetMapping("/{id}")
    public AccountRespDto getById(@PathVariable @Valid Integer id){
        return accountService.getById(id);
    }


    @PostMapping("/filter")
    public List<AccountRespDto> filterAccounts(@RequestBody AccountFilterDto accountFilterDto) {
        return accountService.filterAccounts(accountFilterDto);
    }


}
