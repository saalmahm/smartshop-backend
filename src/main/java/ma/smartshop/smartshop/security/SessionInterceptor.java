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

        // 1) Laisser passer les requêtes préflight CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        // 2) Laisser passer l'auth (login/logout)
        if (path.startsWith("/auth")) {
            return true;
        }

        // 3) Vérifier la présence d'une session + USER_ID
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

        // 4) Règles pour les endpoints admin
        if (path.startsWith("/admin")) {
            if (userRole != UserRole.ADMIN) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
                return false;
            }
        }

        // 5) Règles pour les endpoints client (/client, /me)
        if (path.startsWith("/client") || path.startsWith("/me")) {
            if (userRole != UserRole.CLIENT) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
                return false;
            }
        }

        return true;
    }
}