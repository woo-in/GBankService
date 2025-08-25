package bankapp.account.dao;

import bankapp.account.factory.AccountFactory;
import bankapp.account.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
/**
 * {@inheritDoc}
 * <p>
 * 이 구현체는 Spring JDBC의 JdbcTemplate을 사용하여
 * MariaDB 데이터베이스에 직접 SQL을 실행하는 방식으로 Account 데이터 접근 로직을 수행합니다.
 */
public class JdbcAccountDao implements AccountDao{

    private final JdbcTemplate jdbcTemplate;
    private final AccountFactory accountFactory;
    public JdbcAccountDao(DataSource dataSource, AccountFactory accountFactory) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.accountFactory = accountFactory;
    }

    @Override
    public <T extends Account> T insertAccount(T account) {
        String sql = "INSERT INTO ACCOUNT (member_id , account_number , balance , account_type , nickname) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, account.getMemberId());
            ps.setString(2, account.getAccountNumber());
            ps.setBigDecimal(3, account.getBalance());
            ps.setString(4, account.getAccountType());
            ps.setString(5, account.getNickname());
            return ps;
        }, keyHolder);

        // TODO: (NPE 고민)
        long generatedId = keyHolder.getKey().longValue();
        account.setAccountId(generatedId);
        return account;
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {


        String sql = "SELECT * FROM ACCOUNT WHERE account_number = ?";
        try {
            Account account = jdbcTemplate.queryForObject(sql, accountRowMapper(), accountNumber);
            return Optional.ofNullable(account);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Account> findByMemberId(Long memberId) {
        String sql = "SELECT * FROM ACCOUNT WHERE member_id = ?";
        return jdbcTemplate.query(sql, accountRowMapper(), memberId);
    }




    /**
     * 데이터베이스 조회 결과(ResultSet)를 Account 객체로 변환해주는 RowMapper.
     * AccountFactory를 사용하여 account_type에 맞는 구체적인 Account 자식 클래스(예: PrimaryAccount)를 생성합니다.
     */
    private RowMapper<Account> accountRowMapper() {
        return (rs, rowNum) -> {
            String accountType = rs.getString("account_type");

            Account account = accountFactory.createAccount(accountType);

            account.setAccountId(rs.getLong("account_id"));
            account.setMemberId(rs.getLong("member_id"));
            account.setAccountNumber(rs.getString("account_number"));
            account.setBalance(rs.getBigDecimal("balance"));
            account.setAccountType(accountType);
            account.setNickname(rs.getString("nickname"));
            account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            return account;
        };
    }

}
