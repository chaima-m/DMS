/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  io.jsonwebtoken.Claims
 *  io.jsonwebtoken.JwtException
 *  io.jsonwebtoken.Jwts
 *  io.jsonwebtoken.security.Keys
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Component
 */
package com.dms.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value(value="${jwt.secret}")
    private String secret;
    @Value(value="${jwt.expiration}")
    private long expiration;

    private SecretKey getKey() {
        byte[] k;
        byte[] b = this.secret.getBytes();
        byte[] byArray = k = b.length < 32 ? new byte[32] : b;
        if (b.length < 32) {
            System.arraycopy(b, 0, k, 0, b.length);
        }
        return Keys.hmacShaKeyFor((byte[])k);
    }

    public String generateToken(String email, List<String> roles, List<String> departments) {
        return Jwts.builder().subject(email).claim("roles", roles).claim("departments", departments).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + this.expiration)).signWith((Key)this.getKey()).compact();
    }

    public String extractEmail(String token) {
        return this.claims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        return (List)this.claims(token).get((Object)"roles");
    }

    public List<String> extractDepartments(String token) {
        Object d = this.claims(token).get((Object)"departments");
        return d instanceof List ? (List)d : List.of();
    }

    public boolean validateToken(String token) {
        try {
            this.claims(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims claims(String token) {
        return (Claims)Jwts.parser().verifyWith(this.getKey()).build().parseSignedClaims((CharSequence)token).getPayload();
    }
}
