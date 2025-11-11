package utils;

import java.io.File;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
	private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            String reportPath = System.getProperty("user.dir") + "/reports/BankMuscat_Onboarding.html";
            createInstance(reportPath);
        }
        return extent;
    }

    private static void createInstance(String fileName) {
        File reportFile = new File(fileName);
        reportFile.getParentFile().mkdirs();

        ExtentSparkReporter spark = new ExtentSparkReporter(fileName);

        // Base theme (dark for contrast)
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("BankMuscat – Digital Onboarding");
        spark.config().setReportName("🚀 BankMuscat | API + POS + Digital Suite");
        spark.config().setTimeStampFormat("dd-MMM-yyyy hh:mm:ss a");

        // Custom CSS
        spark.config().setCss(
            "@import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@500&display=swap');" +
            "@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&display=swap');" +

            // Body
            "body{font-family:'Montserrat',sans-serif;background:linear-gradient(135deg,#0f2027,#203a43,#2c5364);color:#fff;}" +

            // Navbar & header
            ".nav-wrapper{background:linear-gradient(90deg,#1CB5E0 0%,#000851 100%);}" +
            ".brand-logo{font-family:'Orbitron',sans-serif;font-size:22px;letter-spacing:1px;text-shadow:0 0 8px #1CB5E0;}" +

            // Card / Test case container
            ".card-panel{background:#222;padding:10px;border-radius:12px;margin:5px 0;}" +

            // Pass/Fail badges
            ".badge-success{background:linear-gradient(45deg,#28a745,#6fefb4)!important;color:#fff!important;font-weight:bold;}" +
            ".badge-fail{background:linear-gradient(45deg,#dc3545,#ff6b6b)!important;color:#fff!important;font-weight:bold;}" +
            ".badge-warning{background:linear-gradient(45deg,#ff8c00,#ffc107)!important;color:#000!important;font-weight:bold;}" +

            // Card title
            ".card-title{font-size:18px;font-weight:700;letter-spacing:0.5px;text-transform:uppercase;}" +

            // Payload & Response block
            "pre{background:#000;color:#fff;padding:10px;border-radius:6px;font-size:13px;overflow-x:auto;}" +

            // Summary / collapsible
            "summary{font-weight:700;color:#1CB5E0;cursor:pointer;transition:color 0.3s;}summary:hover{color:#fff;}" +

            // Hover effect
            ".test-content:hover{box-shadow:0 0 15px #1CB5E0;transform:scale(1.01);transition:all 0.3s ease-in-out;}" +

            // Toggle button for light/dark
            ".theme-toggle{position:fixed;top:15px;right:20px;background:#1CB5E0;color:#000;padding:6px 12px;border-radius:20px;cursor:pointer;z-index:999;}"
        );

        // Optional JS for toggle button
        spark.config().setJs(
            "const btn=document.createElement('div');btn.className='theme-toggle';btn.innerText='Toggle Theme';" +
            "document.body.appendChild(btn);" +
            "let dark=true;btn.onclick=()=>{document.body.style.background=dark?'#f5f5f5':'linear-gradient(135deg,#0f2027,#203a43,#2c5364)';dark=!dark;};"
        );

        extent = new ExtentReports();
        extent.attachReporter(spark);

        // Meta Info
        extent.setSystemInfo("Bank", "BankMuscat");
        extent.setSystemInfo("Environment", "UAT");
        extent.setSystemInfo("Framework", "Java + TestNG + RestAssured");
        extent.setSystemInfo("Generated On", java.time.LocalDateTime.now().toString());
    }
}