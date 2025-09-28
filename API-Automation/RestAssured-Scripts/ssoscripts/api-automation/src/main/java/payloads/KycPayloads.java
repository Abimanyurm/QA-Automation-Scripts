package payloads;

import config.configManager;

public class KycPayloads {

	public static String buildKycPayload(String commerceFile, String commerceNumber, String numberOfMerchantLocations,
			String passportFrontFile, String passportNumber) {

		String key1 = configManager.getProperty("kyc.key1"); // "kyc"
		String key2 = configManager.getProperty("kyc.key2"); // "commerceFile"
		String key3 = configManager.getProperty("kyc.key3"); // "commerceNumber"
		String key4 = configManager.getProperty("kyc.key4"); // "numberOfMerchantLocations"
		String key5 = configManager.getProperty("kyc.key5"); // "passportFrontFile"
		String key6 = configManager.getProperty("kyc.key6"); // "passportNumber"

	       return "{\n" +
           "  \"" + key1 + "\": {\n" +
           "    \"" + key2 + "\": \"" + commerceFile + "\",\n" +
           "    \"" + key3 + "\": \"" + commerceNumber + "\",\n" +
           "    \"" + key4 + "\": " + numberOfMerchantLocations + ",\n" +
           "    \"" + key5 + "\": \"" + passportFrontFile + "\",\n" +
           "    \"" + key6 + "\": \"" + passportNumber + "\"\n" +
           "  }\n" +
           "}";
}}
