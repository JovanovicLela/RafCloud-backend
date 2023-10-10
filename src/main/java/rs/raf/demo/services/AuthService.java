package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.User;
import rs.raf.demo.repositories.UserRepository;

import java.util.ArrayList;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User myUser = this.userRepository.findByEmail(email);
        if(myUser == null) {
            throw new UsernameNotFoundException("Email "+ email +" not found");
        }

        return new org.springframework.security.core.userdetails.User(email, myUser.getPassword(), new ArrayList<>());
    }

    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

}
