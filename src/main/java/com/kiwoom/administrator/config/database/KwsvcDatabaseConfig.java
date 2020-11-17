package com.kiwoom.administrator.config.database;

import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariConfig;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.kiwoom.administrator.account"},
        entityManagerFactoryRef = "kwsvcEntityManagerFactory",
        transactionManagerRef = "kwsvcTransactionManager"
)
@MapperScan(
        basePackages = {"com.kiwoom.administrator.**"},
        sqlSessionFactoryRef = "kwsvcSessionFactory",
        sqlSessionTemplateRef = "kwsvcSqlSessionTemplate",
        annotationClass = KwsvcConnection.class
)
public class KwsvcDatabaseConfig extends HikariConfig {

    @Bean(name = "kwsvcDataSource", destroyMethod="")
    @Primary
    public DataSource dataSource() throws NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:comp/env/jdbc/kwsvcDS");
        //bean.setJndiName("jdbc/kwsvcDS");         // Jeus 설정 시
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(true);
        bean.afterPropertiesSet();
        return new LazyConnectionDataSourceProxy((DataSource) Objects.requireNonNull(bean.getObject()));
    }

    /*----------------------------------------------JPA 설정-----------------------------------------------------*/
    @Bean(name = "kwsvcEntityManagerFactory")
    @Primary
    public EntityManagerFactory entityManagerFactory(@Qualifier("kwsvcDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("com.kiwoom.administrator.account");
        factoryBean.setPersistenceUnitName("kwsvc");
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setJpaPropertyMap(ImmutableMap.of(
                "hibernate.hbm2ddl.auto", "create-drop",
                "hibernate.dialect", "org.hibernate.dialect.H2Dialect",
                "hibernate.show_sql", "true",
                "hibernate.format_sql", "true",
                "hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy"
        ));
        factoryBean.afterPropertiesSet();

        return factoryBean.getObject();
    }

    @Bean(name = "kwsvcTransactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("kwsvcEntityManagerFactory")
                                                                     EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        return transactionManager;
    }

    /*----------------------------------------------Mybatis 설정-----------------------------------------------------*/
    @Bean(name = "kwsvcSessionFactory")
    @Primary
    public SqlSessionFactory kwsvcSqlSessionFactory(@Qualifier("kwsvcDataSource") DataSource dataSource,
                                               ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(
                applicationContext.getResources("classpath*:mybatis/mapper/**/*.xml"));

        return sessionFactoryBean.getObject();
    }

    @Bean(name = "kwsvcSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate kwsvcSqlSessionTemplate(@Qualifier("kwsvcSessionFactory")
                                                                  SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
