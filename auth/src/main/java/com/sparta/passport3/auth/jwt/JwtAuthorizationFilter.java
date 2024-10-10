package com.sparta.passport3.auth.jwt;



import com.sparta.passport3.auth.security.UserDetailsServiceImpl;
import com.sparta.passport3.auth.type.Const;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil JwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtTokenUtil JwtTokenUtil, UserDetailsServiceImpl userDetailsService) {
        this.JwtTokenUtil = JwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        String token = req.getHeader(Const.ACCESS_TOKEN);

        if(null == token) {
            filterChain.doFilter(req, res);

            return;
        }

        if (StringUtils.hasText(token)) {
            // JWT 토큰 substring
            token = JwtTokenUtil.substringToken(token);
            log.info(token);

            // token 검증
            if (!JwtTokenUtil.validateToken(token)) {
                log.error("Token Error");
                return;
            }

            // access token이 맞는지 확인
            String category = JwtTokenUtil.getCategoryFromToken(token);
            if(!category.equals(Const.ACCESS_TOKEN)) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Claims info = JwtTokenUtil.getUserInfoFromToken(token);

            try {
                setAuthentication(info.getSubject()); // user이름 넘겨줌
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리 (securityContext Holder)
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
