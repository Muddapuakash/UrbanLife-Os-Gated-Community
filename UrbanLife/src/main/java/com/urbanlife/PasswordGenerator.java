package com.urbanlife;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

        public static void main(String[] args) {

                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                System.out.println(encoder.encode("admin123"));

                System.out.println(
                                encoder.matches(
                                                "admin123",
                                                "$2a$10$cepEzcl9k3faC9q8hpzhNeGkcjzkOoSseiA9vl7PuyvfrbFscHDve"));
        }
}