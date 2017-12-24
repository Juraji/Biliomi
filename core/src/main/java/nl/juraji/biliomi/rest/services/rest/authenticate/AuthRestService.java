package nl.juraji.biliomi.rest.services.rest.authenticate;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.security.ApiLogin;
import nl.juraji.biliomi.model.core.security.ApiSecuritySettings;
import nl.juraji.biliomi.model.core.settings.SettingsDao;
import nl.juraji.biliomi.model.internal.rest.auth.RestAuthorizationRequest;
import nl.juraji.biliomi.model.internal.rest.auth.RestAuthorizationResponse;
import nl.juraji.biliomi.model.internal.rest.auth.RestRefreshTokenRequest;
import nl.juraji.biliomi.rest.config.Responses;
import nl.juraji.biliomi.utility.security.JWTGenerator;
import nl.juraji.biliomi.utility.security.PasswordEncryptor;
import nl.juraji.biliomi.utility.security.TokenType;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
      authorizationResponse.setMessage(RestAuthorizationResponse.getFaultLoginEmptyMsg());
      return Responses.ok(authorizationResponse);
    }

    ApiSecuritySettings settings = settingsDao.getSettings(ApiSecuritySettings.class);

    // Search for credentials by username (ignores casing)
    ApiLogin userCredentials = settings.getLogins().stream()
        .filter(apiLogin -> apiLogin.getUser().getUsername().equalsIgnoreCase(authorization.getUsername()))
        .findFirst()
        .orElse(null);

    // Check if user was found within credentialsSets
    if (userCredentials == null) {
      authorizationResponse.setMessage(RestAuthorizationResponse.getFaultUsernameInvalidMsg());
      return Responses.ok(authorizationResponse);
    }

    byte[] encryptedPassword = userCredentials.getPassword();
    byte[] passwordSalt = userCredentials.getSalt();

    // Check request password against database values
    PasswordEncryptor encryptor = new PasswordEncryptor();
    if (!encryptor.authenticate(authorization.getPassword(), encryptedPassword, passwordSalt)) {
      authorizationResponse.setMessage(RestAuthorizationResponse.getFaultPasswordInvalidMsg());
      return Responses.ok(authorizationResponse);
    }

    String token = jwtGenerator.generateAuthorizationToken(settings.getSecret(), userCredentials.getUser());
    String refreshToken = jwtGenerator.generateRefreshToken(settings.getSecret(), userCredentials.getUser());
    authorizationResponse.setMessage(RestAuthorizationResponse.getLoginOkMsg());
    authorizationResponse.setAuthorizationToken(token);
    authorizationResponse.setRefreshToken(refreshToken);
    return Responses.ok(authorizationResponse);
  }

  @POST
  @PermitAll
  @Path("/refresh")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response refresh(RestRefreshTokenRequest refreshTokenRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {
    RestAuthorizationResponse authorizationResponse = new RestAuthorizationResponse();

    // Check if username and password are defined in request
    if (refreshTokenRequest == null || StringUtils.isEmpty(refreshTokenRequest.getRefreshToken())) {
      authorizationResponse.setMessage(RestAuthorizationResponse.getFaultLoginEmptyMsg());
      return Responses.ok(authorizationResponse);
    }

    ApiSecuritySettings settings = settingsDao.getSettings(ApiSecuritySettings.class);

    // Decode refresh token
    Claims claims;
    try {
      claims = jwtGenerator.validateToken(settings.getSecret(), refreshTokenRequest.getRefreshToken(), TokenType.REFRESH);
    } catch (JwtException e) {
      authorizationResponse.setMessage(e.getMessage());
      return Responses.ok(authorizationResponse);
    }

    // Search for credentials by token subject (user display name, exact)
    User user = settings.getLogins().stream()
        .filter(apiLogin -> apiLogin.getUser().getUsername().equals(claims.getSubject()))
        .map(ApiLogin::getUser)
        .findFirst()
        .orElse(null);

    // Check if user was found within credentialsSets
    if (user == null) {
      authorizationResponse.setMessage(RestAuthorizationResponse.getFaultInvalidTokenMsg());
      return Responses.ok(authorizationResponse);
    }

    String token = jwtGenerator.generateAuthorizationToken(settings.getSecret(), user);
    String refreshToken = jwtGenerator.generateRefreshToken(settings.getSecret(), user);
    authorizationResponse.setMessage(RestAuthorizationResponse.getLoginOkMsg());
    authorizationResponse.setAuthorizationToken(token);
    authorizationResponse.setRefreshToken(refreshToken);
    return Responses.ok(authorizationResponse);
  }
}
