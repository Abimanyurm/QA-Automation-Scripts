package ibportal;

import base.BaseApi;

import config.configManager;
import payloads.ConfirmPagePayload;
import payloads.DeleteShopPayload;
import payloads.GlobalPayloads;
import payloads.OtpPagePayload;
import payloads.ShopDetailsPayload;
import payloads.SubmitPagePayload;
import payloads.channelPayload;
import utils.TestDataProvider;
import utils.ExcelUtils;
import utils.ExtentManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import payloads.kycPayload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.*;
import payloads.channelPayload.ChannelRequest;

import com.aventstack.extentreports.*;
import com.fasterxml.jackson.core.JsonProcessingException;

public class useAccount extends BaseApi {

	private static final String passed = null;
	private String createEndpoint;
	private String updateEndpoint;
	private String fetchEndpoint;
	private static String groupMerchantId;
	static int writtenCount;

	private static ExtentReports extent;
	private static ExtentTest test;
	private static List<String> deletedMids = new ArrayList<>();
    private static List<String> activeMids  = new ArrayList<>();

	@BeforeClass
	public void setupEndpoints() {
		createEndpoint = configManager.getProperty("createAccountEndpoint");
		updateEndpoint = configManager.getProperty("updateAccountEndpoint");
		fetchEndpoint = configManager.getProperty("fetch.Details");
		extent = ExtentManager.getInstance();
	}

	@AfterClass
	public void tearDown() {
		if (extent != null) {
			extent.flush();
		}
	}

	// 🔹 Unified professional logging method
	private void logTestResult(String testCase, String endpoint, Object payload, Response response, boolean passed) {
		long responseTime = response.getTime();
		int statusCode = response.getStatusCode();
		String actualStatus = response.jsonPath().getString("status");
		String gmIdLog = groupMerchantId != null ? groupMerchantId : "N/A";

		// Console log with color codes
		System.out.printf(
				"\u001B[36mTestCase:\u001B[0m %-35s | \u001B[32mExpected:\u001B[0m %-8s | \u001B[33mActual:\u001B[0m %-8s | \u001B[35mResult:\u001B[0m %-5s | \u001B[34mHTTP:\u001B[0m %d | \u001B[36mGMID:\u001B[0m %s | \u001B[33mRespTime:\u001B[0m %dms%n",
				testCase, response.jsonPath().getString("status"), actualStatus, (passed ? "PASS" : "FAIL"), statusCode,
				gmIdLog, responseTime);

		// ExtentReport logging
		test.info("<b>🔹 Endpoint:</b> " + endpoint);
		test.info("<b>📌 GMID:</b> " + gmIdLog);
		test.info("<b>⏱ Response Time:</b> " + responseTime + " ms");
		test.info("<b>📂 Payload:</b><pre style='color:purple;'>" + payload + "</pre>");
		test.info("<b>📥 Response:</b><pre style='color:green;'>" + response.asString() + "</pre>");

		if (passed)
			test.pass("<span style='color:green;font-weight:bold;'>✅ Test Passed</span>");
		else
			test.fail("<span style='color:red;font-weight:bold;'>❌ Test Failed</span>");
	}

	// ----------------------- ACCOUNT CREATE / UPDATE -----------------------
	@Test(dataProvider = "accountData", dataProviderClass = TestDataProvider.class)
	public void createOrUpdateAccount(String accountNo, String cifNumber, String gmId, String expectedStatus,
			String testCase, boolean isUpdate) throws InterruptedException {

		test = extent.createTest(testCase).assignCategory(isUpdate ? "Update" : "Create");

		GlobalPayloads payload = new GlobalPayloads();
		payload.setAccountNo(accountNo);
		payload.setCifNumber(cifNumber);

		if (isUpdate) {
			payload.setGroupMerchantId(groupMerchantId != null ? groupMerchantId : gmId);
		}
		String endpoint = isUpdate ? updateEndpoint + payload.getGroupMerchantId() : createEndpoint;

		Response response = RestAssured.given().body(payload).request(isUpdate ? "PUT" : "POST", endpoint);

		boolean passed = expectedStatus.equals(response.jsonPath().getString("status"));

		logTestResult(testCase, endpoint, payload, response, passed);

		if (!isUpdate && "success".equals(expectedStatus)) {
			String gmIdFromResponse = response.jsonPath().getString("data.groupMerchantId");
			if (gmIdFromResponse != null && !gmIdFromResponse.isEmpty()) {
				groupMerchantId = gmIdFromResponse;
				Thread.sleep(500);
				configManager.setProperty("latest.gmid", groupMerchantId);

			} else {
				test.skip("GM ID not found – skipping remaining tests");
				throw new SkipException("GM ID not found – skipping all remaining tests.");
			}
		}

		Assert.assertEquals(response.jsonPath().getString("status"), expectedStatus,
				"Status mismatch for test case: " + testCase);
	}

	@Test(priority = 2)
	public void crossCase_getMerchantWithoutKyc() throws InterruptedException {

		String testCase = "CrossCase – Get merchant without submission of KYC details";
		test = extent.createTest(testCase).assignCategory("Negative");

		String channelName = configManager.getProperty("channels." + "C01");
		channelPayload posChannel = new channelPayload(channelName, true);
		ChannelRequest requestPayload = new ChannelRequest(Arrays.asList(posChannel));

		String gmId = groupMerchantId != null ? groupMerchantId : configManager.getProperty("account.gmidin");

		Thread.sleep(200);

		// Update channel
		RestAssured.given().contentType("application/json").body(requestPayload).when()
				.put(configManager.getProperty("updateChannelEndpoint") + gmId);

		Thread.sleep(200);

		GlobalPayloads payload2 = TestDataProvider.getSingleMerchantData();

		String endpoint = fetchEndpoint + gmId;

		Thread.sleep(200);
		Response response2 = RestAssured.given().body(payload2).request("GET", endpoint);

		int actualCode = response2.getStatusCode();
		int expectedCode = 200;
		Thread.sleep(200);

		List<String> merchantIds = response2.jsonPath().getList("Merchants._id");
		String actual = (merchantIds != null && !merchantIds.isEmpty()) ? merchantIds.get(0) : null;

		Assert.assertEquals(actualCode, expectedCode, "Status code mismatch");
		Assert.assertTrue(actual == null || actual.isEmpty(),
				"Merchant ID should be null or empty when no KYC submission");

		logTestResult(testCase, endpoint, requestPayload, response2, merchantIds == null || merchantIds.isEmpty());

	}

	@Test(priority = 3)
	public void crossCase_getComfirmationSubmissionWithoutKyc() throws InterruptedException {

		// 🔹 Start Extent test
		String testCase = "CrossCase – ConfirmPage submission without Kyc details";
		test = extent.createTest(testCase).assignCategory("Negative");

		// 🔹 Prepare Request Payload
		boolean termsCondition = Boolean.parseBoolean(configManager.getProperty("confirmPage.key3"));
		ConfirmPagePayload.ConfirmPage confirmPage = new ConfirmPagePayload.ConfirmPage(termsCondition);
		ConfirmPagePayload requestPayload = new ConfirmPagePayload(confirmPage);

		// 🔹 Execute API Request
		String gmId = groupMerchantId;
		String endpoint = updateEndpoint + gmId;
		Response response = RestAssured.given().body(requestPayload).put(endpoint);
		// 🔹 Expected & Actual
		String expectedStatus = "failed";
		String expectedError = "Need to add location details in kyc page"; // ✅ updated expected message

		String actualStatus = response.jsonPath().getString("status");
		String actualError = response.jsonPath().getString("error");

		// 🔹 Log & Assert
		boolean passed = expectedStatus.equals(actualStatus) && expectedError.equals(actualError);
		logTestResult(testCase, endpoint, requestPayload, response, passed);

		Assert.assertEquals(actualStatus, expectedStatus, "Status mismatch for test case: " + testCase);
		Assert.assertEquals(actualError, expectedError, "Error message mismatch for test case: " + testCase);
	}

	@Test(dataProvider = "kycDatas", dataProviderClass = TestDataProvider.class, priority = 4) // run after account
	// create/update
	public void crossCase_KycThenShopWithoutChannel(String groupMerchant, String commerceFile, String commerceNumber,
			int location, String passportFile, String passportNumber, String expectedStatus, String testCase,
			boolean useValidGm) throws InterruptedException {

		if (!useValidGm) {
			throw new SkipException("Skipping this test as useValidGm is false");
		}

		// 1️⃣ Start Extent test
		test = extent.createTest("CrossCase – KYC then Shop without POS").assignCategory("Negative");

		channelPayload posChannel = new channelPayload(configManager.getProperty("channels.C01"), false);
		ChannelRequest requestPayload = new ChannelRequest(Arrays.asList(posChannel));

		String gmId = groupMerchantId != null ? groupMerchantId : configManager.getProperty("account.gmidin");

		Response response0 = RestAssured.given().contentType("application/json").body(requestPayload).when()
				.put(configManager.getProperty("updateChannelEndpoint") + gmId);

		Thread.sleep(2000);

		// ----------------- KYC UPDATE -----------------
		kycPayload payload = new kycPayload(commerceFile, commerceNumber, location, passportFile, passportNumber);

		Response response = RestAssured.given().contentType("application/json").body(payload).when()
				.put(updateEndpoint + gmId);

		boolean passed1 = expectedStatus.equals(response.jsonPath().getString("status"));

		Thread.sleep(2000);

		// --------------- MERCHANT DETAILS ------------------

		GlobalPayloads payload2 = new GlobalPayloads();
		payload2.setAccountNo(config.configManager.getProperty("account.value1"));
		payload2.setCifNumber(config.configManager.getProperty("account.value2"));
		
		ExcelUtils.clearMerchantIds();//clear merchant data

		String endpoint = fetchEndpoint + gmId;

		Response response2 = RestAssured.given().body(payload2).request("GET", endpoint);
		

		// Defensive null / empty handling for merchantIds
		List<String> merchantIds = response2.jsonPath().getList("Merchants._id");
		int writtenCount = 0;
		if (merchantIds != null && !merchantIds.isEmpty()) {
			writtenCount = ExcelUtils.writeMerchantId(merchantIds);
			System.out.println("\nNo of MIDs created: " + writtenCount);
		} else {
			System.out.println("No MIDs were created!");
		}

		Thread.sleep(2000);

		// ----------------- SHOP LOCATION -----------------

		Thread.sleep(2000);
		ShopDetailsPayload payload1 = TestDataProvider.getSingleShopDetailsPayload();

		Response response1 = RestAssured.given().body(payload1).request("PUT", updateEndpoint + gmId);

		Assert.assertEquals(response.jsonPath().getString("status"), "success", "Shop details creation failed");

		// ✅ Validate the *negative* expected result
		String actualStatus = response1.jsonPath().getString("status");
		String actualError = response1.jsonPath().getString("error");

		boolean passedfinal = "failed".equals(actualStatus)
				&& "Pos is not selected in choose service page".equals(actualError)
				&& expectedStatus.equals(response.jsonPath().getString("status")) && writtenCount != 0
				&& "success".equals(response0.jsonPath().getString("status"));

		Thread.sleep(2000);

		logTestResult("CrossCase – Shop Details (Negative)", updateEndpoint + gmId, payload1, response1, passedfinal);
		
		ExcelUtils.clearMerchantIds();//clear merchant data

		// Hard assertion for report
		Assert.assertEquals(actualStatus, "failed", "Status should be failed");
		Assert.assertEquals(actualError, "Pos is not selected in choose service page", "Error message mismatch");

	}

	// ----------------------- CHANNEL UPDATE -----------------------
	@Test(dataProvider = "channelData", dataProviderClass = TestDataProvider.class, priority = 5)
	public void updateChannels(String channelKey, boolean selected, String testCase, String expectedStatus)
			throws JsonProcessingException {

		test = extent.createTest(testCase).assignCategory("Channel Update");

		String channelName = configManager.getProperty("channels." + channelKey);
		channelPayload posChannel = new channelPayload(channelName, selected);
		ChannelRequest requestPayload = new ChannelRequest(Arrays.asList(posChannel));

		String gmId = groupMerchantId != null ? groupMerchantId : configManager.getProperty("account.gmidin");

		//System.out.println(new ObjectMapper().writeValueAsString(requestPayload));

		Response response = RestAssured.given().contentType("application/json").body(requestPayload).when()
				.put(configManager.getProperty("updateChannelEndpoint") + gmId);

		boolean passed = expectedStatus.equals(response.jsonPath().getString("status"));

		logTestResult(testCase, configManager.getProperty("updateChannelEndpoint") + gmId, requestPayload, response,
				passed);

		Assert.assertEquals(response.jsonPath().getString("status"), expectedStatus,
				"Channel update status mismatch for test case: " + testCase);
	}

	// ----------------------- KYC UPDATE -----------------------
	@Test(dataProvider = "kycData", dataProviderClass = TestDataProvider.class, priority = 6)
	public void updateKyc(String groupMerchant, String commerceFile, String commerceNumber, int location,
			String passportFile, String passportNumber, String expectedStatus, String testCase, boolean useValidGm)
			throws InterruptedException {

		test = extent.createTest(testCase).assignCategory("KYC Update");

		kycPayload payload = new kycPayload(commerceFile, commerceNumber, location, passportFile, passportNumber);

		String gmId = groupMerchant;
		Response response = RestAssured.given().contentType("application/json").body(payload).when()
				.put(updateEndpoint + gmId);

		boolean passed = expectedStatus.equals(response.jsonPath().getString("status"));

		logTestResult(testCase, updateEndpoint + gmId, payload, response, passed);

		Thread.sleep(2000);
		Assert.assertEquals(response.jsonPath().getString("status"), expectedStatus,
				String.format("Case: %s | Expected: %s | Actual: %s", testCase, expectedStatus,
						response.jsonPath().getString("status")));
	}

	// ----------------------- FETCH CUSTOMER DETAILS -----------------------
	@Test(dataProvider = "merchantData", dataProviderClass = TestDataProvider.class, priority = 7)
	public void fetchDetails(String accountNo, String cifNumber, String gmId, String expectedStatus, String testCase,
			boolean isUpdate) throws InterruptedException {

		test = extent.createTest(testCase).assignCategory("Customer details enquiry");
		
		ExcelUtils.clearMerchantIds();

		GlobalPayloads payload = new GlobalPayloads();
		payload.setAccountNo(accountNo);
		payload.setCifNumber(cifNumber);

		String endpoint = fetchEndpoint + (groupMerchantId != null ? groupMerchantId : gmId);
		

		Response response = RestAssured.given().body(payload).request("GET", endpoint);

		boolean passed = response.getStatusCode() == 200;
		//logTestResult(testCase, endpoint, payload, response, passed);

		// Reset writtenCount before writing new data
		writtenCount = 0;

		// Defensive handling for merchantIds
		List<String> merchantIds = response.jsonPath().getList("Merchants._id");

		int shopLocationCountAfter = safeInt(response, "Merchants.locations.size()");
		int merchantLocCountAfter = safeInt(response, "numberOfMerchantLocations");
		int softPosLocCountAfter = safeInt(response, "numberOfSoftPosMerchantLocations");

		int totalLocationCountAfter = shopLocationCountAfter + merchantLocCountAfter + softPosLocCountAfter;

		if (merchantIds != null && !merchantIds.isEmpty()) {
			writtenCount = ExcelUtils.writeMerchantId(merchantIds);
			System.out.println("\n===========================================");
		    System.out.println("🟢 Total merchants created = " + writtenCount);
		    System.out.println("===========================================");
		    
		} else {
			System.out.println("No merchant IDs fetched; nothing written to Excel.");
		}

		boolean passed1 = (writtenCount == totalLocationCountAfter);
		
		logTestResult(testCase, updateEndpoint + gmId, payload, response, passed);

		Thread.sleep(2000);
		Assert.assertEquals(response.getStatusCode(), 200, "Status mismatch for test case: " + testCase);
	}
	
	 /** STEP 1️: Delete merchant(s) and store deleted MIDs */
    @Test(dataProvider = "deleteShopData",dataProviderClass = TestDataProvider.class, priority = 8)
    public void deleteMerchants(DeleteShopPayload deletePayload) throws InterruptedException {
    	
    	test = extent.createTest("shop deletion before or /After location ").assignCategory("Delete operations");

        String endpoint = updateEndpoint + groupMerchantId;
        Response resp = RestAssured.given().body(deletePayload).put(endpoint);
        
        int statusCode = resp.getStatusCode();
        String status = resp.jsonPath().getString("status");

        boolean isPassed = (statusCode == 200) && "success".equalsIgnoreCase(status);

        Assert.assertEquals(resp.getStatusCode(), 200, "Delete API failed");
        Assert.assertEquals(resp.jsonPath().getString("status"), "success",
                "Delete status not success for MID: " + deletePayload.getMerchantId());

        deletedMids.add(deletePayload.getMerchantId());
        System.out.println("✅ Deleted MID stored: " + deletePayload.getMerchantId());
        Thread.sleep(300); // slight pause for DB consistency
        logTestResult("shop deletion before or /After location", updateEndpoint + groupMerchantId, deletePayload, resp, isPassed);
    }

    @Test(dependsOnMethods = "deleteMerchants",dataProviderClass = TestDataProvider.class, priority = 9)
    public void validateDeletedMerchants() {
    	
    	test = extent.createTest("Merchant application status check post removal ").assignCategory("Delete operations");

        GlobalPayloads payload = new GlobalPayloads();
        payload.setAccountNo(configManager.getProperty("account.value1"));
        payload.setCifNumber(configManager.getProperty("account.value2"));
        
        Response resp = RestAssured.given().body(payload).get(fetchEndpoint + groupMerchantId);

        Assert.assertEquals(resp.getStatusCode(), 200, "❌ Fetch API failed");

        List<Map<String, Object>> merchants = resp.jsonPath().getList("Merchants");
        Assert.assertNotNull(merchants, "❌ 'Merchants' array missing in response");

        for (String mid : deletedMids) {

            // 🔍 Find the matching group merchant by _id
            Map<String, Object> groupMerchant = merchants.stream()
                    .filter(m -> mid.equals(m.get("_id")))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("❌ Group Merchant not found: " + mid));

            // 📦 Extract the nested Merchants array
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> innerMerchants = (List<Map<String, Object>>) groupMerchant.get("Merchants");
            if (innerMerchants == null || innerMerchants.isEmpty()) {
                throw new AssertionError("❌ No nested Merchants found for MID: " + mid);
            }

            // 🏷 Loop through each nested Merchant
            for (Map<String, Object> inner : innerMerchants) {

                // ✅ Get the latest status & description from inner.statusHistory
                String latestStatus = "N/A";
                String latestDesc   = "N/A";

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> history = (List<Map<String, Object>>) inner.get("statusHistory");
                if (history != null && !history.isEmpty()) {
                    Map<String, Object> lastEntry = history.get(history.size() - 1);
                    latestStatus = String.valueOf(lastEntry.getOrDefault("status", "N/A"));
                    latestDesc   = String.valueOf(lastEntry.getOrDefault("description", "No description"));
                }
                
                String expected_status="closed";
                String expected_desc="Reduce location while self onboarding";
                
                boolean isPassed=expected_status.equalsIgnoreCase(latestStatus)&&expected_desc.equalsIgnoreCase(latestDesc);
                
                
                

            // 🔎 Log details for reporting
            System.out.println("✅ MID: " + mid + " → Status: " + latestStatus + ", Description: " + latestDesc);
            
            logTestResult("Merchant application status check post removal", updateEndpoint + groupMerchantId, payload, resp,isPassed);

            Assert.assertEquals(false, null);

            }}
    }


	// ----------------------- SHOP DETAILS -----------------------
	@Test(dataProvider = "shopDetailsData", dataProviderClass = TestDataProvider.class, priority = 10)
	public void createOrUpdateShopDetails(ShopDetailsPayload payload) {

		test = extent.createTest("Shop Details Test for Merchant").assignCategory("Shop Details");

		String gmId = groupMerchantId;
		
		 GlobalPayloads payload2 = new GlobalPayloads();
	        payload2.setAccountNo(configManager.getProperty("account.value1"));
	        payload2.setCifNumber(configManager.getProperty("account.value2"));

	        Response resp = RestAssured.given().body(payload2).get(fetchEndpoint + groupMerchantId);
	        Assert.assertEquals(resp.getStatusCode(), 200);

	        // filter merchants whose status != Closed
	        List<Map<String,Object>> merchants = resp.jsonPath().getList("Merchants");
	        activeMids = merchants.stream()
	                .filter(m -> !"closed".equalsIgnoreCase(String.valueOf(m.get("status"))))
	                .map(m -> String.valueOf(m.get("_id")))
	                .collect(Collectors.toList());

	        if (activeMids.isEmpty()) {
	            System.out.println("No active merchants to update.");
	            return;
	        }

	        // write active mids to Excel
	        int writtenCount = ExcelUtils.writeMerchantId(activeMids);
	        System.out.println("🟢 Active merchants written to Excel: " + writtenCount);
	        
	        
		
		Response response = RestAssured.given().body(payload).request("PUT", updateEndpoint + gmId);

		boolean passed = "success".equals(response.jsonPath().getString("status"));

		logTestResult("Shop Details Test", updateEndpoint + gmId, payload, response, passed);

		Assert.assertEquals(response.jsonPath().getString("status"), "success", "Shop details creation failed");
	}
   // // Optional final validation (reuse deleted list) */
   //    @Test(dependsOnMethods = "shopDetailsData", priority = 4)
   //    public void revalidateAfterUpdate() {
   //    Simply reuse the same validation logic
   //    validateDeletedMerchants();
   //    }
	
	// ----------------------- CONFIRM PAGE -----------------------
	@Test(dataProvider = "confirmPageData", dataProviderClass = TestDataProvider.class, priority = 11)
	public void updateConfirmPage(String termsConditionValue, String testCase, String expectedStatus) {

		test = extent.createTest(testCase).assignCategory("UserConfirm");

		boolean termsCondition = Boolean.parseBoolean(termsConditionValue);
		ConfirmPagePayload.ConfirmPage confirmPage = new ConfirmPagePayload.ConfirmPage(termsCondition);
		ConfirmPagePayload requestPayload = new ConfirmPagePayload(confirmPage);

		String gmId = groupMerchantId;
		Response response = RestAssured.given().body(requestPayload).request("PUT", updateEndpoint + gmId);

		boolean passed = expectedStatus.equals(response.jsonPath().getString("status"));

		logTestResult(testCase, updateEndpoint + gmId, requestPayload, response, passed);

		Assert.assertEquals(response.jsonPath().getString("status"), expectedStatus,
				"Status mismatch for test case: " + testCase);
	}

	// ----------------------- OTP PAGE -----------------------
	@Test(dataProvider = "otpData", dataProviderClass = TestDataProvider.class, priority = 12)
	public void verifyOtpPage(String otp, String otpFrom, String testCase, String expectedStatus) {

		test = extent.createTest(testCase).assignCategory("OTP");

		OtpPagePayload requestPayload = new OtpPagePayload(otp, otpFrom);

		String gmId = groupMerchantId;
		Response response = RestAssured.given().body(requestPayload).request("PUT", updateEndpoint + gmId);

		boolean passed = expectedStatus.equals(response.jsonPath().getString("status"));

		logTestResult(testCase, updateEndpoint + gmId, requestPayload, response, passed);

		Assert.assertEquals(response.jsonPath().getString("status"), expectedStatus,
				"Status mismatch for test case: " + testCase);
	}

	// ----------------------- SUBMIT PAGE -----------------------
	@Test(dataProvider = "submitPageData", dataProviderClass = TestDataProvider.class, priority = 13)
	public void verifySubmitPage(String submitPageValue, String testCase, String expectedStatus) {

		test = extent.createTest(testCase).assignCategory("SubmitPage");

		boolean submitPage = Boolean.parseBoolean(submitPageValue);
		SubmitPagePayload requestPayload = new SubmitPagePayload(submitPage);

		String gmId = groupMerchantId;
		Response response = RestAssured.given().body(requestPayload).request("PUT", updateEndpoint + gmId);

		boolean passed = expectedStatus.equals(response.jsonPath().getString("status"));

		logTestResult(testCase, updateEndpoint + gmId, requestPayload, response, passed);

		Assert.assertEquals(response.jsonPath().getString("status"), expectedStatus,
				"Status mismatch for test case: " + testCase);
		System.out.println("***********created****************");
	}

	// 🔧 helper to safely get integers
	private int safeInt(Response resp, String path) {
		Object val = resp.jsonPath().get(path);
		return (val == null) ? 0 : resp.jsonPath().getInt(path);
	}
}


