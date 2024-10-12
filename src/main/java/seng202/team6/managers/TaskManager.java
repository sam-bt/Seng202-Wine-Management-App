package seng202.team6.managers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TaskManager handles the execution of background tasks and provides callbacks
 * for pre-execution and post-execution actions, ensuring proper threading behavior
 * for JavaFX applications.
 */
public class TaskManager {

  public static final Logger log = LogManager.getLogger(TaskManager.class);
  private final ExecutorService executorService = Executors.newFixedThreadPool(1);

  /**
   * Shuts down the TaskManager's ExecutorService, stopping any new tasks from being submitted.
   * Should be called when the application is being torn down.
   */
  public void teardown() {
    executorService.shutdown();
  }

  /**
   * Creates a new TaskBuilder for configuring and submitting a background task.
   *
   * @param <T> The type of result produced by the task.
   * @return a TaskBuilder for configuring a background task.
   */
  public <T> TaskBuilder<T> create() {
    return new TaskBuilder<>();
  }

  /**
   * TaskBuilder is a fluent builder class for configuring tasks with optional callbacks
   * to run before execution, during execution, upon completion, or when an exception occurs.
   *
   * @param <T> The type of the result produced by the task.
   */
  public class TaskBuilder<T> {
    private Supplier<T> onBeforeRun;  // Runs on the JavaFX thread before the task starts
    private Supplier<T> onRun;        // Runs on a background thread
    private Consumer<T> onCompletion; // Runs on the JavaFX thread after the task completes
    private Consumer<Throwable> onException; // Handles exceptions in a background thread

    /**
     * Specifies an action to run on the JavaFX thread before the task starts.
     *
     * @param onBeforeRun A Supplier function that runs on the main thread before the task begins.
     * @return the current TaskBuilder instance.
     */
    public TaskBuilder<T> onBeforeRun(Supplier<T> onBeforeRun) {
      this.onBeforeRun = onBeforeRun;
      return this;
    }

    /**
     * Specifies the main task logic to run on a background thread.
     *
     * @param onRun A Supplier function that contains the task's background logic.
     * @return the current TaskBuilder instance.
     */
    public TaskBuilder<T> onRun(Supplier<T> onRun) {
      this.onRun = onRun;
      return this;
    }

    /**
     * Specifies an action to run on the JavaFX thread after the task completes.
     *
     * @param onCompletion A Consumer function that handles the result of the task upon completion.
     * @return the current TaskBuilder instance.
     */
    public TaskBuilder<T> onCompletion(Consumer<T> onCompletion) {
      this.onCompletion = onCompletion;
      return this;
    }

    /**
     * Specifies an action to handle any exception thrown during the execution of the task.
     *
     * @param onException A Consumer function to handle exceptions in the task.
     * @return the current TaskBuilder instance.
     */
    public TaskBuilder<T> onException(Consumer<Throwable> onException) {
      this.onException = onException;
      return this;
    }

    /**
     * Builds the JavaFX Task and ensures that callbacks are run on the appropriate threads.
     */
    public void submit() {
      Task<T> task = new Task<>() {
        @Override
        protected T call() {
          // run onBeforeRun on the JavaFX thread if provided
          if (onBeforeRun != null) {
            Platform.runLater(() -> onBeforeRun.get());
          }
          // run the main task logic on the background thread
          if (onRun != null) {
            return onRun.get();
          }
          return null;
        }
      };
      task.setOnSucceeded(event -> {
        if (onCompletion != null) {
          Platform.runLater(() -> onCompletion.accept(task.getValue()));
        }
      });
      task.setOnFailed(event -> {
        Throwable exception = task.getException();
        if (onException != null) {
          onException.accept(exception);
        } else {
          log.error("Task failed", exception);
        }
      });
      executorService.submit(task);
    }
  }
}
