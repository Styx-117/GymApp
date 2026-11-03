package com.smartfit.gymApp.config;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigurationTest {
    private Map<String,String> settings(){
        return new HashMap<>(Map.of("spring.datasource.url","jdbc:postgresql://localhost/test",
            "spring.datasource.username","test","spring.datasource.password","test"));
    }
    @Test void defineLimitesTimeoutYPreparadasSinConectar(){
        var c=ConexionBD.config(settings()::get,true);
        assertEquals(6,c.getMaximumPoolSize());assertEquals(1,c.getMinimumIdle());
        assertEquals(10000,c.getConnectionTimeout());assertEquals(3000,c.getValidationTimeout());
        assertEquals("SET statement_timeout TO 15000",c.getConnectionInitSql());
        assertEquals("128",c.getDataSourceProperties().getProperty("preparedStatementCacheQueries"));
    }
    @Test void aliasesDeAplicacionUsanElMismoDestinoEnSpring(){
        var s=settings();s.put("GYMAPP_DB_URL","jdbc:postgresql://localhost/isolated");
        s.put("GYMAPP_DB_USER","isolated");s.put("GYMAPP_DB_PASSWORD","isolated");
        var c=ConexionBD.config(s::get,true);
        assertEquals(s.get("GYMAPP_DB_URL"),c.getJdbcUrl());assertEquals("isolated",c.getUsername());
        assertEquals("isolated",c.getPassword());
    }
    @Test void standaloneNoReservaConexionesInactivas(){
        assertEquals(0,ConexionBD.config(settings()::get,false).getMinimumIdle());
    }
    @Test void rechazaConfiguracionIncompletaSinRevelarla(){
        assertThrows(IllegalStateException.class,()->ConexionBD.config(key->null,true));
        var s=settings();s.put("spring.datasource.hikari.maximum-pool-size","invalido");
        assertThrows(IllegalArgumentException.class,()->ConexionBD.config(s::get,true));
    }
}
