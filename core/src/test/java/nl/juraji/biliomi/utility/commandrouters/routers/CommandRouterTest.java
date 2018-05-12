package nl.juraji.biliomi.utility.commandrouters.routers;

import nl.juraji.biliomi.components.ComponentManager;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.factories.reflections.ReflectionUtils;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Created by Juraji on 12-10-2017.
 * Biliomi
 * <p>
 * These tests are not to test the working of units but rather
 * to enforce command runner methods to have implemented the correct interface.
 */
public class CommandRouterTest {

    /**
     * A main command runner method's interface should fit the following:
     * - Access: public
     * - Return:  boolean
     * - Parameters: User, Arguments
     */
    @Test
    public void checkCommandMethodInterfaces() {
        assertMethodInterface(CommandRoute.class, boolean.class, new Class[]{User.class, Arguments.class});
    }

    /**
     * A sub command runner method's interface should fit the following:
     * - Access: public
     * - Return:  boolean
     * - Parameters: User, Arguments
     */
    @Test
    public void checkSubCommandMethodInterfaces() {
        assertMethodInterface(SubCommandRoute.class, boolean.class, new Class[]{User.class, Arguments.class});
    }

    /**
     * A console command runner method's interface should fit the following:
     * - Access: public
     * - Return:  boolean
     * - Parameters: ConsoleInputEvent
     */
    @Test
    public void checkCliCommandMethodInterface() {
        assertMethodInterface(CliCommandRoute.class, boolean.class, new Class[]{ConsoleInputEvent.class});
    }

    private void assertMethodInterface(Class<? extends Annotation> annotation, Class returnType, Class[] parameterTypes) {
        ReflectionUtils.forClassPackage(ComponentManager.class)
                .annotatedMethods(annotation)
                .forEach(method -> {
                    System.out.println("Checking " + annotation.getSimpleName() + " method interface for: "
                            + method.getDeclaringClass().getSimpleName() + " -> " + method.getName() + "()");
                    assertTrue("Command methods should be public", Modifier.isPublic(method.getModifiers()));
                    assertEquals("Command methods should return " + returnType.getSimpleName(), method.getReturnType(), returnType);
                    assertArrayEquals("Command method expects invalid paramters", parameterTypes, method.getParameterTypes());
                });
    }
}