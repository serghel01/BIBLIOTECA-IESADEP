
package com.iesa.dep.controllers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.iesa.dep.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // login admin: username/password from env ADMIN_USER / ADMIN_PASS
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body){
        String user = System.getenv("ADMIN_USER");
        String pass = System.getenv("ADMIN_PASS");
        if(user==null) user="admin";
        if(pass==null) pass="supersecret";
        if(body==null || !user.equals(body.get("username")) || !pass.equals(body.get("password"))){
            return ResponseEntity.status(401).body(Map.of("error","Credenciales inválidas"));
        }
        String token = JwtUtil.generateToken(user);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader(value="Authorization", required=false) String auth){
        if(auth==null || !auth.startsWith("Bearer ")) return ResponseEntity.status(401).body(Map.of("error","No autorizado"));
        String token = auth.substring(7);
        String user = JwtUtil.validateTokenAndGetUser(token);
        if(user==null) return ResponseEntity.status(401).body(Map.of("error","Token inválido"));
        return ResponseEntity.ok(Map.of("user", user));
    }
}
