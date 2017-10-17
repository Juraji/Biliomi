package nl.juraji.biliomi.utility.factories.concurrent;

import nl.juraji.biliomi.Biliomi;
import nl.juraji.biliomi.utility.exceptions.UncaughtThreadExceptionHandler;
import nl.juraji.biliomi.utility.types.MutableString;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultThreadFactory implements ThreadFactory {
  private static final ThreadGroup DEFAULT_THREAD_GROUP = new ThreadGroup("BiliomiWorkers");

  private final String threadName;
  private final boolean isSingleThread;
  private final AtomicInteger serialNumber = new AtomicInteger(0);

  private DefaultThreadFactory(String name, boolean isSingleThread) {
    this.threadName = name;
    this.isSingleThread = isSingleThread;
  }

  public static DefaultThreadFactory newFactory(String name, boolean isSingleThread) {
    return new DefaultThreadFactory(name, isSingleThread);
  }

  /**
   * Build a thread name to match Biliomi's standard
   *
   * @param name The thread name
   * @return The standardized thread name
   */
  public static String threadNameBuilder(String name) {
    return threadNameBuilder(name, -1);
  }

  /**
   * Build a thread name to match Biliomi's standard
   *
   * @param name         The thread name
   * @param serialNumber A serial number, usually incremented from previous or -1 to ommit
   * @return The standardized thread name
   */
  public static String threadNameBuilder(String name, int serialNumber) {
    MutableString threadName = new MutableString(Biliomi.class.getSimpleName());
    threadName.append(" -> ");
    threadName.append(name);

    if (serialNumber > -1) {
      threadName.append('-');
      threadName.append(serialNumber);
    }

    return threadName.toString();
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread thread = new Thread(DEFAULT_THREAD_GROUP, r);
    thread.setName(formattedName());
    thread.setUncaughtExceptionHandler(new UncaughtThreadExceptionHandler());
    return thread;
  }

  private String formattedName() {
    if (isSingleThread) {
      return threadNameBuilder(threadName);
    }
    return threadNameBuilder(threadName, serialNumber.getAndIncrement());
  }
}
