package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.User;
import rs.raf.demo.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user) {
        return this.userRepository.save(user);
    }

    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return this.userRepository.findById(id);
    }

    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public Long deleteByEmail(String email) {
        return this.userRepository.deleteByEmail(email);
    }


}
