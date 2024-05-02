package aspect;

import com.bob.bankapispringapp.entity.Client;
import com.bob.bankapispringapp.entity.ClientLog;
import com.bob.bankapispringapp.model.LoginReq;
import com.bob.bankapispringapp.repository.ClientLogRepository;
import com.bob.bankapispringapp.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Aspect
@Component
@Configurable
@RequiredArgsConstructor
public class ClientLoggingAspect {
    private final ClientLogRepository clientLogRepository;
    private final ClientRepository clientRepository;

    @SneakyThrows
    @AfterReturning("execution(* com.bob.bankapispringapp.service.impl.AuthServiceImpl.authenticate(..))")
    public void logSignIn(JoinPoint joinPoint) {
        log.info("dnvf");
        ClientLog clientLog = new ClientLog();
        LoginReq loginReq = (LoginReq) joinPoint.getArgs()[0];
        String username = loginReq.getUsername();

        Optional<Client> clientOptional = clientRepository.findByUsername(username);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            clientLog.setClient(client);
            clientLog.setLastLoginDate(LocalDate.now());
            clientLogRepository.save(clientLog);
            log.info("client login logged");
        }
    }
}
