  package dat3.security.service;

  import com.fasterxml.jackson.databind.ObjectMapper;
  import dat3.openai_demo.dtos.*;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.http.HttpStatus;
  import org.springframework.http.MediaType;
  import org.springframework.stereotype.Service;
  import org.springframework.web.reactive.function.BodyInserters;
  import org.springframework.web.reactive.function.client.WebClient;
  import org.springframework.web.reactive.function.client.WebClientResponseException;
  import org.springframework.web.server.ResponseStatusException;

  import java.net.URI;

  /*
  This code utilizes WebClient along with several other classes from org.springframework.web.reactive.
  However, the code is NOT reactive due to the use of the block() method, which bridges the reactive code (WebClient)
  to our imperative code (the way we have used Spring Boot up until now).

  You will not truly benefit from WebClient unless you need to make several external requests in parallel.
  Additionally, the WebClient API is very clean, so if you are familiar with HTTP, it should be easy to
  understand what's going on in this code.
  */

  @Service
  public class OpenAiService {

    public static final Logger logger = LoggerFactory.getLogger(OpenAiService.class);

    @Value("${app.api-key}")
    private String API_KEY;

    //See here for a decent explanation of the parameters send to the API via the requestBody
    //https://platform.openai.com/docs/api-reference/completions/create

    public final static String URL = "https://api.openai.com/v1/chat/completions";
    public final static String MODEL = "gpt-3.5-turbo";
    public final static double TEMPERATURE = 1;
    public final static int MAX_TOKENS = 3000;
    public final static double FREQUENCY_PENALTY = 0.0;
    public final static double PRESENCE_PENALTY = 0.0;
    public final static double TOP_P = 1.0;

    private WebClient client;

    public OpenAiService() {
      this.client = WebClient.create();
    }

    //Use this constructor for testing, to inject a mock client
    public OpenAiService(WebClient client) {
      this.client = client;
    }

    public MyResponse makeRequest(String userPrompt, String _systemMessage) {

      ChatCompletionRequest requestDto = new ChatCompletionRequest();
      requestDto.setModel(MODEL);
      requestDto.setTemperature(TEMPERATURE);
      requestDto.setMax_tokens(MAX_TOKENS);
      requestDto.setTop_p(TOP_P);
      requestDto.setFrequency_penalty(FREQUENCY_PENALTY);
      requestDto.setPresence_penalty(PRESENCE_PENALTY);
      requestDto.getMessages().add(new ChatCompletionRequest.Message("system", _systemMessage));
      requestDto.getMessages().add(new ChatCompletionRequest.Message("user", userPrompt));

      ObjectMapper mapper = new ObjectMapper();
      String json = "";
      String err = null;
      try {
        json = mapper.writeValueAsString(requestDto);
        System.out.println(json);
        ChatCompletionResponse response = client.post()
                .uri(new URI(URL))
                .header("Authorization", "Bearer " + API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .block();
        String responseMsg = response.getChoices().get(0).getMessage().getContent();
        int tokensUsed = response.getUsage().getTotal_tokens();
        int promptTokens = response.getUsage().getPrompt_tokens();
        System.out.println("Tokens used: " + tokensUsed);
        System.out.print(". Cost ($0.002 / 1K tokens) : $" + String.format("%6f", (tokensUsed * 0.002 / 1000)));
        System.out.println(". For 1$, this is the amount of similar requests you can make: " + Math.round(1 / (tokensUsed * 0.002 / 1000)));

        System.out.println("Prompt tokens used: " + promptTokens);
        System.out.print(". Cost ($0.001 / 1K tokens) : $" + String.format("%6f", (promptTokens * 0.001 / 1000)));
        System.out.println(". For 1$, this is the amount of similar requests you can make: " + Math.round(1 / (promptTokens * 0.001 / 1000)));

        System.out.println("For 1$, you can make a total of: " + Math.round(1 / ((promptTokens * 0.001 / 1000) + (tokensUsed * 0.002 / 1000))) + " prompts and responses");

        return new MyResponse(responseMsg);
      } catch (WebClientResponseException e) {
        int statusCode = e.getRawStatusCode();
        if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
          // 401 Unauthorized: Invalid API key or missing authentication
          logger.error("Error: Unauthorized - Invalid API key or missing authentication");
          err = "Unauthorized - Invalid API key or missing authentication";
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, err); }
        else {
        //This is how you can get the status code and message reported back by the remote API
        logger.error("Error response status code: " + e.getRawStatusCode());
        logger.error("Error response body: " + e.getResponseBodyAsString());
        logger.error("WebClientResponseException", e);
        err = "Internal Server Error, due to a failed request to external service. You could try again" +
                "( While you develop, make sure to consult the detailed error message on your backend)";
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err); }
      } catch (Exception e) {
        logger.error("Exception", e);
        err = "Internal Server Error - You could try again" +
                "( While you develop, make sure to consult the detailed error message on your backend)";
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
      }
    }

    public String generateUserPrompt(UserResponse user, UserPromptResponse userPromptResponse) {
      String baseUserPrompt = "I am a " + user.getAge() + " old "
              + user.getSex()
              + " and" + " my activity level is: " + user.getActivityLevel()
              + ". My goals are: " + user.getGoals() + "The following mealplan should include" + userPromptResponse.getMealChecklist();

      String userPromptNoAllergies = baseUserPrompt
              + ". I would prefer if some of the meals included: " + userPromptResponse.getPreferences();

      String userPromptNoPreferences = baseUserPrompt
              + ". The mealplan must not include: " + user.getAllergies();

      String userPromptAll = baseUserPrompt
              + ". The mealplan must not include: " + user.getAllergies()
              + ". I would prefer if some of the meals included: " + userPromptResponse.getPreferences();

      if (userPromptResponse.getPreferences().isEmpty() && user.getAllergies().isEmpty()) {
        return baseUserPrompt;
      } else if (userPromptResponse.getPreferences().isEmpty()) {
        return userPromptNoPreferences;
      } else if (user.getAllergies().isEmpty()) {
        return userPromptNoAllergies;
      }
      return userPromptAll;
    }
  }
