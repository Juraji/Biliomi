package nl.juraji.biliomi.utility.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import nl.juraji.biliomi.model.internal.rest.auth.TokenType;
import nl.juraji.biliomi.model.internal.rest.auth.TokenUserType;
import nl.juraji.biliomi.utility.calculate.EnumUtils;

/**
 * Created by Juraji on 4-6-2018.
 * Biliomi
 */
public class JwtClaims extends DefaultClaims {

    private static final String CHANNEL = "chn";
    private static final String USER_DISPLAY_NAME = "usr";
    private static final String USER_TYPE = "utp";
    private static final String TOKEN_TYPE = "ttp";

    public JwtClaims() {
    }

    public JwtClaims(Claims claims) {
        super(claims);
    }

    public String getChannel() {
        return super.get(CHANNEL, String.class);
    }

    public void setChannel(String channel) {
        super.put(CHANNEL, channel);
    }

    public String getUserDisplayName() {
        return super.get(USER_DISPLAY_NAME, String.class);
    }

    public void setUserDisplayName(String userDisplayName) {
        super.put(USER_DISPLAY_NAME, userDisplayName);
    }

    public TokenUserType getUserType() {
        return this.getEnum(USER_TYPE, TokenUserType.class);
    }

    public void setUserType(TokenUserType userType) {
        super.put(USER_TYPE, userType);
    }

    public TokenType getTokenType() {
        return this.getEnum(TOKEN_TYPE, TokenType.class);
    }

    public void setTokenType(TokenType tokenType) {
        super.put(TOKEN_TYPE, tokenType);
    }

    private <E extends Enum<E>> E getEnum(String key, Class<E> enumClass) {
        final String o = super.get(key, String.class);
        return EnumUtils.toEnum(o, enumClass);
    }
}
