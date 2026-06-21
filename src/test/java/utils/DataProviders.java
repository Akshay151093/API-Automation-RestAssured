package utils;

import endpoints.UserEndPoints;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;

public class DataProviders {

    private static final Logger logger = LogManagerUtil.getLogger(UserEndPoints.class);
    String file="TestData_Users";
    String sheet="Input_2";

    @DataProvider(name="payload")
    private String [][] getUserPayload(){
        logger.info("Loading DataProvider : payload");
        int rowCount = ExcelUtils.getRowCount(file, sheet) - 1;  //1st row is header
        int colCount = ExcelUtils.getColumnCount(file, sheet);
        String[][] payload = new String[rowCount][colCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                payload[i][j] = ExcelUtils.getCellValue(file, sheet, i + 1, j + 1);
            }
        }
        logger.info("Loaded {} test records", payload.length);
        return payload;
    }

    @DataProvider(name="usernames")
    private String [] getUsernames(){
        logger.info("Loading DataProvider : usernames");
        int rowCount = ExcelUtils.getRowCount(file, sheet) - 1;
        int usernameCol = ExcelUtils.getColumnIndex(file, sheet, "Username");
        String[] usernames = new String[rowCount];
        for (int i = 0; i < rowCount; i++) {
            usernames[i] = ExcelUtils.getCellValue(file, sheet, i + 1, usernameCol);
        }
        logger.info("Loaded {} test records", usernames.length);
        return usernames;
    }
}
