package util;

import com.opencsv.CSVWriter;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static String difference(final String theSource, final String theTarget) {
        final JsonValue src = Json.createReader(new StringReader(theSource)).readValue();
        final JsonValue tgt = Json.createReader(new StringReader(theTarget)).readValue();
        return format(Json.createDiff(src.asJsonObject(), tgt.asJsonObject()).toJsonArray());
    }

    public static String merge(final String theSource, final String theTarget) {
        final JsonValue src = Json.createReader(new StringReader(theSource)).readValue();
        final JsonValue tgt = Json.createReader(new StringReader(theTarget)).readValue();
        return format(Json.createMergeDiff(src, tgt).apply(src));
    }

    public static String format(final String json) {
        return format(Json.createReader(new StringReader(json)).readValue());
    }

    public static String format(final JsonValue json) {
        final StringWriter stringWriter = new StringWriter();
        prettyPrint(json, stringWriter);
        return stringWriter.toString();
    }

    public static JsonObject toJson(final String json) {
        return Json.createReader(new StringReader(json)).readObject();
    }

    public static String writeToCsv(final JsonArray json, final Writer writer) {
        final CSVWriter cav = new CSVWriter(writer, ',', '"', '"', "\n");
        for (final JsonValue entry : json) {
            final List<String> row = new ArrayList<>();
            for (final JsonValue value : entry.asJsonObject().values())
                row.add(value.toString().substring(1, value.toString().length() - 1));
            cav.writeNext(row.toArray(new String[0]));
        }
        return writer.toString();
    }

    private static void prettyPrint(final JsonValue json, final Writer writer) {
        final Map<String, Object> config = Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true);
        final JsonWriterFactory writerFactory = Json.createWriterFactory(config);
        try (final JsonWriter jsonWriter = writerFactory.createWriter(writer)) {
            jsonWriter.write(json);
        }
    }
}
