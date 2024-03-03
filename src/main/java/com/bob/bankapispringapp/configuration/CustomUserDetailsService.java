package com.bob.bankapispringapp.configuration;


import com.bob.bankapispringapp.entity.Authority;
import com.bob.bankapispringapp.entity.Client;
import com.bob.bankapispringapp.enums.UserStatus;
import com.bob.bankapispringapp.exception.IsNotActiveException;
import com.bob.bankapispringapp.repository.ClientRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;

    public CustomUserDetailsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client client = clientRepository.findByUsername(username).orElseThrow();
        checkUserProfileStatus(client);
        List<String> roles = new ArrayList<>();
        Set<Authority> authorities = client.getAuthorities();
        for (Authority authority : authorities) {
            roles.add(authority.getName());
        }
        UserDetails userDetails;
        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(client.getUsername())
                .password(client.getPassword())
                .roles(roles.toArray(new String[0]))
                .build();
        return userDetails;
    }

        private void checkUserProfileStatus(Client client) throws IsNotActiveException {
        if(client.getStatus() != UserStatus.ACTIVE.getValue()){
            throw new IsNotActiveException(client.getUsername() + " is not active");
        }
    }
}