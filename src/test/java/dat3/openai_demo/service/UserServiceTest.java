package dat3.openai_demo.service;

import dat3.openai_demo.TestUtils;
import dat3.openai_demo.dtos.UserRequest;
import dat3.openai_demo.dtos.UserResponse;
import dat3.openai_demo.entity.User;
import dat3.openai_demo.repository.AllergyRepository;
import dat3.openai_demo.repository.UserRepository;
import dat3.security.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserServiceTest {
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AllergyRepository allergyRepository;

    private boolean dataInitialized = false;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, allergyRepository);
        if(!dataInitialized) {
            userRepository.deleteAll();
            TestUtils.setupTestUsers(userRepository, allergyRepository);
            dataInitialized = true;
        }
    }

    @Test
    void getUser() {
        UserResponse user = userService.getUser("testuser1");
        assertEquals(1, user.getAllergies().size());
    }

    @Test
    void getUsers(){
        List<UserResponse> users = userService.getUsers();
        assertEquals(2, users.size());
    }

    @Test
    void updateUser(){
        User user = userRepository.findByUsername("testuser1");
        assertEquals(180, user.getHeight());

        UserRequest userRequest = new UserRequest(user);
        userRequest.setHeight(300);

        UserResponse editUser = userService.updateUser(userRequest, "testuser1");
        assertEquals(300, editUser.getHeight());
    }

    @Test
    void removeTokenFromUser(){
        User user = userRepository.findByUsername("testuser1");
        assertEquals(25, user.getCredits());

        userService.removeOneCreditFromUser("testuser1");
        assertEquals(24, user.getCredits());
    }
}
