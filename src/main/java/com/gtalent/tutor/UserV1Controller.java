package com.gtalent.tutor;

import com.gtalent.tutor.models.User;
import com.gtalent.tutor.requests.CreateUserRequest;
import com.gtalent.tutor.responses.CreateUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserV1Controller {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserV1Controller(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {

        String sql = "insert into users (username, email) values (?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, request.getUsername(), request.getEmail());
        if (rowsAffected > 0 ) {
            CreateUserResponse response = new CreateUserResponse(request.getUsername());
            return  new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        //user序列化回傳json
        return ResponseEntity.badRequest().build();
    }
}
