package com.gtalent.tutor;

import com.gtalent.tutor.models.User;
import com.gtalent.tutor.requests.CreateUserRequest;
import com.gtalent.tutor.requests.UpdateUserRequest;
import com.gtalent.tutor.responses.CreateUserResponse;
import com.gtalent.tutor.responses.GetUserResponse;
import com.gtalent.tutor.responses.UpdateUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UserV1Controller {

//    由annotation 注入
//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    //由建構子 注入
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

    @GetMapping
    public ResponseEntity<List<GetUserResponse>> getAllUsers() {
        String sql = "select * from users";
        List<User> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
        return ResponseEntity.ok(users.stream().map(GetUserResponse::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable int id) {
        String sql = "select * from users where id=?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
            return ResponseEntity.ok(new GetUserResponse(user));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateUserResponse> updateUserById(@PathVariable int id, @RequestBody UpdateUserRequest request) {
        String sql = "update users set username=? where id=?";
        int rowsAffected = jdbcTemplate.update(sql, request.getUsername(), id);
        if (rowsAffected > 0 ) {
            UpdateUserResponse response = new UpdateUserResponse(request.getUsername());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable int id) {
        String sql = "delete from users where id=?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        if (rowsAffected > 0 ) {

            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
