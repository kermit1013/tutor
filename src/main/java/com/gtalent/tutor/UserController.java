package com.gtalent.tutor;

import com.gtalent.tutor.models.User;
import com.gtalent.tutor.requests.CreateUserRequest;
import com.gtalent.tutor.requests.UpdateUserRequest;
import com.gtalent.tutor.responses.CreateUserResponse;
import com.gtalent.tutor.responses.GetUserResponse;
import com.gtalent.tutor.responses.UpdateUserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> mockUser = new HashMap<>();
    //模擬DB自增ID的效果
    private final AtomicInteger atomicInteger = new AtomicInteger();

    // 建構子: 每當UserController被讀取時要作的事情...
    public UserController() {
        // 1.假的資料庫
        mockUser.put(1, new User(1, "admin", "admin@gmail.com"));
        mockUser.put(2, new User(2, "user1", "user1@gmail.com"));
        mockUser.put(3, new User(3, "user2", "user2@gmail.com"));
        // 2.設定自增ID起始數字
        atomicInteger.set(4);
    }


    @GetMapping
    // response id, username 資安隱私考量
    // Map<key, map<key, val>>
    public ResponseEntity<List<GetUserResponse>> getAllUsers() {
        List<User> userList = new ArrayList<>(mockUser.values());
        List<GetUserResponse> responses = new ArrayList<>();
        for (User user : userList) {
            GetUserResponse response = new GetUserResponse(user.getId(),user.getUsername());
//            response.setId(user.getId());
//            response.setUsername(user.getUsername());
            responses.add(response);
        }

        return ResponseEntity.ok(responses) ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable int id) {
        User user = mockUser.get(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        GetUserResponse response = new GetUserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    //只能更新username
    public ResponseEntity<UpdateUserResponse> updateUserById(@PathVariable int id, @RequestBody UpdateUserRequest request) {
        User user = mockUser.get(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        System.out.println(request.getUsername());

        //update user w/ request body from postman
        user.setUsername(request.getUsername());

        UpdateUserResponse response = new UpdateUserResponse(user.getUsername());
        return ResponseEntity.ok(response);
    }
    @PostMapping
    // /createuser
    // /users
    //request body反序列化username,email
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        int newId = atomicInteger.getAndIncrement();
        User user = new User(newId, request.getUsername(), request.getEmail());
        mockUser.put(newId, user);
        //username
        CreateUserResponse response =  new CreateUserResponse(user.getUsername());
        //user序列化回傳json
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable int id) {
        User user = mockUser.get(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        mockUser.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<GetUserResponse>> searchUser(@RequestParam String keyword) {
        List<GetUserResponse> results = mockUser.values()
                .stream()
                //lambda起手示
                .filter(user -> //過濾出來符合條件的users [keyword:admin]
                user.getUsername().toLowerCase().contains(keyword.toLowerCase()))//結果為T的user[admin]
                //所有結果為T的user[admin] mapping(轉)成 GetUserResponse
                .map(GetUserResponse::new)
//                .map(GetUserResponse::new)
//                .map(user -> this.toGetUserResponse(user))
               .toList();
        return ResponseEntity.ok(results) ;
    }

    @GetMapping("/normal")
    public ResponseEntity<List<GetUserResponse>> getNormalUser() {
        List<GetUserResponse> results = mockUser.values()
                .stream()
                //lambda起手示
                .filter(user ->
                        !user.getUsername().toLowerCase().contains("admin"))
                .map(GetUserResponse::new)
                .toList();
        return ResponseEntity.ok(results) ;
    }

    private GetUserResponse toGetUserResponse (User user) {
        return new GetUserResponse(user.getId(),user.getUsername());
    }
}
