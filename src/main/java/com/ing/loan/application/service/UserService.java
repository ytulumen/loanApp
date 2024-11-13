package com.ing.loan.application.service;

import com.ing.loan.application.entity.Role;
import com.ing.loan.application.entity.User;
import com.ing.loan.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final RoleService roleService;

    @Lazy
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean isUserAdmin(String username) {
        UserDetails userDetails = loadUserByUsername(username);

        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            if (Objects.equals(authority.getAuthority(), "ROLE_ADMIN"))
                return true;
        }

        return false;
    }

    @Transactional
    public User createNewCustomer(String username, String password, long customerId) {
        return userRepository.save(
                new User(
                        0L,
                        username,
                        roleService.getCustomerRole(),
                        customerId,
                        bCryptPasswordEncoder.encode(password)
                )
        );
    }
}
