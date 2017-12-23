package nl.juraji.biliomi.utility.types.xml;

import nl.juraji.biliomi.model.internal.rest.query.RestSortDirective;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

/**
 * Created by Juraji on 21-12-2017.
 * Biliomi
 */
public class XmlElementPathBeanComparator<T> implements Comparator<T> {
  private final String propertyPath;

  /**
   * Compare bean properties by XmlElement annotation name.
   *
   * @param xmlElementPath A dot separated path of XmlElement names (case insensitive)
   * @param rootBeanType   The the root bean Class/Type
   */
  public XmlElementPathBeanComparator(String xmlElementPath, Class<T> rootBeanType) {
    try {
      this.propertyPath = new XmlPathConverter<>(rootBeanType).convert(xmlElementPath);
    } catch (XmlPathConverter.XmlPathException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static <T> Comparator<T> forSortDirective(RestSortDirective sortDirective, Class<T> rootClass) {
    Comparator<T> comparator = new XmlElementPathBeanComparator<>(sortDirective.getProperty(), rootClass);
    return (sortDirective.isDescending() ? comparator.reversed() : comparator);
  }

  @Override
  public int compare(T o1, T o2) {
    Comparable obj1 = null;
    Comparable obj2 = null;

    try {
      obj1 = (Comparable) PropertyUtils.getProperty(o1, this.propertyPath);
      obj2 = (Comparable) PropertyUtils.getProperty(o2, this.propertyPath);
    } catch (NestedNullException ignored) {
      // Ignored, als het property NULL is, dan vergelijken met NULL
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalArgumentException("Could not retrieve property " + this.propertyPath, e);
    }

    Comparator<Comparable<Object>> objectComparator = null;
    if ((obj1 != null && obj2 != null) && obj1 instanceof String) {
      obj1 = ((String) obj1).toLowerCase().trim();
      obj2 = ((String) obj2).toLowerCase().trim();

      if (!StringUtils.isEmpty((String) obj1) && !StringUtils.isEmpty((String) obj2)) {
        if (StringUtils.isNumeric((String) obj1) && StringUtils.isNumeric((String) obj2)) {
          objectComparator = Comparator.comparingDouble(o -> new Double(String.valueOf(o)));
        }
      }
    }

    if (objectComparator == null) {
      objectComparator = Comparator.naturalOrder();
    }

    //noinspection unchecked
    return Comparator.nullsLast(objectComparator).compare(obj1, obj2);
  }
}
