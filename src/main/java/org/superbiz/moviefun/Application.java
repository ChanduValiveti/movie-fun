package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials(@Value("${VCAP_SERVICES}") String VCAPServices)
    {
            return new DatabaseServiceCredentials(VCAPServices);
    }

//    @Bean
//    public DataSource movieDataSource(DatabaseServiceCredentials serviceCredentials) {
//
//        MysqlDataSource dataSource = new MysqlDataSource();
//        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
//        return dataSource;
//    }


//    @Bean
//    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
//        MysqlDataSource dataSource = new MysqlDataSource();
//        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
//        return dataSource;
//    }

    @Bean
    public HikariDataSource movieDataSource(DatabaseServiceCredentials serviceCredentials) {

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        HikariConfig config = new HikariConfig();
        config.setDataSource(dataSource);
        return new HikariDataSource(config);
    }


    @Bean
    public HikariDataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        HikariDataSource hickariDataSource =  new HikariDataSource();
        hickariDataSource.setDataSource(dataSource);
        return hickariDataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter()
    {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapterObj = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapterObj.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapterObj.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapterObj.setGenerateDdl(true);

        return hibernateJpaVendorAdapterObj;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean movieEntityManager(DataSource movieDataSource,
                                                                     HibernateJpaVendorAdapter hibernateJpaVendorAdapter)
    {

        LocalContainerEntityManagerFactoryBean localContainerEntityManagerMoviesFactoryBean = new LocalContainerEntityManagerFactoryBean();

        localContainerEntityManagerMoviesFactoryBean.setDataSource(movieDataSource);
        localContainerEntityManagerMoviesFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerMoviesFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainerEntityManagerMoviesFactoryBean.setPersistenceUnitName("persistence.movies");


        return localContainerEntityManagerMoviesFactoryBean;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean albumEntityManager(DataSource albumsDataSource,
                                                                     HibernateJpaVendorAdapter hibernateJpaVendorAdapter)
    {

        LocalContainerEntityManagerFactoryBean localContainerEntityManagerAlbumsFactoryBean = new LocalContainerEntityManagerFactoryBean();

        localContainerEntityManagerAlbumsFactoryBean.setDataSource(albumsDataSource);
        localContainerEntityManagerAlbumsFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerAlbumsFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainerEntityManagerAlbumsFactoryBean.setPersistenceUnitName("persistence.albums");

        return localContainerEntityManagerAlbumsFactoryBean;
    }

    @Bean
    public PlatformTransactionManager movieTransManager(EntityManagerFactory movieEntityManager)
    {
        return new JpaTransactionManager(movieEntityManager);

    }


    @Bean
    public PlatformTransactionManager albumTransManager(EntityManagerFactory albumEntityManager)
    {
        return new JpaTransactionManager(albumEntityManager);

    }

    @Bean
    public TransactionTemplate movieTransactionOperation(PlatformTransactionManager movieTransManager)
    {
        return new TransactionTemplate(movieTransManager);

    }


    @Bean
    public TransactionTemplate albumTransactionOperation(PlatformTransactionManager albumTransManager)
    {
        return new TransactionTemplate(albumTransManager);

    }

}
