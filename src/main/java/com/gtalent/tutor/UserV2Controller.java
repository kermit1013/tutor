package com.gtalent.tutor;

import com.gtalent.tutor.models.User;
import com.gtalent.tutor.repositories.UserRepository;
import com.gtalent.tutor.requests.CreateUserRequest;
import com.gtalent.tutor.requests.UpdateUserRequest;
import com.gtalent.tutor.responses.GetUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v2/users")
@CrossOrigin("*")
public class UserV2Controller {

    private final UserRepository userRepository;

    @Autowired
    public UserV2Controller(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<GetUserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users.stream().map(GetUserResponse::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserResponse> getUsersById(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        GetUserResponse response = new GetUserResponse(user.get());
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<GetUserResponse> updateUsersById(@PathVariable int id, @RequestBody UpdateUserRequest request) {
        Optional<User> user = userRepository.findById(id);
        User updatedUser = user.get();
        updatedUser.setUsername(request.getUsername());
        User savedUser = userRepository.save(updatedUser);
        GetUserResponse response = new GetUserResponse(savedUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<GetUserResponse> createUsers(@RequestBody CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        User savedUser = userRepository.save(user);
        GetUserResponse response = new GetUserResponse(savedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsersById(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        userRepository.delete(user.get());
        return ResponseEntity.noContent().build();
    }
}
