package trungtamngoaingu.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungtamngoaingu.hcmute.entity.UserAccount;
import trungtamngoaingu.hcmute.repository.UserAccountRepository;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserAccountRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thiếu username hoặc password");
        }

        Optional<UserAccount> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            UserAccount user = userOpt.get();
            // So sánh Password đơn giản (ko dùng BCrypt Password encoder theo yêu cầu đồ án)
            if (user.getPasswordHash().equals(password)) {
                // Trả về JSON chứa thông tin user + Role để Frontend phân quyền
                return ResponseEntity.ok(user);
            }
        }

        // Nếu ko khớp User/Pass
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản hoặc mật khẩu không chính xác!");
    }
}
