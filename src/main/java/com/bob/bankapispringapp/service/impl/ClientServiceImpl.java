package com.bob.bankapispringapp.service.impl;

import com.bob.bankapispringapp.entity.Authority;
import com.bob.bankapispringapp.entity.Client;
import com.bob.bankapispringapp.exception.AlreadyExistsException;
import com.bob.bankapispringapp.exception.EntityNotFoundException;
import com.bob.bankapispringapp.mapper.ClientMapper;
import com.bob.bankapispringapp.model.ChangePass;
import com.bob.bankapispringapp.model.requestDTO.ClientReqDto;
import com.bob.bankapispringapp.model.responseDTO.ClientRespDto;
import com.bob.bankapispringapp.repository.ClientRepository;
import com.bob.bankapispringapp.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void add(ClientReqDto clientReqDto) {
        log.info("client add method started by -> {}", clientReqDto.getUsername());
        if(clientRepository.findByUsername(clientReqDto.getUsername()).isPresent()){
            throw new AlreadyExistsException("Client already exists!");
        }
        Client client = clientMapper.toEntityForAdd(clientReqDto);
        client.setPassword(passwordEncoder.encode(clientReqDto.getPassword()));
        Authority roles = new Authority(clientReqDto.getAuthorityName().name());
        Set<Authority> authorities = new HashSet<>();
        authorities.add(roles);
        client.setAuthorities(authorities);
        clientRepository.save(client);
        log.info("{} -> client registered", client.getUsername());
    }

    @Override
    public List<ClientRespDto> get() {
        String username = "imran";
        log.info("get method started by: {}", username);
        List<ClientRespDto> clientRespDtos = clientRepository.findAll().stream()
                .map(clientMapper::toDto).toList();
        log.error("error");
        log.info("get method done");
        return clientRespDtos;
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

    @Override
    public void changePassword(ChangePass changePass, Integer userId) {
        Optional<Client> clientOptional = clientRepository.findById(userId);
        if(clientOptional.isPresent()){
            Client client = clientOptional.get();
            if(passwordEncoder.matches(changePass.getOldPass(), client.getPassword())
            && changePass.getNewPass().equals(changePass.getReNewPass())){
               client.setPassword(passwordEncoder.encode(changePass.getNewPass()));
               clientRepository.save(client);
            }
        }
    }




}
