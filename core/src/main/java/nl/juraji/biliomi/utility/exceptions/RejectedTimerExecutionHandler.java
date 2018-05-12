package nl.juraji.biliomi.utility.exceptions;

import org.apache.logging.log4j.LogManager;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public final class RejectedTimerExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        LogManager.getLogger(getClass()).error("{} Failed to execute:\n{}", executor.getClass().getSimpleName(), r.toString());
    }
}
