package bankapp.dao;

import bankapp.model.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Component
public class MemberDao {

    final private JdbcTemplate jdbcTemplate;

    public MemberDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    /*
     * 회원 정보를 DB에 저장하고, 생성된 ID를 포함한 Member 객체를 반환합니다.
     * @param member 저장할 회원 정보 (id는 null 상태)
     * @return 생성된 id가 포함된 회원 정보
     */

    public Member save(Member member){
        String sql = "INSERT INTO MEMBER (username , password , name) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1 , member.getUsername());
            ps.setString(2 , member.getPassword());
            ps.setString(3 , member.getName());
            return ps;
        } , keyHolder);

        long generatedId = keyHolder.getKey().longValue();
        member.setMemberId(generatedId);
        return member;
    }

    /*
     * username으로 회원을 조회하여 Member 객체로 반환합니다.
     * 회원이 존재하지 않으면 null을 반환합니다.
     * @param username 조회할 사용자 아이디
     * @return Member 객체 또는 null
     */
    public Member findByUsername(String username) {
        String sql = "SELECT * FROM MEMBER WHERE username = ?";
        // 결과를 List<Member> 로 받습니다.
        List<Member> result = jdbcTemplate.query(sql, memberRowMapper(), username);
        // 리스트가 비어있으면 null, 아니면 첫 번째 요소를 반환합니다.
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * 데이터베이스 조회 결과를 Member 객체로 변환해주는 RowMapper입니다.
     */
    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getLong("member_id"));
            member.setUsername(rs.getString("username"));
            member.setPassword(rs.getString("password"));
            member.setName(rs.getString("name"));
            member.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return member;
        };
    }
}
