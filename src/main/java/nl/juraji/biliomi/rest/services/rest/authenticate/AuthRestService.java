package nl.juraji.biliomi.rest.services.rest.authenticate;

import nl.juraji.biliomi.model.core.settings.SettingsDao;
import nl.juraji.biliomi.model.core.security.ApiLogin;
import nl.juraji.biliomi.model.core.security.ApiSecuritySettings;
import nl.juraji.biliomi.model.internal.rest.auth.RestAuthorizationRequest;
import nl.juraji.biliomi.model.internal.rest.auth.RestAuthorizationResponse;
import nl.juraji.biliomi.rest.config.Responses;
import nl.juraji.biliomi.utility.security.JWTGenerator;
import nl.juraji.biliomi.utility.security.PasswordEncryption;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

/**
 * Created by Juraji on 16-6-2017.
 * Biliomi v3
 */
@Path("/auth")
public class AuthRestService {

  @Inject
  private SettingsDao settingsDao;

  @Inject
  private JWTGenerator jwtGenerator;

  @POST
  @PermitAll
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response login(RestAuthorizationRequest authorization) throws InvalidKeySpecException, NoSuchAlgorithmException {
    RestAuthorizationResponse authorizationResponse = new RestAuthorizationResponse();

    // Check if username and password are defined in request
    if (authorization == null || authorization.isEmpty()) {
      authorizationResponse.setMessage(RestAuthorizationResponse.MSG_FAULT_LOGIN_EMPTY);
      return Responses.ok(authorizationResponse);
    }

    ApiSecuritySettings settings = settingsDao.getSettings(ApiSecuritySettings.class);
    Set<ApiLogin> credentialsSet = settings.getLogins();

    // Search for credentials by username (ignores casing)
    ApiLogin userCredentials = credentialsSet.stream()
        .filter(apiLogin -> apiLogin.getUser().getUsername().equalsIgnoreCase(authorization.getUsername()))
        .findFirst()
        .orElse(null);

    // Check if user was found within credentialsSets
    if (userCredentials == null) {
      authorizationResponse.setMessage(RestAuthorizationResponse.MSG_FAULT_USERNAME_INVALID);
      return Responses.ok(authorizationResponse);
    }

    byte[] encryptedPassword = userCredentials.getPassword();
    byte[] passwordSalt = userCredentials.getSalt();

    // Check request password against database values
    if (!PasswordEncryption.authenticate(authorization.getPassword(), encryptedPassword, passwordSalt)) {
      authorizationResponse.setMessage(RestAuthorizationResponse.MSG_FAULT_PASSWORD_INVALID);
      return Responses.ok(authorizationResponse);
    }

    String token = jwtGenerator.generateToken(settings.getSecret(), userCredentials.getUser());
    authorizationResponse.setMessage(RestAuthorizationResponse.MSG_LOGIN_OK);
    authorizationResponse.setToken(token);
    return Responses.ok(authorizationResponse);
  }
}
