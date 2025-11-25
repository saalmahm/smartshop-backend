package ma.smartshop.smartshop.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.smartshop.smartshop.enums.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();

        if (path.startsWith("/auth")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Non authentifié");
            return false;
        }

        Object roleAttr = session.getAttribute("USER_ROLE");
        if (roleAttr == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Non authentifié");
            return false;
        }

        UserRole userRole;
        if (roleAttr instanceof UserRole) {
            userRole = (UserRole) roleAttr;
        } else {
            userRole = UserRole.valueOf(roleAttr.toString());
        }

        if (path.startsWith("/admin")) {
            if (userRole != UserRole.ADMIN) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
                return false;
            }
        }

        if (path.startsWith("/client") || path.startsWith("/me")) {
            if (userRole != UserRole.CLIENT) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
                return false;
            }
        }

        return true;
    }
}