package dat3.security.service;

import dat3.openai_demo.dtos.UserRequest;
import dat3.openai_demo.dtos.UserResponse;
import dat3.openai_demo.entity.Allergy;
import dat3.openai_demo.entity.User;
import dat3.openai_demo.repository.AllergyRepository;
import dat3.openai_demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepository userRepository;
    private AllergyRepository allergyRepository;

    public UserService(UserRepository userRepository, AllergyRepository allergyRepository) {
        this.userRepository = userRepository;
        this.allergyRepository = allergyRepository;
    }

    public List<UserResponse> getUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(UserResponse::new).collect(Collectors.toList());
    }

    public UserResponse getUser(String username){
        User user = userRepository.findById(username).orElseThrow(()->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        return new UserResponse(user);
    }

    public User createUser(String username, int weight, int height, String sex, String activityLevel, String goals, List<Allergy> allergies) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setWeight(weight);
        newUser.setHeight(height);
        newUser.setSex(sex);
        newUser.setActivityLevel(activityLevel);
        newUser.setGoals(goals);
        newUser.setAllergies(allergies);
        return userRepository.save(newUser);
    }

    public UserResponse updateUser(UserRequest body, String username){
        User editUser = userRepository.findById(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        editUser.setWeight(body.getWeight());
        editUser.setHeight(body.getHeight());
        editUser.setAge(body.getAge());

        for (Allergy allergy : body.getAllergies()){
            Allergy newAllergy = allergyRepository.findByName(allergy.getName());
            if(newAllergy == null){
                newAllergy = allergyRepository.save(allergy);
                editUser.addAllergy(newAllergy);
            }
        }
        editUser.setActivityLevel(body.getActivityLevel());
        editUser.setGoals(body.getGoals());

        return new UserResponse(userRepository.save(editUser));
    }

    public void removeOneCreditFromUser(String username){

        System.out.println("Repo BEFORE, credits: " + userRepository.findByUsername(username).getCredits());

        User editUser = userRepository.findById(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        editUser.setCredits(editUser.getCredits() - 1);
        userRepository.save(editUser);

        System.out.println("Repo AFTER, credits: " + userRepository.findByUsername(username).getCredits());

    }

}
