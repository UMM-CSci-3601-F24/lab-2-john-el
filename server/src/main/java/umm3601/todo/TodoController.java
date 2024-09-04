package umm3601.todo;

import java.io.IOException;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import umm3601.Controller;
// import umm3601.todo.TodoDatabase;


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





// public void getTodosByStatus(Context ctx) {
//   Boolean status = null;
//   if(ctx.pathParam("status").equals("incomplete")){
//     status = false;
//   } else if (ctx.pathParam("status").equals("complete")){
//       status = true;
//   }
//   Todo[] todos = todoDatabase.filterTodosByStatus(status);
//   if (todos != null) {
//     ctx.json(todos);
//     ctx.status(HttpStatus.OK);
//   } else {
//     throw new NotFoundResponse("No todo with status " + status + "was found.");
//   }
// }

// public void filterTodosByOwner(Context ctx) {
//     String owner = ctx.pathParam("owner");
//     Todo[] todos = todoDatabase.filterTodosByOwner(owner);
//     if (todos != null) {
//       ctx.json(todos);
//       ctx.status(HttpStatus.OK);
//     } else {
//       throw new NotFoundResponse("No todo with owner " + owner + " was found.");
//     }
// }

@Override
  public void addRoutes(Javalin server) {
    server.get("api/todos/{id}", this::getTodosByID);
    server.get("/api/todos", this::getTodos);
    //server.get("/api/todos?owner=Blanche", this::filterTodosByOwner);
    //server.get("/api/todos?status=complete", this::getTodosByStatus);
    // server.get("/api/todos?category=groceries", this::filterTodosByCategory);
  }
}
