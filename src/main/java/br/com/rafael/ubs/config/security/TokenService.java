package br.com.rafael.ubs.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.rafael.ubs.model.User;

@Service
public class TokenService {

	@Value("${ubs.jwt.expiration}")
	private String expiration;
	
	@Value("${ubs.jwt.secret}")
	private String secret;

	public String generateToken(Authentication authentication) {
		User userLogged = (User) authentication.getPrincipal();
		Date today = new Date();
		Date expirationDate = new Date(today.getTime() + Long.valueOf(expiration));
			
		return Jwts.builder()
				.setIssuer("API UBS")
				.setSubject(userLogged.getId().toString())
				.setIssuedAt(today)
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS256, secret)
				.compact();
	}
	
	public boolean isTokenValid(String token) {

		try {
			Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public Long getUserId(String token) {
		Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
		
		return Long.valueOf(claims.getSubject());
	}
}