import org.junit.jupiter.api.Test;
import util.JsonUtil;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class JsonUtilTest {

    private static final String IDEAL_SOURCE_JSON =
            "{" +
                "\"name\": {" +
                    "\"first\": \"John\"," +
                    "\"last\": \"Doe\"" +
                "}," +
                "\"address\": null," +
                "\"birthday\": \"1980-01-01\"," +
                "\"company\": \"Acme\"," +
                "\"occupation\": \"Software engineer\"," +
                "\"phones\": [" +
                    "{" +
                        "\"number\": \"000000000\"," +
                        "\"type\": \"home\"" +
                    "},{" +
                        "\"number\": \"999999999\"," +
                        "\"type\": \"mobile\"" +
                    "}" +
                "]" +
            "}";

    private static final String IDEAL_TARGET_JSON =
            "{" +
                "\"name\": {" +
                    "\"first\": \"Jane\"," +
                    "\"last\": \"Doe\"," +
                    "\"nickname\": \"Jenny\"" +
                "}," +
                "\"birthday\": \"1980-01-01\"," +
                "\"occupation\": null," +
                "\"phones\": [" +
                    "{" +
                        "\"number\": \"111111111\"," +
                        "\"type\": \"mobile\"" +
                    "}" +
                "]," +
                "\"favorite\": true," +
                "\"groups\": [" +
                    "\"close-friends\"," +
                    "\"gym\"" +
                "]" +
            "}";

    private static final String IDEAL_MERGE_JSON =
            "\n" +
            "{\n" +
            "    \"name\": {\n" +
            "        \"first\": \"Jane\",\n" +
            "        \"last\": \"Doe\",\n" +
            "        \"nickname\": \"Jenny\"\n" +
            "    },\n" +
            "    \"birthday\": \"1980-01-01\",\n" +
            "    \"phones\": [\n" +
            "        {\n" +
            "            \"number\": \"111111111\",\n" +
            "            \"type\": \"mobile\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"favorite\": true,\n" +
            "    \"groups\": [\n" +
            "        \"close-friends\",\n" +
            "        \"gym\"\n" +
            "    ]\n" +
            "}";

    private static final String IDEAL_DIFF_JSON =
            "\n" +
            "[\n" +
            "    {\n" +
            "        \"op\": \"replace\",\n" +
            "        \"path\": \"/name/first\",\n" +
            "        \"value\": \"Jane\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"op\": \"add\",\n" +
            "        \"path\": \"/name/nickname\",\n" +
            "        \"value\": \"Jenny\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"op\": \"remove\",\n" +
            "        \"path\": \"/address\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"op\": \"remove\",\n" +
            "        \"path\": \"/company\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"op\": \"replace\",\n" +
            "        \"path\": \"/occupation\",\n" +
            "        \"value\": null\n" +
            "    },\n" +
            "    {\n" +
            "        \"op\": \"replace\",\n" +
            "        \"path\": \"/phones/1/number\",\n" +
            "        \"value\": \"111111111\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"op\": \"remove\",\n" +
            "        \"path\": \"/phones/0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"op\": \"add\",\n" +
            "        \"path\": \"/favorite\",\n" +
            "        \"value\": true\n" +
            "    },\n" +
            "    {\n" +
            "        \"op\": \"add\",\n" +
            "        \"path\": \"/groups\",\n" +
            "        \"value\": [\n" +
            "            \"close-friends\",\n" +
            "            \"gym\"\n" +
            "        ]\n" +
            "    }\n" +
            "]";

    private static final String IDEAL_SHAREPOINT_JSON =
            "\n" +
            "{\n" +
            "    \"d\": {\n" +
            "        \"results\": [\n" +
            "            {\n" +
            "                \"Title\": \"Person\",\n" +
            "                \"PUBLISHED_x0020_INDICATOR\": \"Yes\",\n" +
            "                \"VACANCY_x0020_NUMBER\": \"0\",\n" +
            "                \"SENIOR_x0020_TYPE\": \"manager\",\n" +
            "                \"ORG\": \"TBG\",\n" +
            "                \"TIER\": \"3\",\n" +
            "                \"OPEN\": \"02/09/19\",\n" +
            "                \"METHOD\": \"Secret\",\n" +
            "                \"CLOSE\": \"02/09/19\",\n" +
            "                \"PANEL_x0020_MEMBERS\": \"3\",\n" +
            "                \"STATUS\": \"Active\",\n" +
            "                \"PUBLISHED_x0020_COMMENTS\": \"Cool\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"Title\": \"Person\",\n" +
            "                \"PUBLISHED_x0020_INDICATOR\": \"Yes\",\n" +
            "                \"VACANCY_x0020_NUMBER\": \"0\",\n" +
            "                \"SENIOR_x0020_TYPE\": \"manager\",\n" +
            "                \"ORG\": \"TBG\",\n" +
            "                \"TIER\": \"3\",\n" +
            "                \"OPEN\": \"02/09/19\",\n" +
            "                \"METHOD\": \"Secret\",\n" +
            "                \"CLOSE\": \"02/09/19\",\n" +
            "                \"PANEL_x0020_MEMBERS\": \"3\",\n" +
            "                \"STATUS\": \"Active\",\n" +
            "                \"PUBLISHED_x0020_COMMENTS\": \"Cool\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    @Test
    void differenceIdealTestCase() {
        assertEquals(IDEAL_DIFF_JSON, JsonUtil.difference(IDEAL_SOURCE_JSON, IDEAL_TARGET_JSON));
    }

    @Test
    void mergeIdealTestCase() {
        assertEquals(IDEAL_MERGE_JSON, JsonUtil.merge(IDEAL_SOURCE_JSON, IDEAL_TARGET_JSON));
    }

    @Test
    void formatOverloadedSimilarTestCase() {
        assertEquals(IDEAL_DIFF_JSON, JsonUtil.format(IDEAL_DIFF_JSON));
        assertEquals(IDEAL_MERGE_JSON, JsonUtil.format(IDEAL_MERGE_JSON));
        assertNotEquals(IDEAL_SOURCE_JSON, JsonUtil.format(IDEAL_SOURCE_JSON));
        assertNotEquals(IDEAL_TARGET_JSON, JsonUtil.format(IDEAL_TARGET_JSON));
    }

    @Test
    void writeToCsvSharepointTestCase() {
        System.out.println(
                JsonUtil.writeToCsv(JsonUtil.toJson(IDEAL_SHAREPOINT_JSON).getJsonObject("d").getJsonArray("results"),
                new StringWriter()));
    }
}
