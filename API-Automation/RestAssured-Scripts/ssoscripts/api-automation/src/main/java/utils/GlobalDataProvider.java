package utils;

import config.configManager;
import org.testng.annotations.*;

public class GlobalDataProvider {

	private static final String VGMID = configManager.getProperty("latest.gmid");
	private static final String IGMID = configManager.getProperty("account.gmidin");
	private static final String Vcommerce = configManager.getProperty("commerceNumber");
	private static final String Vcommercefile = configManager.getProperty("valid.commerceFile");
	private static final String VLocation = configManager.getProperty("LocationValid");
	private static final String Vpassportfile = configManager.getProperty("valid.passport");
	private static final String VpassportNo = configManager.getProperty("valid.passportNo");

	private static final String Ncommercefile = configManager.getProperty("Null.commerceFile");
	private static final String MinLocation = configManager.getProperty("LocationMin");
	private static final String MaxLocation = configManager.getProperty("LocationMax");
	private static final String NLocation = configManager.getProperty("LocationNull");
	private static final String Npassportfile = configManager.getProperty("Null.passport");
	private static final String NpassportNo = configManager.getProperty("Null.passportNo");
	private static final String InpassportNo = configManager.getProperty("Invalid.passportNo");

	@DataProvider(name = "kycTestData")
	public static Object[][] kycTestData() {
		return new Object[][] {

				{ VGMID, Vcommercefile, Vcommerce, MinLocation, Npassportfile, NpassportNo, "failed", "Locations < min",
						true },
				{ VGMID, Vcommercefile, Vcommerce, NLocation, Npassportfile, NpassportNo, "success", "Locations at max",
						true },
				{ VGMID, Vcommercefile, Vcommerce, VLocation, Npassportfile, NpassportNo, "success",
						"Valid KYC without passport", true },
				{ VGMID, Vcommercefile, Vcommerce, VLocation, Vpassportfile, VpassportNo, "success",
						"Valid KYC with passport", true },
				{ IGMID, Vcommercefile, Vcommerce, VLocation, Npassportfile, NpassportNo, "failed", "Invalid GMID",
						false },
				{ VGMID, Ncommercefile, Vcommerce, VLocation, Npassportfile, NpassportNo, "success",
						"Missing commerceFile", true },
				{ VGMID, Vcommercefile, Vcommerce, VLocation, Npassportfile, NpassportNo, "success",
						"No passport fields", true },
				{ VGMID, Vcommercefile, Vcommerce, VLocation, Vpassportfile, NpassportNo, "failed",
						"Passport file only", true },
				{ VGMID, Vcommercefile, Vcommerce, VLocation, Npassportfile, VpassportNo, "failed",
						"Passport number only", true },

				{ VGMID, Vcommercefile, Vcommerce, MaxLocation, Npassportfile, NpassportNo, "failed", "Locations > max",
						true },
				{ VGMID, Vcommercefile, Vcommerce, "2", Vpassportfile, InpassportNo, "failed",
						"Invalid passport file format", true },

				{ VGMID, Vcommercefile, Vcommerce, "40", Npassportfile, NpassportNo, "success", "Locations at max,",
						true }

		};
	}

}
