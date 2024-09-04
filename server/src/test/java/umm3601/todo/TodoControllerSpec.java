package umm3601.todo;

// import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
//import io.javalin.http.NotFoundResponse;
import umm3601.Main;
//import umm3601.user.UserDatabase;

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
    todoController.getTodosByID(ctx);
     assertEquals("Blanche", todo.toString());
  }

  @Test
   public void canGetTodosByStatusFalse() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("status", Arrays.asList(new String[] {"false"})); //risky gamble here converting bool to string
    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    verify(ctx).json(todoArrayCaptor.capture());
    for (Todo todo : todoArrayCaptor.getValue()) {
      assertEquals(false, todo.status);
    }
  }

  // @Test
  // public void respondsAppropriatelyToRequestForNonexistentOwner() throws IOException {
  //   when(ctx.pathParam("owner")).thenReturn(null);
  //   Throwable exception = Assertions.assertThrows(NotFoundResponse.class, () -> {
  //     todoController.getTodos(ctx);
  //   });
  //   assertEquals("No todo with owner " + null + " was found.", exception.getMessage());
  // }

  @Test //based off canGetUsersWithSpecifiedID
  public void canGetTodosByID() throws IOException {
    // A specific user ID known to be in the "database".
    String id = "5889598555fbbad472586a56";
    // Get the user associated with that ID.
    Todo todo = db.getTodosByID(id);

    when(ctx.pathParam("id")).thenReturn(id);

    todoController.getTodosByID(ctx);
    verify(ctx).json(todo);
    verify(ctx).status(HttpStatus.OK);
  }

  @Test
  public void respondsAppropriatelyToRequestForNonexistentId() throws IOException {
    when(ctx.pathParam("id")).thenReturn(null);
    Throwable exception = Assertions.assertThrows(NotFoundResponse.class, () -> {
      todoController.getTodosByID(ctx);
    });
    assertEquals("No todo with id " + null + " was found.", exception.getMessage());
  }


  //TESTS FOR OWNER
  @Test
  public void canFilterTodosByOwner() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("owner", Arrays.asList(new String[] {"Blanche"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.filterTodosByOwner(ctx);

    verify(ctx).json(todoArrayCaptor.capture());
    for (Todo todo : todoArrayCaptor.getValue()) {
      assertEquals("Blanche", todo.owner);
    }
  }

  @Test
  public void respondsAppropriatelyToRequestForNonexistentOwner() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("owner", Arrays.asList(new String[] {"Bubba"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    Throwable exception = Assertions.assertThrows(NotFoundResponse.class, () -> {
      todoController.filterTodosByOwner(ctx);
    });
    assertEquals("No todo with owner " + "Bubba" + " was found.", exception.getMessage());
  }
  @Test
  public void canGetTodosByBody() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("contains", Arrays.asList(new String[] {"qui"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    for (Todo todo : todoArrayCaptor.getValue()) {
      assertTrue(todo.body.contains("qui"), "Body <" + todo.body + "> didn't contain 'qui'.");
    }
  }
}
