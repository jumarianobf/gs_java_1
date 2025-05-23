package br.com.fiap.challenge.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OracleDatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OracleDatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            // Consulta simples para verificar se o banco está respondendo
            Integer result = jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Integer.class);

            if (result != null && result == 1) {
                return Health.up()
                        .withDetail("database", "Oracle")
                        .withDetail("status", "Connected")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "Oracle")
                        .withDetail("error", "Unexpected response from database")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "Oracle")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
