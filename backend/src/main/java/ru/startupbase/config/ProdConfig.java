package ru.startupbase.config;

import com.mchange.v2.c3p0.DriverManagerDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({CommonConfig.class})
@EnableTransactionManagement
public class ProdConfig {

  @Bean
  DataSource dataSource() {
    DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(false);
    driverManagerDataSource.setJdbcUrl("jdbc:postgresql://127.0.0.1:5433/mp?stringtype=unspecified");
    driverManagerDataSource.setUser("postgres");
    driverManagerDataSource.setIdentityToken("mp");
    return driverManagerDataSource;
  }

  @Bean
  LocalSessionFactoryBean sessionFactory(DataSource dataSource, MappingConfig mappingConfig) {
    LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
    sessionFactoryBean.setDataSource(dataSource);
    sessionFactoryBean.setAnnotatedClasses(mappingConfig.getMappings());

    Properties hibernateProperties = new Properties();
    hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
    hibernateProperties.setProperty("hibernate.hbm2ddl", "validate");
    hibernateProperties.setProperty("hibernate.show_sql", "false");
    hibernateProperties.setProperty("hibernate.format_sql", "false");
    hibernateProperties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
    hibernateProperties.setProperty("hibernate.jdbc.use_get_generated_keys", "true");

    sessionFactoryBean.setHibernateProperties(hibernateProperties);
    return sessionFactoryBean;
  }

  @Bean
  HibernateTransactionManager transactionManager(SessionFactory sessionFactory, DataSource dataSource) {
    HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager(sessionFactory);
    hibernateTransactionManager.setDataSource(dataSource);
    return hibernateTransactionManager;
  }
}
