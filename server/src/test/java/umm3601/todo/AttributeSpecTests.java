package umm3601.todo;

//import static org.junit.jupiter.api.Assertions.assertArrayEquals;
//import static org.junit.jupiter.api.Assertions.assertArrayEquals;
// import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
// import java.util.HashMap;
// import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
// import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

// import io.javalin.Javalin;
// import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
// import io.javalin.http.HttpStatus;
// import io.javalin.http.NotFoundResponse;
//import io.javalin.http.NotFoundResponse;
import umm3601.Main;
//import umm3601.todo.TodoDatabase;


@SuppressWarnings({"MagicNumber"})
public class AttributeSpecTests {
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
  public void canOrderByOwner() {
    // write parameters
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] {"owner"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    // run filter
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    Todo[] todos = todoArrayCaptor.getValue();
    // checks that all todo owners are in order
    for (int i = 0; i < todos.length - 1; i++) {
    assertTrue(todos[i].owner.compareTo(todos[i + 1].owner) <= 0, "Owner " + todos[i].owner
    + " should be <= owner " + todos[i + 1].owner + " at position " + i);
    }
  }

  @Test
  public void canOrderByStatus() {
    // define parameters
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] {"status"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    // run filter
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    Todo[] todos = todoArrayCaptor.getValue();
    // check that all statuses returned are in order
    for (int i = 0; i < todos.length - 1; i++) {
    assertTrue(todos[i].status.toString().compareTo(todos[i + 1].status.toString()) <= 0,
    "Status " + todos[i].status.toString() + " should be <= status "
    + todos[i + 1].status.toString() + " at position " + i);
    }
  }

 @Test
  public void canOrderByCategory() {
    // write parameters
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] {"category"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    // run filter
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    Todo[] todos = todoArrayCaptor.getValue();
    // check that all todo categories are in order
    for (int i = 0; i < todos.length - 1; i++) {
    assertTrue(todos[i].category.compareTo(todos[i + 1].category)
    <= 0, "Category " + todos[i].category + " should be <= category " + todos[i + 1].category + " at position " + i);
    }
  }
  @Test
  public void canOrderByBody() {
    // write parameters
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] {"body"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    // run filter
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    Todo[] todos = todoArrayCaptor.getValue();
    // checks that all todo bodies are in order
    for (int i = 0; i < todos.length - 1; i++) {
    assertTrue(todos[i].body.compareTo(todos[i + 1].body) <= 0,
    "Body " + todos[i].body + " should be <= body " + todos[i + 1].body + " at position " + i);
    }
  }
}
