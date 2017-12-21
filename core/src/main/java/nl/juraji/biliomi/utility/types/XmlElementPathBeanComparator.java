package nl.juraji.biliomi.utility.types;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
  public XmlElementPathBeanComparator(String xmlElementPath, Class rootBeanType) {
    this.propertyPath = translatePropertyPath(rootBeanType, xmlElementPath);
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
      throw new RuntimeException("Could not retrieve property " + this.propertyPath, e);
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

  private static String translatePropertyPath(Class type, String xmlElementPath) {
    if (xmlElementPath == null) {
      throw new IllegalArgumentException("Can not defer an empty xml name");
    }

    String path;

    if (xmlElementPath.contains(".")) {
      String[] xmlElementNames = xmlElementPath.split("\\.");
      List<String> newPathList = new ArrayList<>();
      Class currentType = type;

      for (String xmlElementName : xmlElementNames) {
        Field propertyField = findPropertyField(currentType, xmlElementName);

        if (propertyField == null) {
          // If the current property is not found
          // clear the list and break the loop.
          newPathList.clear();
          break;
        }

        newPathList.add(propertyField.getName());
        currentType = propertyField.getType();
      }

      path = newPathList.stream()
          .reduce((left, right) -> left + "." + right)
          .orElse(null);
    } else {
      Field propertyField = findPropertyField(type, xmlElementPath);
      path = (propertyField != null ? propertyField.getName() : null);
    }

    if (path == null) {
      throw new IllegalArgumentException("Unable to defer XML name " + xmlElementPath + " in type " + type.getName());
    }

    return path;
  }

  private static Field findPropertyField(Class type, String xmlElementName) {
    Optional<Field> optField = Arrays.stream(type.getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(XmlElement.class))
        .filter(field -> field.getAnnotation(XmlElement.class).name().equalsIgnoreCase(xmlElementName))
        .findFirst();

    if (optField.isPresent()) {
      return optField.get();
    } else if (type.getSuperclass() != null) {
      return findPropertyField(type.getSuperclass(), xmlElementName);
    } else {
      return null;
    }
  }
}
