package com.gtalent.tutor.configs;

import com.gtalent.tutor.models.User;
import com.gtalent.tutor.repositories.UserRepository;
import com.gtalent.tutor.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    //驗證流程的核心：基於 OncePerRequestFilter 自定義的一個過濾器 JwtAuthFilter
    //確保每個請求都會經過此過濾器一次

    private final JwtService jwtService;
    private final UserRepository userRepository;

    //依賴注入
    @Autowired
    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    //必須實作繼承類的抽象方法 == 過濾器執行的主要邏輯...
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 從http headers中獲取Authorization欄位-> Bearer ......
        String authHeader = request.getHeader("Authorization");
        // 檢查Authorization格式是否正確
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            //該次請求過濾器結束生命週期=>將請求繼續往下傳遞...
            filterChain.doFilter(request, response);
            return;
        }
        // 若開頭格式（Bearer...）正確，則擷取第七字元開始的字串（實際jwť）
        String jwtToken = authHeader.substring(7);
        String username = jwtService.getUsernameFromToken(jwtToken);
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // db裡面找到對應的username
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent() ) {
                //*** 若使用Spring Security (library)必須包含 授權 (Authorization)邏輯->「該用戶能做什麼？」***
                List<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                //該token並非jwt token，而是Spring Security內部使用的token(包含user & authorities)
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.get(), null, authorities);
                // 將 內部使用的token 投進 Spring Security 認證箱（SecurityContextHolder）
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        //該次請求過濾器結束生命週期=>將請求繼續往下傳遞...
        filterChain.doFilter(request, response);
    }
}
