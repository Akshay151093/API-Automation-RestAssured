package utils;

import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;

public class ExcelUtils {

    private static final Logger logger = LogManagerUtil.getLogger(ExcelUtils.class);
    private static final DataFormatter DATA_FORMATTER = new DataFormatter(Locale.US);
    private static XSSFSheet ExcelWSheet;
    private static XSSFWorkbook ExcelWBook;
    private static String currentFilePath;
    private static final String TEST_DATA_PATH = "testData/";
    private static final String EXCEL_EXTENSION = ".xlsx";

    private static String getPath(String fileName) {
        return TEST_DATA_PATH + fileName + EXCEL_EXTENSION;
    }

    public static void setExcelFile(String fileName, String sheetName) throws Exception {
        String path=getPath(fileName);
        logger.info("Loading Excel file '{}' and sheet '{}'.", fileName, sheetName);
        if (ExcelWBook != null && path.equals(currentFilePath)) {
            setSheet(sheetName);
            logger.debug("Workbook already loaded. Switching to sheet '{}'.", sheetName);
            return;
        }
        closeWorkbook();
        logger.debug("Opening workbook: {}", path);
        try (FileInputStream excelFile = new FileInputStream(path)) {
            ExcelWBook = new XSSFWorkbook(excelFile);
            ExcelWSheet = ExcelWBook.getSheet(sheetName);
            if (ExcelWSheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }
            ExcelWBook.setForceFormulaRecalculation(true);
            currentFilePath = path;
            logger.info("Workbook '{}' loaded successfully.", fileName);
            logger.debug("Active sheet set to '{}'.", sheetName);
        } catch (Exception e) {
            logger.error("Unable to load workbook '{}' and sheet '{}'.", fileName, sheetName, e);
            throw e;
        }
    }

    private static void setSheet(String sheetName) {
        ExcelWSheet = ExcelWBook.getSheet(sheetName);
        logger.debug("Selecting sheet '{}'.", sheetName);
        if (ExcelWSheet == null) {
            logger.error("Sheet '{}' not found.", sheetName);
            throw new RuntimeException("Sheet not found: " + sheetName);
        }
    }

    private static Integer getRowNumber(String key, int col) {
        logger.debug("Searching for test data label '{}'.", key);
        for (Row row : ExcelWSheet) {
            Cell cell = row.getCell(col);
            String text = DATA_FORMATTER.formatCellValue(cell);
            if (key.equals(text)) {
                logger.debug("Test data label '{}' found at row {}.", key, row.getRowNum());
                return row.getRowNum();
            }
        }
        logger.warn("Test data label '{}' not found.", key);
        return -1;
    }

    private static void closeWorkbook() {
        try {
            if (ExcelWBook != null) {
                ExcelWBook.close();
                ExcelWBook = null;
                ExcelWSheet = null;
                currentFilePath = null;
                logger.info("Closing Excel workbook.");
            }
        } catch (IOException e) {
            logger.error("Error while closing workbook.", e);
            throw new RuntimeException("Error closing workbook", e);
        }
    }

    private static String getExcelCellValue(int rowNum, int colNum) {
        try {
            Row row = ExcelWSheet.getRow(rowNum);
            if (row == null) {
                return "";
            }
            Cell cell = row.getCell(colNum);
            if (cell == null) {
                return "";
            }
            if (cell.getCellType() == CellType.FORMULA) {
                FormulaEvaluator evaluator = ExcelWBook.getCreationHelper().createFormulaEvaluator();
                cell = evaluator.evaluateInCell(cell);
            }
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                default -> new DataFormatter(Locale.US).formatCellValue(cell);
            };
        } catch (Exception e) {
            throw new RuntimeException("Error reading cell data", e);
        }
    }

    public static LinkedHashMap<String, String> getDataFromRow(String testDataLabel) {
        logger.info("Fetching test data for '{}'.", testDataLabel);
        int rowNumber = getRowNumber(testDataLabel, 0);
        if (rowNumber == -1) {
            logger.warn("No test data found for '{}'.", testDataLabel);
            return null;
        }
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        int col = 2;
        int maxColumns = 50;
        while (col < maxColumns) {
            String label = getExcelCellValue(rowNumber, col);
            if (label == null || label.isEmpty()) {
                break;
            }
            String value = getExcelCellValue(rowNumber + 1, col);
            map.put(label, value);
            col++;
        }
        logger.info("Loaded {} data fields for '{}'.", map.size(), testDataLabel);
        return map;
    }

    public static int getRowCount(String file, String sheet) {
        logger.debug("Calculating row count for file '{}' and sheet '{}'.", file, sheet);
        try {
            setExcelFile(file, sheet);
            int count = ExcelWSheet.getLastRowNum() + 1;
            while (count > 0) {
                Row row = ExcelWSheet.getRow(count - 1);
                if (row != null) {
                    boolean hasData = false;
                    for (Cell cell : row) {
                        if (!new DataFormatter().formatCellValue(cell).trim().isEmpty()) {
                            hasData = true;
                            break;
                        }
                    }
                    if (hasData) {
                        break;
                    }
                }
                count--;
            }
            logger.debug("Sheet '{}' contains {} populated rows.", sheet, count);
            return count;
        } catch (Exception e) {
            logger.error("Unable to calculate row count for sheet '{}'.", sheet, e);
            throw new RuntimeException("Error getting row count", e);
        }
    }

    public static int getColumnCount(String file, String sheet) {
        logger.debug("Calculating column count for sheet '{}'.", sheet);
        try {
            setExcelFile(file, sheet);
            Row headerRow = ExcelWSheet.getRow(0);
            if (headerRow == null) {
                return 0;
            }
            int count = 0;

            for (Cell cell : headerRow) {
                if (!DATA_FORMATTER.formatCellValue(cell).trim().isEmpty()) {
                    count++;
                }
            }
            logger.debug("Sheet '{}' contains {} populated columns.", sheet, count);
            return count;
        } catch (Exception e) {
            logger.error("Unable to calculate column count for sheet '{}'.", sheet, e);
            throw new RuntimeException("Error getting column count", e);
        }
    }

    public static String getCellValue(String file, String sheet, int rowNum, int colNum) {
        logger.trace("Reading cell [Sheet='{}', Row={}, Column={}]", sheet, rowNum, colNum);
        try {
            setExcelFile(file, sheet);
            return getExcelCellValue(rowNum, colNum);
        } catch (Exception e) {
            logger.error("Failed to read cell [Sheet='{}', Row={}, Column={}]", sheet, rowNum, colNum, e);
            throw new RuntimeException(
                    String.format("Error reading cell [%d, %d] from sheet '%s'", rowNum, colNum, sheet),
                    e);
        }
    }

    public static int getColumnIndex(String file, String sheet, String columnName) {
        logger.debug("Searching for column '{}'.", columnName);
        try {
            setExcelFile(file, sheet);
            Row headerRow = ExcelWSheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Header row not found.");
            }
            for (Cell cell : headerRow) {
                String value = DATA_FORMATTER.formatCellValue(cell).trim();
                if (value.equalsIgnoreCase(columnName.trim())) {
                    logger.debug("Column '{}' found at index {}.", columnName, cell.getColumnIndex());
                    return cell.getColumnIndex();
                }
            }
            logger.error("Column '{}' not found.", columnName);
            throw new RuntimeException("Column not found: " + columnName);
        } catch (Exception e) {
            throw new RuntimeException("Error getting column index for: " + columnName, e);
        }
    }
}
