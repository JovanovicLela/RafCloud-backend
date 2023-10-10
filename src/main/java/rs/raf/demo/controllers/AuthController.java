package rs.raf.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.Permission;
import rs.raf.demo.requests.LoginRequest;
import rs.raf.demo.responses.LoginResponse;
import rs.raf.demo.services.AuthService;
import rs.raf.demo.utils.JwtUtil;
//import rs.edu.raf.spring_project.model.AuthReq;
//import rs.edu.raf.spring_project.model.AuthRes;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    // password testing purpose only
    private final PasswordEncoder passwordEncoder;

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, AuthService authService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());

        }

        Permission permission = this.authService.findByEmail(loginRequest.getEmail()).getPermission();

        return ResponseEntity.ok(new LoginResponse(jwtUtil.generateToken(loginRequest.getEmail(), permission)));
    }

}
