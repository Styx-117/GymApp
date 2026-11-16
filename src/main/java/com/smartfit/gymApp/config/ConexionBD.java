package com.smartfit.gymApp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.function.Function;

/** Una misma fuente para JDBC y JPA; cerrar una Connection la devuelve al pool. */
public final class ConexionBD {
    private static volatile DataSource source;
    private static HikariDataSource standalone;
    private ConexionBD() {}

    public static Connection conectar() throws SQLException {
        return dataSource().getConnection();
    }

    public static synchronized void usarDataSource(DataSource shared) {
        if (shared == null) throw new IllegalArgumentException("La fuente de datos es obligatoria.");
        if (standalone != null && standalone != shared) { standalone.close(); standalone = null; }
        source = shared;
    }

    private static DataSource dataSource() {
        DataSource current = source;
        if (current != null) return current;
        synchronized (ConexionBD.class) {
            if (source == null) {
                Properties properties = new Properties();
                try (var input = ConexionBD.class.getResourceAsStream("/application.properties")) {
                    if (input != null) properties.load(input);
                } catch (IOException ex) { throw new IllegalStateException("No se pudo leer la configuracion de la base de datos.",ex); }
                standalone = new HikariDataSource(config(key -> setting(properties,key), false));
                source = standalone;
                Runtime.getRuntime().addShutdownHook(new Thread(ConexionBD::cerrarStandalone,"GymApp-Pool-Close"));
            }
            return source;
        }
    }

    private static String setting(Properties properties,String key) {
        String env = System.getenv(key.replace('.','_').replace('-','_').toUpperCase(Locale.ROOT));
        return first(System.getProperty(key), env, properties.getProperty(key));
    }

    static HikariConfig config(Function<String,String> settings, boolean springManaged) {
        String url=first(settings.apply("GYMAPP_DB_URL"),settings.apply("spring.datasource.url"));
        String user=first(settings.apply("GYMAPP_DB_USER"),settings.apply("spring.datasource.username"));
        String password=first(settings.apply("GYMAPP_DB_PASSWORD"),settings.apply("spring.datasource.password"));
        if(url==null||user==null||password==null)throw new IllegalStateException("Falta configurar la conexion PostgreSQL.");
        HikariConfig config=new HikariConfig();
        config.setJdbcUrl(url);config.setUsername(user);config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setPoolName(springManaged?"GymApp-Shared":"GymApp-Standalone");
        config.setMaximumPoolSize(number(settings,"maximum-pool-size",6));
        config.setMinimumIdle(springManaged?number(settings,"minimum-idle",1):0);
        config.setConnectionTimeout(number(settings,"connection-timeout",10000));
        config.setValidationTimeout(number(settings,"validation-timeout",3000));
        config.setIdleTimeout(number(settings,"idle-timeout",300000));
        config.setMaxLifetime(number(settings,"max-lifetime",1500000));
        config.setInitializationFailTimeout(-1);
        config.setConnectionInitSql("SET statement_timeout TO "+number(settings,"statement-timeout",15000));
        config.addDataSourceProperty("connectTimeout","10");
        config.addDataSourceProperty("socketTimeout","30");
        config.addDataSourceProperty("cancelSignalTimeout","3");
        config.addDataSourceProperty("tcpKeepAlive","true");
        config.addDataSourceProperty("preparedStatementCacheQueries","128");
        config.addDataSourceProperty("preparedStatementCacheSizeMiB","5");
        config.addDataSourceProperty("ApplicationName","GymApp");
        return config;
    }

    private static int number(Function<String,String> settings,String suffix,int fallback) {
        String value=settings.apply("spring.datasource.hikari."+suffix);
        if(value==null||value.isBlank())return fallback;
        try {int n=Integer.parseInt(value);if(n<0)throw new NumberFormatException();return n;}
        catch(NumberFormatException ex){throw new IllegalArgumentException("Configuracion de conexiones invalida: "+suffix);}
    }

    private static String first(String... values) {
        for(String value:values)if(value!=null&&!value.isBlank())return value;
        return null;
    }

    private static synchronized void cerrarStandalone() {
        if(standalone!=null){standalone.close();if(source==standalone)source=null;standalone=null;}
    }
}
