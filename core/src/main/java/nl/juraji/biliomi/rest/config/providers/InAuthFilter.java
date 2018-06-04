package nl.juraji.biliomi.rest.config.providers;

import io.jsonwebtoken.JwtException;
import nl.juraji.biliomi.model.core.security.ApiSecuritySettings;
import nl.juraji.biliomi.model.core.settings.SettingsDao;
import nl.juraji.biliomi.model.internal.rest.auth.RestAuthorizationResponse;
import nl.juraji.biliomi.model.internal.rest.auth.TokenType;
import nl.juraji.biliomi.model.internal.rest.auth.TokenUserType;
import nl.juraji.biliomi.rest.config.RestRequestInfoHolder;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import nl.juraji.biliomi.utility.security.JWTGenerator;
import nl.juraji.biliomi.utility.security.JwtClaims;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;

import javax.annotation.Priority;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by Juraji on 16-6-2017.
 * Biliomi v3
 */
@Provider
@Priority(Priorities.AUTHORIZATION)
public class InAuthFilter implements ContainerRequestFilter {
    private static final String OPTIONS_METHOD = HttpMethod.OPTIONS.toString();
    private static final String APPLICATION_WADL_PATH = "application.wadl";

    @Inject
    private SettingsDao settingsDao;

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    private JWTGenerator jwtGenerator;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (isPermittedByDefault(requestContext) || isPermitAllPresent()) {
            return;
        }

        String authorizationToken = requestContext.getHeaders().getFirst(HttpHeader.AUTHORIZATION.asString());
        ApiSecuritySettings settings = settingsDao.getSettings(ApiSecuritySettings.class);

        try {
            RestRequestInfoHolder.RequestInfo requestInfo = RestRequestInfoHolder.getRequestInfo();
            JwtClaims claims = jwtGenerator.validateToken(settings.getSecret(), authorizationToken, TokenType.AUTH);

            if (TokenUserType.APPLICATION.equals(claims.getUserType())) {
                // Application tokens are granted access by default, but ONLY to GET endpoints
                boolean isGETRequest = "GET".equals(requestContext.getMethod());
                boolean isGETResource = resourceInfo.getResourceMethod().isAnnotationPresent(GET.class);
                if (!isGETRequest || !isGETResource) {
                    throw new JwtException("Application tokens may only execute GET requests");
                }
            } else {
                boolean userNonExistent = settings.getLogins().stream()
                        .noneMatch(apiLogin -> apiLogin.getUser().getUsername().equals(claims.getSubject()));

                if (userNonExistent) {
                    throw new JwtException("No login known for user " + claims.getSubject());
                }

            }

            requestInfo.setUsername(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            RestAuthorizationResponse fault = new RestAuthorizationResponse();
            if (e instanceof IllegalArgumentException) {
                fault.setMessage(RestAuthorizationResponse.getFaultAuthorizationMissingMsg());
            } else {
                fault.setMessage(e.getMessage());
            }

            String faultBody = JacksonMarshaller.marshal(fault);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(faultBody)
                    .build());
        }
    }

    /**
     * Check the resource class and method for the presence of @NoAuthorization
     *
     * @return True if present else False
     */
    private boolean isPermitAllPresent() {
        Class<?> resourceClass = resourceInfo.getResourceClass();
        Method resourceMethod = resourceInfo.getResourceMethod();
        return resourceClass.isAnnotationPresent(PermitAll.class) || resourceMethod.isAnnotationPresent(PermitAll.class);
    }

    private boolean isPermittedByDefault(ContainerRequestContext requestContext) {
        return OPTIONS_METHOD.equals(requestContext.getMethod()) || APPLICATION_WADL_PATH.equals(requestContext.getUriInfo().getPath());
    }
}
