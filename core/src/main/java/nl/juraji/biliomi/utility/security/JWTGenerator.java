package nl.juraji.biliomi.utility.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import nl.juraji.biliomi.Biliomi;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.rest.auth.TokenType;
import nl.juraji.biliomi.model.internal.rest.auth.TokenUserType;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppData;
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
    private static final String CLAIMS_USER_DISPLAY_NAME = "usr";
    private static final String CLAIMS_USER_TYPE = "utp";
    private static final String CLAIMS_TOKEN_TYPE = "ttp";
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    @Inject
    @ChannelName
    private String channelName;
    @Inject
    @AppData("rest.security.token.auth.expiresaftermillis")
    private Long authTokenExpiry;
    @Inject
    @AppData("rest.security.token.refresh.expiresaftermillis")
    private Long refreshTokenExpiry;
    @Inject
    @AppData("rest.security.token.auth.allowedskewseconds")
    private Long allowedSkewSeconds;

    /**
     * Generate a new JWT for the given user
     *
     * @param secretBytes The JWT secret as byte array
     * @param user        The user to encode the token for
     * @return The generated token as string
     */
    public String generateAuthorizationToken(@NotNull byte[] secretBytes, @NotNull User user) {
        Claims claims = new DefaultClaims();
        claims.put(CLAIMS_TOKEN_TYPE, TokenType.AUTH);

        if (user.isCaster()) {
            claims.put(CLAIMS_USER_TYPE, TokenUserType.CASTER);
        } else if (user.isModerator()) {
            claims.put(CLAIMS_USER_TYPE, TokenUserType.MODERATOR);
        } else {
            throw new IllegalStateException("User \"" + user.getDisplayName() + "\" is not a caster nor a moderator");
        }

        return generateToken(secretBytes, claims, user, authTokenExpiry);
    }

    public String generateRefreshToken(byte[] secretBytes, User user) {
        DefaultClaims claims = new DefaultClaims();
        claims.put(CLAIMS_TOKEN_TYPE, TokenType.REFRESH);

        return generateToken(secretBytes, claims, user, refreshTokenExpiry);
    }

    public Claims validateToken(@NotNull byte[] secretBytes, @NotNull String token, TokenType requiredTokenType) throws JwtException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, signatureAlgorithm.getJcaName());

        Claims claims = Jwts.parser()
                .setSigningKey(secretKeySpec)
                .setAllowedClockSkewSeconds(allowedSkewSeconds)
                .parseClaimsJws(token)
                .getBody();

        Object claimedTokenType = claims.getOrDefault(CLAIMS_TOKEN_TYPE, null);
        if (claimedTokenType == null) {
            throw new JwtException("Token is missing token type claim");
        } else {
            TokenType tokenType = EnumUtils.toEnum((String) claimedTokenType, TokenType.class);
            if (!requiredTokenType.equals(tokenType)) {
                throw new JwtException("Wanted token type " + requiredTokenType.toString() + " but got " + tokenType.toString());
            }
        }

        Object claimedChannel = claims.getOrDefault(CLAIMS_CHANNEL, null);
        if (!channelName.equals(claimedChannel)) {
            throw new JwtException("Token is not for channel " + channelName);
        }

        return claims;
    }

    private String generateToken(byte[] secretBytes, Claims claims, User user, long expiresInMillis) {
        DefaultJwsHeader jwsHeader = new DefaultJwsHeader();
        Date now = new Date();
        Date expiresAt = new Date();

        jwsHeader.setAlgorithm(signatureAlgorithm.getValue());
        jwsHeader.setType(Header.JWT_TYPE);


        expiresAt.setTime(now.getTime() + expiresInMillis);
        claims.setIssuedAt(now);
        claims.setSubject(user.getUsername());
        claims.setIssuer(Biliomi.class.getSimpleName());
        claims.setExpiration(expiresAt);
        claims.put(CLAIMS_CHANNEL, channelName);
        claims.put(CLAIMS_USER_DISPLAY_NAME, user.getDisplayName());

        JwtBuilder builder = Jwts.builder()
                .setHeader((Map<String, Object>) jwsHeader)
                .setClaims(claims)
                .signWith(signatureAlgorithm, secretBytes);

        return builder.compact();
    }
}
