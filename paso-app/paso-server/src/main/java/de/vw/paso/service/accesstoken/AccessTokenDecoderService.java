package de.vw.paso.service.accesstoken;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenDecoderService implements JwtDecoder {

  private static final String ALGORITHM = "RSA";

  private final JwtParser jwtParser;

  AccessTokenDecoderService(@Value("${paso.access-token.key.public}") String publicKeyStr) {
    try {
      PublicKey publicKey = createPublicKey(publicKeyStr);
      jwtParser = Jwts.parser().verifyWith(publicKey).build();
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      throw new RuntimeException("Could not create public key", e);
    }
  }

  @Override
  public Jwt decode(String accessToken) {
    Claims claims = parseClaims(accessToken);

    return Jwt.withTokenValue(accessToken)
      .header("typ", "JWT")
      .subject(claims.getSubject())
      .issuedAt(claims.getIssuedAt().toInstant())
      .expiresAt(claims.getExpiration().toInstant())
      .claim("firstName", claims.get("firstName"))
      .claim("lastName", claims.get("lastName"))
      .claim("email", claims.get("email"))
      .build();
  }

  private PublicKey createPublicKey(String publicKeyStr)
    throws InvalidKeySpecException, NoSuchAlgorithmException {
    return KeyFactory.getInstance(ALGORITHM)
      .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr)));
  }

  private Claims parseClaims(String accessToken) {
    return jwtParser.parseSignedClaims(accessToken).getPayload();
  }

}
