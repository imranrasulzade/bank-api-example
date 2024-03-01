package com.bob.bankapispringapp.service.impl;

import com.bob.bankapispringapp.entity.Authority;
import com.bob.bankapispringapp.entity.Client;
import com.bob.bankapispringapp.enums.AuthorityName;
import com.bob.bankapispringapp.exception.EntityNotFoundException;
import com.bob.bankapispringapp.mapper.ClientMapper;
import com.bob.bankapispringapp.model.requestDTO.ClientReqDto;
import com.bob.bankapispringapp.model.requestDTO.PageableRequestDto;
import com.bob.bankapispringapp.model.responseDTO.ClientRespDto;
import com.bob.bankapispringapp.repository.ClientRepository;
import com.bob.bankapispringapp.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public void add(ClientReqDto clientReqDto) {
        log.info("client add method started by -> {}", clientReqDto.getUsername());
        Client client = clientMapper.toEntityForAdd(clientReqDto);
        Authority roles = new Authority(clientReqDto.getAuthorityName().name());
        Set<Authority> authorities = new HashSet<>();
        authorities.add(roles);
        client.setAuthorities(authorities);
        clientRepository.save(client);
        log.info("{} -> client registered", client.getUsername());
    }

    @Override
    public List<ClientRespDto> get() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto).toList();
    }

    @Override
    public ClientRespDto getById(Integer id) {
        return clientRepository.findById(id)
                .map(clientMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("CLIENT_NOT_FOUND"));
    }

//    @Override
//    public Page<ClientRespDto> getAllClients(Pageable pageable) {
//        return clientRepository.findAll(pageable).map(clientMapper::toDto);
//    }

    @Override
    public Page<ClientRespDto> getAllClients(int page, int size, String sort) {
        PageRequest pageRequest = PageRequest.of(page-1,
                size,
                Sort.Direction.fromString(sort),
                "id");
        return clientRepository.findAll(pageRequest).map(clientMapper::toDto);
    }




}
