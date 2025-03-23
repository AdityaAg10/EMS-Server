package com.jwt.implementation.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jwt.implementation.config.JwtGeneratorValidator;
import com.jwt.implementation.model.User;
import com.jwt.implementation.DTO.UserDTO;
import com.jwt.implementation.repository.UserRepository;
import com.jwt.implementation.service.DefaultUserService;

@RestController
public class RestAppController {

	@Autowired
	UserRepository userRepo;

	@Autowired
	AuthenticationManager authManager;

	@Autowired
	JwtGeneratorValidator jwtGenVal;

	@Autowired
	BCryptPasswordEncoder bcCryptPasswordEncoder;

	@Autowired
	DefaultUserService userService;

	@PostMapping("/registration")
	public ResponseEntity<Object> registerUser(@RequestBody UserDTO userDto) {
		User users =  userService.save(userDto);
		if (users.equals(null))
			return generateResponse("Not able to save user ", HttpStatus.BAD_REQUEST, userDto);
		else
			return generateResponse("User saved successfully : " + users.getId(), HttpStatus.OK, users);
	}

	@PostMapping("/genToken")
	public ResponseEntity<?> generateJwtToken(@RequestBody UserDTO userDto) {
		try {
			Authentication authentication = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(userDto.getUserName(), userDto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			String token = jwtGenVal.generateToken(authentication);
			return ResponseEntity.ok(Collections.singletonMap("token", token));
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Invalid username or password"));
		} catch (DisabledException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("error", "User account is disabled"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An unexpected error occurred"));
		}
	}


	@GetMapping("/welcomeAdmin")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String welcome() {
		return "WelcomeAdmin";
	}

	@GetMapping("/welcomeUser")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String welcomeUser() {
		return "WelcomeUSER";
	}



	public ResponseEntity<Object> generateResponse(String message, HttpStatus st, Object responseobj) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", message);
		map.put("Status", st.value());
		map.put("data", responseobj);

		return new ResponseEntity<Object>(map, st);
	}

}
