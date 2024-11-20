//package com.molta.common;
//
//import com.molta.common.JwtTokenProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
////import javax.servlet.http.HttpServletRequest;
////import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
//                                    jakarta.servlet.http.HttpServletResponse response,
//                                    jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
//
//        // 요청 헤더에서 JWT 토큰을 추출
//        String token = getJwtFromRequest(request);
//
//        // 토큰 유효성 검사
//        if (token != null && jwtTokenProvider.validateToken(token)) {
//            // 토큰에서 사용자 정보 추출
//            String userId = jwtTokenProvider.getUsernameFromToken(token);
//
//            // 사용자 인증 설정
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userId, null, null);
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            // SecurityContext에 사용자 설정
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        // 필터 체인 계속 진행
//        filterChain.doFilter(request, response);
//    }
//
//    // 요청 헤더에서 JWT 토큰을 추출하는 메서드
//    private String getJwtFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//}
