package br.com.fiap.challenge.service.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMetrics {

    private final Counter usuarioCreations;
    private final Counter usuarioUpdates;
    private final Counter usuarioDeletions;

    @Autowired
    public UsuarioMetrics(MeterRegistry registry) {
        this.usuarioCreations = Counter.builder("app.usuario.creations")
                .description("Number of users created")
                .register(registry);

        this.usuarioUpdates = Counter.builder("app.usuario.updates")
                .description("Number of users updated")
                .register(registry);

        this.usuarioDeletions = Counter.builder("app.usuario.deletions")
                .description("Number of users deleted")
                .register(registry);
    }

    public void incrementUsuarioCreations() {
        this.usuarioCreations.increment();
    }

    public void incrementUsuarioUpdates() {
        this.usuarioUpdates.increment();
    }

    public void incrementUsuarioDeletions() {
        this.usuarioDeletions.increment();
    }
}
