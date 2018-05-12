package nl.juraji.biliomi.utility.calculate;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
public final class WeldUtils {
    private static final String WELD_PROXY_IDENTIFIER = "_WeldSubclass";

    private WeldUtils() {
        // Factory class
    }

    /**
     * Get the real class for a weld proxy class.
     * Checks if the classname ends with WELD_PROXY_IDENTIFIER
     * If true the super class is returned, which would be the actual class
     * If False the class is returned
     *
     * @param c The Weld proxy class
     * @return The super class of the given proxy class or the class itself if o is not a proxy
     */
    public static Class<?> getAbsoluteClassForClass(Class c) {
        if (c.getSimpleName().endsWith(WELD_PROXY_IDENTIFIER)) {
            return c.getSuperclass();
        }
        return c;
    }

    /**
     * Get the real class for a weld proxy.
     * Checks if the classname ends with WELD_PROXY_IDENTIFIER
     * If true the super class is returned, which would be the actual class
     * If False the class is returned
     *
     * @param o The Weld proxy
     * @return The super class of the given proxy or the class itself if o is not a proxy
     */
    public static Class<?> getAbsoluteClass(Object o) {
        return getAbsoluteClassForClass(o.getClass());
    }
}
