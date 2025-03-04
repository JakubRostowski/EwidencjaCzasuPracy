package Models;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExcelFile {
    private Workbook workbook;
    private final ArrayList<String> dates;

    public ExcelFile(ArrayList<String> dates) {
        this.workbook = new XSSFWorkbook();
        this.dates = dates;
    }

    public void export() throws IOException {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
    }

    public void create(List<Employee> employees) {
        this.workbook = new XSSFWorkbook();
        CellStyle borderStyle = setBorderStyle(workbook);

        for (Employee employee : employees) {
            Sheet sheet = workbook.createSheet(employee.getName());
            setPrintSettings(sheet);
            setColumnWidths(sheet);
            setEmployeeNames(employee, sheet);
            setColumnNames(sheet, borderStyle);
            writeRows(employee, sheet, borderStyle);
        }
    }

    private void setPrintSettings(Sheet sheet) {
        sheet.getPrintSetup().setScale((short) 140);
        sheet.setPrintGridlines(true);
    }

    private CellStyle setBorderStyle(Workbook workbook) {
        CellStyle borders = workbook.createCellStyle();
        borders.setBorderTop(BorderStyle.THIN);
        borders.setBorderBottom(BorderStyle.THIN);
        borders.setBorderLeft(BorderStyle.THIN);
        borders.setBorderRight(BorderStyle.THIN);
        return borders;
    }

    private void setColumnWidths(Sheet sheet) {
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 2800);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
    }

    private void setEmployeeNames(Employee employee, Sheet sheet) {
        Row header = sheet.createRow(0);
        Cell headerCell = header.createCell(2);
        headerCell.setCellValue("Imię i nazwisko: " + employee.getName());
    }

    private void writeRows(Employee employee, Sheet sheet, CellStyle borderStyle) {
        int rowIndex = 2;
        for (String date : this.dates) {
            Row row = sheet.createRow(rowIndex);
            writeDayOfWeek(date, row);
            writeDateCell(borderStyle, date, row);

            ArrayList<String[]> entriesOfDate = new ArrayList<>();

            populateEntriesOfDate(employee, date, entriesOfDate);
//            for debugging
//            writeEntryCounts(row, entriesOfDate);

            writeTotalHours(row, entriesOfDate);

            if (entriesOfDate.size() >= 2) {
                entriesOfDate = simplifyEntries(entriesOfDate);
            }

            writeTimestamps(row, entriesOfDate);

            rowIndex++;
        }
    }

    private void populateEntriesOfDate(Employee employee, String date, ArrayList<String[]> entriesOfDate) {
        for (SingleEntry entry : employee.getEntries()) {
            if (date.equals(entry.getDate())) {
                String[] timeAndTypes = {entry.getTime(), entry.getType()};
                entriesOfDate.add(timeAndTypes);
            }
        }
    }

    private void writeDayOfWeek(String date, Row row) {
        Cell dayCell = row.createCell(0);
        dayCell.setCellValue(getPolishSubstitute(date));
    }

    private String getPolishSubstitute(String date) {
        int year = Integer.parseInt(date.split("-")[0]);
        int month = Integer.parseInt(date.split("-")[1]);
        int day = Integer.parseInt(date.split("-")[2]);

        LocalDate localDate = LocalDate.of(year, month, day);
        java.time.DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        return switch (dayOfWeek.getValue()) {
            case 1 -> "Pon";
            case 2 -> "Wt";
            case 3 -> "Śr";
            case 4 -> "Czw";
            case 5 -> "Pt";
            case 6 -> "Sob";
            case 7 -> "Niedz";
            default -> "";
        };
    }

    private void writeDateCell(CellStyle borderStyle, String date, Row row) {
        Cell dateCell = row.createCell(1);
        dateCell.setCellValue(date);
        dateCell.setCellStyle(borderStyle);
    }

    private void writeTotalHours(Row row, ArrayList<String[]> entriesOfDate) {
        Cell totalHours = row.createCell(4);
        if (entriesOfDate.size() == 2) {
            if (!entriesOfDate.get(0)[1].equals(entriesOfDate.get(1)[1])) {
                int inHours, inMinutes, outHours, outMinutes;

                if (entriesOfDate.get(0)[1].equals("WEJŚCIE")) {
                    inHours = Integer.parseInt(entriesOfDate.get(0)[0].split(":")[0]);
                    inMinutes = Integer.parseInt(entriesOfDate.get(0)[0].split(":")[1]);
                    outHours = Integer.parseInt(entriesOfDate.get(1)[0].split(":")[0]);
                    outMinutes = Integer.parseInt(entriesOfDate.get(1)[0].split(":")[1]);
                } else {
                    inHours = Integer.parseInt(entriesOfDate.get(1)[0].split(":")[0]);
                    inMinutes = Integer.parseInt(entriesOfDate.get(1)[0].split(":")[1]);
                    outHours = Integer.parseInt(entriesOfDate.get(0)[0].split(":")[0]);
                    outMinutes = Integer.parseInt(entriesOfDate.get(0)[0].split(":")[1]);
                }
                int inTotalMinutes = inHours * 60 + inMinutes;
                int outTotalMinutes = outHours * 60 + outMinutes;

                boolean isNightShift = checkNightShift(inTotalMinutes, outTotalMinutes);

                if (isNightShift) {
                    int actualDayShift = 1440 - inTotalMinutes;
                    int totalMinutesWorked = outTotalMinutes + actualDayShift;
                    printHours(totalHours, totalMinutesWorked);
                } else {
                    int totalMinutesWorked = outTotalMinutes - inTotalMinutes;
                    printHours(totalHours, totalMinutesWorked);
                }

            }
        }
    }

    private void printHours(Cell totalHours, int totalMinutesWorked) {
        String totalTimeWorked = totalMinutesWorked / 60 + ":" +
                (totalMinutesWorked % 60 < 10 ? "0" + totalMinutesWorked % 60 : totalMinutesWorked % 60);
        totalHours.setCellValue(totalTimeWorked);
    }

    private boolean checkNightShift(int inTotalMinutes, int outTotalMinutes) {
        return inTotalMinutes > outTotalMinutes;
    }

    private void writeEntryCounts(Row row, ArrayList<String[]> entriesOfDate) {
        Cell entryNumber = row.createCell(5);
        entryNumber.setCellValue(entriesOfDate.size());
    }

    private void writeTimestamps(Row row, ArrayList<String[]> entriesOfDate) {
        for (String[] entryOfDate : entriesOfDate) {
            Cell entryCell = getCell(row, entryOfDate);
            entryCell.setCellValue(entryOfDate[0]);
        }
    }

    private ArrayList<String[]> simplifyEntries(ArrayList<String[]> entriesOfDate) {
        int in = countEntryType(entriesOfDate, "WEJŚCIE");
        int out = countEntryType(entriesOfDate, "WYJŚCIE");

        ArrayList<String[]> simplifiedEntries = new ArrayList<>();

        if (in > 1 || out > 1) {
            if (in > 1) {
                estimateInaccurateTimes(entriesOfDate, simplifiedEntries, "WEJŚCIE");
            } else {
                for (String[] entry : entriesOfDate) {
                    if (entry[1].equals("WEJŚCIE")) {
                        simplifiedEntries.add(entry);
                    }
                }
            }
            if (out > 1) {
                estimateInaccurateTimes(entriesOfDate, simplifiedEntries, "WYJŚCIE");
            } else {
                for (String[] entry : entriesOfDate) {
                    if (entry[1].equals("WYJŚCIE")) {
                        simplifiedEntries.add(entry);
                    }
                }
            }
            return simplifiedEntries;
        }
        return entriesOfDate;
    }

    private int countEntryType(ArrayList<String[]> entriesOfDate, String searchedEntryType) {
        int counter = 0;
        for (String[] entryOfDate : entriesOfDate) {
            if (entryOfDate[1].equals(searchedEntryType)) {
                counter++;
            }
        }
        return counter;
    }

    private void estimateInaccurateTimes(ArrayList<String[]> entriesOfDate, ArrayList<String[]> simplifiedEntries, String entryType) {
        String firstValue = "";
        String lastValue = "";

        for (String[] entryOfDate : entriesOfDate) {
            if (entryOfDate[1].equals(entryType)) {
                if (firstValue.equals("")) {
                    firstValue = entryOfDate[0];
                }
                lastValue = entryOfDate[0];
            }
        }
        String estimatedTime = firstValue + " / " + lastValue;
        String[] output = {estimatedTime, entryType};
        simplifiedEntries.add(output);
    }

    private Cell getCell(Row row, String[] entryInfo) {
        Cell entryCell = null;
        if (entryInfo[1].equals("WEJŚCIE")) {
            entryCell = row.createCell(2);
        } else if (entryInfo[1].equals("WYJŚCIE")) {
            entryCell = row.createCell(3);
        }
        return entryCell;
    }

    private void setColumnNames(Sheet sheet, CellStyle borderStyle) {
        Row columnNames = sheet.createRow(1);
        Cell in = columnNames.createCell(2);
        in.setCellValue("Wejście");
        in.setCellStyle(borderStyle);
        Cell out = columnNames.createCell(3);
        out.setCellValue("Wyjście");
        out.setCellStyle(borderStyle);
        Cell totalHours = columnNames.createCell(4);
        totalHours.setCellValue("Razem");
        totalHours.setCellStyle(borderStyle);
    }
}
