//package com.real_estate.post.filter;
//
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Slf4j
//@Service
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//	@Autowired
//	private JwtTokenProvider tokenProvider;
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request,
//	                                HttpServletResponse response, FilterChain filterChain)
//		throws ServletException, IOException {
//		System.out.println("di vao filter");
////		try {
////
////			String jwt = getJwtFromRequest(request);
////			System.out.println("trong filter: " +jwt);
////			if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
////				String userId = tokenProvider.getUserIdFromJWT(jwt);
////				AccountEntity accountEntity = accountService.loadUserByUsername(userId);
////				if(accountEntity != null) {
////					System.out.println("Id cua user: " + accountEntity.getUserId());
////					UsernamePasswordAuthenticationToken
////						authentication = new UsernamePasswordAuthenticationToken(accountEntity, accountEntity.getUserId(), accountEntity.getAuthorities());
////					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////					SecurityContextHolder.getContext().setAuthentication(authentication);
////				}
////			} else {
////				SecurityContextHolder.getContext().setAuthentication(null);
////			}
////		} catch (Exception ex) {
////			log.error("failed on set user authentication", ex);
////		}
////        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//		filterChain.doFilter(request, response);
//	}
//
//	private String getJwtFromRequest(HttpServletRequest request) {
//		String bearerToken = request.getHeader("Authorization");
//		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//			return bearerToken.substring(7);
//		}
//		return null;
//	}
//}
