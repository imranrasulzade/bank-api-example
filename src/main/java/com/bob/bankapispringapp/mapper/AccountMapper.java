package com.bob.bankapispringapp.mapper;

import com.bob.bankapispringapp.entity.Account;
import com.bob.bankapispringapp.model.requestDTO.AccountReqDto;
import com.bob.bankapispringapp.model.responseDTO.AccountRespDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class AccountMapper {

    @Mapping(source = "branchId", target = "branch.id")
    @Mapping(source = "clientId", target = "client.id")
    public abstract Account toEntityForAdd(AccountReqDto accountReqDto);

    @Mapping(source = "branch.id", target = "branchId")
    @Mapping(source = "client.id", target = "clientId")
    public abstract AccountRespDto toDto(Account account);
}
