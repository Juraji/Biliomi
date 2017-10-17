package nl.juraji.biliomi.model.core.security.tokens;

import nl.juraji.biliomi.utility.types.hibernatetypes.DateTimeISO8601Type;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Juraji on 6-9-2017.
 * Biliomi v3
 */
@Entity
public class AuthToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column
  @NotNull
  @Enumerated(EnumType.STRING)
  private TokenGroup tokenGroup;

  @Column
  @NotNull
  private String name;

  @Column(length = 1024)
  @NotNull
  private String token;

  @Column(length = 1024)
  private String refreshToken;

  @Column
  private String userId;

  @Column(length = 1024)
  private String secret;

  @Column
  @Type(type = DateTimeISO8601Type.TYPE)
  private DateTime issuedAt;

  @Column
  private Long timeToLive;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public TokenGroup getTokenGroup() {
    return tokenGroup;
  }

  public void setTokenGroup(TokenGroup tokenGroup) {
    this.tokenGroup = tokenGroup;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public DateTime getIssuedAt() {
    return issuedAt;
  }

  public void setIssuedAt(DateTime issuedAt) {
    this.issuedAt = issuedAt;
  }

  public Long getTimeToLive() {
    return timeToLive;
  }

  public void setTimeToLive(Long timeToLive) {
    this.timeToLive = timeToLive;
  }

  public DateTime getExpiryTime() {
    if (issuedAt != null && timeToLive != null && timeToLive > 0) {
      return issuedAt.withDurationAdded(timeToLive, 1);
    }

    return null;
  }
}
