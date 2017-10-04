package nl.juraji.biliomi.utility.commandrouters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Juraji on 1-5-2017.
 * Biliomi v3
 *
 * Add this annotation to methods implementing subcommands.
 * Then call captureSubCommand(...) inside the parent method to have the SubCommandRouter execute it.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommandRoute {
  String parentCommand();
  String command();
}
