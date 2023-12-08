package dat3.openai_demo.api;

import dat3.openai_demo.dtos.UserRequest;
import dat3.openai_demo.dtos.UserResponse;
import dat3.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    public List<UserResponse> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("{username}")
    public UserResponse getUser(@PathVariable String username){
        return userService.getUser(username);
    }

    @PutMapping("{username}")
    public UserResponse updateUserSettings(@PathVariable String username, @RequestBody UserRequest userRequest){
        return userService.updateUser(userRequest, username);
    }
}
