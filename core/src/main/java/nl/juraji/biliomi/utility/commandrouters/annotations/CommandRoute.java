package nl.juraji.biliomi.utility.commandrouters.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Juraji on 28-4-2017.
 * Biliomi v3
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandRoute {
  String command();
  boolean systemCommand() default false;  // Caster only
  boolean modCanActivate() default false; // Moderators can always activate (overrides systemCommand value)
  long defaultCooldown() default 0;
  long defaultPrice() default 0;
}
