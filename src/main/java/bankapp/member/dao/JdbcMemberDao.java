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
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Slf4j
@Repository
@Validated
/**
 * {@inheritDoc}
 * <p>
 * 이 구현체는 Spring JDBC의 JdbcTemplate을 사용하여
 * MariaDB 데이터베이스에 직접 SQL을 실행하는 방식으로 Member 데이터 접근 로직을 수행합니다.
 */
public class JdbcMemberDao implements MemberDao{

    final private JdbcTemplate jdbcTemplate;
    public JdbcMemberDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
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


        // TODO : 검토 (NPE)
        long generatedId = keyHolder.getKey().longValue();
        member.setMemberId(generatedId);
        return member;
    }

    @Override
    public Optional<Member> findByUsername(String username) {
        String sql = "SELECT * FROM MEMBER WHERE username = ?";

        try {
            Member result = jdbcTemplate.queryForObject(sql, memberRowMapper(), username);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Member> findByMemberId(Long memberId) {
        String sql = "SELECT * FROM MEMBER WHERE member_id = ?";

        try {
            Member result = jdbcTemplate.queryForObject(sql, memberRowMapper(), memberId);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
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




