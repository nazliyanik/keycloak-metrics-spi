package org.jboss.aerogear.keycloak.metrics;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.RealmModel;
import io.prometheus.client.Gauge;

public class MetricsEventListener implements EventListenerProvider {

    public final static String ID = "metrics-listener";

    private final static Logger logger = Logger.getLogger(MetricsEventListener.class);
    private final RealmProvider realmProvider;
    private final KeycloakSession keycloakSession;

    // Constructor now takes both RealmProvider and KeycloakSession to allow more
    // comprehensive metrics tracking
    public MetricsEventListener(RealmProvider realmProvider, KeycloakSession keycloakSession) {
        this.realmProvider = realmProvider;
        this.keycloakSession = keycloakSession;
    }

    @Override
    public void onEvent(Event event) {
        logEventDetails(event);

        // Handle different event types and record metrics accordingly
        switch (event.getType()) {
            case LOGIN:
                PrometheusExporter.instance().recordLogin(event, realmProvider);
                break;
            case CLIENT_LOGIN:
                PrometheusExporter.instance().recordClientLogin(event, realmProvider);
                break;
            case REGISTER:
                PrometheusExporter.instance().recordRegistration(event, realmProvider);
                break;
            case REFRESH_TOKEN:
                PrometheusExporter.instance().recordRefreshToken(event, realmProvider);
                break;
            case CODE_TO_TOKEN:
                PrometheusExporter.instance().recordCodeToToken(event, realmProvider);
                break;
            case REGISTER_ERROR:
                PrometheusExporter.instance().recordRegistrationError(event, realmProvider);
                break;
            case LOGIN_ERROR:
                PrometheusExporter.instance().recordLoginError(event, realmProvider);
                break;
            case CLIENT_LOGIN_ERROR:
                PrometheusExporter.instance().recordClientLoginError(event, realmProvider);
                break;
            case REFRESH_TOKEN_ERROR:
                PrometheusExporter.instance().recordRefreshTokenError(event, realmProvider);
                break;
            case CODE_TO_TOKEN_ERROR:
                PrometheusExporter.instance().recordCodeToTokenError(event, realmProvider);
                break;
            default:
                PrometheusExporter.instance().recordGenericEvent(event, realmProvider);
        }

        // New method call to collect and record active client session metrics
        recordClientSessionMetrics(event.getRealmId());
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        logAdminEventDetails(event);

        PrometheusExporter.instance().recordGenericAdminEvent(event, realmProvider);
    }

    private void recordClientSessionMetrics(String realmId) {
        RealmModel realmModel = realmProvider.getRealm(realmId);

        if (realmModel != null) {
            // Fetch active client session stats and record them using Prometheus Gauge
            keycloakSession.sessions().getActiveClientSessionStats(realmModel, false).entrySet()
                    .forEach(perClient -> Gauge.build()
                            .name("sessions") // Metric name for active sessions
                            .help("Number of Active Sessions") // Description of the metric
                            .labelNames("realm", "offline", "clientId") // Labels for the metric
                            .register()
                            .set(perClient.getValue())); // Set the value for the gauge
        }
    }

    private void logEventDetails(Event event) {
        // Log details of the event for debugging purposes
        logger.debugf("Received user event of type %s in realm %s",
                event.getType().name(),
                event.getRealmId());
    }

    private void logAdminEventDetails(AdminEvent event) {
        // Log details of the admin event for debugging purposes
        logger.debugf("Received admin event of type %s (%s) in realm %s",
                event.getOperationType().name(),
                event.getResourceType().name(),
                event.getRealmId());
    }

    @Override
    public void close() {
        // No resources to clean up, but method is provided as part of the interface
    }
}
