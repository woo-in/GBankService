package bankapp.core.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

//import bankapp.core.aspect.TransactionLogAspect;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration 
@EnableAspectJAutoProxy
public class AppCtx{ 

// TODO: 템플릿 폴더 구조 정리하기

	// dataSource 빈 등록 
	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		
		DataSource ds = new DataSource();
		ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost:3306/bankapp_db?serverTimezone=UTC&characterEncoding=UTF-8");
		ds.setUsername("root"); 
		ds.setPassword("02050205");
		
		// 10 개로 시작 , 최대 활성 갯수는 10000 
		ds.setInitialSize(10); 
		ds.setMaxActive(10000); 
	
		return ds; 
	}

	// 트랜잭션 매니저 빈 등록 
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager tm = new DataSourceTransactionManager(); 
		tm.setDataSource(dataSource());
		return tm ;
	}


//	@Bean
//	public TransactionLogAspect transactionLogAspect() {
//		return new TransactionLogAspect();
//	}

	@Bean
	public WebClient webClient(){
		return WebClient.builder()
				.baseUrl("https://www.alphavantage.co")
				.build();
	}
}







