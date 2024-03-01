package com.bob.bankapispringapp.service;

import com.bob.bankapispringapp.entity.Account;
import com.bob.bankapispringapp.model.requestDTO.AccountFilterDto;
import com.bob.bankapispringapp.model.requestDTO.AccountReqDto;
import com.bob.bankapispringapp.model.responseDTO.AccountRespDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountService {

    void add(AccountReqDto accountReqDto);
    List<AccountRespDto> get();

    AccountRespDto getById(Integer id);
    List<AccountRespDto> filterAccounts(AccountFilterDto accountFilterDto);

}
