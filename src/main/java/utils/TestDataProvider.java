package utils;

import java.util.ArrayList;

import java.util.Arrays;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.testng.annotations.DataProvider;
import config.configManager;
import payloads.DeleteShopPayload;
import payloads.GlobalPayloads;
import payloads.ShopDetailsPayload;

public class TestDataProvider {

	static int numberOfMerchants;

	@DataProvider(name = "accountData")
	public static Object[][] accountData() {
		// Define all possible test cases
		return new Object[][] {
				{ configManager.getProperty("account.value1"), configManager.getProperty("account.value2"), null,
						"success", "Valid Account", false },
				{ configManager.getProperty("account.valuein1"), configManager.getProperty("account.value2"), null,
						"failed", "Invalid Account No", false } };
	}

	// Add other cases like before...

	@DataProvider(name = "channelData")
	public static Object[][] channelData() {
		return new Object[][] {
				{ configManager.getProperty("channels.C02"), true, "SOFTPOS Channel - valid", "success" },
				//{ "C09", true, "Invalid Channel", "failed" },
				{ configManager.getProperty("channels.C01"), true, "POS Channel - valid", "success" },
				{ configManager.getProperty("channels.C10"), true, "POS Channel - valid", "success" } };
	}

	@DataProvider(name = "kycData")
	public static Object[][] kycData() {
		return new Object[][] {
				// ------------------- POS ONLY -------------------
				{ configManager.getProperty("account.gmidin"), configManager.getProperty("Invalid.commerceFile"),
						configManager.getProperty("commerceNumber"),
						Integer.parseInt(configManager.getProperty("LocationValid")),
						Integer.parseInt(configManager.getProperty("LocationSoftMin")),
						configManager.getProperty("Invalid.passport"), configManager.getProperty("Invalid.passportNo"),
						"failed", "Invalid KYC - POS", false, "POS" },
				
				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
							configManager.getProperty("commerceNumber"),
							Integer.parseInt(configManager.getProperty("LocationMin")),
							Integer.parseInt(configManager.getProperty("LocationSoftMin")),
							configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
							"failed", "Min location - POS", true, "POS" },


				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
						configManager.getProperty("commerceNumber"),
						Integer.parseInt(configManager.getProperty("LocationMax")),
						Integer.parseInt(configManager.getProperty("LocationSoftMin")),
						configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
						"failed", "Max location - POS", true, "POS" },
				
				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
							configManager.getProperty("commerceNumber"),
							Integer.parseInt(configManager.getProperty("LocationValid")),
							Integer.parseInt(configManager.getProperty("LocationSoftMin")),
							configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
							"success", "Valid KYC - POS", true, "POS" },

				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
						configManager.getProperty("commerceNumber"),
						Integer.parseInt(configManager.getProperty("LocationMin")),
						Integer.parseInt(configManager.getProperty("LocationSoftMin")),
						configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
						"failed", "Min location - POS", true, "POS" },

				// ------------------- SOFTPOS ONLY -------------------

				{ configManager.getProperty("account.gmidin"), configManager.getProperty("Invalid.commerceFile"),
						configManager.getProperty("commerceNumber"),
						Integer.parseInt(configManager.getProperty("LocationValid")),
						Integer.parseInt(configManager.getProperty("LocationSoftvalid")),
						configManager.getProperty("Invalid.passport"), configManager.getProperty("Invalid.passportNo"),
						"failed", "Invalid KYC - SoftPOS", false, "SOFTPOS" },

				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
						configManager.getProperty("commerceNumber"),
						Integer.parseInt(configManager.getProperty("LocationValid")),
						Integer.parseInt(configManager.getProperty("LocationSoftMax")),
						configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
						"failed", "Max location - SoftPOS", true, "SOFTPOS" },

				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
						configManager.getProperty("commerceNumber"),
						Integer.parseInt(configManager.getProperty("LocationValid")),
						Integer.parseInt(configManager.getProperty("LocationSoftvalid")),
						configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
						"success", "Valid KYC - SoftPOS", true, "SOFTPOS" },
				
  			{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
						configManager.getProperty("commerceNumber"),
							Integer.parseInt(configManager.getProperty("LocationValid")),
						Integer.parseInt(configManager.getProperty("LocationSoftMin")),
						configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
						"failed", "Min location - SoftPOS", true, "SOFTPOS" },

//
//				// ------------------- POS + SOFTPOS -------------------
//				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
//						configManager.getProperty("commerceNumber"),
//						Integer.parseInt(configManager.getProperty("LocationValid")),
//						Integer.parseInt(configManager.getProperty("LocationSoftvalid")),
//						configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
//						"success", "Valid KYC - POS+SoftPOS", true, "POS+SOFTPOS" },
//
//				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
//						configManager.getProperty("commerceNumber"),
//						Integer.parseInt(configManager.getProperty("LocationMax")),
//						Integer.parseInt(configManager.getProperty("LocationSoftvalid")),
//						configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
//						"failed", "Max location for pos - POS+SoftPOS", true, "POS+SOFTPOS" },
//
//				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
//						configManager.getProperty("commerceNumber"),
//						Integer.parseInt(configManager.getProperty("LocationValid")),
//						Integer.parseInt(configManager.getProperty("LocationSoftMax")),
//						configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
//						"failed", "Max location for softpos - POS+SoftPOS", true, "POS+SOFTPOS" },
//
//				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
//						configManager.getProperty("commerceNumber"),
//						Integer.parseInt(configManager.getProperty("LocationMin")),
//						Integer.parseInt(configManager.getProperty("LocationSoftvalid")),
//						configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
//						"failed", "Min location pos only- POS+SoftPOS", true, "POS+SOFTPOS" },
//
//				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
//						configManager.getProperty("commerceNumber"),
//						Integer.parseInt(configManager.getProperty("LocationValid")),
//						Integer.parseInt(configManager.getProperty("LocationSoftMin")),
//						configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
//						"failed", "Min location softpos only - POS+SoftPOS", true, "POS+SOFTPOS" } 
				};

	}

	@DataProvider(name = "kycDatas")
	public static Object[][] kycData1() {
		return new Object[][] {

			{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
				configManager.getProperty("commerceNumber"),
				Integer.parseInt(configManager.getProperty("LocationValid")),
				Integer.parseInt(configManager.getProperty("LocationSoftMin")),
				configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
				"success", "Valid KYC - POS", true, "POS" }

		};
	}

	@DataProvider(name = "merchantData")
	public static Object[][] merchantData() {
		// Define all possible test cases
		return new Object[][] { { configManager.getProperty("account.value1"),
				configManager.getProperty("account.value2"), null, "200", "Details", false },

		};

	}
	
	@DataProvider(name = "shopDetailsData")
	public static Object[][] shopDetailsData() throws InterruptedException {

	    Scanner sc = new Scanner(System.in);
	    System.out.print("\nEnter number of POS MIDs to update: ");
	    int posCount = sc.nextInt();
	    System.out.print("Enter number of SoftPOS MIDs to update: ");
	    int softPosCount = sc.nextInt();

	    // Fetch merchant IDs by type
	    List<String> posMids = ExcelUtils.getLastNPosMerchantIds(posCount);
	    List<String> softPosMids = ExcelUtils.getLastNSoftPosMerchantIds(softPosCount);

	    // Fetch shop details filtered by type
	    List<Map<String, String>> posShopData = ExcelUtils.getShopDataByType("POS");
	    List<Map<String, String>> softPosShopData = ExcelUtils.getShopDataByType("SoftPOS");

	    // Combine both sets
	    List<Object[]> combinedData = new ArrayList<>();

	    // Map POS mids to POS shop rows
	    for (String mid : posMids) {
	        for (Map<String, String> row : posShopData) {
	            ShopDetailsPayload payload = buildShopPayload(row, mid);
	            combinedData.add(new Object[]{payload});
	        }
	    }

	    // Map SoftPOS mids to SoftPOS shop rows
	    for (String mid : softPosMids) {
	        for (Map<String, String> row : softPosShopData) {
	            ShopDetailsPayload payload = buildShopPayload(row, mid);
	            combinedData.add(new Object[]{payload});
	        }
	    }

	    // Convert list to array
	    Object[][] data = new Object[combinedData.size()][];
	    for (int i = 0; i < combinedData.size(); i++) {
	        data[i] = combinedData.get(i);
	    }
	    System.out.println("POS MIDs: " + posMids);
	    System.out.println("SoftPOS MIDs: " + softPosMids);
	    System.out.println("POS ShopData: " + posShopData.size());
	    System.out.println("SoftPOS ShopData: " + softPosShopData.size());

	    return data;
	}

	// === helper method ===//-------------->For split Pos and softpos request in same sheet<--------------//
	private static ShopDetailsPayload buildShopPayload(Map<String, String> row, String mid) throws InterruptedException {
	    ShopDetailsPayload payload = new ShopDetailsPayload();
	    ShopDetailsPayload.ShopDetails shop = new ShopDetailsPayload.ShopDetails();

	    shop.setLocation(row.get("location"));
	    shop.setAddress(row.get("address"));
	    shop.setArea(row.get("area"));
	    shop.setCity(row.get("city"));
	    shop.setState(row.get("state"));
	    shop.setPostalCode((int) Double.parseDouble(row.get("postalCode")));
	    shop.setNoOfPOSDevices((int) Double.parseDouble(row.get("noOfPOSDevices")));
	    shop.setSoftPOS((int) Double.parseDouble(row.get("softPOS")));
	    shop.setSupervisorNumber(row.get("supervisorNumber"));
	    shop.setLocationMunicipalityFile(row.get("locationMunicipalityFile"));
	    shop.setBizCategoryName(row.get("bizCategoryName"));
	    shop.setBizCategoryId(row.get("bizCategoryId"));
	    shop.setBussinessAddress(row.get("bussinessAddress"));
	    shop.setPhotoOfThePermisesFile(row.get("photoOfThePermisesFile"));
	    shop.setSignboardFile(row.get("signboardFile"));
	    shop.setCounty(row.get("county"));
	    shop.setRegion(row.get("region"));

	    String terminals = row.get("terminalNumbers");
	    shop.setTerminalNumbers(
	        terminals != null && !terminals.isEmpty()
	            ? Arrays.asList(terminals.split(","))
	            : Collections.emptyList()
	    );

	    String inputsStr = row.get("inputs");
	    shop.setInputs(
	        inputsStr != null && !inputsStr.isEmpty()
	            ? Arrays.asList(inputsStr.split(","))
	            : Collections.emptyList()
	    );

	    shop.setMcc(row.get("mcc"));
	    shop.setMobile(row.get("mobile"));
	    shop.setPhone(row.get("phone"));
	    shop.setIsPg(Boolean.parseBoolean(row.get("isPg")));
	    shop.setSeamlessIntegration(Boolean.parseBoolean(row.get("seamlessIntegration")));
	    shop.setSiMid(row.get("siMid"));
	    shop.setFirstName(row.get("firstName"));
	    shop.setLastName(row.get("lastName"));
	    shop.setPobox((int) Double.parseDouble(row.get("pobox")));
	    shop.setEmail(row.get("email"));
	    shop.setEntityName(row.get("entityName"));
	    shop.setMerchantBusinessNature(row.get("merchantBusinessNature"));
	    shop.setContactPerson(row.get("contactPerson"));
	    shop.setContactPersonMobile(row.get("contactPersonMobile"));
	    shop.setBizWebsite(row.get("bizWebsite"));
	    shop.setBusiness(row.get("business"));
	    shop.setTransactionRefundAllow(
	        row.get("transactionRefundAllow") != null
	            ? Boolean.valueOf(row.get("transactionRefundAllow"))
	            : null
	    );
	    shop.setBinSupported(row.get("binSupported"));

	    ShopDetailsPayload.ShopDetails.BusinessVolume bv = new ShopDetailsPayload.ShopDetails.BusinessVolume();
	    bv.setMin((int) Double.parseDouble(row.get("businessVolumeMin")));
	    bv.setMax((int) Double.parseDouble(row.get("businessVolumeMax")));
	    shop.setBusinessVolume(bv);

	    shop.setCommercialName(row.get("commercialName"));
	    shop.setLatitude(Double.parseDouble(row.get("latitude")));
	    shop.setLongitude(Double.parseDouble(row.get("longitude")));

	    payload.setShopDetails(shop);
	    payload.setMerchantId(mid);

	    Thread.sleep(1000);
	    return payload;
	    
	    
	}
	@DataProvider(name = "deleteShopData")
	public static Object[][] deleteShopData() {

		Scanner sc = new Scanner(System.in);

		System.out.print("Enter number of POS merchants to delete: ");
		int posCount = sc.nextInt();

		System.out.print("Enter number of SoftPOS merchants to delete: ");
		int softPosCount = sc.nextInt();

		// Fetch last N POS and SoftPOS merchant IDs separately
		List<String> posMids = ExcelUtils.getLastNPosMerchantIds(posCount);
		List<String> softPosMids = ExcelUtils.getLastNSoftPosMerchantIds(softPosCount);

		List<String> allMids = new ArrayList<>();
		allMids.addAll(posMids);
		allMids.addAll(softPosMids);

		if (allMids.isEmpty()) {
			System.out.println("⚠️ No merchants available to delete.");
			return new Object[0][0];
		}

		Object[][] data = new Object[allMids.size()][1];
		for (int i = 0; i < allMids.size(); i++) {
			data[i][0] = new DeleteShopPayload(true, allMids.get(i));
		}

		System.out.println("\n✅ Merchants selected for deletion: " + allMids);
		ExcelUtils.deleteSpecificMerchantIds(allMids);
		System.out.println("✅ Deleted merchant IDs flushed from Excel.");

		return data;
	}

	@DataProvider(name = "confirmPageData")
	public static Object[][] confirmPageData() {
		return new Object[][] {

				{ config.configManager.getProperty("confirmPage.key3"), "Confirm Page TC - enabled", "success" },
				{ config.configManager.getProperty("confirmPage.key4"), "Confirm Page TC - disabled", "failed" } };
	}

	@DataProvider(name = "otpData")
	public static Object[][] otpData() {
		return new Object[][] {
				{ config.configManager.getProperty("otpw"), config.configManager.getProperty("otpFrom"),
						"Invalid OTP Test", "failed" },
				{ config.configManager.getProperty("otp"), config.configManager.getProperty("otpFrom"),
						"Valid OTP Test", "success" }

		};
	}

	@DataProvider(name = "submitPageData")
	public static Object[][] submitPageData() {
		return new Object[][] { { "false", "Submit Page Disabled Test", "failed" },
				{ "true", "Submit Page Enabled Test", "success" }

		};
	}

	/**
	 * Helper to fetch one populated ShopDetailsPayload (first Excel+MID combo) so
	 * that cross-case tests can reuse the same Payload without a DataProvider.
	 */
	public static ShopDetailsPayload getSingleShopDetailsPayload() {
		try {
			Object[][] allData = shopDetailsData();
			if (allData != null && allData.length > 0 && allData[0].length > 0) {
				return (ShopDetailsPayload) allData[0][0];
			} else {
				throw new RuntimeException("shopDetailsData returned no rows. Check Excel or input source.");
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to load ShopDetailsPayload from DataProvider", e);
		}
	}

	// Add these methods at the bottom of TestDataProvider class

	/**
	 * Helper to fetch one valid Account record
	 */
	public static Object[] getSingleAccountData() {
		Object[][] data = accountData();
		if (data != null && data.length > 0) {
			return data[0]; // returns first row as Object[]
		}
		throw new RuntimeException("No account data available");
	}

	/**
	 * Helper to fetch one valid Channel record
	 */
	public static Object[] getSingleChannelData() {
		Object[][] data = channelData();
		if (data != null && data.length > 0) {
			return data[0];
		}
		throw new RuntimeException("No channel data available");
	}

	/**
	 * Helper to fetch one valid KYC record
	 */
	public static Object[] getSingleKycData() {
		Object[][] data = kycData();
		if (data != null && data.length > 0) {
			return data[0];
		}
		throw new RuntimeException("No KYC data available");
	}

	/**
	 * Helper to fetch one valid Merchant record
	 */
	public static GlobalPayloads getSingleMerchantData() {
		Object[][] data = merchantData();
		if (data != null && data.length > 0) {
			Object[] row = data[0];
			GlobalPayloads payload = new GlobalPayloads();
			payload.setAccountNo((String) row[0]);
			payload.setCifNumber((String) row[1]);
			// set other fields if required
			return payload;
		}
		throw new RuntimeException("No merchant data available");
	}

	/**
	 * Helper to fetch one valid Confirm Page record
	 */
	public static Object[] getSingleConfirmPageData() {
		Object[][] data = confirmPageData();
		if (data != null && data.length > 0) {
			return data[0];
		}
		throw new RuntimeException("No confirm page data available");
	}

	/**
	 * Helper to fetch one valid OTP record
	 */
	public static Object[] getSingleOtpData() {
		Object[][] data = otpData();
		if (data != null && data.length > 0) {
			return data[0];
		}
		throw new RuntimeException("No OTP data available");
	}

	/**
	 * Helper to fetch one valid Submit Page record
	 */
	public static Object[] getSingleSubmitPageData() {
		Object[][] data = submitPageData();
		if (data != null && data.length > 0) {
			return data[0];
		}
		throw new RuntimeException("No submit page data available");
	}

}
