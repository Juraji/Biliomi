package nl.juraji.biliomi.utility.events.interceptors;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * Created by Juraji on 24-4-2017.
 * Biliomi v3
 */
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventBusSubscriber {
}
