package bankapp.member.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Member {

    private Long memberId;
    private String username;
    private String password;
    private String name;
    private LocalDateTime createdAt;

}
