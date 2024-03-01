package com.bob.bankapispringapp.service.impl;

import com.bob.bankapispringapp.entity.Account;
import com.bob.bankapispringapp.entity.Branch;
import com.bob.bankapispringapp.entity.Client;
import com.bob.bankapispringapp.exception.EntityNotFoundException;
import com.bob.bankapispringapp.mapper.AccountMapper;
import com.bob.bankapispringapp.model.requestDTO.AccountFilterDto;
import com.bob.bankapispringapp.model.requestDTO.AccountReqDto;
import com.bob.bankapispringapp.model.responseDTO.AccountRespDto;
import com.bob.bankapispringapp.repository.AccountRepository;
import com.bob.bankapispringapp.service.AccountService;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public void add(AccountReqDto accountReqDto) {
        accountRepository.save(accountMapper.toEntityForAdd(accountReqDto));
    }

    @Override
    public List<AccountRespDto> get(){
        return accountRepository.findAll().stream()
                .map(accountMapper::toDto).toList();
    }

    @Override
    public AccountRespDto getById(Integer id) {
        return accountRepository.findById(id).map(accountMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("ACCOUNT_NOT_FOUND"));
    }

    public List<AccountRespDto> filterAccounts(AccountFilterDto accountFilterDto) {
        Specification<Account> accountSpecification = getAccountSpecification(accountFilterDto);
        return accountRepository.findAll(accountSpecification).stream().map(accountMapper::toDto).toList();
    }


    private Specification<Account> getAccountSpecification(AccountFilterDto accountFilterDto){
        accountFilterDto.setAccountNumber("%" + accountFilterDto.getAccountNumber() + "%");

        Specification<Account> accountNumberSpecification = ((root, query, criteriaBuilder) ->
                accountFilterDto.getAccountNumber() == null || accountFilterDto.getAccountNumber().isBlank() ?
                        null : criteriaBuilder
                        .like(criteriaBuilder.upper(root.get("accountNumber")), accountFilterDto.getAccountNumber().toUpperCase()));

        Specification<Account> currencySpecification = ((root, query, criteriaBuilder) ->
                accountFilterDto.getCurrency() == null ?
                        null : criteriaBuilder.equal(criteriaBuilder.upper(root.get("currency")), accountFilterDto.getCurrency().name()));

        Specification<Account> clientSpecification = ((root, query, criteriaBuilder) -> {
            if (accountFilterDto.getClientId() == null) {
                return null;
            } else {
                Join<Account, Client> clientJoin = root.join("client");
                return criteriaBuilder.equal(clientJoin.get("id"), accountFilterDto.getClientId());
            }
        });

        Specification<Account> branchSpecification = ((root, query, criteriaBuilder) -> {
            if (accountFilterDto.getBranchId() == null) {
                return null;
            } else {
                Join<Account, Branch> branchJoin = root.join("branch");
                return criteriaBuilder.equal(branchJoin.get("id"), accountFilterDto.getBranchId());
            }
        });

        return Specification.where(accountNumberSpecification)
                .or(currencySpecification)
                .or(clientSpecification)
                .or(branchSpecification);
    }

}
