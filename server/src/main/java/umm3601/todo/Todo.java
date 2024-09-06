package umm3601.todo;

@SuppressWarnings({"VisibilityModifier"})
public class Todo {
  @SuppressWarnings({"MemberName"})
  public String _id;
  public String owner;
  public Boolean status;
  public String body;
  public String category;
  @Override

  public String toString() {
    return owner;
  }
}

  // By default Java field names shouldn't start with underscores.
  // Here, though, we *have* to use the name `_id` to match the
  // name of the field in the database.
  // Having some kind of `toString()` allows us to print `User`s,
  // which can be useful/necessary in error handling. This only
  // returns the name, but it could be extended to return more or
  // all of the fields combined into a single string.
  //
  // The other option would be to return `_id`, but that can be
  // `null` if we're trying to add a new `User` to the database
  // that doesn't yet have an `_id`, so returning `name` seemed
  // the better bet.
