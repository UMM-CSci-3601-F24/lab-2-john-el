package umm3601.todo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.BadRequestResponse;

public class TodoDatabase {

  private Todo[] allTodos;

  public TodoDatabase(String todoDataFile) throws IOException {
    //makes sure JSON file is there
    InputStream resourceAsStream = getClass().getResourceAsStream(todoDataFile);
    //throws error if todoDataFile is not found
    if (resourceAsStream == null) {
      throw new IOException("Could not find " + todoDataFile);
    }
    // constructs reader to parse JSON file objects
    InputStreamReader reader = new InputStreamReader(resourceAsStream);
    ObjectMapper objectMapper = new ObjectMapper();
    // Read our todo data file into an array of Todo objects.
    allTodos = objectMapper.readValue(reader, Todo[].class);
  }

  public int size() {
    return allTodos.length;
  }

  /**
   * Gets the single Todo associated with an ID or returns Null.
   *
   * @param id the ID of the desired todo
   * @return the todo with the given ID, or null if there is no todo with that ID
   */
  public Todo getTodosByID(String id) {
    return Arrays.stream(allTodos).filter(x -> x._id.equals(id)).findFirst().orElse(null);
  }

  /**
   * Get an array of all the todos satisfying the queries in the params.
   * @param queryParams map of key-value pairs for the query
   * @return an array of all the todos matching the given criteria
   */
  public Todo[] listTodos(Map<String, List<String>> queryParams) {
    Todo[] filteredTodos = allTodos;

    // Filter owner if defined
    if (queryParams.containsKey("owner")) {
      String ownerParam = queryParams.get("owner").get(0); //grabs name of owner
      try {
        String targetOwner = queryParams.get("owner").get(0);
        filteredTodos = filterTodosByOwner(targetOwner);
      } catch (NumberFormatException e) { //catches if name is not parsable(should never happen)
        throw new BadRequestResponse("Specified owner '" + ownerParam + "' can't be parsed to a String");
      }
    }
    // Filter category if defined
    if (queryParams.containsKey("category")) {
      String targetCategory = queryParams.get("category").get(0); //gets desired category
      filteredTodos = filterTodosByCategory(targetCategory); //runs associated filtering function
    }
    //filter status if defined
    if (queryParams.containsKey("status")) {
      boolean targetStatus = queryParams.get("status").get(0).equals("complete");
      filteredTodos = filterTodosByStatus(targetStatus);
    }
    // filter if limit is defined
    if (queryParams.containsKey("limit")) {
      int limit = Integer.valueOf(queryParams.get("limit").get(0));
      filteredTodos = filterTodosWithLimit(limit);
    }
    //filter if looking in body
    if (queryParams.containsKey("contains")) {
      String targetBody = queryParams.get("contains").get(0);
        filteredTodos = filterTodosByBody(targetBody);
    }
    return filteredTodos;
  }

  /**
   * Get an array of all the todos with the correct status.
   *
   * @param todos     the list of todos to filter by status
   * @param targetStatus the desired status
   * @return array of todos with target status
   */
    public Todo[] filterTodosByStatus(Boolean targetStatus) {
      return Arrays.stream(allTodos).filter(x -> x.status == targetStatus).toArray(Todo[]::new);
    }

  /**
   * Get an array of all the users having the target category.
   *
   * @param todos         the list of todos to filter by company
   * @param targetCategory the target company to look for
   * @return array of todos with target category
   */
  public Todo[] filterTodosByCategory(String targetCategory) {
    return Arrays.stream(allTodos).filter(x -> x.category.equals(targetCategory)).toArray(Todo[]::new);
  }

  // filter by owner
  // @param todos --to filter
  //@param targetOwner -- owner to look for
  // @return -- array of todos with target owner
  public Todo[] filterTodosByOwner(String targetOwner) {
    return Arrays.stream(allTodos).filter(x -> x.owner.equals(targetOwner)).toArray(Todo[]::new);
  }

  /*
   * filter with limit
   * @param todos list of todos
   * @param limit -- amount of todos to show
   * @return --array of length limit to return
   */
  public Todo[] filterTodosWithLimit(int limit) {

    if (allTodos.length <= limit) { //if limit is greater than allTodos.length then return allTodos
      return allTodos;
    } else { //if limit is smaller than copy over limit number of items
      Todo[] temp = new Todo[limit];
      for (int i = 0; i < limit; i++) {
        temp[i] = allTodos[i];
      }
      return temp;
    }
  }

  /*]
   * filter by body
   * @param todos list of todos
   * @param targetBody --string that body will contain
   * @return list of todos with intended string in the body
   */
  public Todo[] filterTodosByBody(String targetBody) {
    return Arrays.stream(allTodos).filter(x -> x.body.contains(targetBody)).toArray(Todo[]::new);
  }
}
