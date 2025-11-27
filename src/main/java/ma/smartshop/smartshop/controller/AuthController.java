package ma.smartshop.smartshop.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.dto.auth.LoginRequest;
import ma.smartshop.smartshop.entity.User;
import ma.smartshop.smartshop.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                HttpSession session) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("USER_ROLE", user.getRole());

        return ResponseEntity.ok("Logged in as " + user.getRole());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out");
    }
}