package org.zalando.catwatch.backend.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JsonDateListSerializer extends JsonSerializer<List<Date>> {

    @Override
    public void serialize(List<Date> dates, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        jsonGenerator.writeStartArray();
        for (Date date: dates) {
            String formattedDate = format.format(date);
            jsonGenerator.writeString(formattedDate);
        }
        jsonGenerator.writeEndArray();
    }
}
