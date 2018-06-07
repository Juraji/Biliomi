package nl.juraji.biliomi.utility.factories.marshalling.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

/**
 * Created by Juraji on 6-6-2018.
 * Biliomi
 *
 * Serializes Joda DateTime objects to ISO 8601 format with timezone
 */
public class JodaDateTimeSerializer extends StdSerializer<DateTime> {

    private final DateTimeFormatter formatter;

    public JodaDateTimeSerializer() {
        this(DateTime.class);
    }

    public JodaDateTimeSerializer(Class<DateTime> t) {
        super(t);
        this.formatter = ISODateTimeFormat.dateTime();
    }

    @Override
    public void serialize(DateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(dateTime.toString(this.formatter));
    }
}
