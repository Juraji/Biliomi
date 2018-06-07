package nl.juraji.biliomi.utility.factories.marshalling.serializers;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.DateTime;

/**
 * Created by Juraji on 6-6-2018.
 * Biliomi
 */
public class BiliomiSerializersModule extends SimpleModule {
    public BiliomiSerializersModule() {
        this.addSerializer(DateTime.class, new JodaDateTimeSerializer());
    }
}
