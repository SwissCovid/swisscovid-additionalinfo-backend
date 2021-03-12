package org.dpppt.additionalinfo.backend.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.dpppt.additionalinfo.backend.ws.statistics.SplunkResponse;
import org.dpppt.additionalinfo.backend.ws.statistics.SplunkResult;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

class TestSplunkResponse {

    @Test
    @Ignore("for local testing")
    void testResponseParsing() throws IOException {
        String jsonString = Files.readString(Paths.get(""));
        List<SplunkResult> resultList = extractResultFromSplunkApiString(jsonString);
    }

    private List<SplunkResult> extractResultFromSplunkApiString(String splunkApiResponse)
            throws JsonMappingException, JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        List<SplunkResult> result = new ArrayList<>();
        String[] lines = splunkApiResponse.split("\\n");
        for (String line : lines) {
            SplunkResponse response = om.readValue(line, SplunkResponse.class);
            result.add(response.getResult());
        }
        Collections.sort(
                result, Collections.reverseOrder(Comparator.comparing(SplunkResult::getTime)));
        return result;
    }
}
