package com.bob.bankapispringapp.repository;

import com.bob.bankapispringapp.entity.ClientLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientLogRepository extends JpaRepository<ClientLog, Integer> {
}
