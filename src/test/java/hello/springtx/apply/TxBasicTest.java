package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.*;

/**
 * 선언적 트랜잭션 방식(@Transactional) 에서 스프링 트랜잭션은 AOP 를 기반으로 동작한다
 * @Transactional 이 특정 클래스나 메서드에 하나라도 있으면 트랜잭션 AOP 적용의 대상이 되고,
 * 트랜잭션 AOP 는 프록시를 만들어 스프링 컨테이너에 등록한다
 * 그리고 실제 객체 대신에 트랜잭션을 처리해주는 프록시 객체를 스프링 빈에 등록한다
 *
 */
@Slf4j
@SpringBootTest
public class TxBasicTest {

    //스프링 컨테이너는 실제 객체 대신에 스프링 빈으로 등록되어 있는 프록시를 의존관계 주입한다
    //프록시는 BasicService 를 상속해서 만들어지기 때문에 다형성 적용. 따라서 BasicService 대신에 자식인 프록시를 주입할 수 있다
    @Autowired BasicService basicService;

    @Test
    void proxyCheck() {
        log.info("AOP class={}", basicService.getClass());
        assertThat(AopUtils.isAopProxy(basicService)).isTrue(); //프록시 체크
    }

    @Test
    void txTest() {
        basicService.tx();
        basicService.nonTx();
    }

    @TestConfiguration
    static class TxBasicTestConfig {

        @Bean
        BasicService basicService() {
            return new BasicService();
        }
    }

    @Slf4j
    static class BasicService {

        @Transactional
        public void tx() {
            log.info("call tx");
            //현재 쓰레드에서 트랜잭션이 적용되어 있는지 확인
            //true 이면 트랜잭션 적용
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }

        public void nonTx() {
            log.info("call nonTx");
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }
}
