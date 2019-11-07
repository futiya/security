package com.ibm.sba.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.ibm.sba.config.JwtConfig;
import com.ibm.sba.model.UserPassword;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Candise Li (jieqli@cn.ibm.com)
 * @create 2019-11-01 23:23
 */
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager manager;
    private final JwtConfig jwtConfig;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.manager = authenticationManager;
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtConfig.getUri(),"POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {

            UserPassword creds = new ObjectMapper().readValue(request.getInputStream(), UserPassword.class);

            // 2. Create auth object (contains credentials) which will be used by auth manager
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    creds.getUsername(), creds.getPassword(), Collections.emptyList());

            // 3. Authentication manager authenticate the user, and use UserDetialsServiceImpl::loadUserByUsername() method to load the user.
            return manager.authenticate(authToken);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
        }
        Long now = System.currentTimeMillis();
        String user = authResult.getName();
        List<String> roles = authResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String token = Jwts.builder()
                .setSubject(user)
                // Convert to list of strings.
                // This is important because it affects the way we get them back in the Gateway.
                .claim("authorities", roles)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration() * 1000))  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
                .compact();

        // Add token to header
//		response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("username", user);
        json.addProperty("role", roles.get(0).split("_")[1]);
        response.getWriter().write(json.toString());

        //SecurityContextHolder.getContext().setAuthentication(authResult);
//        this.rememberMeServices.loginSuccess(request, response, authResult);
//        if (this.eventPublisher != null) {
//            this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
//        }
//
//        this.successHandler.onAuthenticationSuccess(request, response, authResult);
    }
}
