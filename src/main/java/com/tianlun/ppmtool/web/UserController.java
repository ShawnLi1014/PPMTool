package com.tianlun.ppmtool.web;

import com.tianlun.ppmtool.domain.User;
import com.tianlun.ppmtool.payload.JWTLoginSuccessResponse;
import com.tianlun.ppmtool.payload.LoginRequest;
import com.tianlun.ppmtool.security.JwtTokenProvider;
import com.tianlun.ppmtool.services.MapValidationErrorService;
import com.tianlun.ppmtool.services.UserService;
import com.tianlun.ppmtool.validator.UserValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.tianlun.ppmtool.security.SecurityConstants.TOKEN_PREFIX;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private MapValidationErrorService mapValidationErrorService;
    private UserService userService;
    private UserValidator userValidator;
    private JwtTokenProvider jwtTokenProvider;
    private AuthenticationManager authenticationManager;

    public UserController(MapValidationErrorService mapValidationErrorService,
                          UserService userService,
                          UserValidator userValidator,
                          JwtTokenProvider jwtTokenProvider,
                          AuthenticationManager authenticationManager) {
        this.mapValidationErrorService = mapValidationErrorService;
        this.userService = userService;
        this.userValidator = userValidator;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {
        // validate password match
        userValidator.validate(user, result);
        ResponseEntity<?> errorMaps = mapValidationErrorService.mapValidationService(result);
        if (errorMaps != null) {
            return errorMaps;
        }

        User newUser = userService.saveUser(user);
        return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        ResponseEntity<?> errorMaps = mapValidationErrorService.mapValidationService(result);
        if (errorMaps != null) {
            return errorMaps;
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTLoginSuccessResponse(true, jwt));

    }
}
