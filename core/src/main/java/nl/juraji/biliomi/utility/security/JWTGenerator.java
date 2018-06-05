package nl.juraji.biliomi.utility.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import nl.juraji.biliomi.Biliomi;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.rest.auth.TokenType;
import nl.juraji.biliomi.model.internal.rest.auth.TokenUserType;
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
     * Generate a new authorization JWT for the given user
     *
     * @param secretBytes The JWT secret as byte array
     * @param user        The user to encode the token for
     * @return The generated token as string
     */
    public String generateAuthorizationToken(@NotNull byte[] secretBytes, @NotNull User user) {
        JwtClaims claims = new JwtClaims();
        claims.setTokenType(TokenType.AUTH);

        if (user.isCaster()) {
            claims.setUserType(TokenUserType.CASTER);
        } else if (user.isModerator()) {
            claims.setUserType(TokenUserType.MODERATOR);
        } else {
            throw new IllegalStateException("User \"" + user.getDisplayName() + "\" is not a caster nor a moderator");
        }

        final Date expiryDate = new Date();
        expiryDate.setTime(expiryDate.getTime() + authTokenExpiry);
        return generateToken(secretBytes, claims, user, expiryDate);
    }

    /**
     * Generate a new refresh JWT for the given user
     *
     * @param secretBytes The JWT secret as byte array
     * @param user        The user to encode the token for
     * @return The generated token as string
     */
    public String generateRefreshToken(byte[] secretBytes, User user) {
        JwtClaims claims = new JwtClaims();
        claims.setTokenType(TokenType.REFRESH);

        final Date expiryDate = new Date();
        expiryDate.setTime(expiryDate.getTime() + refreshTokenExpiry);
        return generateToken(secretBytes, claims, user, expiryDate);
    }

    /**
     * Generate a new application token for the given application name.
     * Beware that this token has an (almost) infinite lifespan!
     *
     * @param secretBytes     The JWT secret as byte array
     * @param applicationName The application name to encode the token for
     * @return The generated token as string
     */
    public String generateApplicationToken(byte[] secretBytes, String applicationName) {
        JwtClaims claims = new JwtClaims();
        claims.setTokenType(TokenType.AUTH);
        claims.setUserType(TokenUserType.APPLICATION);
        User user = new User();
        user.setUsername(applicationName);
        user.setDisplayName(applicationName);

        // Set an insanely far off expiry date, to emulate a never expire
        final Date expiryDate = new Date(Long.MAX_VALUE);
        return generateToken(secretBytes, claims, user, expiryDate);
    }

    /**
     * Validate a JWT token against the secret, expiry date and token type
     *
     * @param secretBytes       The JWT secret as byte array
     * @param token             The token to validate
     * @param requiredTokenType The expected token type
     * @return The claims extracted from a valid token
     * @throws JwtException If the token secret does not match, the token is expired or the token type does not match
     */
    public JwtClaims validateToken(byte[] secretBytes, String token, TokenType requiredTokenType) throws JwtException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, signatureAlgorithm.getJcaName());

        Claims claims = Jwts.parser()
                .setSigningKey(secretKeySpec)
                .setAllowedClockSkewSeconds(allowedSkewSeconds)
                .parseClaimsJws(token)
                .getBody();

        final JwtClaims jwtClaims = new JwtClaims(claims);
        final TokenType claimedTokenType = jwtClaims.getTokenType();
        if (claimedTokenType == null) {
            throw new JwtException("Token is missing token type claim");
        } else {
            if (!requiredTokenType.equals(claimedTokenType)) {
                throw new JwtException("Wanted token type " + requiredTokenType.toString() + " but got " + claimedTokenType.toString());
            }
        }

        Object claimedChannel = jwtClaims.getChannel();
        if (!channelName.equals(claimedChannel)) {
            throw new JwtException("Token is not for channel " + channelName);
        }

        return new JwtClaims(claims);
    }

    private String generateToken(byte[] secretBytes, JwtClaims claims, User user, Date expiresAt) {
        DefaultJwsHeader jwsHeader = new DefaultJwsHeader();
        Date now = new Date();

        jwsHeader.setAlgorithm(signatureAlgorithm.getValue());
        jwsHeader.setType(Header.JWT_TYPE);

        claims.setIssuedAt(now);
        claims.setSubject(user.getUsername());
        claims.setIssuer(Biliomi.class.getSimpleName());
        claims.setExpiration(expiresAt);
        claims.setChannel(channelName);
        claims.setUserDisplayName(user.getDisplayName());

        return Jwts.builder()
                .setHeader((Map<String, Object>) jwsHeader)
                .setClaims(claims)
                .signWith(signatureAlgorithm, secretBytes)
                .compact();
    }
}
