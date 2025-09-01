package bankapp.account.dao.ledger;

import bankapp.account.model.ledger.AccountLedger;
import bankapp.account.model.ledger.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcAccountLedgerDao implements AccountLedgerDao {

    private final JdbcTemplate jdbcTemplate;
    public JdbcAccountLedgerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AccountLedger save(AccountLedger accountLedger) {
        String sql = "INSERT INTO ACCOUNT_LEDGER (account_id, transaction_type, amount, balance_after, description) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, accountLedger.getAccountId());
            ps.setString(2, accountLedger.getTransactionType().name());
            ps.setBigDecimal(3, accountLedger.getAmount());
            ps.setBigDecimal(4, accountLedger.getBalanceAfter());
            ps.setString(5, accountLedger.getDescription());
            return ps;
        }, keyHolder);

        long generatedId = keyHolder.getKey().longValue();

        return findById(generatedId)
                .orElseThrow(() -> new DataIntegrityViolationException("원장 기록 저장 후 데이터를 조회할 수 없습니다. ID: " + generatedId));
    }

    /**
     * ledger_id를 사용하여 단일 원장 기록을 조회합니다.
     * @param ledgerId 조회할 원장 기록의 ID
     * @return 조회된 AccountLedger 객체를 담은 Optional
     */
    public Optional<AccountLedger> findById(Long ledgerId) {
        String sql = "SELECT * FROM ACCOUNT_LEDGER WHERE ledger_id = ?";
        try {
            AccountLedger accountLedger = jdbcTemplate.queryForObject(sql, accountLedgerRowMapper(), ledgerId);
            return Optional.ofNullable(accountLedger);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 데이터베이스 조회 결과(ResultSet)를 AccountLedger 객체로 변환해주는 RowMapper
     */
    private RowMapper<AccountLedger> accountLedgerRowMapper() {
        return (rs, rowNum) -> {
            AccountLedger accountLedger = new AccountLedger();
            accountLedger.setLedgerId(rs.getLong("ledger_id"));
            accountLedger.setAccountId(rs.getLong("account_id"));
            // DB의 VARCHAR를 Java의 Enum 타입으로 변환
            accountLedger.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
            accountLedger.setAmount(rs.getBigDecimal("amount"));
            accountLedger.setBalanceAfter(rs.getBigDecimal("balance_after"));
            accountLedger.setDescription(rs.getString("description"));
            accountLedger.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return accountLedger;
        };
    }




}
