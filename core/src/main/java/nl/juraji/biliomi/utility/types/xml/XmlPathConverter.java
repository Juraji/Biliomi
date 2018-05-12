package nl.juraji.biliomi.utility.types.xml;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Juraji on 22-12-2017.
 * Biliomi
 */
public class XmlPathConverter<T> {
    private final Class<T> type;

    public XmlPathConverter(Class<T> type) {
        this.type = type;
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

    /**
     * Convert the given XmlElement path to it's bean property path
     *
     * @param xmlElementPath A XMLElement path (case insensitive)
     * @return The corresponding bean property path
     */
    public String convert(String xmlElementPath) throws XmlPathException {
        if (StringUtils.isEmpty(xmlElementPath)) {
            throw new XmlPathException("Can not defer an empty xml name");
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
            throw new XmlPathException("Unable to defer XML name " + xmlElementPath + " in type " + type.getName());
        }

        return path;
    }

    public static class XmlPathException extends Exception {
        public XmlPathException(String message) {
            super(message);
        }
    }
}
