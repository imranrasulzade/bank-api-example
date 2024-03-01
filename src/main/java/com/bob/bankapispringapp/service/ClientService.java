package com.bob.bankapispringapp.service;

import com.bob.bankapispringapp.model.requestDTO.ClientReqDto;
import com.bob.bankapispringapp.model.responseDTO.ClientRespDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface ClientService {
    void add(ClientReqDto clientReqDto);

    List<ClientRespDto> get();

    ClientRespDto getById(Integer id);
//    Page<ClientRespDto> getAllClients(Pageable pageable);

    Page<ClientRespDto> getAllClients(int page, int size, String sort);
}
