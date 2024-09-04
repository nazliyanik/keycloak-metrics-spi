package org.jboss.aerogear.keycloak.metrics;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.RealmProvider;

public class MetricsEventListenerFactory implements EventListenerProviderFactory {

    // Store the KeycloakSessionFactory instance for later use
    private KeycloakSessionFactory keycloakSessionFactory;

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        // Create a new instance of MetricsEventListener, passing both RealmProvider and
        // KeycloakSession
        return new MetricsEventListener(session.getProvider(RealmProvider.class), session);
    }

    @Override
    public void init(Config.Scope config) {
        // No specific initialization required at this stage
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Store the KeycloakSessionFactory for later use in the class
        this.keycloakSessionFactory = factory;
    }

    @Override
    public void close() {
        // No resources to close, but this method could be used for cleanup if necessary
    }

    @Override
    public String getId() {
        // Return the unique ID for this EventListenerProvider
        return MetricsEventListener.ID;
    }
}
