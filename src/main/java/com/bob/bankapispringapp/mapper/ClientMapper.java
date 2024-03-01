package com.bob.bankapispringapp.mapper;

import com.bob.bankapispringapp.entity.Client;
import com.bob.bankapispringapp.model.requestDTO.ClientReqDto;
import com.bob.bankapispringapp.model.responseDTO.ClientRespDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ClientMapper {


    public abstract Client toEntityForAdd(ClientReqDto clientReqDto);
    public abstract ClientRespDto toDto(Client client);
}
