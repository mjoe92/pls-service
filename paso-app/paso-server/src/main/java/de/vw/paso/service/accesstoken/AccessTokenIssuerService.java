package de.vw.paso.service.accesstoken;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import de.vw.paso.user.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenIssuerService {

  private static final String ALGORITHM = "RSA";

  private final int validity;
  private final PrivateKey privateKey;

  AccessTokenIssuerService(@Value("${paso.access-token.key.private}") String privateKeyStr,
    @Value("${paso.access-token.validity}") int validity) {
    this.validity = validity;
    try {
      privateKey = createPrivateKey(privateKeyStr);
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      throw new RuntimeException("Could not create private and public key", e);
    }
  }

  public final String issueAccessToken(User user) {
    if (user.getId() == null || user.getId().isBlank()) {
      throw new IllegalArgumentException("User id not set");
    }

    return Jwts.builder()
      .header().add("typ", "JWT").and()
      .subject(user.getId())
      .issuedAt(Date.from(Instant.now()))
      .expiration(Date.from(Instant.now().plus(Duration.ofMinutes(validity))))
      .claim("userId", user.getId())
      .claim("firstName", user.getFirstName())
      .claim("lastName", user.getLastName())
      .claim("email", user.getEmail())
      .signWith(privateKey, SIG.RS256)
      .compact();
  }

  private PrivateKey createPrivateKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
    KeyFactory instance = KeyFactory.getInstance(ALGORITHM);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
    return instance.generatePrivate(keySpec);
  }

}
