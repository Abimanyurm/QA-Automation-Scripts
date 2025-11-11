package ibportal;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import base.BaseApi;
import config.configManager;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import utils.InventoryDataProvider;
import utils.InventoryUtils;

public class InventoryManangement extends BaseApi {

	private String mmsEndpoint;
    private String mmsFinal;
    private String getEndpoint;
    private int totalCount;

    private static final int MAX_REQUESTS_BEFORE_PAUSE = 9;
    private static final int SHORT_DELAY_MS = 300;       // 300-700ms
    private static final int LONG_PAUSE_MS = 904000;      // 30s
    private static final int FIREWALL_PAUSE_MS = 904000;  // 60s

    @BeforeClass
    public void setup() {
        mmsEndpoint = configManager.getProperty("mms-endpoint");
        mmsFinal = configManager.getProperty("mms-final");
        getEndpoint = configManager.getProperty("getEndpoint");

        // Update auth token
        this.setAuthToken(configManager.getProperty("authToken"));

        // Clear old Inventory IDs
        InventoryUtils.clearInventoryIds();

        System.out.println("✅ MMS Endpoint: " + mmsEndpoint);
        System.out.println("✅ MMS Final: " + mmsFinal);
    }

    @Test
    public void testFetchAllInventoryDocs() {
        try {
            // Step 1: Fetch totalCount with default limit
            String initialUrl = mmsEndpoint + "10" + mmsFinal;
            Response initialResponse = request("GET", initialUrl, null);

            if (!isJsonResponse(initialResponse)) {
                System.out.println("❌ Failed to get initial totalCount or blocked by firewall");
                return;
            }

            totalCount = initialResponse.jsonPath().getInt("totalCount");
            System.out.println("✅ Total Count from MMS API: " + totalCount);

            if (totalCount <= 0) {
                System.out.println("⚠️ No inventory docs found, skipping further steps");
                return;
            }

            // Step 2: Fetch all docs using totalCount
            String finalUrl = mmsEndpoint + totalCount + mmsFinal;
            Response finalResponse = request("GET", finalUrl, null);

            if (!isJsonResponse(finalResponse)) {
                System.out.println("❌ Failed to fetch all inventory docs or blocked by firewall");
                return;
            }

            // Extract _id list from docs array
            List<Map<String, Object>> docs = finalResponse.jsonPath().getList("docs");
            List<String> ids = docs.stream().map(doc -> (String) doc.get("_id")).collect(Collectors.toList());

            System.out.println("✅ Total IDs fetched: " + ids.size());

            InventoryUtils.writeInventoryIds(ids);

        } catch (Exception e) {
            System.out.println("❌ Exception in testFetchAllInventoryDocs: " + e.getMessage());
        }
    }

    @Test(priority = 2, dataProvider = "InventoryIds", dataProviderClass = InventoryDataProvider.class)
    public void testFetchInventoryById(String objectId) throws InterruptedException {
        // Increment request counter
        InventoryDataProvider.reference++;

        // Long pause after every MAX_REQUESTS_BEFORE_PAUSE requests
        if (InventoryDataProvider.reference % MAX_REQUESTS_BEFORE_PAUSE == 0) {
            System.out.println("⏳ Pausing " + (LONG_PAUSE_MS / 1000) + "s to avoid firewall block...");
            Thread.sleep(LONG_PAUSE_MS);
        }

        // Random short delay
        Thread.sleep(SHORT_DELAY_MS + (int) (Math.random() * 400));

        String endpoint = getEndpoint + objectId;
        //Response response = request("GET", endpoint, null);

        if (!isJsonResponse(response)) {
            System.out.println("❌ Failed to fetch inventory for ID: " + objectId);
            return;
        }

        // Detect WAF/Firewall block (HTML page)
        String bodyStr = response.getBody().asString();
        if (bodyStr.contains("Blocked Request") || bodyStr.contains("Support ID")) {
            System.out.println("⚠️ Firewall block detected for ObjectId: " + objectId + ". Pausing 10 min...");
            Thread.sleep(FIREWALL_PAUSE_MS);
            return;
        }

        System.out.println("✅ Response received for ObjectId: " + objectId);
        System.out.println(response.getBody().asPrettyString());

        // Extract terminal data and write to Excel
        JsonPath json = response.jsonPath();
        Thread.sleep(2000);
        InventoryUtils.extractTerminalData(json, objectId);
        Thread.sleep(1000);
    }

    // Utility method to check if response is JSON and not blocked
    private boolean isJsonResponse(Response response) {
        if (response == null) return false;
        String body = response.getBody().asString();
        return response.getContentType() != null
                && response.getContentType().contains("application/json")
                && !body.contains("Blocked Request")
                && !body.contains("Support ID")
                && !body.startsWith("<HTML");
    }

}