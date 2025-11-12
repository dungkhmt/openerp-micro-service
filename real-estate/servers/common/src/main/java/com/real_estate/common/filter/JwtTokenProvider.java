package com.real_estate.common.filter;

import com.real_estate.common.models.AccountEntity;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenProvider {
	private String JWT_SECRET;

	private long JWT_EXPIRATION;

	// Tạo ra jwt từ thông tin user
	public String generateToken(AccountEntity accountEntity) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
		Map<String, String> claims = new HashMap<>();
		claims.put("accountId", accountEntity.getAccountId().toString());
		return Jwts.builder()
			.setSubject("jwts")
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(SignatureAlgorithm.HS256, JWT_SECRET)
			.compact();
	}

	// Lấy thông tin user từ jwt
	public String getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser()
			.setSigningKey(JWT_SECRET)
			.parseClaimsJws(token)
			.getBody();
		return (String) claims.get("accountId");
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
			return true;
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
		}
		return false;
	}
}