package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.example.demo.entity.base.Customer;
import com.example.demo.repository.CustomerRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Customer customer = customerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Customer not found"));

        return User.builder()
                .username(customer.getEmail())
                .password(customer.getPasswordHash())
                .authorities("ROLE_CUSTOMER")
                .build();
    }
}