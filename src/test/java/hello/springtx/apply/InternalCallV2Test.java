package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 트랜잭션 AOP 주의사항
 * 메서드 내부 호출 때문에 트랜잭션 프록시가 적용되지 않는 문제 해결
 * 즉, 내부 호출을 피하기 위해 트랜잭션이 적용된 메서드를 별도의 클래스로 분리한다
 */
@SpringBootTest
@Slf4j
public class InternalCallV2Test {

    @Autowired
    CallService callService; //트랜잭션 프록시 객체 주입

    @Test
    void externalCall() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1Config {
        @Bean
        CallService callService() {
            return new CallService(internalService());
        }

        @Bean
        InternalService internalService() {
            return new InternalService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService {

        private final InternalService internalService;

        //트랜잭션 적용X
        public void external() {
            log.info("call external");
            printTxInfo();
            internalService.internal(); //내부에서 트랜잭션이 적용된 메서드 실행 -> 트랜잭션 적용 X
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }

    @Slf4j
    static class InternalService {

        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }

}
