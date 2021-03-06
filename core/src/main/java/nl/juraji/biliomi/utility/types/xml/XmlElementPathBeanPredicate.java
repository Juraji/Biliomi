package nl.juraji.biliomi.utility.types.xml;

import nl.juraji.biliomi.model.internal.rest.query.RestFilterDirective;
import nl.juraji.biliomi.model.internal.rest.query.RestFilterOperator;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.estreams.EStream;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Created by Juraji on 22-12-2017.
 * Biliomi
 */
public class XmlElementPathBeanPredicate<T> implements Predicate<T> {

    private final String propertyPath;
    private final Object value;
    private final Class<?> valueClass;
    private final RestFilterOperator operator;

    public XmlElementPathBeanPredicate(String propertyPath, RestFilterOperator operator, Object value, Class<T> rootBeanType) {
        Objects.requireNonNull(propertyPath);
        Objects.requireNonNull(operator);

        this.value = value;
        this.valueClass = (value == null ? null : value.getClass());
        this.operator = operator;

        try {
            this.propertyPath = new XmlPathConverter<>(rootBeanType).convert(propertyPath);
        } catch (XmlPathConverter.XmlPathException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> Predicate<T> forFilterDirective(RestFilterDirective filterDirective, Class<T> rootBeanType) {
        Predicate<T> predicate = new XmlElementPathBeanPredicate<>(filterDirective.getProperty(), filterDirective.getOperator(), filterDirective.getValue(), rootBeanType);
        return (filterDirective.isNegative() ? predicate.negate() : predicate);
    }

    private static boolean testDateTime(DateTime a, String b, RestFilterOperator operator) {
        DateTime bdt = new DateTime(b);
        switch (operator) {
            case EQUALS:
                return a.equals(bdt);
            case LESSER_THAN:
                return a.isBefore(bdt);
            case GREATER_THAN:
                return a.isAfter(bdt);
            default:
                return false;
        }
    }

    private static boolean testBoolean(Boolean a, Boolean b, RestFilterOperator operator) {
        switch (operator) {
            case EQUALS:
                return a.equals(b);
            default:
                return false;
        }
    }

    private static <E extends Enum<E>> boolean testEnum(Enum<E> tEnum, String value, RestFilterOperator operator) {
        // noinspection unchecked
        Enum<E> vEnum = EnumUtils.toEnum(value, tEnum.getClass());
        switch (operator) {
            case EQUALS:
                return tEnum.equals(vEnum);
            default:
                return false;
        }
    }

    /**
     * Match String values (ignores case)
     */
    private static boolean testString(String a, String b, RestFilterOperator operator) {
        switch (operator) {
            case EQUALS:
                return a.equalsIgnoreCase(b);
            case CONTAINS:
                return StringUtils.containsIgnoreCase(a, b);
            case LESSER_THAN:
                return a.length() < b.length();
            case GREATER_THAN:
                return a.length() > b.length();
            default:
                return false;
        }
    }

    /**
     * Match numeric values (ignores case)
     */
    private static boolean testNumber(Number a, Number b, RestFilterOperator operator) {
        switch (operator) {
            case EQUALS:
                return a.doubleValue() == b.doubleValue();
            case CONTAINS:
                return StringUtils.containsIgnoreCase(String.valueOf(a), String.valueOf(b));
            case LESSER_THAN:
                return a.doubleValue() < b.doubleValue();
            case GREATER_THAN:
                return a.doubleValue() > b.doubleValue();
            default:
                return false;
        }
    }

    private static boolean testCollectionAgainsString(Collection<?> a, String b, RestFilterOperator operator) {
        switch (operator) {
            case CONTAINS:
                if (!a.isEmpty()) {
                    return a.stream().anyMatch(o -> {
                        if (String.class.isAssignableFrom(o.getClass())) {
                            return StringUtils.containsIgnoreCase((CharSequence) o, b);
                        } else {
                            return EStream.from(o.getClass().getDeclaredFields())
                                    .filter(field -> String.class.isAssignableFrom(field.getType()))
                                    .map(field -> PropertyUtils.getProperty(o, field.getName()))
                                    .filter(Objects::nonNull)
                                    .anyMatch(v -> StringUtils.containsIgnoreCase((CharSequence) v, b));
                        }
                    });
                }
            default:
                return false;
        }
    }

    /**
     * Match Collection values (LT and GT are matched by collection size)
     */
    private static boolean testCollection(Collection<?> a, Collection<?> b, RestFilterOperator operator) {
        switch (operator) {
            case EQUALS:
                return a.size() == b.size() && a.containsAll(b);
            case CONTAINS:
                return b.stream().anyMatch(a::contains);
            case LESSER_THAN:
                return a.size() < b.size();
            case GREATER_THAN:
                return a.size() > b.size();
            default:
                return false;
        }
    }

    @Override
    public boolean test(T t) {
        // If t is null it will not match
        if (t == null) {
            return false;
        }

        Object tValue;
        try {
            tValue = PropertyUtils.getProperty(t, propertyPath);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Failed retrieving test value " + propertyPath, e);
        }

        // NULL values
        if (value == null) {
            return (tValue == null);
        } else if (tValue == null) {
            return false;
        }

        // Test DateTime
        if (DateTime.class.isAssignableFrom(tValue.getClass())
                && String.class.isAssignableFrom(valueClass)) {
            return testDateTime((DateTime) tValue, (String) value, operator);
        }

        // Test enums
        if (Enum.class.isAssignableFrom(tValue.getClass()) && String.class.isAssignableFrom(valueClass)) {
            // noinspection unchecked
            return testEnum((Enum) tValue, (String) value, operator);
        }

        // Test numbers
        if (Number.class.isAssignableFrom(tValue.getClass()) && Number.class.isAssignableFrom(valueClass)) {
            return testNumber((Number) tValue, (Number) value, operator);
        }

        // Test collections against string
        if (Collection.class.isAssignableFrom(tValue.getClass()) && String.class.isAssignableFrom(valueClass)) {
            return testCollectionAgainsString((Collection<?>) tValue, (String) value, operator);
        }

        // Test Class equality
        if (!tValue.getClass().isAssignableFrom(valueClass)) {
            return false;
        }

        // Test booleans
        if (Boolean.class.isAssignableFrom(valueClass)) {
            return testBoolean((Boolean) tValue, (Boolean) value, operator);
        }

        // Test strings
        if (String.class.isAssignableFrom(valueClass)) {
            return testString((String) tValue, (String) value, operator);
        }

        // Test collections
        if (Collection.class.isAssignableFrom(valueClass)) {
            return testCollection((Collection<?>) tValue, (Collection<?>) value, operator);
        }

        // Default to object equals
        return value.equals(tValue);
    }
}
