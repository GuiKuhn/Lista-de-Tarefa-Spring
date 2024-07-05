package br.com.todolist.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class FilterTaskAuth extends OncePerRequestFilter{

    

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        var servletPath = request.getServletPath();        
        if (servletPath.startsWith("/tasks/")) {
            // Pegar a autorização do header
            var autorization = request.getHeader("Authorization");
            // Verificar se a autorização é válida
            var authEncoded = autorization.substring("Basic".length()).trim();
            var authDecoded = new String(java.util.Base64.getDecoder().decode(authEncoded));
            String[] authArray = authDecoded.split(":");
            String username = authArray[0];
            String password = authArray[1];
            var user = userRepository.findByUsername(username);
            if(user == null) {
                response.sendError(401);
                return;
            }else{
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (!passwordVerify.verified) {
                    response.sendError(401);
                    return;
                    
                }else{
                    request.setAttribute("userId", user.getId());
                    filterChain.doFilter(request, response);
                }
            }
        }else{
            filterChain.doFilter(request, response);
        }

        
        

    }

   
    
}
