package bankapp.member.dao;

import bankapp.member.model.Member;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Slf4j
@Component
@Validated
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

    public Member insertMember(@NotNull(message = "insertMember(Member) cannot be null") Member member){

        String sql = "INSERT INTO MEMBER (username , password , name) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();


            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, member.getUsername());
                ps.setString(2, member.getPassword());
                ps.setString(3, member.getName());
                return ps;
            }, keyHolder);


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
    public Optional<Member> findByUsername(String username) {
        String sql = "SELECT * FROM MEMBER WHERE username = ?";

        try {
            // username 으로 조회
            Member result = jdbcTemplate.queryForObject(sql, memberRowMapper(), username);
            // 조회된 결과 반환
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            // 조회된 결과가 없을 경우 , 비어있는 optional (정상 상황)
            return Optional.empty();
        }
    }

    // memberId 로 정보 조회
    public Optional<Member> findByMemberId(Long memberId) {
        String sql = "SELECT * FROM MEMBER WHERE member_id = ?";

        try {
            // username 으로 조회
            Member result = jdbcTemplate.queryForObject(sql, memberRowMapper(), memberId);
            // 조회된 결과 반환
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            // 조회된 결과가 없을 경우 , 비어있는 optional (정상 상황)
            return Optional.empty();
        }

    }






    /**
     * 데이터베이스 조회 결과를 Member 객체로 변환해주는 RowMapper
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
