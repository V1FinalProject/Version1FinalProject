package com.example.tagging;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Reads the mock nominations spreadsheet. This is seed data now rather than the
// live source: NominationStore calls loadAll() once at startup and serves every
// request from memory after that.
@Component
public class NominationExcelLoader {

    private static final int HEADER_ROW_INDEX = 0;

    /**
     * These rows predate the {@code quarter} field entirely (the spreadsheet
     * has no such column), so every row from it is stamped as the current
     * programme round - see {@link Quarter#CURRENT_QUARTER}.
     */
    private static final String LEGACY_SEED_QUARTER = Quarter.CURRENT_QUARTER;

    private final String filePath;

    public NominationExcelLoader(
            @Value("${tagging.nominations-file:../data/Star_Awards_Mock_Nominations.xlsx}") String filePath) {
        this.filePath = filePath;
    }

    public List<Nomination> loadAll() {
        List<Nomination> nominations = new ArrayList<>();

        try (InputStream inputStream = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || hasBlankId(row)) {
                    continue;
                }
                nominations.add(toNomination(row));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read nominations spreadsheet at " + filePath, e);
        }

        return nominations;
    }

    private boolean hasBlankId(Row row) {
        Cell idCell = row.getCell(0);
        return idCell == null || idCell.getCellType() == CellType.BLANK;
    }

    private Nomination toNomination(Row row) {
        int id = (int) row.getCell(0).getNumericCellValue();
        LocalDateTime timestamp = row.getCell(1).getLocalDateTimeCellValue();
        String nominatorName = stringValue(row.getCell(2));
        String nominatorEmail = stringValue(row.getCell(3));
        String nomineeName = stringValue(row.getCell(4));
        String nomineeEmail = stringValue(row.getCell(5));
        String justification = stringValue(row.getCell(6));
        String category = stringValue(row.getCell(7));

        return Nomination.fromJustification(id, timestamp, nominatorName, nominatorEmail, nomineeName,
                nomineeEmail, justification, category, LEGACY_SEED_QUARTER);
    }

    private String stringValue(Cell cell) {
        return cell == null ? "" : cell.getStringCellValue().trim();
    }
}
