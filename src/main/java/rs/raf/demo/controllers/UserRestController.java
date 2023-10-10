package rs.raf.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.Machine;
import rs.raf.demo.model.User;
import rs.raf.demo.services.UserService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRestController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody User user) {

        try {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            this.userService.save(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }



    @Transactional
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody User user) {

        try {

            User managedUser = userService.findByEmail(user.getEmail()); //old one

            List<Machine> machines = new ArrayList<>();
            if (managedUser != null) {
                user.setMachines(managedUser.getMachines());
            }

            if (managedUser == null) {
                return ResponseEntity.notFound().build();
            }
            if (user.getName() != null) {
                managedUser.setName(user.getName());
            }
            if (user.getLastname() != null) {
                managedUser.setLastname(user.getLastname());
            }
            if (user.getPassword() != null) {
                managedUser.setPassword(this.passwordEncoder.encode(user.getPassword()));
            }
            if (user.getPermission() != null) {
                managedUser.setPermission(user.getPermission());
            }
            if (user.getMachines() != null) {
                machines = user.getMachines();
                for (Machine m: machines) {
                    m.setUser(managedUser);
                }
                managedUser.setMachines(machines);
            }

            userService.save(managedUser);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAllUsers() {
        return userService.findAll();
    }


    @GetMapping(value = "/getUser/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(@PathVariable("email") String email) {
        User user = userService.findByEmail(email);
        if (user != null)
            return ResponseEntity.ok(userService.findByEmail(email));
        return ResponseEntity.badRequest().build();
    }

    @Transactional
    @DeleteMapping(value = "/delete/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable("email") String email) {
        long countDeleted = userService.deleteByEmail(email);
        if (countDeleted > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

}
