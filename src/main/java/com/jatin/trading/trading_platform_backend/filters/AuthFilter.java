package com.jatin.trading.trading_platform_backend.filters;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;
import com.jatin.trading.trading_platform_backend.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class AuthFilter extends GenericFilterBean {
  
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String authHeader = httpRequest.getHeader("Authorization");
    if (authHeader != null) {
      String[] authHeaderArr = authHeader.split("Bearer ");
      if (authHeaderArr.length > 1 && authHeaderArr[1] != null) {
        String token = authHeaderArr[1];
        try {
          Claims claims = Jwts.parser().setSigningKey(Constants.API_SECRET_KEY)
            .parseClaimsJws(token).getBody();
          // Adding user id to the request object
          httpRequest.setAttribute("userId", Integer.parseInt(claims.get("userId").toString()));
        } catch (Exception e) {
          System.out.println("Token " + e.getMessage());
          httpResponse.sendError(HttpStatus.FORBIDDEN.value(), "invalid/expired token");
          return;
        }
      } else {
        httpResponse.sendError(HttpStatus.FORBIDDEN.value(), "Authorization token must be Bearer [token]");
        return;
      }
    } else {
      httpResponse.sendError(HttpStatus.FORBIDDEN.value(), "Authorization token must be provided");
      return;
    }
    // to continue the processing
    chain.doFilter(request, response);
  }

}
