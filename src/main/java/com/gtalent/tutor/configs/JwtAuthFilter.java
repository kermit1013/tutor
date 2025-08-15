package com.gtalent.tutor.configs;

import com.gtalent.tutor.models.User;
import com.gtalent.tutor.repositories.UserRepository;
import com.gtalent.tutor.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("authHeader" + authHeader);
        // 1. 檢查Authorization格式是否正確
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwtToken = authHeader.substring(7);
        String username = jwtService.getUsernameFromToken(jwtToken);
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                System.out.println("user.isPresent() : "+ user);
                Collection<? extends GrantedAuthority> authorities = getUserAuthorities(user.get());

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.get(), null, authorities);
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private Collection<? extends GrantedAuthority> getUserAuthorities(User user) {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
