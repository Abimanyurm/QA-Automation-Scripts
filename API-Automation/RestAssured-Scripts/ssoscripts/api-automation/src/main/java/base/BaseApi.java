package base;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import config.configManager;

public class BaseApi {

	public String baseUrl = configManager.getProperty("baseURI");
	public String authToken = configManager.getProperty("authToken");

	protected RequestSpecification request;

	public BaseApi() {
		request = new RequestSpecBuilder().setBaseUri(baseUrl).setContentType("application/json")
				.addHeader("Authorization", "Bearer " + authToken).build();
		// .log(LogDetail.ALL)

		RestAssured.requestSpecification = request;
	}
}
