package util;

import com.opencsv.CSVWriter;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParsingException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * Handles many of the commonly desired functions to do with JSON in Java. The original intent was to provide JSON
 * accessibility in Java without reliance on model classes, as required in other libraries such as Gson.
 *
 * Dependencies:
 * <i>Some of these dependencies may be removed with extra logic if required, but are already tested and
 * provide optimal solutions to many of the problems that are addressed by the libraries.</i>
 *   <dependency>
 *       <groupId>javax.json</groupId>
 *       <artifactId>javax.json-api</artifactId>
 *       <version>1.1.2</version>
 *   </dependency>
 *   <dependency>
 *       <groupId>org.glassfish</groupId>
 *       <artifactId>javax.json</artifactId>
 *       <version>1.1.2</version>
 *   </dependency>
 *   <dependency>
 *       <groupId>com.opencsv</groupId>
 *       <artifactId>opencsv</artifactId>
 *       <version>4.1</version>
 *   </dependency>
 *
 * @author NeighbargerJ
 * @version 13 March 2019
 */
public final class JsonUtil {

    /** Private constructor to avoid external instantiation of this class */
    private JsonUtil() {}

    /**
     * Determines the difference between the two passed {@link String} objects, determining what has been replaced,
     * added, and removed between the source and the target JSONs.
     *
     * @see #difference(JsonValue, JsonValue)
     * @param theSource the source {@link String} JSON
     * @param theTarget the target {@link String} JSON
     * @return the generated difference JSON as a {@link String}
     */
    public static String difference(final String theSource, final String theTarget) {
        try (final JsonReader src = Json.createReader(new StringReader(theSource));
             final JsonReader tgt = Json.createReader(new StringReader(theTarget))) {
            return format(difference(src.readValue(), tgt.readValue()));
        }
    }

    /**
     * Determines the difference between the two passed {@link JsonValue} objects, determining what has been replaced,
     * added, and removed between the source and the target JSONs.
     *
     * @param theSource the source {@link JsonValue} JSON
     * @param theTarget the target {@link JsonValue} JSON
     * @return the generated difference JSON as a {@link JsonValue}
     */
    public static JsonValue difference(final JsonValue theSource, final JsonValue theTarget) {
        return Json.createDiff(theSource.asJsonObject(), theTarget.asJsonObject()).toJsonArray();
    }

    /**
     * Determines the merge between the two passed {@link String} JSONs against the source.
     *
     * @param theSource the source {@link String} JSON
     * @param theTarget the target {@link String} JSON
     * @return the generated difference JSON as a {@link String}
     */
    public static String merge(final String theSource, final String theTarget) {
        try (final JsonReader src = Json.createReader(new StringReader(theSource));
             final JsonReader tgt = Json.createReader(new StringReader(theTarget))) {
            return format(merge(src.readValue(), tgt.readValue()));
        }
    }

    /**
     * Determines the merge between the two passed {@link JsonValue} objects against the source.
     *
     * @param theSource the source {@link JsonValue} JSON
     * @param theTarget the target {@link JsonValue} JSON
     * @return the generated difference JSON as a {@link JsonValue}
     */
    public static JsonValue merge(final JsonValue theSource, final JsonValue theTarget) {
        return Json.createMergeDiff(theSource, theTarget).apply(theSource);
    }

    /**
     * Converts the passed {@link String} to a {@link JsonObject}
     *
     * @param json the {@link String} Object to convert
     * @return the converted {@link JsonObject}
     * @throws JsonParsingException if the passed JSON is malformed (i.e.: missing a comma or bracket)
     */
    public static JsonObject toJson(final String json) {
        try (final JsonReader reader = Json.createReader(new StringReader(json))) {
            return reader.readObject();
        }
    }

    /**
     * Writes the {@link String} JSON into a standard readable format, handling indentation and new lines between each
     * JSON entity.
     *
     * @see #format(JsonValue)
     * @param json the {@link String} Object to format
     * @return the formatted JSON {@link String}
     */
    public static String format(final String json) {
        try (final JsonReader reader = Json.createReader(new StringReader(json))) {
            return format(reader.readValue());
        }
    }

    /**
     * Writes the {@link JsonValue} into a standard readable format, handling indentation and new lines between each
     * JSON entity.
     *
     * @param json the {@link JsonValue} Object to format
     * @return the formatted JSON {@link String}
     */
    public static String format(final JsonValue json) {
        final StringWriter stringWriter = new StringWriter();
        prettyPrint(json, stringWriter);
        /* For some reason, '\n' is prepended and not appended by the JsonWriter. Fix is in this return statement. */
        return stringWriter.toString().substring(1) + '\n';
    }

    /**
     * Takes a {@link JsonValue} Object and prints it to the passed {@link Writer} with whitespace and newlines.
     *
     * @param json the {@link JsonValue} Object to print
     * @param writer the {@link Writer} destination of the printed JSON
     */
    private static void prettyPrint(final JsonValue json, final Writer writer) {
        final JsonWriterFactory writerFactory =
                Json.createWriterFactory(Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true));
        try (final JsonWriter jsonWriter = writerFactory.createWriter(writer)) {
            jsonWriter.write(json);
        }
    }

    /**
     * Writes the passed {@link JsonArray} in a CSV format with headers. The first JSON array entry is used to define
     * the sequence of columns. If a key exists within the first entry and not within the subsequent entries, an empty
     * string is written to the CSV. Keys that are present within the JSON that are not within the first entry are
     * ignored.
     *
     * @param json the {@link JsonArray} Object to print
     * @param writer the {@link Writer} destination of the printed JSON
     */
    public static void printToCsv(final JsonArray json, final Writer writer) {
        printToCsv(json, writer, null, true);
    }

    /**
     * Writes the passed {@link JsonArray} in a CSV format. Contrary to the implementation of
     * {@link #printToCsv(JsonArray, Writer)}, the key-set is defined from all keys within the Json as opposed to only
     * the first key. This introduces further reliability and does not reduce flexibility, but it increases runtime and
     * may still have inconsistent ordering of CSV columns. It is still recommended to write headers, but this method
     * allows the client code to accept that risk because all of the data will exist within the Json.
     *
     * @param json the {@link JsonArray} Object to print
     * @param writer the {@link Writer} destination of the printed JSON
     * @param headers if headers should be written to the generated CSV
     */
    public static void printToCsvVerbose(final JsonArray json, final Writer writer, final boolean headers) {
        final Set<String> sequence = new HashSet<>();
        json.forEach(v -> sequence.addAll(v.asJsonObject().keySet()));
        printToCsv(json, writer, sequence.toArray(new String[0]), headers);
    }

    /**
     * Writes the passed {@link JsonArray} in a CSV format. If a key within the passed sequence is not within the JSON
     * object, an empty string is written to the CSV. Keys that are present within the JSON that are not in the sequence
     * are ignored. If the sequence is null, it will be generated using the key set of the first entry and headers will
     * be written, otherwise the column headers will not be written.
     *
     * @param json the {@link JsonArray} Object to print
     * @param writer the {@link Writer} destination of the printed JSON
     * @param sequence the order of the JSON keys within the CSV
     */
    public static void printToCsv(final JsonArray json, final Writer writer, final String[] sequence) {
        printToCsv(json, writer, sequence, sequence == null);
    }

    /**
     * Writes the passed {@link JsonArray} in a CSV format. If a key within the passed sequence is not within the JSON
     * object, an empty string is written to the CSV. Keys that are present within the JSON that are not in the sequence
     * are ignored. If the sequence is null, it will be generated using the key set of the first entry;
     * <b>it is HIGHLY recommended to output headers when the sequence is null.</b>
     *
     * @param json the {@link JsonArray} Object to print
     * @param writer the {@link Writer} destination of the printed JSON
     * @param sequence the order of the JSON keys within the CSV
     * @param headers if headers should be written to the generated CSV
     */
    public static void printToCsv(final JsonArray json, final Writer writer, final String[] sequence, boolean headers) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(writer);
        final String[] keySequence;
        final CSVWriter csv;

        /* Supports option to pass a null sequence. */
        if (sequence == null) keySequence = json.get(0).asJsonObject().keySet().toArray(new String[0]);
        else keySequence = sequence.clone();

        /* Write the header to the CSV. */
        csv = new CSVWriter(writer, ',', '"', '"', "\n");
        if (headers) csv.writeNext(keySequence);

        /* Iterate through the JSON object and write to the CSV. */
        for (final JsonValue entry : json) {
            final List<String> row = new ArrayList<>();
            final JsonObject object = entry.asJsonObject();
            for (final String key : keySequence) {
                String value;
                try {value = object.getString(key);}
                catch (final NullPointerException e) {value = "";}
                if (!value.isEmpty() && value.charAt(0) == '"') row.add(value.substring(1, value.length() - 1));
                else row.add(value);
            }
            csv.writeNext(row.toArray(new String[0]));
        }
    }
}
