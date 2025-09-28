package utils;

import java.io.File;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            String reportPath = System.getProperty("user.dir") + "/reports/Salispay_POS_Report.html";
            createInstance(reportPath);
        }
        return extent;
    }

    private static void createInstance(String fileName) {
        File reportFile = new File(fileName);
        reportFile.getParentFile().mkdirs();

        ExtentSparkReporter spark = new ExtentSparkReporter(fileName);

        // POS machine theme
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("Salispay – POS Terminal Dashboard");
        spark.config().setReportName("💳 Salispay Test Terminal");
        spark.config().setTimeStampFormat("dd-MMM-yyyy hh:mm:ss a");

        // Custom POS-inspired CSS
        spark.config().setCss(
            "@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&display=swap');" +
            "body{font-family:'Share Tech Mono', monospace;background:#0b0c10;color:#c5c6c7;}" +
            ".nav-wrapper{background:#1f2833;color:#66fcf1;font-weight:bold;}" +
            ".brand-logo{font-size:24px;text-align:center;padding:10px;color:#45a29e;text-shadow:0 0 5px #66fcf1;}" +
            ".card-panel{background:#1c1c1c;border:2px solid #45a29e;border-radius:8px;margin:10px;padding:15px;" +
            "box-shadow:0 0 15px #45a29e;} " +
            ".card-title{font-size:16px;font-weight:bold;color:#66fcf1;margin-bottom:5px;}" +
            ".badge-success{background:#45a29e;color:#0b0c10;font-weight:bold;padding:2px 6px;border-radius:4px;}" +
            ".badge-fail{background:#ff4b5c;color:#0b0c10;font-weight:bold;padding:2px 6px;border-radius:4px;}" +
            ".badge-warning{background:#f5a623;color:#0b0c10;font-weight:bold;padding:2px 6px;border-radius:4px;}" +
            "pre{background:#0b0c10;color:#66fcf1;padding:8px;border-radius:4px;overflow-x:auto;}" +
            "summary{font-weight:bold;color:#45a29e;cursor:pointer;}" +
            ".test-content:hover{box-shadow:0 0 20px #66fcf1;transform:scale(1.02);transition:all 0.3s ease-in-out;}" +
            ".theme-toggle{position:fixed;top:15px;right:20px;background:#45a29e;color:#0b0c10;" +
            "padding:6px 12px;border-radius:20px;cursor:pointer;z-index:999;font-weight:bold;}"
        );

        // Optional JS for light/dark toggle
        spark.config().setJs(
            "const btn=document.createElement('div');" +
            "btn.className='theme-toggle';btn.innerText='Toggle Theme';" +
            "document.body.appendChild(btn);" +
            "let dark=true;" +
            "btn.onclick=()=>{" +
            "document.body.style.background=dark?'#c5c6c7':'#0b0c10';" +
            "document.body.style.color=dark?'#0b0c10':'#c5c6c7';dark=!dark;" +
            "};"
        );

        extent = new ExtentReports();
        extent.attachReporter(spark);

        // Meta info
        extent.setSystemInfo("Application", "Salispay POS");
        extent.setSystemInfo("Bank", "BankMuscat");
        extent.setSystemInfo("Environment", "UAT");
        extent.setSystemInfo("Framework", "Java + TestNG + RestAssured");
        extent.setSystemInfo("Generated On", java.time.LocalDateTime.now().toString());
    }
}