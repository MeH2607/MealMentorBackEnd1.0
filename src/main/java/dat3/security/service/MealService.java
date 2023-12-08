package dat3.security.service;

import dat3.openai_demo.dtos.MealResponse;
import dat3.openai_demo.dtos.SaveMealResponse;
import dat3.openai_demo.entity.Ingredient;
import dat3.openai_demo.entity.Meal;
import dat3.openai_demo.entity.User;
import dat3.openai_demo.repository.IngredientRepository;
import dat3.openai_demo.repository.MealRepository;
import dat3.openai_demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    private final IngredientRepository ingredientRepository;

    @Autowired
    public MealService(MealRepository mealRepository, UserRepository userRepository, IngredientRepository ingredientRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public void saveMeal(Meal meal) {
        mealRepository.save(meal);
    }

    public List<Meal> getAllMeals(){
        return mealRepository.findAll();
    }


    public void saveMealToUser(SaveMealResponse mealToSave) {
        User user = userRepository.findByUsername(mealToSave.getUsername());

        if (user == null) {
            throw new RuntimeException("User not found");

        } else {
            Meal meal = new Meal();
            meal.setMealType(mealToSave.getMealType());
            meal.setTitle(mealToSave.getTitle());
            meal.setInstructions(mealToSave.getInstructions());
            meal.setCalories(mealToSave.getCalories());
            meal.setCarbs(mealToSave.getCarbohydrates());
            meal.setFat(mealToSave.getFat());
            meal.setProtein(mealToSave.getProtein());
            meal.setTimeToMake(mealToSave.getTimeToMake());
            meal.setDescription(mealToSave.getDescription());
            for (String i : mealToSave.getIngredients()) {
                Ingredient ingredient = new Ingredient(i);
                ingredientRepository.save(ingredient);
                meal.addIngredient(ingredient);
            }
            meal.setUser(user);
            // Save the meal first
            mealRepository.save(meal);

            // Associate the meal with the user
            user.saveMeal(meal);




        }

    }
    public List<MealResponse> getMeals(){
        List<Meal> meals = mealRepository.findAll();
        return meals.stream().map(MealResponse::new).collect(Collectors.toList());
    }

    public List<MealResponse> getUsersSavedMeals(String username) {
        User user = userRepository.findByUsername(username);
        List<Meal> meals = user.getSavedMeals();
        return meals.stream().map(MealResponse::new).collect(Collectors.toList());
    }
}
