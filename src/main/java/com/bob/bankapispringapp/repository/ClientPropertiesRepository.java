package com.bob.bankapispringapp.repository;

import com.bob.bankapispringapp.entity.ClientProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientPropertiesRepository extends JpaRepository<ClientProperties, Integer> {

    List<ClientProperties> findClientPropertiesByClient_Id(Integer id);


}
