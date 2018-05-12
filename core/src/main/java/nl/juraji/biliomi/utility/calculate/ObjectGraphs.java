package nl.juraji.biliomi.utility.calculate;

import com.google.common.base.Preconditions;
import nl.juraji.biliomi.utility.estreams.EStream;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;

import java.beans.PropertyDescriptor;
import java.util.Collection;

/**
 * Created by Juraji on 19-7-2017.
 * Biliomi v3
 */
public final class ObjectGraphs {
    private ObjectGraphs() {
        // Private constructor
    }

    /**
     * Deep merge to POJO objects
     *
     * @param target The target POJO
     * @param source The source POJO
     * @param <T>    The POJO type
     * @return The merge result
     */
    public static <T> T mergeObjects(T target, T source) {
        // Check that target and source are of the same type
        Preconditions.checkArgument(
                target.getClass().equals(source.getClass()),
                "Type mismatch %s -> %s",
                source.getClass().getSimpleName(),
                target.getClass().getSimpleName()
        );

        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(target);

        EStream.from(descriptors).forEach(propertyDescriptor -> {

            Object targetValue = PropertyUtils.getProperty(target, propertyDescriptor.getName());
            Object sourceValue = PropertyUtils.getProperty(source, propertyDescriptor.getName());

            if (sourceValue != null) {
                if (targetValue == null) {
                    PropertyUtils.setProperty(target, propertyDescriptor.getName(), sourceValue);
                } else {
                    if (isJavaType(propertyDescriptor)) {
                        if (targetValue != sourceValue) {
                            PropertyUtils.setProperty(target, propertyDescriptor.getName(), sourceValue);
                        }
                    } else {
                        mergeObjects(targetValue, sourceValue);
                    }
                }
            }
        });

        return target;
    }

    /**
     * Recursively initialize all non-primitive properties of an object
     *
     * @param o The object to initialize
     */
    public static void initializeObjectGraph(Object o) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(o);

        EStream.from(descriptors)
                .filter(propertyDescriptor -> !isJavaType(propertyDescriptor))
                .filter(propertyDescriptor -> PropertyUtils.getProperty(o, propertyDescriptor.getName()) == null)
                .forEach(propertyDescriptor -> {
                    Class<?> type = propertyDescriptor.getPropertyType();
                    Object subO = type.getDeclaredConstructor().newInstance();
                    PropertyUtils.setProperty(o, propertyDescriptor.getName(), subO);
                    initializeObjectGraph(subO);
                });
    }

    /**
     * Check if the given property is a Java type
     *
     * @param pd A PropertyDescriptor
     * @return True when the property type is a Java type, else False
     */
    public static boolean isJavaType(PropertyDescriptor pd) {
        return isJavaType(pd.getPropertyType());
    }

    /**
     * Check if the given class is a Java type
     *
     * @param type A class
     * @return True when the property type is a Java type, else False
     */
    public static boolean isJavaType(Class<?> type) {
        return (ClassUtils.isPrimitiveOrWrapper(type)
                || Class.class.equals(type)
                || String.class.equals(type)
                || Collection.class.isAssignableFrom(type)
                || Void.class.isAssignableFrom(type));
    }
}
