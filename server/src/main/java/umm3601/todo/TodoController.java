package umm3601.todo;

import java.io.IOException;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import umm3601.Controller;


public class TodoController implements Controller {
  private TodoDatabase todoDatabase;

  public TodoController(TodoDatabase todoDatabase) {
    this.todoDatabase = todoDatabase;
  }

  public static TodoController buildTodoController(String todoDataFile) throws IOException {
    TodoController todoController = null;

    TodoDatabase todoDatabase = new TodoDatabase(todoDataFile);
    todoController = new TodoController(todoDatabase);

    return todoController;
  }

  public void getTodosByID(Context ctx) {
    String id = ctx.pathParam("id");
    Todo todos = todoDatabase.getTodosByID(id);
    if (todos != null) {
      ctx.json(todos);
      ctx.status(HttpStatus.OK);
    } else {
      throw new NotFoundResponse("No todo with id " + id + " was found.");
    }
  }

  public void getTodos(Context ctx) {
    Todo[] todos = todoDatabase.listTodos(ctx.queryParamMap());
    ctx.json(todos);
  }

  public void filterTodosByOwner(Context ctx) {
    String owner = ctx.queryParamMap().get("owner").get(0);
      Todo[] todos = todoDatabase.filterTodosByOwner(owner);
      if (todos.length != 0) {
        ctx.json(todos);
        ctx.status(HttpStatus.OK);
      } else {
        throw new NotFoundResponse("No todo with owner " + owner + " was found.");
      }
  }

@Override
  public void addRoutes(Javalin server) {
    server.get("api/todos/{id}", this::getTodosByID);
    server.get("/api/todos", this::getTodos);
  }
}
