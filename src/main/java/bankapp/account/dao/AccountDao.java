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
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class AccountDao {

    private final JdbcTemplate jdbcTemplate;
    private final AccountFactory accountFactory;

    public AccountDao(DataSource dataSource, AccountFactory accountFactory) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.accountFactory = accountFactory;
    }


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

        long generatedId = keyHolder.getKey().longValue();
        account.setAccountId(generatedId);
        return account;
    }

    /**
     * 계좌 번호로 계좌를 조회합니다.
     * 계좌 번호는 시스템에서 고유한 값이라고 가정합니다.
     *
     * @param accountNumber 조회할 계좌 번호
     * @return 계좌가 존재하면 해당 계좌를 담은 Optional, 그렇지 않으면 비어있는 Optional을 반환합니다.
     */
    public Optional<Account> findByAccountNumber(String accountNumber) {


        // 이전 대화에서 사용한 'ACCOUNT' 테이블 이름을 사용합니다.
        String sql = "SELECT * FROM ACCOUNT WHERE account_number = ?";
        try {
            // queryForObject는 결과가 정확히 하나일 것으로 예상될 때 사용합니다.
            // 결과가 없으면 EmptyResultDataAccessException을, 2개 이상이면 IncorrectResultSizeDataAccessException을 던집니다.
            Account account = jdbcTemplate.queryForObject(sql, accountRowMapper(), accountNumber);

            // 조회된 account 객체를 Optional로 감싸서 반환합니다.
            return Optional.ofNullable(account);

        } catch (EmptyResultDataAccessException e) {
            // 조회 결과가 없을 때 발생하는 예외입니다.
            // 이는 오류가 아니라 정상적인 경우일 수 있으므로, 비어있는 Optional을 반환합니다.
            return Optional.empty();
        }
    }

    public List<Account> findByMemberId(Long memberId) {
        String sql = "SELECT * FROM ACCOUNT WHERE member_id = ?";
        // query() 메서드는 여러 개의 row를 결과로 받을 때 사용합니다.
        // 결과가 없을 경우 예외를 발생시키지 않고 비어있는 List를 반환합니다.
        return jdbcTemplate.query(sql, accountRowMapper(), memberId);
    }




    // 데이터베이스 조회 결과를 Account 객체로 변환해주는 RowMapper
    private RowMapper<Account> accountRowMapper() {
        return (rs, rowNum) -> {
            String accountType = rs.getString("account_type");

            // 1. Factory를 통해 타입에 맞는 객체를 생성합니다.
            Account account = accountFactory.createAccount(accountType);

            // 2. 공통 필드를 설정합니다.
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
