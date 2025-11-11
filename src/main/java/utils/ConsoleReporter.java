package utils;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class ConsoleReporter implements ITestListener {

	public void onTestSuccess(ITestResult result) {
		System.out.printf("Test Passed: %-30s%n", result.getMethod().getMethodName());
	}

	@Override
	public void onTestFailure(ITestResult result) {
		System.out.printf("Test Failed: %-30s | Reason: %s%n", result.getMethod().getMethodName(),
				result.getThrowable().getMessage());
	}

}
