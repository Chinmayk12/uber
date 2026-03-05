package com.chinuthon.project.uber.uber.Security;
import com.chinuthon.project.uber.uber.entities.User;
import com.chinuthon.project.uber.uber.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
/*
    used for authenticating and authorizing users based on JWT tokens in incoming HTTP requests.
    Here in this code we are first getting thr JWT token verifying it and once it is verified then
    later we are passing that token to the context holder so that it can be used further in the application. and
    then later we are proceed ing with the next filter chain.
*/
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    /*responsible for resolving exceptions and turning them into HTTP responses.
    Some exception happens anywhere (controller, service, filter, etc.)
    HandlerExceptionResolver is the guy who catches it and decides what response (like error JSON, status code)
    should be sent back to the client.
    */
    @Autowired // used to pass the exception from filter chain context to normal context (dispatcher servlet context)
    // so that its exception can be handled by global exception handler
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try{
            //final String requestTokenHeader = request.getHeader("Authorization");
            final String requestTokenHeader = request.getHeader("Authorization");

            if(requestTokenHeader==null || !requestTokenHeader.startsWith("Bearer")){
                filterChain.doFilter(request,response);
                return;
            }

            String token = requestTokenHeader.split("Bearer ")[1];
            Long userId  = jwtService.getUserIdFromToken(token);

            if(userId!=null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.getUserById(userId);

                // this is not logging it is used for authentication purpose & reconstructing authentication from JWT
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());

                // for maintaining details like remote address, session id etc.
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request,response);
        }
        catch (Exception exception){
            handlerExceptionResolver.resolveException(request,response,null,exception);
        }
    }
}
