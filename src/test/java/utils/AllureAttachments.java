package utils;

import io.qameta.allure.Allure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AllureAttachments {

    private static final String LOG_FILE = "logs/automation.log";

    public static void attachLogFile() {
        try {
            if (Files.exists(Paths.get(LOG_FILE))) {
                Allure.addAttachment(
                        "Execution Logs",
                        "text/plain",
                        Files.newInputStream(Paths.get(LOG_FILE)),
                        ".log");
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
