package com.auth.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth.services.AppUserDetailsService;
import com.auth.utills.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
@Component
@RequiredArgsConstructor

public class JWTRequestFilter extends OncePerRequestFilter {
    private final AppUserDetailsService appUserDetailsService;
    private final JWTUtil jwtUtil;

    private static final List<String> PUBLIC_URLS = List.of("login", "/register", "/send-reset-otp", "/reset-password", 
    "/logout");


    @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

    String path = request.getServletPath();

    if (PUBLIC_URLS.contains(path)) {
        filterChain.doFilter(request, response);
        return;
    }

    String jwt = null;
    String email = null;

    // Check the Authorization header
    final String authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        jwt = authorizationHeader.substring(7);
    }

    // If JWT not found in header, check cookies
    if (jwt == null) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
    }

    // Validate the token and set the security context
    try {
        if (jwt != null && jwt.split("\\.").length == 3) { // ✅ Check token structure
            email = jwtUtil.extractEmail(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
    } catch (Exception ex) {
        System.out.println("JWT Filter Error: " + ex.getMessage()); // Optional: log this properly
        // You could also send a 401 response or let it pass depending on your requirement
    }

    filterChain.doFilter(request, response);
}


//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//             throws ServletException, IOException {
//              String path =   request.getServletPath();
//              if(PUBLIC_URLS.contains(path)){
//                 filterChain.doFilter(request, response);
//                 return;

//              }

//              String jwt = null;
//              String email = null;

//              // check the authorization header 

//              final String authorizationHeader = request.getHeader("Authorization");
//              if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
//                jwt= authorizationHeader.substring(7);
//              }
// // if it is not found  , check Cookie


//              if(jwt == null){
//                 Cookie [] cookies = request.getCookies();

//                 if(cookies != null){
//                     for(Cookie cookie : cookies){
//                         if("jwt".equals(cookie.getName())){
//                             jwt = cookie.getValue();
//                             break;
//                         }
//                     }
//                 }
//              }
// // validate the token and set the security context


// if(jwt != null){
//     email = jwtUtil.extractEmail(jwt);

//     if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
//      UserDetails userDetails =    appUserDetailsService.loadUserByUsername(email);
// if(jwtUtil.validateToken(email, userDetails)){
//     UsernamePasswordAuthenticationToken  passwordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails , null , userDetails.getAuthorities());
//     passwordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//     SecurityContextHolder.getContext().setAuthentication(passwordAuthenticationToken);
// }

//     }
// }
//         filterChain.doFilter(request, response);

//     }

}
