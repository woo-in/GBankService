package bankapp.account.dao;

import bankapp.account.model.PrimaryAccount;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Slf4j
@Component
public class AccountDao {

    private final JdbcTemplate jdbcTemplate;

    public AccountDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PrimaryAccount insertAccount(PrimaryAccount account){
        String sql = "INSERT INTO ACCOUNT (member_id , account_number , balance , account_type , nickname) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1 , account.getMemberId());
            ps.setString(2 , account.getAccountNumber());
            ps.setBigDecimal(3 , account.getBalance());
            ps.setString(4 , account.getAccountType());
            ps.setString(5 , account.getNickname());
            return ps;
        } , keyHolder);

        long generatedId = keyHolder.getKey().longValue();
        account.setAccountId(generatedId);
        return account;
    }
}
