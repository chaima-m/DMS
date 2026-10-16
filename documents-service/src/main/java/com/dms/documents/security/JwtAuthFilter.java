/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  io.jsonwebtoken.Claims
 *  io.jsonwebtoken.JwtException
 *  io.jsonwebtoken.Jwts
 *  io.jsonwebtoken.security.Keys
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.security.authentication.UsernamePasswordAuthenticationToken
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.authority.SimpleGrantedAuthority
 *  org.springframework.security.core.context.SecurityContextHolder
 *  org.springframework.stereotype.Component
 *  org.springframework.web.filter.OncePerRequestFilter
 */
package com.dms.documents.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter
extends OncePerRequestFilter {
    @Value(value="${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = this.secret.getBytes();
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            return Keys.hmacShaKeyFor((byte[])padded);
        }
        return Keys.hmacShaKeyFor((byte[])keyBytes);
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        String token = header.substring(7);
        try {
            Claims claims = (Claims)Jwts.parser().verifyWith(this.getSigningKey()).build().parseSignedClaims((CharSequence)token).getPayload();
            String email = claims.getSubject();
            List<?> roles = (List<?>) claims.get("roles");
            List<SimpleGrantedAuthority> authorities = roles == null ? List.of() : roles.stream().filter(String.class::isInstance).map(String.class::cast).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken((Object)email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication((Authentication)auth);
        }
        catch (JwtException e) {
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
            return;
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
}
