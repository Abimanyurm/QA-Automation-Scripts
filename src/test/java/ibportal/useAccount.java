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
import payloads.globalDeletePayload;

import com.aventstack.extentreports.*;
import com.fasterxml.jackson.core.JsonProcessingException;

public class useAccount extends BaseApi {

	private static final String passed = null;
	private String createEndpoint;
	private String updateEndpoint;
	private String fetchEndpoint;
	
	private String globalDeleteEndpoint;
	private static String groupMerchantId;
	private String account;
	private String cif;
	
	static int writtenCount;

	private static ExtentReports extent;
	private static ExtentTest test;
	private static List<String> deletedMids = new ArrayList<>();
	private static List<String> activeMids = new ArrayList<>();

	@BeforeClass
	public void setupEndpoints() {
		createEndpoint = configManager.getProperty("createAccountEndpoint");
		updateEndpoint = configManager.getProperty("updateAccountEndpoint");
		fetchEndpoint = configManager.getProperty("fetch.Details");
		globalDeleteEndpoint = configManager.getProperty("deleteGlobal");
		extent = ExtentManager.getInstance();
	}

	@AfterClass
	public void tearDown() {
		if (extent != null) {
			extent.flush();
		}
	}

	// 🔹 Unified professional logging method
	@SuppressWarnings("unused")
	private void logTestResult(String testCase, String endpoint, Object payload, Response response, boolean passed) {
		long responseTime = response.getTime();
		int statusCode = response.getStatusCode();
		String actualStatus = response.jsonPath().getString("status");
		String gmIdLog = groupMerchantId != null ? groupMerchantId : "N/A";
		
	    if (response == null) {
	        test.fail("<div style='background:#111;color:#fff;padding:10px;border-radius:8px;'>"
	                + "<b>🔹 Test Case:</b> " + testCase + "<br>"
	                + "<b>📌 Endpoint:</b> " + endpoint + "<br>"
	                + "<b>📂 Payload:</b><pre style='background:#222;color:#fff;padding:10px;border-radius:8px;'>"
	                + payload + "</pre>"
	                + "<b>❌ No Response Received from API</b>"
	                + "</div>");
	        return; // skip further logging
	    }

		// Console log for quick debug
		System.out.printf(
				"\u001B[36mTestCase:\u001B[0m %-35s | \u001B[32mExpected:\u001B[0m %-8s | \u001B[33mActual:\u001B[0m %-8s | \u001B[35mResult:\u001B[0m %-5s | \u001B[34mHTTP:\u001B[0m %d | \u001B[36mGMID:\u001B[0m %s | \u001B[33mRespTime:\u001B[0m %dms%n",
				testCase, actualStatus, actualStatus, (passed ? "PASS" : "FAIL"), statusCode, gmIdLog, responseTime);

		// ExtentReport logging with dark theme
		test.info("<div style='background:#222;padding:10px;border-radius:8px;margin:5px 0;color:#fff;'>"
				+ "<b>🔹 Test Case:</b> " + testCase + "<br>" + "<b>📌 Endpoint:</b> " + endpoint + "<br>"
				+ "<b>📌 GMID:</b> " + gmIdLog + "<br>" + "<b>⏱ Response Time:</b> " + responseTime + " ms<br>"
				+ "<b>📂 Payload:</b><pre style='background:#000;color:#fff;padding:10px;border-radius:8px;'>" + payload
				+ "</pre>"
				+ "<b>📥 Response:</b><pre style='background:#000;color:#fff;padding:10px;border-radius:8px;'>"
				+ response.asPrettyString() + "</pre></div>");

		if (passed) {
			test.pass("<span style='color:green;font-weight:bold;'>✅ Test Passed</span>");
		} else {
			test.fail("<span style='color:red;font-weight:bold;'>❌ Test Failed</span>");
		}
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
				account=accountNo;
				cif=cifNumber;
				
				Thread.sleep(500);
				configManager.setProperty("latest.gmid", groupMerchantId);

			} else {
				test.skip("GM ID not found – skipping remaining tests");
				throw new SkipException("GM ID not found – skipping all remaining tests.");
			}
		}

		
		System.out.println("Git update");
		System.out.println("Git update");
		
		Assert.assertEquals(response.jsonPath().getString("status"), expectedStatus,
				"Status mismatch for test case: " + testCase);
	}

	@Test(dataProvider = "channelData", dataProviderClass = TestDataProvider.class, priority = 2)
	public void crossCase_getMerchantWithoutKyc(String channelKey, boolean selected, String testCases,
			String expectedStatus) throws InterruptedException {

		String testCase = "CrossCase – Get merchant without submission of KYC details";
		test = extent.createTest(testCase).assignCategory("Negative");

		ChannelRequest requestPayload;

		// String channelName = configManager.getProperty("channels.C01");

		if (channelKey.equalsIgnoreCase("POS+softPOS")) {

			channelPayload posChannel1 = new channelPayload(configManager.getProperty("channels.C01"), true);
			channelPayload posChannel2 = new channelPayload(configManager.getProperty("channels.C02"), true);
			requestPayload = new ChannelRequest(Arrays.asList(posChannel1, posChannel2));

		} else {
			channelPayload posChannel = new channelPayload(channelKey, true);
			requestPayload = new ChannelRequest(Arrays.asList(posChannel));

		}

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

	@Test(dataProvider = "kycDatas", dataProviderClass = TestDataProvider.class, priority = 4)
	public void crossCase_KycThenShopWithoutChannel(String groupMerchant, String commerceFile, String commerceNumber,
			int location, int softposloc, String passportFile, String passportNumber, String expectedStatus,
			String testCase, boolean useValidGm, String selectedChannel) throws InterruptedException {

		if (!useValidGm) {
			throw new SkipException("Skipping this test as useValidGm is false");
		}	

		test = extent.createTest("CrossCase – KYC then Shop without POS").assignCategory("Negative");

		// ----------------- CHANNEL UPDATE (POS not selected) -----------------
		channelPayload posChannel = new channelPayload(configManager.getProperty("channels.C01"), false);
		ChannelRequest requestPayload = new ChannelRequest(Arrays.asList(posChannel));

		String gmId = groupMerchantId != null ? groupMerchantId : configManager.getProperty("account.gmidin");

		Response response0 = RestAssured.given().contentType("application/json").body(requestPayload)
				.put(configManager.getProperty("updateChannelEndpoint") + gmId);

		Thread.sleep(2000);

		// ----------------- KYC UPDATE ----------------
		kycPayload payload = new kycPayload(commerceFile, commerceNumber, location, softposloc, passportFile,
				passportNumber);

		// ✅ Adjust location counts based on channel

		//payload.getKyc().adjustLocationsBasedOnChannel(selectedChannel);

		Response response = RestAssured.given().contentType("application/json").body(payload)
				.put(updateEndpoint + gmId);
		boolean passed1 = expectedStatus.equals(response.jsonPath().getString("status"));

		Thread.sleep(2000);

		// ----------------- FETCH MERCHANT DETAILS -----------------
		GlobalPayloads payload2 = new GlobalPayloads();
		payload2.setAccountNo(configManager.getProperty("account.value1"));
		payload2.setCifNumber(configManager.getProperty("account.value2"));

		// Fetch response
		String endpoint = fetchEndpoint + (groupMerchantId != null ? groupMerchantId : gmId);
		Response responses = RestAssured.given().body(payload).request("GET", endpoint);

		// Extract merchants
		List<Map<String, Object>> merchants = responses.jsonPath().getList("Merchants");

		List<String> merchantIds = new ArrayList<>();
		List<String> merchantTypes = new ArrayList<>();

		for (Map<String, Object> merchant : merchants) {
			merchantIds.add((String) merchant.get("_id"));

			String merchantType = "POS"; // default
			if (merchant.get("editedDetails") != null) {
				Map<String, Object> editedDetails = (Map<String, Object>) merchant.get("editedDetails");
				if (editedDetails.get("companyInformation") != null) {
					Map<String, Object> companyInfo = (Map<String, Object>) editedDetails.get("companyInformation");
					if (companyInfo.get("merchantType") != null) {
						merchantType = companyInfo.get("merchantType").toString();
					}
				}
			}
			merchantTypes.add(merchantType);
		}

		// Write to Excel
		Map<String, Integer> countMap = ExcelUtils.writeMerchantId(merchantIds, merchantTypes);

		int posCount = countMap.getOrDefault("POS", 0);
		int softPosCount = countMap.getOrDefault("SoftPOS", 0);
		int totalWritten = posCount + softPosCount;

		// Log summary
		System.out.println("\n===========================================");
		System.out.println(
				"🟢 Total merchants written = " + totalWritten + " | POS: " + posCount + " | SoftPOS: " + softPosCount);
		System.out.println("===========================================");
		Thread.sleep(2000);

		// ----------------- SHOP LOCATION -----------------
		ShopDetailsPayload payload1 = TestDataProvider.getSingleShopDetailsPayload();
		Response response1 = RestAssured.given().body(payload1).request("PUT", updateEndpoint + gmId);

		String actualStatus = response1.jsonPath().getString("status");
		String actualError = response1.jsonPath().getString("error");

		boolean passedFinal = "failed".equals(actualStatus)
				&& "Pos is not selected in choose service page".equals(actualError)
				&& expectedStatus.equals(response.jsonPath().getString("status")) && totalWritten != 0
				&& "success".equals(response0.jsonPath().getString("status"));

		logTestResult("CrossCase – Shop Details (Negative)", updateEndpoint + gmId, payload1, response1, passedFinal);

		Response deleteResponse = deleteEntity("delete", "7360640",globalDeleteEndpoint);

		// Optional: Check response status immediately
		if ("success".equalsIgnoreCase(deleteResponse.jsonPath().getString("status"))) {
			System.out.println("Deletion successful ✅");
		} else {
			System.out.println("Deletion failed ❌");
		}
		// Assertions
		Assert.assertEquals(actualStatus, "failed", "Status should be failed");
		Assert.assertEquals(actualError, "Pos is not selected in choose service page", "Error message mismatch");
	}

	// ----------------------- CHANNEL UPDATE -----------------------
	@Test(dataProvider = "channelData", dataProviderClass = TestDataProvider.class, priority = 5)
	public void updateChannels(String channelKey, boolean selected, String testCase, String expectedStatus)
			throws JsonProcessingException, InterruptedException {

		test = extent.createTest(testCase).assignCategory("Channel Update");
		ChannelRequest requestPayload;
		
		

		if (channelKey.equalsIgnoreCase("POS+softPOS")) {

			channelPayload posChannel1 = new channelPayload(configManager.getProperty("channels.C01"), true);
			channelPayload posChannel2 = new channelPayload(configManager.getProperty("channels.C02"), true);
			requestPayload = new ChannelRequest(Arrays.asList(posChannel1, posChannel2));

		} else {
			channelPayload posChannel = new channelPayload(channelKey, true);
			requestPayload = new ChannelRequest(Arrays.asList(posChannel));

		}
        
		GlobalPayloads payload = new GlobalPayloads();
		payload.setAccountNo(account);
		payload.setCifNumber(cif);
		Response response = RestAssured.given().body(payload).request("POST", createEndpoint);
		String gmIdFromResponse = response.jsonPath().getString("data.groupMerchantId");
		if (gmIdFromResponse != null && !gmIdFromResponse.isEmpty()) {
			groupMerchantId = gmIdFromResponse;
			Thread.sleep(500);
			configManager.setProperty("latest.gmid", groupMerchantId);

		} else {
			test.skip("GM ID not found – skipping remaining tests");
			throw new SkipException("GM ID not found – skipping all remaining tests.");
		}
		
		// String channelName = configManager.getProperty("channels." + channelKey);
		// channelPayload posChannel = new channelPayload(channelName, selected);
		// ChannelRequest requestPayload = new
		// ChannelRequest(Arrays.asList(posChannel));

		
				//!= null ? groupMerchantId : configManager.getProperty("account.gmidin");

		// System.out.println(new ObjectMapper().writeValueAsString(requestPayload));

		Response response1 = RestAssured.given().contentType("application/json").body(requestPayload).when()
				.put(configManager.getProperty("updateChannelEndpoint") + groupMerchantId);

		boolean passed = expectedStatus.equals(response1.jsonPath().getString("status"));

		logTestResult(testCase, configManager.getProperty("updateChannelEndpoint") + groupMerchantId, requestPayload, response1,
				passed);

		Assert.assertEquals(response.jsonPath().getString("status"), expectedStatus,
				"Channel update status mismatch for test case: " + testCase);
	}

	// ----------------------- KYC UPDATE -----------------------
	@Test(dataProvider = "kycData", dataProviderClass = TestDataProvider.class, priority = 6)
	public void updateKyc(String groupMerchant, String commerceFile, String commerceNumber, int location,
			int softposloc, String passportFile, String passportNumber, String expectedStatus, String testCase,
			boolean useValidGm, String selectedChannel) throws InterruptedException {

		test = extent.createTest(testCase).assignCategory("KYC Update");

		// ----------------- KYC UPDATE -----------------
		kycPayload payload = new kycPayload(commerceFile, commerceNumber, location, softposloc, passportFile,
				passportNumber);

		// ✅ Adjust location counts based on channel

		// payload.getKyc().adjustLocationsBasedOnChannel(selectedChannel);

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

	@Test(dataProvider = "merchantData", dataProviderClass = TestDataProvider.class, priority = 7)
	public void fetchDetails(String accountNo, String cifNumber, String gmId, String expectedStatus, String testCase,
			boolean isUpdate) throws InterruptedException {

		test = extent.createTest(testCase).assignCategory("Customer details enquiry");

		// Clear previous merchant data
		ExcelUtils.clearMerchantIds();

		// Prepare payload
		GlobalPayloads payload = new GlobalPayloads();
		payload.setAccountNo(accountNo);
		payload.setCifNumber(cifNumber);

		// Fetch response
		String endpoint = fetchEndpoint + (groupMerchantId != null ? groupMerchantId : gmId);
		Response response = RestAssured.given().body(payload).request("GET", endpoint);

		// Extract merchants
		List<Map<String, Object>> merchants = response.jsonPath().getList("Merchants");

		List<String> merchantIds = new ArrayList<>();
		List<String> merchantTypes = new ArrayList<>();

		for (Map<String, Object> merchant : merchants) {
			merchantIds.add((String) merchant.get("_id"));

			String merchantType = "POS"; // default
			if (merchant.get("editedDetails") != null) {
				Map<String, Object> editedDetails = (Map<String, Object>) merchant.get("editedDetails");
				if (editedDetails.get("companyInformation") != null) {
					Map<String, Object> companyInfo = (Map<String, Object>) editedDetails.get("companyInformation");
					if (companyInfo.get("merchantType") != null) {
						merchantType = companyInfo.get("merchantType").toString();
					}
				}
			}
			merchantTypes.add(merchantType);
		}

		// Write to Excel
		Map<String, Integer> countMap = ExcelUtils.writeMerchantId(merchantIds, merchantTypes);

		int posCount = countMap.getOrDefault("POS", 0);
		int softPosCount = countMap.getOrDefault("SoftPOS", 0);
		int totalWritten = posCount + softPosCount;

		// Log summary
		System.out.println("\n===========================================");
		System.out.println(
				"🟢 Total merchants written = " + totalWritten + " | POS: " + posCount + " | SoftPOS: " + softPosCount);
		System.out.println("===========================================");

		// Log detailed test result
		logTestResult(testCase, updateEndpoint + gmId, payload, response, true);

		// Assertion
		Assert.assertEquals(response.getStatusCode(), 200, "Status mismatch for test case: " + testCase);
	}

	/** STEP 1️⃣: Delete merchant(s) and store deleted MIDs */
	@Test(dataProvider = "deleteShopData", dataProviderClass = TestDataProvider.class, priority = 8)
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
		logTestResult("shop deletion before or /After location", updateEndpoint + groupMerchantId, deletePayload, resp,
				isPassed);
	}

	@Test(dependsOnMethods = "deleteMerchants", dataProviderClass = TestDataProvider.class, priority = 9)
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
			Map<String, Object> groupMerchant = merchants.stream().filter(m -> mid.equals(m.get("_id"))).findFirst()
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
				String latestDesc = "N/A";

				@SuppressWarnings("unchecked")
				List<Map<String, Object>> history = (List<Map<String, Object>>) inner.get("statusHistory");
				if (history != null && !history.isEmpty()) {
					Map<String, Object> lastEntry = history.get(history.size() - 1);
					latestStatus = String.valueOf(lastEntry.getOrDefault("status", "N/A"));
					latestDesc = String.valueOf(lastEntry.getOrDefault("description", "No description"));
				}

				String expected_status = "closed";
				String expected_desc = "Reduce location while self onboarding";

				boolean isPassed = expected_status.equalsIgnoreCase(latestStatus)
						&& expected_desc.equalsIgnoreCase(latestDesc);

				// 🔎 Log details for reporting
				System.out.println("✅ MID: " + mid + " → Status: " + latestStatus + ", Description: " + latestDesc);

				logTestResult("Merchant application status check post removal", updateEndpoint + groupMerchantId,
						payload, resp, isPassed);

				Assert.assertEquals(false, null);
				// Optionally write to Excel or report
				// ExcelReport.writeDeletedMerchant(mid, status, latestDesc);
			}
		}
	}

	@Test(dataProvider = "shopDetailsData", dataProviderClass = TestDataProvider.class, priority = 10)
	public void createOrUpdateShopDetails(ShopDetailsPayload payload) {

		test = extent.createTest("Shop Details Test for Merchant").assignCategory("Shop Details");
		String gmId = groupMerchantId;

		// Fetch merchants first
		GlobalPayloads payload2 = new GlobalPayloads();
		payload2.setAccountNo(configManager.getProperty("account.value1"));
		payload2.setCifNumber(configManager.getProperty("account.value2"));

		Response resp = RestAssured.given().body(payload2).get(fetchEndpoint + gmId);
		Assert.assertEquals(resp.getStatusCode(), 200);

		List<Map<String, Object>> merchants = resp.jsonPath().getList("Merchants");

		// Separate active POS and SoftPOS merchants
		List<String> activePosMids = merchants.stream()
				.filter(m -> !"closed".equalsIgnoreCase(String.valueOf(m.get("status"))))
				.filter(m -> "POS".equalsIgnoreCase(String.valueOf(m.get("type"))))
				.map(m -> String.valueOf(m.get("_id"))).collect(Collectors.toList());

		List<String> activeSoftPosMids = merchants.stream()
				.filter(m -> !"closed".equalsIgnoreCase(String.valueOf(m.get("status"))))
				.filter(m -> "SOFTPOS".equalsIgnoreCase(String.valueOf(m.get("type"))))
				.map(m -> String.valueOf(m.get("_id"))).collect(Collectors.toList());

		if (!activePosMids.isEmpty()) {
			List<String> posTypes = activePosMids.stream().map(id -> "POS").collect(Collectors.toList());
			Map<String, Integer> posCountMap = ExcelUtils.writeMerchantId(activePosMids, posTypes);
			System.out.println("🟢 Active POS merchants written: " + posCountMap.getOrDefault("POS", 0));
		}

		if (!activeSoftPosMids.isEmpty()) {
			List<String> softTypes = activeSoftPosMids.stream().map(id -> "SoftPOS").collect(Collectors.toList());
			Map<String, Integer> softCountMap = ExcelUtils.writeMerchantId(activeSoftPosMids, softTypes);
			System.out.println("🟢 Active SoftPOS merchants written: " + softCountMap.getOrDefault("SoftPOS", 0));
		}

		// Continue with shop update
		Response response = RestAssured.given().body(payload).request("PUT", updateEndpoint + gmId);
		boolean passed = "success".equals(response.jsonPath().getString("status"));
		logTestResult("Shop Details Test", updateEndpoint + gmId, payload, response, passed);

		Assert.assertEquals(response.jsonPath().getString("status"), "success", "Shop details creation failed");
	}
//	  /** STEP 4️⃣: Optional final validation (reuse deleted list) */
//    @Test(dependsOnMethods = "shopDetailsData", priority = 4)
//    public void revalidateAfterUpdate() {
//        // Simply reuse the same validation logic
//        validateDeletedMerchants();
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

	/**
	 * Reusable delete method to call anywhere in the class
	 * 
	 * @param type      Type of entity to delete (e.g., "Merchant")
	 * @param cifNumber CIF number of the entity to delete
	 * @param endpoint  API endpoint for delete
	 * @return Response object from API call
	 */
	public Response deleteEntity(String type, String cifNumber, String endpoint) {
		globalDeletePayload payload = new globalDeletePayload(type, cifNumber);

		Response response = RestAssured.given().contentType("application/json").body(payload).post(endpoint); // or
																												// .post()/.delete()
																												// depending
																												// on
																												// your
																												// AP
		System.out.println("Delete Payload: " + payload);
		System.out.println("Endpoint: " + endpoint);
		System.out.println("Response: " + response.asPrettyString());

		return response;
	}
}

// ----------------------- CROSS CASE : KYC + Shop Location -------------

// @Test(priority = 5) // run after account
// public void crossCase_ConfirmationWithoutChannel() {
//
// // 🔹 Start Extent test
// String testCase = "CrossCase – ConfirmPage submission without POS Selection";
// test = extent.createTest(testCase).assignCategory("Negative");
//
// // 🔹 Prepare Request Payload
// boolean termsCondition =
// Boolean.parseBoolean(configManager.getProperty("confirmPage.key3"));
// ConfirmPagePayload.ConfirmPage confirmPage = new
// ConfirmPagePayload.ConfirmPage(termsCondition);
// ConfirmPagePayload requestPayload = new ConfirmPagePayload(confirmPage);
//
// // 🔹 Execute API Request
// String gmId = groupMerchantId;
// String endpoint = updateEndpoint + gmId;
// Response response = RestAssured.given().body(requestPayload).put(endpoint);
//
// // 🔹 Expected & Actual
// String expectedStatus = "failed"; // since POS is not selected
// String actualStatus = response.jsonPath().getString("status");
// String actualError = response.jsonPath().getString("error");
//
// // 🔹 Log & Assert
// logTestResult(testCase, endpoint, requestPayload, response,
// expectedStatus.equals(actualStatus));
// Assert.assertEquals(actualStatus, expectedStatus, "Status mismatch for test
// case: " + testCase);
// Assert.assertEquals(actualError, "Need to fill all location details", "Error
// message mismatch");
//
// }
