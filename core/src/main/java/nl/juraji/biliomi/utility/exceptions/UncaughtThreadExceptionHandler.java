package nl.juraji.biliomi.utility.exceptions;


import org.apache.logging.log4j.LogManager;

public final class UncaughtThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    LogManager.getLogger(getClass()).error("Error on thread {}", t.getName(), e);
  }
}
