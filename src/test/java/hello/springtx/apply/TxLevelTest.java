package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @Transactional 의 적용 위치에 따른 우선순위
 * 우선 스프링은 메서드와 클래스에 에노테이션을 붙일 수 있다면, 더 구체적이고 자세한 메서드가 우선순위를 가진다
 *
 */
@SpringBootTest
public class TxLevelTest {

    @Autowired LevelService service;

    @Test
    void orderTest() {
        service.write();
        service.read();
    }

    @TestConfiguration
    static class TxLevelTestConfig {
        @Bean
        LevelService levelService() {
            return new LevelService();
        }
    }

    @Slf4j
    @Transactional(readOnly = true) //readOnly=false 는 default 옵션이므로 생략가능
    static class LevelService {

        @Transactional(readOnly = false) //클래스보다 메서드가 더 구체적이므로 해당 옵션이 적용된다
        public void write() {
            log.info("call write");
            printTxInfo();
        }

        //상위 클래스의 옵션을 따른다
        public void read() {
            log.info("call read");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("tx readOnly={}", readOnly);
        }
    }
}
