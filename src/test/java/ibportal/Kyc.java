package ibportal;

import base.BaseApi;
import config.configManager;
import payloads.KycPayloads;
import utils.GlobalDataProvider;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class Kyc extends BaseApi {

	private static final String endPoint = configManager.getProperty("updateKycEndpoint");

	@Test(dataProvider = "kycTestData", dataProviderClass = GlobalDataProvider.class)
	public void testKycApi(String groupMerchant, String commerceFile, String commerceNumber, String location, String passportFile, String passportNumber, String expectedStatus, String testCase,
			boolean useValidGm) {

		String payload = KycPayloads.buildKycPayload(commerceFile, commerceNumber, location, passportFile, passportNumber);
		String gmId = useValidGm ? configManager.getProperty("latest.gmid")
				: configManager.getProperty("account.gmidin");

		Response response = RestAssured.given().contentType("application/json").body(payload).when()
				.put(endPoint + gmId);


		Assert.assertEquals(response.getStatusCode(), 200, "Unexpected HTTP status code for " + testCase);

		String actualStatus = response.jsonPath().getString("status");
		String errorCode = response.jsonPath().getString("error") != null ? response.jsonPath().getString("error")
				: "-";

		if ("success".equalsIgnoreCase(actualStatus)) {
			System.out.printf("Case: %-25s | Expected: %-7s | Actual: %-7s | HTTP: %d%n", testCase, expectedStatus, actualStatus,
					response.getStatusCode());
		} else {
			System.out.printf("Case: %-25s | Expected: %-7s | Actual: %-7s | ErrorCode: %-5s | HTTP: %d%n", testCase, expectedStatus,
					actualStatus, errorCode, response.getStatusCode());
		}

		Assert.assertEquals(actualStatus, expectedStatus,
				String.format("❌ Case: %s | Expected: %s | Actual: %s | ErrorCode: %s | Response: %s", testCase, expectedStatus,
						actualStatus, errorCode, response.asString()));
	}
}