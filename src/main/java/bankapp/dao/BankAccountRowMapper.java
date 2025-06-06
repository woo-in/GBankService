package bankapp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import bankapp.account.BankAccount;
import bankapp.account.HighCreditAccount;
import bankapp.account.NormalAccount;
import org.springframework.jdbc.core.RowMapper;

public class BankAccountRowMapper implements RowMapper<BankAccount>{
	
	@Override
	public BankAccount mapRow(ResultSet rs , int rowNum) throws SQLException {
		
		if(rs.getString(5).equals("NormalAccount")) {
			return new NormalAccount(
					rs.getInt(1),
					rs.getString(2),
					rs.getDouble(3),
					rs.getInt(4),
					rs.getString(5)
					);
		}
		
		if(rs.getString(5).equals("HighCreditAccount")) {
			return new HighCreditAccount(
					rs.getInt(1) , 
					rs.getString(2) , 
					rs.getDouble(3) , 
					rs.getInt(4) , 
					rs.getInt(6), 
					rs.getString(5));
		}
		
	
		// 실행 안함 
		return null ;
	}
}
