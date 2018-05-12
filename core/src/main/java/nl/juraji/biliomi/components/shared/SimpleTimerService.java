package nl.juraji.biliomi.components.shared;

import nl.juraji.biliomi.utility.types.components.TimerService;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 3-6-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class SimpleTimerService extends TimerService {

    @PostConstruct
    private void initMessageTimerService() {
        this.start();
    }

    public void schedule(Runnable command, long delay, TimeUnit unit) {
        super.schedule(command, delay, unit);
    }
}
