package seng202.team6.managers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TaskManager {
  public static final Logger log = LogManager.getLogger(TaskManager.class);
  private final ExecutorService executorService = Executors.newFixedThreadPool(1);

  public void teardown() {
    executorService.shutdown();
  }

  public <T> void submit(CallbackTask<T> task) {
    executorService.submit(task.create());
  }

  public interface CallbackTask<T> {

    T onRun();

    void onCompletion(T t);

    default void onException(Throwable throwable) {

    }

    private Task<T> create() {
      Task<T> task = new Task<>() {
        @Override
        protected T call() {
          return onRun();
        }
      };

      task.setOnSucceeded(event -> runOnJavaFxPlatform(() -> {
        try {
          onCompletion(task.get());
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException(e);
        }
      }));
      task.setOnFailed(event -> log.error("Failed to execute task", task.getException()));
      return task;
    }

    private void runOnJavaFxPlatform(Runnable runnable) {
      if (Platform.isNestedLoopRunning()) {
        Platform.runLater(runnable);
        return;
      }
      runnable.run();
    }
  }
}
