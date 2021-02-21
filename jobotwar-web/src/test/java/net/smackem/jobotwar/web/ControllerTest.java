package net.smackem.jobotwar.web;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.smackem.jobotwar.web.persist.DaoFactories;
import net.smackem.jobotwar.web.persist.DaoFactory;
import org.junit.After;
import org.junit.Before;

import java.sql.SQLException;

public class ControllerTest {
    private static final int port = 55666;
    private static final String baseUri = "http://localhost:" + port + "/";
    private WebApp remoteServer;
    private DaoFactory daoFactory;
    RestClient http;

    @Before
    public void setUp() throws ClassNotFoundException {
        this.daoFactory = createInMemorySqlDaoFactory();
        this.remoteServer = new WebApp(port, this.daoFactory);
        this.http = new RestClient(baseUri);
    }

    @After
    public void tearDown() throws Exception {
        this.remoteServer.close();
        this.daoFactory.close();
    }

    private static DaoFactory createInMemorySqlDaoFactory() throws ClassNotFoundException {
        final HikariConfig config = new HikariConfig();
        //no longer needed: Class.forName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:jobotwar;INIT=RUNSCRIPT FROM 'classpath:sql/init.sql'");
        final HikariDataSource dataSource = new HikariDataSource(config);
        return DaoFactories.sql(() -> {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, dataSource);
    }
}
