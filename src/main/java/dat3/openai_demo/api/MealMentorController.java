package dat3.openai_demo.api;

import dat3.openai_demo.dtos.MyResponse;
import dat3.openai_demo.dtos.UserPromptResponse;
import dat3.openai_demo.dtos.UserResponse;
import dat3.security.service.OpenAiService;
import dat3.security.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/mealPlanGenerator")
@CrossOrigin
public class MealMentorController {

  private OpenAiService openAiService;
  private UserService userService;
  String SYSTEM_MESSAGE = getSystemMessage();

  public MealMentorController(OpenAiService openAiService, UserService userService) {
    this.openAiService = openAiService;
    this.userService = userService;
  }


    @PostMapping()
      public MyResponse generatePrompt(@RequestBody UserPromptResponse userPromptResponse){

      if (userPromptResponse.getPreferences().contains("test")) {
          return getTestingResponse();
      } else {

          // Check if the user has credits
          if (userService.getUser(userPromptResponse.getUsername()).getCredits() <= 0) {

              return new MyResponse("{\n" +
                      "    \"Message\": \"USER HAS NO CREDITS\"\n" +
                      "}");

          } else {

              // Removing one credit from the user's credits
              String usernamePrompting = userPromptResponse.getUsername();
              UserResponse user = userService.getUser(usernamePrompting);
              userService.removeOneCreditFromUser(usernamePrompting);

              // Printing how many cr
              System.out.println("****** " + user.getUsername() + " is PROMPTING *******\n" +
                      "****** and he/she has got " +
                      userService.getUser(usernamePrompting).getCredits()
                      + " credits left *****");

              if (user.getWeight() == 0 || user.getHeight() == 0 || user.getAge() == 0 || user.getActivityLevel() == null){
                  throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                          "User has not filled in all the required information");
              }

              return openAiService.makeRequest(openAiService.generateUserPrompt(user, userPromptResponse), SYSTEM_MESSAGE);
          }

      }


  }

  private String getSystemMessage() {

      final String string =
              "You are an AI assistant whose sole function is to generate structured meal plans tailored to users' dietary requirements. " +
              "When prompted, you will produce a JSON object detailing a meal plan that includes meal titles, ingredients with quantities, " +
              "and approximate calorie count for the meal. "
              +
              "Ingredients should be presented as a simple string, for example 3 eggs or 2 tbsp salt or 200g flour"
              +
              "In the \"Description\" section, provide a short explanation for the nutritional value of the dish, " +
              "and why this specific meal have been picked for the user, " +
              "in relation the user's weight, activity level, preferences, allergies and the users goals."
              +
              "Be sure to mention that the dish do not contain what the user is allergic to."
              +
              "MealType value have to be either Breakfast, Lunch, Dinner or Snack, and is written with capital starting letter"
              +
              "......"
              +
              "BELOW IS AN EXAMPLE OF YOU SHOULD STRUCTURE THE RECIPE, AND NOT SOMETHING YOU SHOULD RETURN:"
              + "{\n" +
                      "  \"Meal1\": {\n" +
                      "    \"MealType\": \"type of meal\",\n" +
                      "    \"Title\": \"title of meal\",\n" +
                      "    \"Ingredients\": [\n" +
                      "      \"50 g xxx\",\n" +
                      "      \"150 g xxx\",\n" +
                      "      \"1 cup xxx\",\n" +
                      "      \"1/2 xxx\",\n" +
                      "      \"xxx and xxx to taste\"\n" +
                      "    ],\n" +
                      "    \"Calories\": \"xxx kcal\",\n" +
                      "    \"Protein\": \"xxx g\",\n" +
                      "    \"Carbohydrates\": \"xxx g\",\n" +
                      "    \"Fat\": \"xxx g\",\n" +
                      "    \"Time to make\": \"xxx min\",\n" +
                      "    \"Description\": \"This xxx is xxx.\",\n" +
                      "    \"Instructions\": \"1. xxx 2. xxx 3. xxx. 4. xxx. 5. xxx. 6. Serve.\"\n" +
                      "  },\n" +
                      "  \"Meal2\": {\n" +
                      "    \"MealType\": \"type of meal\",\n" +
                      "    \"Title\": \"title of meal\",\n" +
                      "    \"Ingredients\": [\n" +
                      "      \"50 g xxx\",\n" +
                      "      \"150 g xxx\",\n" +
                      "      \"1 cup xxx\",\n" +
                      "      \"1/2 xxx\",\n" +
                      "      \"xxx and xxx to taste\"\n" +
                      "    ],\n" +
                      "    \"Calories\": \"xxx kcal\",\n" +
                      "    \"Protein\": \"xxx g\",\n" +
                      "    \"Carbohydrates\": \"xxx g\",\n" +
                      "    \"Fat\": \"xxx g\",\n" +
                      "    \"Time to make\": \"xxx min\",\n" +
                      "    \"Description\": \"This xxx is xxx.\",\n" +
                      "    \"Instructions\": \"1. xxx 2. xxx 3. xxx. 4. xxx. 5. xxx. 6. Serve.\"\n" +
                      "  }\n" +
                      "  // Add more meals in similar structure if needed\n" +
                      "}"
              +

              "Each ingredient's quantity should be listed in metric units or amount." +
              "Include meals for Breakfast, Lunch, and/or Dinner, depending on the user input. " +
              "If the user's request lacks details for meal plan creation, " +
              "the API should respond with a JSON object asking for the necessary information. " +
              "The API's responses should consistently follow this format " +
              "and should contain only the JSON-formatted meal plan, not any regular text, and no followup questions.\n" +
              "The response should only contain meals for 1 day\n";

      return string;
  }

    private MyResponse getTestingResponse() {

      /* DONT TOUCH THIS */

        String jsonData = "[\n" +
        "  {\n" +
                "    \"MealType\": \"Breakfast\",\n" +
                "    \"Title\": \"Protein-Packed Omelette\",\n" +
                "    \"Ingredients\": [\n" +
                "      \"3 eggs\",\n" +
                "      \"50g lean ham\",\n" +
                "      \"1/4 onion, chopped\",\n" +
                "      \"1/4 bell pepper, chopped\",\n" +
                "      \"1/4 cup shredded cheese\",\n" +
                "      \"Salt and pepper to taste\"\n" +
                "    ],\n" +
                "    \"Calories\": \"397 kcal\",\n" +
                "    \"Protein\": \"33g\",\n" +
                "    \"Carbohydrates\": \"6g\",\n" +
                "    \"Fat\": \"28g\",\n" +
                "    \"Time to make\": \"15 min\",\n" +
                "    \"Description\": \"This protein-packed omelette is a nutritious and delicious way to start your day. Eggs are a complete source of protein, while lean ham adds more protein and depth of flavor. The onions and bell peppers provide essential vitamins and minerals, and the shredded cheese adds creaminess to the omelette. This meal is tailored to your goal of gaining muscle by providing a high amount of protein to support muscle growth.\",\n" +
                "    \"Instructions\": \"1. In a bowl, whisk the eggs with salt and pepper. \\n2. Heat a non-stick pan over medium heat and add the chopped onion and bell pepper. Cook for 2-3 minutes until they soften. \\n3. Add the lean ham to the pan and cook for another 2-3 minutes. \\n4. Pour the whisked eggs over the ingredients in the pan and let it cook until the edges start to set. \\n5. Sprinkle the shredded cheese on top and fold the omelette in half. Cook for another 1-2 minutes until the cheese melts. \\n6. Serve hot.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"MealType\": \"Lunch\",\n" +
                "    \"Title\": \"Grilled Chicken Salad\",\n" +
                "    \"Ingredients\": [\n" +
                "      \"150g grilled chicken breast\",\n" +
                "      \"2 cups mixed salad greens\",\n" +
                "      \"1/4 cucumber, sliced\",\n" +
                "      \"1/4 carrot, grated\",\n" +
                "      \"10 cherry tomatoes, halved\",\n" +
                "      \"2 tbsp balsamic vinaigrette\"\n" +
                "    ],\n" +
                "    \"Calories\": \"320 kcal\",\n" +
                "    \"Protein\": \"40g\",\n" +
                "    \"Carbohydrates\": \"12g\",\n" +
                "    \"Fat\": \"13g\",\n" +
                "    \"Time to make\": \"20 min\",\n" +
                "    \"Description\": \"This grilled chicken salad is a light and nutritious meal that provides a good balance of protein, carbohydrates, and healthy fats. Grilled chicken breast is a lean source of protein that supports muscle growth. The mixed salad greens, cucumber, carrot, and cherry tomatoes provide essential vitamins, minerals, and dietary fiber. The balsamic vinaigrette adds tanginess and flavor to the salad.\",\n" +
                "    \"Instructions\": \"1. Grill the chicken breast until cooked through, then slice it into strips. \\n2. In a large bowl, combine the mixed salad greens, sliced cucumber, grated carrot, and cherry tomatoes. \\n3. Add the grilled chicken strips to the bowl. \\n4. Drizzle the balsamic vinaigrette over the salad and toss to combine. \\n5. Serve chilled.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"MealType\": \"Dinner\",\n" +
                "    \"Title\": \"Baked Salmon with Quinoa and Roasted Vegetables\",\n" +
                "    \"Ingredients\": [\n" +
                "      \"150g salmon fillet\",\n" +
                "      \"1/2 cup cooked quinoa\",\n" +
                "      \"1 cup mixed roasted vegetables (bell peppers, zucchini, and broccoli)\",\n" +
                "      \"2 tbsp olive oil\",\n" +
                "      \"Salt and pepper to taste\"\n" +
                "    ],\n" +
                "    \"Calories\": \"470 kcal\",\n" +
                "    \"Protein\": \"35g\",\n" +
                "    \"Carbohydrates\": \"30g\",\n" +
                "    \"Fat\": \"25g\",\n" +
                "    \"Time to make\": \"30 min\",\n" +
                "    \"Description\": \"This baked salmon with quinoa and roasted vegetables is a nutrient-dense meal that is rich in protein, omega-3 fatty acids, and complex carbohydrates. Salmon is an excellent source of high-quality protein and omega-3 fatty acids, which have anti-inflammatory properties and support muscle recovery. Quinoa provides additional protein and is a good source of fiber and essential nutrients. The mixed roasted vegetables add color, flavor, and a variety of vitamins and minerals to the dish.\",\n" +
                "    \"Instructions\": \"1. Preheat the oven to 400°F (200°C). \\n2. Place the salmon fillet on a baking sheet lined with parchment paper. Drizzle with olive oil and season with salt and pepper. \\n3. In a separate baking dish, toss the mixed roasted vegetables with olive oil, salt, and pepper. \\n4. Place both the salmon and the vegetables in the preheated oven. Cook for about 20 minutes or until the salmon is cooked through and the vegetables are tender. \\n5. Remove from the oven and let the salmon rest for a few minutes. \\n6. Serve the baked salmon on a bed of cooked quinoa, alongside the roasted vegetables.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"MealType\": \"Snack\",\n" +
                "    \"Title\": \"Greek Yogurt with Berries and Almonds\",\n" +
                "    \"Ingredients\": [\n" +
                "      \"150g Greek yogurt\",\n" +
                "      \"1/4 cup mixed berries (blueberries, strawberries, raspberries)\",\n" +
                "      \"2 tbsp almonds, chopped\"\n" +
                "    ],\n" +
                "    \"Calories\": \"200 kcal\",\n" +
                "    \"Protein\": \"15g\",\n" +
                "    \"Carbohydrates\": \"15g\",\n" +
                "    \"Fat\": \"9g\",\n" +
                "    \"Time to make\": \"5 min\",\n" +
                "    \"Description\": \"This Greek yogurt with berries and almonds is a nutritious and satisfying snack. Greek yogurt is high in protein and calcium, which are essential for muscle growth and maintaining strong bones. Berries are rich in antioxidants and provide natural sweetness, while almonds add a crunchy texture and healthy fats. This snack is a great option for boosting protein intake and reducing hunger between meals.\",\n" +
                "    \"Instructions\": \"1. In a bowl, add the Greek yogurt. \\n2. Top the yogurt with mixed berries and chopped almonds. \\n3. Serve chilled.\"\n" +
                "  }\n" +
                "]";
        ;




        return new MyResponse(jsonData);

    }


}
