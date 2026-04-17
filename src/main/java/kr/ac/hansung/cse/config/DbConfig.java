package kr.ac.hansung.cse.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {
        "kr.ac.hansung.cse.service",
        "kr.ac.hansung.cse.repository"
})
public class DbConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://mysql:3306/productdb" +
                  "?useSSL=false" +
                  "&allowPublicKeyRetrieval=true" +
                  "&serverTimezone=Asia/Seoul" +
                  "&useUnicode=true" +
                  "&characterEncoding=UTF-8");
        ds.setUsername("appuser");
        ds.setPassword("apppass");
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf =
                new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter()); // JPA 구현체: Hibernate
        emf.setPackagesToScan("kr.ac.hansung.cse.model");         // @Entity 클래스 위치
        emf.setJpaProperties(hibernateProperties());
        return emf;
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.hbm2ddl.auto",
                "none");                                  // DDL 자동 실행 안 함 (init.sql이 담당)
        props.setProperty("hibernate.show_sql",  "true"); // 실행 SQL 콘솔 출력 (학습용)
        props.setProperty("hibernate.format_sql", "true"); // SQL 줄바꿈 출력
        // dialect, allow_jdbc_metadata_access 생략 → Hibernate가 JDBC 메타데이터로 자동 감지
        return props;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
