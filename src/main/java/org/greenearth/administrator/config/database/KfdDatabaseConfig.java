package org.greenearth.administrator.config.database;

import com.google.common.collect.ImmutableMap;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        basePackages = {"org.greenearth.administrator.sample"},
        entityManagerFactoryRef = "kfdEntityManagerFactory",
        transactionManagerRef = "kfdTransactionManager"
)
@MapperScan(
        basePackages = {"org.greenearth.administrator.**"},
        sqlSessionFactoryRef = "kfdSessionFactory",
        sqlSessionTemplateRef = "kfdSqlSessionTemplate",
        annotationClass = KfdConnection.class
)
public class KfdDatabaseConfig {

    @Bean(name = "kfdDataSource", destroyMethod="")
    public DataSource dataSource() throws NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:comp/env/jdbc/kfdDS");
        //bean.setJndiName("jdbc/kfdDS");       // Jeus 설정 시
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(true);
        bean.afterPropertiesSet();
        return new LazyConnectionDataSourceProxy((DataSource) Objects.requireNonNull(bean.getObject()));
    }

    /*----------------------------------------------JPA 설정-----------------------------------------------------*/
    @Bean(name = "kfdEntityManagerFactory")
    public EntityManagerFactory entityManagerFactory(@Qualifier("kfdDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("org.greenearth.administrator.sample");
        factoryBean.setPersistenceUnitName("kfd");
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setJpaPropertyMap(ImmutableMap.of(
                "hibernate.hbm2ddl.auto", "update",
                "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect",
                "hibernate.show_sql", "true",
                "hibernate.format_sql", "true",
                "hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy"
        ));
        factoryBean.afterPropertiesSet();

        return factoryBean.getObject();
    }

    @Bean(name = "kfdTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("kfdEntityManagerFactory")
                                                                 EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        return transactionManager;
    }

    /*----------------------------------------------Mybatis 설정-----------------------------------------------------*/
    @Bean(name = "kfdSessionFactory")
    public SqlSessionFactory kfdSqlSessionFactory(@Qualifier("kfdDataSource") DataSource dataSource,
                                                    ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(
                applicationContext.getResources("classpath*:mybatis/mapper/**/*.xml"));

        return sessionFactoryBean.getObject();
    }

    @Bean(name = "kfdSqlSessionTemplate")
    public SqlSessionTemplate kfdSqlSessionTemplate(@Qualifier("kfdSessionFactory")
                                                              SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}