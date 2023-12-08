package dat3.openai_demo.config;

import dat3.openai_demo.entity.Allergy;
import dat3.openai_demo.entity.Ingredient;
import dat3.openai_demo.entity.Meal;
import dat3.openai_demo.entity.User;
import dat3.openai_demo.repository.AllergyRepository;
import dat3.openai_demo.repository.IngredientRepository;
import dat3.openai_demo.repository.MealRepository;
import dat3.openai_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class DeveloperData implements ApplicationRunner {
    MealRepository mealRepository;
    UserRepository userRepository;
    AllergyRepository allergyRepository;
    IngredientRepository ingredientRepository;

    String passwordUsedByAll = "password";


    public DeveloperData(UserRepository userRepository, AllergyRepository allergyRepository, MealRepository mealRepository,
                         IngredientRepository ingredientRepository) {
        this.userRepository = userRepository;
        this.allergyRepository = allergyRepository;
        this.ingredientRepository = ingredientRepository;
        this.mealRepository = mealRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        setupUsers();
     //   setupTestMeals();
    }

    private void setupUsers() {

        Allergy peanutAllergy = new Allergy("Peanuts");
        Allergy fishAllergy = new Allergy("Fish");
        Allergy dairyAllergy = new Allergy("Dairy");

        allergyRepository.save(peanutAllergy);
        allergyRepository.save(fishAllergy);
        allergyRepository.save(dairyAllergy);

        // TODO list burde være den type som kun kan tage en unikk

        List<Allergy> allergies = new ArrayList<Allergy>();
        allergies.add(peanutAllergy);

        List<Allergy> allergies2 = new ArrayList<Allergy>();
        allergies2.add(peanutAllergy);
        allergies2.add(fishAllergy);

        User user1 = new User("testuser1", passwordUsedByAll, "testuser1@test.dk", 80,
                180, 80, 25, allergies, "Male", "Moderate", "Gain muscle");
        User user2 = new User("testuser2", passwordUsedByAll, "testuser2@test.dk", 90,
                200, 50, 0, allergies2, "Male", "No excercise", "Lose weight");
        User user3 = new User("testuser3", passwordUsedByAll, "testuser3@test.dk", 90,
                200, 50, 2, allergies2, "Male", "No excercise", "Lose weight");

        userRepository.saveAll(Arrays.asList(user1, user2, user3));

    }

// comment to restart
    private void setupTestMeals() {

        Meal meal1 = new Meal("breakfast", "tomato breakfast", "bake it", 100, 50, 200, 10);
        Meal meal2 = new Meal("lunch", "banana lunch", "eat it raw", 100, 50, 200, 10);

        Ingredient ingredient = new Ingredient("1 Tomato");
        Ingredient ingredient2 = new Ingredient("Bananas");
        Ingredient ingredient3 = new Ingredient("1 Tomato");
        ingredientRepository.save(ingredient);
        ingredientRepository.save(ingredient2);
        ingredientRepository.save(ingredient3);
        List<Ingredient> ingToMeal2 = new ArrayList<>();
        ingToMeal2.add(ingredient2);
        meal2.setIngredients(ingToMeal2);
        mealRepository.saveAll(Arrays.asList(meal1, meal2));

    }

}
