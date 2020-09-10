package org.dpppt.additionalinfo.backend.ws.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CustomLocalDateSerializer extends StdSerializer<LocalDate> {

    public CustomLocalDateSerializer() {
        this(null);
    }

    public CustomLocalDateSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider arg2)
            throws IOException, JsonProcessingException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        gen.writeString(value.toString(formatter));
    }
}
