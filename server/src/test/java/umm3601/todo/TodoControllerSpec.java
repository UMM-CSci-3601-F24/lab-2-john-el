package umm3601.todo;

//import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.javalin.Javalin;
//import io.javalin.http.BadRequestResponse;
//import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
//import io.javalin.http.NotFoundResponse;
import umm3601.Main;
//import umm3601.todo.TodoDatabase;

@SuppressWarnings({"MagicNumber"})
public class TodoControllerSpec {
  private TodoController todoController;
  private static TodoDatabase db;
  @Mock
  private Context ctx;
  @Captor
  private ArgumentCaptor<Todo[]> todoArrayCaptor;


  @BeforeEach
  public void setUp() throws IOException {
    MockitoAnnotations.openMocks(this);
    db = new TodoDatabase(Main.TODO_DATA_FILE);
    todoController = new TodoController(db);
  }

  @Test
  public void canBuildController() throws IOException {
    TodoController controller = TodoController.buildTodoController(Main.TODO_DATA_FILE);
    Javalin mockServer = Mockito.mock(Javalin.class);
    controller.addRoutes(mockServer);
    verify(mockServer, Mockito.atLeast(2)).get(any(), any());
  }

  @Test
  public void buildControllerFailsWithIllegalDbFile() {
    Assertions.assertThrows(IOException.class, () -> {
      TodoController.buildTodoController("this is not a legal file name");
    });
  }

  @Test
  public void canGetAllTodos() throws IOException {
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    assertEquals(db.size(), todoArrayCaptor.getValue().length);
  }

  @Test
  public void canGetTodo() throws IOException {
    String id = "58895985a22c04e761776d54";
    Todo todo = db.getTodosByID(id);
    when(ctx.pathParam("id")).thenReturn(id);
    todoController.getTodo(ctx);
     assertEquals("Blanche", todo.toString());
  }

  //STATUS TESTS
  @Test //based off of canGteUsersWithCompany
  public void canGetTodosByStatusTrue() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>(); //creates new hashMap for query
    queryParams.put("status", Arrays.asList(new String[] {"complete"})); //assigns query variables to Hashmap
    when(ctx.queryParamMap()).thenReturn(queryParams); //when ctx receives queryParamMap then return queryParams
    todoController.getTodos(ctx); //gets todos(with the parameters passed through the context)
    verify(ctx).json(todoArrayCaptor.capture()); //captures json elements that fit parameters
    for (Todo todo : todoArrayCaptor.getValue()) { //for each captured todo
      assertEquals(true, todo.status); //check that the status is the one we are looking for
    }
  }
  @Test
   public void canGetTodosByStatusFalse() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("status", Arrays.asList(new String[] {"incomplete"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    for (Todo todo : todoArrayCaptor.getValue()) {
      assertEquals(false, todo.status);
    }
  }

  //ID TESTS
  @Test //based off canGetUsersWithSpecifiedID
  public void canGetTodosByID() throws IOException {
    String id = "5889598555fbbad472586a56"; // Picked from a todo we know is in the database
    Todo todo = db.getTodosByID(id); // Get the todo associated with that ID.
    when(ctx.pathParam("id")).thenReturn(id);

    todoController.getTodo(ctx); //get todo that fits parameter
    verify(ctx).json(todo); // makes sure method ran
    verify(ctx).status(HttpStatus.OK); //makes sure status isn't bad
  }
  @Test
  public void respondsAppropriatelyToRequestForNonexistentId() throws IOException {
    when(ctx.pathParam("id")).thenReturn(null); //gives di witch is null
    //pulls error message to use in assertEquals
    Throwable exception = Assertions.assertThrows(NotFoundResponse.class, () -> {
      todoController.getTodo(ctx);
    });
    //makes sure error was thrown
    assertEquals("No todo with id " + null + " was found.", exception.getMessage());
  }

  //LIMIT TESTS
  @Test
  public void respondsAppropriatelyToRequestForBadLimit() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("limit", Arrays.asList(new String[] {"!@"})); //maps parameters and gives an unparsable limit
    when(ctx.queryParamMap()).thenReturn(queryParams);

    Throwable exception = Assertions.assertThrows(NumberFormatException.class, () -> { //pulls error for AssertEquals
      todoController.getTodos(ctx);
    });
    assertEquals("For input string: \"!@\"", exception.getMessage()); //checks message is right
  }
  @Test
  public void canGetTodosWithLimitAbove() throws IOException {
    //  will return length of db if limit is larger
    Map<String, List<String>> queryParams = new HashMap<>();
    //assumes db is < 100,000, would be need to changed if have a large database
    queryParams.put("limit", Arrays.asList(new String[] {"100000"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    //checks that the length is the smaller of the limit or db.length()
    assertEquals(db.size(), todoArrayCaptor.getValue().length);
  }
  @Test
  public void canGetTodosWithLimitWithin() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("limit", Arrays.asList(new String[] {"5"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());

    assertEquals(5, todoArrayCaptor.getValue().length);
  }
  @Test
  public void canGetTodosWithLimitZero() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("limit", Arrays.asList(new String[] {"0"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());

    assertEquals(0, todoArrayCaptor.getValue().length);
  }


  //TESTS FOR OWNER
  @Test
  public void canFilterTodosByOwner() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("owner", Arrays.asList(new String[] {"Blanche"}));
    when(ctx.queryParamMap()).thenReturn(queryParams); //creates query Params and passes them

    todoController.getTodos(ctx); //return todos

    verify(ctx).json(todoArrayCaptor.capture()); //record todos
    for (Todo todo : todoArrayCaptor.getValue()) {
      assertEquals("Blanche", todo.owner); //check that each todo returned has correct owner
    }
  }

  @Test
  public void respondsAppropriatelyToRequestForNonexistentOwner() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("owner", Arrays.asList(new String[] {"Bubba"})); //creates query parameters with a bad owner
    when(ctx.queryParamMap()).thenReturn(queryParams); //when has params pass, follow commands
    todoController.getTodos(ctx); //return todos
    verify(ctx).json(todoArrayCaptor.capture()); //capture todos
    assertEquals(todoArrayCaptor.getValue().length, 0); //check that length of todos returned are zero
  }

  //Tests for Body
  @Test
  public void canGetTodosByBody() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("contains", Arrays.asList(new String[] {"qui"}));
    //creates query params and passes them
    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);
    //returns todos and captures them
    verify(ctx).json(todoArrayCaptor.capture());
    for (Todo todo : todoArrayCaptor.getValue()) {
      //will contain or will return error
      assertTrue(todo.body.contains("qui"), "Body <" + todo.body + "> didn't contain 'qui'.");
    }
  }
  @Test
  public void canGetTodosByBodyNonExistent() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("contains", Arrays.asList(new String[] {"zzzzz"}));
    when(ctx.queryParamMap()).thenReturn(queryParams); //creates query params and passes them
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture()); //returns todos and captures them
    //when enter string that we know is not in the body the resulting todoCapture will be empty
    assertEquals(todoArrayCaptor.getValue().length, 0);
  }
}
