package bankapp.member.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    // todo : Member 를 비롯한 모든 엔티티 날짜 어떻게 할지 고민하기
    // 애플리케이션에서 위임 ?
    // 데이터베이스가 자동으로 설정하도록 ?
    @Column(insertable = false, updatable = false , nullable = false)
    private LocalDateTime createdAt;

    protected Member() { }

    public Member(String username, String password, String name) {

        if(username == null || password == null || name == null || username.isEmpty() || password.isEmpty() || name.isEmpty()){
            throw new IllegalArgumentException("null error");
        }

        this.username = username;
        this.password = password;
        this.name = name;
    }

}
