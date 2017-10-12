package nl.juraji.biliomi.utility.commandrouters.annotations;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * Created by Juraji on 7-5-2017.
 * Biliomi v3
 */
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CliCommandRouteImplementor {
}
