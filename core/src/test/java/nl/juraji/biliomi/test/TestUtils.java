package nl.juraji.biliomi.test;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by Juraji on 10-5-2017.
 * Biliomi v3
 */
public final class TestUtils {

    private TestUtils() {
    }

    public static void setStaticFinalField(Class<?> clazz, String fieldName, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        Field targetfield = clazz.getDeclaredField(fieldName);

        targetfield.setAccessible(true);
        modifiersField.setAccessible(true);
        modifiersField.setInt(targetfield, targetfield.getModifiers() & ~Modifier.FINAL);

        targetfield.set(null, newValue);
    }

    /**
     * Will call all @PostConstruct annotated methods in object's inheritance tree,
     * in sequence from parent to child, as CDI spec demands
     *
     * @param object The object to run PostConstruct on
     */
    public static void callPostConstruct(Object object) {
        LinkedList<Class<?>> classInheritanceTree = getClassInheritanceTree(object.getClass());

        classInheritanceTree.stream()
                .flatMap(aClass -> Arrays.stream(aClass.getDeclaredMethods()))
                .filter(method -> method.isAnnotationPresent(PostConstruct.class))
                .forEachOrdered(method -> {
                    try {
                        method.setAccessible(true);
                        method.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static LinkedList<Class<?>> getClassInheritanceTree(Class<?> clazz) {
        LinkedList<Class<?>> tree = new LinkedList<>();
        Class<?> current = clazz;

        do {
            tree.addFirst(current);
            current = current.getSuperclass();
        } while (current.getSuperclass() != null);

        return tree;
    }
}
