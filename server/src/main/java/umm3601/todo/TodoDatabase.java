package umm3601.todo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.BadRequestResponse;

/**
 * A fake "database" of user info
 * <p>
 * Since we don't want to complicate this lab with a real database, we're going
 * to instead just read a bunch of user data from a specified JSON file, and
 * then provide various database-like methods that allow the `UserController` to
 * "query" the "database".
 */
public class TodoDatabase {

  private Todo[] allTodos;

  public TodoDatabase(String todoDataFile) throws IOException {
    // The `.getResourceAsStream` method searches for the given resource in
    // the classpath, and returns `null` if it isn't found. We want to throw
    // an IOException if the data file isn't found, so we need to check for
    // `null` ourselves, and throw an IOException if necessary.
    InputStream resourceAsStream = getClass().getResourceAsStream(todoDataFile);
    if (resourceAsStream == null) {
      throw new IOException("Could not find " + todoDataFile);
    }
    InputStreamReader reader = new InputStreamReader(resourceAsStream);
    // A Jackson JSON mapper knows how to parse JSON into sensible 'User'
    // objects.
    ObjectMapper objectMapper = new ObjectMapper();
    // Read our user data file into an array of User objects.
    allTodos = objectMapper.readValue(reader, Todo[].class);
  }

  public int size() {
    return allTodos.length;
  }


  /**
   * Get the single user specified by the given ID. Return `null` if there is no
   * user with that ID.get
   *
   * @param id the ID of the desired user
   * @return the user with the given ID, or null if there is no user with that ID
   */
  public Todo getTodosByID(String id) {
    return Arrays.stream(allTodos).filter(x -> x._id.equals(id)).findFirst().orElse(null);
  }



  /**
   * Get an array of all the users satisfying the queries in the params.
   *age
   * @param queryParams map of key-value pairs for the query
   * @return an array of all the users matching the given criteria
   */
  public Todo[] listTodos(Map<String, List<String>> queryParams) {
    Todo[] filteredTodos = allTodos;

    // Filter owner if defined
    if (queryParams.containsKey("owner")) {
      String ownerParam = queryParams.get("owner").get(0);
      try {
        String targetOwner = queryParams.get("owner").get(0); //int.parseInt(ownerParam);
        filteredTodos = filterTodosByOwner(targetOwner);
      } catch (NumberFormatException e) {
        throw new BadRequestResponse("Specified owner '" + ownerParam + "' can't be parsed to a String");
      }
    }
    // Filter category if defined
    if (queryParams.containsKey("category")) {
      String targetCategory = queryParams.get("category").get(0);
      filteredTodos = filterTodosByCategory(targetCategory);
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
    //filter if looking by attributes in orderBy Alphabetically
    if (queryParams.containsKey("orderBy")) { //if query parameters contains key orderBy block executes.
      String orderByParam = queryParams.get("orderBy").get(0); //retrieves the first element from list of strings associated with orderBy.

      if (orderByParam.equals("status")) { //if parameter is a status then
        filteredTodos = orderByStatus(); //filtered todos will call method orderByStatus().
      }

      if (orderByParam.equals("owner")) { //if parameter is a owner then
        filteredTodos = orderByOwner(); //filtered todos will call method orderByOwner().
      }

      if (orderByParam.equals("category")) { //if parameter is a category then
        filteredTodos = orderByCategory(); //filtered todos will call method orderByCategory().
      }

      if (orderByParam.equals("body")) { //if parameter is a body then
        filteredTodos = orderByBody(); //filtered todos will call method orderByBody().
      }

    }
    return filteredTodos;
  }

  /**
   * Get an array of all the todos with the correct status.
   *
   * @param todos     the list of todos to filter by status
   * @param targetStatus the desired status
   * @return an array of all the todos from the given list that have the target
   *         status
   */
    public Todo[] filterTodosByStatus(Boolean targetStatus) {
      return Arrays.stream(allTodos).filter(x -> x.status == targetStatus).toArray(Todo[]::new);
    }

  /**
   * Get an array of all the users having the target company.
   *
   * @param users         the list of users to filter by company
   * @param targetCompany the target company to look for
   * @return an array of all the users from the given list that have the target
   *         company
   */
  public Todo[] filterTodosByCategory(String targetCategory) {
    return Arrays.stream(allTodos).filter(x -> x.category.equals(targetCategory)).toArray(Todo[]::new);
  }

  // filter by owner
  public Todo[] filterTodosByOwner(String targetOwner) {
    return Arrays.stream(allTodos).filter(x -> x.owner.equals(targetOwner)).toArray(Todo[]::new);
  }

  public Todo[] filterTodosWithLimit(int limit) {
    limit = Math.min(allTodos.length, limit);
    Todo[] temp = new Todo[limit];
      for (int i = 0; i < limit; i++) {
        temp[i] = allTodos[i];
      }
    return temp;
  }

  public Todo[] filterTodosByBody(String targetBody) {
    return Arrays.stream(allTodos).filter(x -> x.body.contains(targetBody)).toArray(Todo[]::new);
  }

//Order by Status/Owner/Category/Body Alphabetically
public Todo[] orderByStatus() {
  List<Todo> todos = Arrays.asList(allTodos); //convert array to list
  todos.sort(Comparator.comparing(t -> t.status)); //sort by 'status' attribute
  return todos.toArray(new Todo[] {}); //convert list back to array and return
}

public Todo[] orderByOwner() {
  List<Todo> todos = Arrays.asList(allTodos); //convert array to list
  todos.sort(Comparator.comparing(t -> t.owner)); //sort by 'owner' attribute
  return todos.toArray(new Todo[] {}); //convert list back to array and return
}

public Todo[] orderByCategory() {
  List<Todo> todos = Arrays.asList(allTodos); //convert array to list
  todos.sort(Comparator.comparing(t -> t.category)); //sort by 'category' attribute
  return todos.toArray(new Todo[] {}); //convert list back to array and return
}

public Todo[] orderByBody() {
  List<Todo> todos = Arrays.asList(allTodos); //convert array to list
  todos.sort(Comparator.comparing(t -> t.body)); //sort by 'body' attribute
  return todos.toArray(new Todo[] {}); //convert list back to array and return
}


}
