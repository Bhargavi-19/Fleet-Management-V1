package com.example.demo.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.example.demo.entity.base.Customer;
import com.example.demo.entity.base.Staff;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.StaffRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // First check Staff
        Optional<Staff> staff = staffRepository.findByEmail(email);

        if (staff.isPresent()) {
            return User.builder()
                    .username(staff.get().getEmail())
                    .password(staff.get().getPasswordHash())
                    .authorities("ROLE_STAFF")
                    .build();
        }

        // Then check Customer
        Optional<Customer> customer = customerRepository.findByEmail(email);

        if (customer.isPresent()) {
            return User.builder()
                    .username(customer.get().getEmail())
                    .password(customer.get().getPasswordHash())
                    .authorities("ROLE_CUSTOMER")
                    .build();
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}