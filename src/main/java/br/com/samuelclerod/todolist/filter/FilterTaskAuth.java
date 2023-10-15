package br.com.samuelclerod.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.samuelclerod.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getRequestURI().startsWith("/tasks")) {
      var authorization = request.getHeader("Authorization");
      var userAuth = authorization.substring("Basic".length()).trim();
      var userAuthDecoded = new String(Base64.getDecoder().decode(userAuth));
      var username = userAuthDecoded.split(":")[0];
      var password = userAuthDecoded.split(":")[1];

      var user = this.userRepository.findByUsername(username);
      if (user == null) {
        response.sendError(401);
        return;
      }

      var passwordIsValid = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified;
      if (!passwordIsValid) {
        response.sendError(401);
        return;
      }

      request.setAttribute("userId", user.getId());
    }

    filterChain.doFilter(request, response);
  }

}
