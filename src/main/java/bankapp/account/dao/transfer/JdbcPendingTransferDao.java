package bankapp.account.dao.transfer;
import bankapp.account.model.transfer.PendingTransfer;
import bankapp.account.model.transfer.TransferStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * {@inheritDoc}
 * <p>
 * 이 구현체는 Spring JDBC의 JdbcTemplate을 사용하여
 * MariaDB 데이터베이스에 직접 SQL을 실행하는 방식으로 데이터 접근 로직을 수행합니다.
 */
@Slf4j
@Repository
public class JdbcPendingTransferDao implements PendingTransferDao{

    private final JdbcTemplate jdbcTemplate;
    public JdbcPendingTransferDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public PendingTransfer init(PendingTransfer pendingTransfer){
        String sql = "INSERT INTO PENDING_TRANSFER (" +
                "request_id, sender_member_id, sender_account_id, status, " +
                "receiver_bank_name, receiver_account_number, receiver_name, " +
                "amount, message, sender_ledger_id, receiver_ledger_id, " +
                "expires_at, created_at, updated_at" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // UUID 를 사용해 고유한 requestId 생성 및 설정
        String requestId = java.util.UUID.randomUUID().toString();
        pendingTransfer.setRequestId(requestId);

        // 생성 시점의 created_at, updated_at 설정
        pendingTransfer.setCreatedAt(java.time.LocalDateTime.now());
        pendingTransfer.setUpdatedAt(java.time.LocalDateTime.now());

        jdbcTemplate.update(sql,
                pendingTransfer.getRequestId(),
                pendingTransfer.getSenderMemberId(),
                pendingTransfer.getSenderAccountId(),
                pendingTransfer.getStatus().name(),
                pendingTransfer.getReceiverBankName(),
                pendingTransfer.getReceiverAccountNumber(),
                pendingTransfer.getReceiverName(),
                pendingTransfer.getAmount(),
                pendingTransfer.getMessage(),
                pendingTransfer.getSenderLedgerId(),
                pendingTransfer.getReceiverLedgerId(),
                pendingTransfer.getExpiresAt(),
                pendingTransfer.getCreatedAt(),
                pendingTransfer.getUpdatedAt()
        );

        return pendingTransfer;
    }

    @Override
    public Optional<PendingTransfer> findById(String requestId){
        String sql = "SELECT * FROM PENDING_TRANSFER WHERE request_id = ?";
        try {
            PendingTransfer pendingTransfer = jdbcTemplate.queryForObject(sql, pendingTransferRowMapper(), requestId);

            return Optional.ofNullable(pendingTransfer);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public PendingTransfer update(PendingTransfer pendingTransfer){
        String sql = "UPDATE PENDING_TRANSFER SET " +
                "status = ?, " +
                "receiver_bank_name = ?, " +
                "receiver_account_number = ?, " +
                "receiver_name = ?, " +
                "amount = ?, " +
                "message = ?, " +
                "sender_ledger_id = ?, " +
                "receiver_ledger_id = ?, " +
                "updated_at = ? " +
                "WHERE request_id = ?";

        pendingTransfer.setUpdatedAt(java.time.LocalDateTime.now());


        jdbcTemplate.update(sql,
                pendingTransfer.getStatus().name(),
                pendingTransfer.getReceiverBankName(),
                pendingTransfer.getReceiverAccountNumber(),
                pendingTransfer.getReceiverName(),
                pendingTransfer.getAmount(),
                pendingTransfer.getMessage(),
                pendingTransfer.getSenderLedgerId(),
                pendingTransfer.getReceiverLedgerId(),
                pendingTransfer.getUpdatedAt(),
                pendingTransfer.getRequestId()
        );
        return pendingTransfer;
    }


    /**
     * 데이터베이스 조회 결과(ResultSet)를 PendingTransfer 객체로 변환해주는 RowMapper.
     */
    private RowMapper<PendingTransfer> pendingTransferRowMapper() {
        return (rs, rowNum) -> {
                PendingTransfer pt = new PendingTransfer();
                pt.setRequestId(rs.getString("request_id"));
                pt.setSenderMemberId(rs.getLong("sender_member_id"));
                pt.setSenderAccountId(rs.getLong("sender_account_id"));
                pt.setStatus(TransferStatus.valueOf(rs.getString("status")));
                pt.setReceiverBankName(rs.getString("receiver_bank_name"));
                pt.setReceiverAccountNumber(rs.getString("receiver_account_number"));
                pt.setReceiverName(rs.getString("receiver_name"));
                pt.setAmount(rs.getBigDecimal("amount"));
                pt.setMessage(rs.getString("message"));
                pt.setSenderLedgerId(rs.getObject("sender_ledger_id", Long.class));
                pt.setReceiverLedgerId(rs.getObject("receiver_ledger_id", Long.class));
                pt.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
                pt.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                pt.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                return pt;
        };
    }

}
