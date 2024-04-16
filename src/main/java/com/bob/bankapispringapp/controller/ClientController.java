package com.bob.bankapispringapp.controller;

import com.bob.bankapispringapp.model.ChangePass;
import com.bob.bankapispringapp.model.requestDTO.ClientReqDto;
import com.bob.bankapispringapp.model.responseDTO.ClientRespDto;
import com.bob.bankapispringapp.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody @Valid ClientReqDto clientReqDto){
        clientService.add(clientReqDto);
    }

    @GetMapping("/all")
    public List<ClientRespDto> get(){
        return clientService.get();
    }

    @GetMapping("/{id}")
    public ClientRespDto get(@PathVariable @Valid Integer id){
        return clientService.getById(id);
    }

//    @GetMapping("/pageable")
//    public Page<ClientRespDto> getAllClients(Pageable pageable) {
//        return clientService.getAllClients(pageable);
//    }

    @GetMapping("/pageable")
    public Page<ClientRespDto> getAllClients(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "asc") String sortDirection) {
        return clientService.getAllClients(page, size, sortDirection);
    }


    @PatchMapping("change-password/{userId}")
    public void changePassword(@RequestBody ChangePass changePass, @PathVariable Integer userId){
        clientService.changePassword(changePass, userId);
    }


}
