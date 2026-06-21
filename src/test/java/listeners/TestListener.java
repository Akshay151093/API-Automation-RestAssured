package listeners;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import utils.AllureAttachments;

public class TestListener implements ITestListener{

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("STARTED : {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("PASSED : {}", result.getMethod().getMethodName());
        AllureAttachments.attachLogFile();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.info("FAILED : {}", result.getMethod().getMethodName());
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            // Failure message
            Allure.addAttachment(
                    "Failure Message",
                    throwable.getMessage());
            // Complete stack trace
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            Allure.addAttachment(
                    "Stack Trace",
                    "text/plain",
                    sw.toString(),
                    ".txt");
        }
        AllureAttachments.attachLogFile();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.info("SKIPPED : {}", result.getMethod().getMethodName());
    }
}
