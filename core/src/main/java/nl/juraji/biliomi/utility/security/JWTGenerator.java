package nl.juraji.biliomi.utility.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import nl.juraji.biliomi.Biliomi;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.rest.auth.TokenUserType;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.ChannelName;

import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

/**
 * Created by Juraji on 25-6-2017.
 * Biliomi v3
 */
@Default
public class JWTGenerator {
  private static final String CLAIMS_CHANNEL = "chn";
  private static final String CLAIMS_USER_TYPE = "utp";

  @Inject
  @ChannelName
  private String channelName;

  @Inject
  @AppDataValue("rest.security.jwt.expiresin")
  private String jwtExpiresIn;

  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  /**
   * Generate a new JWT for the given user
   *
   * @param secretBytes The JWT secret as byte array
   * @param user        The user to encode the token for
   * @return The generated token as string
   */
  public String generateToken(@NotNull byte[] secretBytes, @NotNull User user) {
    DefaultJwsHeader jwsHeader = new DefaultJwsHeader();
    Claims claims = new DefaultClaims();
    long jwtExpiresIn = Long.parseLong(this.jwtExpiresIn);
    Date now = new Date();
    Date expiresAt = new Date();

    jwsHeader.setAlgorithm(signatureAlgorithm.getValue());
    jwsHeader.setType(Header.JWT_TYPE);

    expiresAt.setTime(now.getTime() + jwtExpiresIn);
    claims.setSubject(user.getDisplayName());
    claims.setIssuedAt(now);
    claims.setIssuer(Biliomi.class.getSimpleName());
    claims.setExpiration(expiresAt);
    claims.put(CLAIMS_CHANNEL, channelName);

    if (user.isCaster()) {
      claims.put(CLAIMS_USER_TYPE, TokenUserType.CASTER);
    } else if (user.isModerator()) {
      claims.put(CLAIMS_USER_TYPE, TokenUserType.MODERATOR);
    } else {
      throw new IllegalStateException("User \"" + user.getDisplayName() + "\" is not a caster nor a moderator");
    }

    JwtBuilder builder = Jwts.builder()
        .setHeader((Map<String, Object>) jwsHeader)
        .setClaims(claims)
        .signWith(signatureAlgorithm, secretBytes);

    return builder.compact();
  }

  public Claims validateToken(@NotNull byte[] secretBytes, @NotNull String token) throws JwtException {
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, signatureAlgorithm.getJcaName());

    Claims claims = Jwts.parser()
        .setSigningKey(secretKeySpec)
        .parseClaimsJws(token)
        .getBody();

    Object claimedChannel = claims.getOrDefault(CLAIMS_CHANNEL, null);
    if (claimedChannel == null || !String.class.isAssignableFrom(claimedChannel.getClass()) || !channelName.equals(claimedChannel)) {
      throw new JwtException("This token is not for channel " + channelName);
    }

    return claims;
  }
}
