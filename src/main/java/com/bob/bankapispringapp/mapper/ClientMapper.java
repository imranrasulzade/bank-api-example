package com.bob.bankapispringapp.mapper;

import com.bob.bankapispringapp.entity.Client;
import com.bob.bankapispringapp.model.ClientForExcel;
import com.bob.bankapispringapp.model.requestDTO.ClientReqDto;
import com.bob.bankapispringapp.model.responseDTO.ClientRespDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ClientMapper {

    public abstract Client toEntityForAdd(ClientReqDto clientReqDto);


    @Mapping(source = "birthdate", target = "birthdate", qualifiedByName = "mapBodStringToLocalDate")
    public abstract Client toEntityFromExcel(ClientForExcel clientForExcel);


    public abstract ClientRespDto toDto(Client client);


    @Mapping(source = "birthdate", target = "birthdate", qualifiedByName = "mapBirthdate")
    public abstract ClientForExcel toExcelModel(Client client);




    @Named("mapBirthdate")
    public Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Named("mapBodStringToLocalDate")
    public LocalDate localDateToDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
