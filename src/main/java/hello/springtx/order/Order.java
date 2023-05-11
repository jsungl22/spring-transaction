package hello.springtx.order;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JPA 를 사용하는 Order 엔티티
 * @Table(name="orders") 라고 테이블 이름을 따로 지정해줬는데, 따로 테이블 이름을 지정하지 않으면 클래스 이름인 order 가 된다
 * 하지만 order 는 데이터베이스 예약어여서 사용할 수 없다
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue
    private Long Id;

    private String username; //정상, 예외, 잔고부족
    private String payStatus; //대기, 완료
}
