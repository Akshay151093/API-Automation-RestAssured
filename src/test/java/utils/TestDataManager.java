package utils;

import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class TestDataManager {

    private static final Logger logger = LogManagerUtil.getLogger(TestDataManager.class);

    private static void loadExcel(String file, String sheet) {
        Objects.requireNonNull(file, "path");
        Objects.requireNonNull(sheet, "sheet");
        try {
            ExcelUtils.setExcelFile(file, sheet);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not load test data workbook: " + file + " sheet=" + sheet, e);
        }
    }

    private static void validateNotEmpty(Map<?, ?> data, String message) {
        if (data == null || data.isEmpty()) {
            throw new IllegalStateException(message);
        }
    }

    public LinkedHashMap<String, String> getTestData(String testDataLabel, String file, String sheet) {
        logger.info("Retrieving test data '{}'.", testDataLabel);
        Objects.requireNonNull(testDataLabel);
        Objects.requireNonNull(file);
        Objects.requireNonNull(sheet);
        testDataLabel = testDataLabel.trim();
        loadExcel(file, sheet);
        LinkedHashMap<String, String> data = ExcelUtils.getDataFromRow(testDataLabel);
        validateNotEmpty(data, "No test data found for: " + testDataLabel);
        logger.info("Test data '{}' retrieved successfully.", testDataLabel);
        return data;
    }
}
