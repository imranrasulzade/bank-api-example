package com.bob.bankapispringapp.repository;

import com.bob.bankapispringapp.entity.ClientProperties;
import com.bob.bankapispringapp.entity.CustomerProps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerPropsRepository extends JpaRepository<CustomerProps, Integer> {

    List<CustomerProps> findByCustomerId(Integer id);
}
