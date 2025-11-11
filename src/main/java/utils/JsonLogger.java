package utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import io.restassured.response.Response;

// : Use this ObjectMapper (from FasterXML Jackson)
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonLogger {

	 private static final ObjectMapper mapper = new ObjectMapper()
	            .enable(SerializationFeature.INDENT_OUTPUT);

	    // Collapsible payload + response with GMID highlight
	    public static void logApiCall(ExtentTest test, String endpoint, Object payload, Response response, String gmid) {
	        try {
	            String prettyPayload = mapper.writeValueAsString(payload);
	            String prettyResponse = mapper.writerWithDefaultPrettyPrinter()
	                                          .writeValueAsString(response.jsonPath().getMap(""));

	            test.log(Status.INFO, "<b>🌐 Endpoint:</b> " + endpoint);
	            test.log(Status.INFO, "<b>🆔 GMID:</b> " + (gmid != null ? gmid : "N/A"));
	            test.log(Status.INFO, "<b>⏱ Response Time:</b> " + response.getTime() + " ms");
	            test.log(Status.INFO, "<b>💻 HTTP Status:</b> " + response.statusCode());

	            test.log(Status.INFO, "<details><summary>📤 <b>Request Payload</b></summary><pre>"
	                    + prettyPayload + "</pre></details>");
	            test.log(Status.INFO, "<details><summary>📥 <b>Response Body</b></summary><pre>"
	                    + prettyResponse + "</pre></details>");

	            // ✅/❌ badge
	            boolean passed = "success".equalsIgnoreCase(response.jsonPath().getString("status"));
	            if (passed) test.pass("✅ Test passed");
	            else test.fail("❌ Test failed");

	        } catch (Exception e) {
	            test.log(Status.WARNING, "⚠️ JSON Parsing Failed: " + e.getMessage());
	        }
	    }
    
}
