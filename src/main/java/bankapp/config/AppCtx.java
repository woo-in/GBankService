package bankapp.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import bankapp.aspect.TransactionLogAspect;
import bankapp.dao.BankAccountDao;
import org.apache.tomcat.jdbc.pool.DataSource;


@Configuration 
@EnableAspectJAutoProxy
public class AppCtx{ 
	
	
	
	// dataSource 빈 등록 
	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		
		DataSource ds = new DataSource();
		ds.setDriverClassName("org.mariadb.jdbc.Driver");
		ds.setUrl("jdbc:mariadb://localhost:3306/BankSystem");
		ds.setUsername("root"); 
		ds.setPassword("0205"); 
		
		// 10 개로 시작 , 최대 활성 갯수는 10000 
		ds.setInitialSize(10); 
		ds.setMaxActive(10000); 
	
		return ds; 
	}
	
	// 데이터 베이스 빈 등록 
	@Bean 
	public BankAccountDao bankAccountDao() {
		return new BankAccountDao(dataSource()); 
	}
	
	// 트랜잭션 매니저 빈 등록 
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager tm = new DataSourceTransactionManager(); 
		tm.setDataSource(dataSource());
		return tm ;
	}
	
	// 로그 데이터를 위한 AOP 빈 등록 
	@Bean
	public TransactionLogAspect transactionLogAspect() {
		return new TransactionLogAspect(); 
	}
	
	
	
}





