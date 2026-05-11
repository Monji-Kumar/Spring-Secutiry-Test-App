package com.example.demo4.SecurityApp;


import com.example.demo4.SecurityApp.entities.User;
import com.example.demo4.SecurityApp.entities.enums.Role;
import com.example.demo4.SecurityApp.services.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
class SecurityAppApplicationTests {

	@Autowired
	private JWTService jwtService;

	@Test
	void contextLoads() {

		User user = new User(1L, "manoj@gmail.com", "1234", "Manoj Kumar");

		String token = jwtService.generateToken(user);

		System.out.println(token);

        Long id = jwtService.getUserIdFromToken(token);

		System.out.println(id);

	}

}
