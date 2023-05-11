package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;

/**
 * 트랜잭션 AOP 주의사항 - 초기화 시점
 * 초기화 코드(@PostConstruct) 와 @Transactional 을 함께 사용하면 트랜잭션이 적용되지 않는다
 * 초기화 코드가 먼저 호출되고, 그 다음에 트랜잭션 AOP 가 적용되기 때문에 초기화 시점에는 해당 메서드에서 트랜잭션을 획득할 수 없다
 *
 * 가장 확실한 대안은 ApplicationReadyEvent 를 사용하는것
 */
@SpringBootTest
public class InitTxTest {

    @Autowired
    Hello hello;

    @Test
    void go() {
        //초기화 코드는 스프링이 초기화 시점에 호출한다.
    }

    @TestConfiguration
    static class InitTxTestConfig {
        @Bean
        Hello hello() {
            return new Hello();
        }
    }
    
    @Slf4j
    static class Hello {

        @PostConstruct
        @Transactional
        public void initV1() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active={}", isActive);
        }
        
        //이 이벤트는 트랜잭션 AOP 를 포함한 스프링 컨테이너가 완전히 생성되고 난 다음에 해당 메서드를 호출해준다
        @EventListener(value = ApplicationReadyEvent.class)
        @Transactional
        public void init2() {
            boolean isActive =
                    TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init ApplicationReadyEvent tx active={}",
                    isActive);
        }
    }
}
