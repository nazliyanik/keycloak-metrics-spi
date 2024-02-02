package org.jboss.aerogear.keycloak.metrics;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

/**
 * This provider registers the MetricsFilter within environments that use Resteasy 4.x and above, e.g. Keycloak.X.
 */
@Provider
public class MetricsFilterProvider {

    @ServerRequestFilter(preMatching = true)
    public void filter(ContainerRequestContext requestContext) {
        MetricsFilter.instance().filter(requestContext);
    }

    @ServerResponseFilter
    public void filter(UriInfo uriInfo, ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        MetricsFilter.instance().filter(uriInfo, requestContext, responseContext);
    }
}
