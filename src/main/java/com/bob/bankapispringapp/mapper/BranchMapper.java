package com.bob.bankapispringapp.mapper;

import com.bob.bankapispringapp.entity.Branch;
import com.bob.bankapispringapp.model.requestDTO.BranchReqDto;
import com.bob.bankapispringapp.model.responseDTO.BranchRespDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class BranchMapper {

    public abstract Branch mapToEntityForAdd(BranchReqDto branchReqDto);
    public abstract BranchRespDto mapToDto(Branch branch);
}
