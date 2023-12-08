package dat3.openai_demo.api;

import dat3.openai_demo.dtos.MealResponse;
import dat3.openai_demo.dtos.SaveMealResponse;
import dat3.openai_demo.entity.Meal;
import dat3.security.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }
    @GetMapping
    public List<MealResponse> getMeals(){
        return mealService.getMeals();
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveMeal(@RequestBody Meal meal) {
        try {
            mealService.saveMeal(meal);
            return ResponseEntity.ok("Meal saved successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving meal.");
        }
    }

    @PostMapping("/saveToUser")
    public ResponseEntity<String> saveMealToUser(@RequestBody SaveMealResponse mealToSave){
        try {
            mealService.saveMealToUser(mealToSave);

            return ResponseEntity.ok("Meal saved to user successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving meal to user.");
        }
    }

    @GetMapping("/usersSavedMeals/{username}")
    public List<MealResponse> getUsersSavedMeals(@PathVariable String username){
        return mealService.getUsersSavedMeals(username);
    }
}