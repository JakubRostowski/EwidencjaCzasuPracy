import Models.Employee;
import Models.SingleEntry;
import Models.SingleEvent;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    private static final String pathToCsv = "C:\\Users\\User\\Desktop\\Jakub\\EventsTest.csv";
    private static ArrayList<String> dates;

    public static void main(String[] args) {

        try (CSVReader reader = new CSVReader(new FileReader(pathToCsv))) {
            List<String[]> r = reader.readAll();
            List<SingleEvent> events = new ArrayList<>();

            populateEvents(r, events);
            deleteDuplicates(events);
            dates = getDates(events);
            List<Employee> employees = extractEmployees(events);

            for (Employee employee : employees) {
                for (SingleEvent event : events) {
                    if (employee.getName().equals(event.getName())) {
                        employee.getEntries().add(new SingleEntry(event.getEntryType(), event.getDate(), event.getTime()));
                    }
                }
            }

            Workbook workbook = createExcel(employees);
            exportExcelFile(workbook);

        } catch (IOException | CsvException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static void populateEvents(List<String[]> r, List<SingleEvent> events) {
        int[] indexes = getColumnIndexes(r);
        for (String[] singleLine : r) {
            if (singleLine[0].startsWith("#")) {
                continue;
            }
            String[] infoForSingleEvent = singleLine[0].split(";");

            String entryType = infoForSingleEvent[indexes[0] - 1];
            String accessType = infoForSingleEvent[indexes[1] - 1];
            String date = infoForSingleEvent[indexes[2] - 1];
            String time = formatToHoursAndMinutes(infoForSingleEvent[indexes[3] - 1]);
            String name = infoForSingleEvent[indexes[4] - 1];

            if (!name.contains("Linia wej�ciowa") && accessType.contains("001")) {
                SingleEvent singleEvent = new SingleEvent(entryType, date, time, name);
                events.add(singleEvent);
            }
        }
    }

    private static int[] getColumnIndexes(List<String[]> r) {
        int[] indexes = new int[5];
        int actualIndex = 0;

        for (String[] singleLine : r) {
            if (singleLine[0].contains("Parametr 1 zdarzenia RCP - nazwa") || singleLine[0].contains("Data")
                    || singleLine[0].contains("Godzina") || singleLine[0].contains("Nazwa u�ytkownika")
                    || singleLine[0].contains("Nazwa zdarzenia")) {

                indexes[actualIndex] = extractIndex(singleLine[0]);
                actualIndex++;
            }

            if (actualIndex == indexes.length) {
                break;
            }
        }
        return indexes;
    }

    private static ArrayList<String> getDates(List<SingleEvent> events) {
        ArrayList<String> dates = new ArrayList<>();
        String savedDate = "";
        for (SingleEvent event : events) {
            if (!savedDate.equals(event.getDate())) {
                dates.add(event.getDate());
                savedDate = event.getDate();
            }
        }
        return dates;
    }

    private static void exportExcelFile(Workbook workbook) throws IOException {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
    }

    private static Workbook createExcel(List<Employee> employees) {
        Workbook workbook = new XSSFWorkbook();
        CellStyle borderStyle = setBorderStyle(workbook);

        for (Employee employee : employees) {
            Sheet sheet = workbook.createSheet(employee.getName());
            sheet.getPrintSetup().setScale((short) 140);
            sheet.setPrintGridlines(true);
            setColumnWidths(sheet);
            setEmployeeNames(employee, sheet);
            setColumnNames(sheet, borderStyle);
            writeRows(employee, sheet, borderStyle);
        }
        return workbook;
    }

    private static CellStyle setBorderStyle(Workbook workbook) {
        CellStyle borders = workbook.createCellStyle();
        borders.setBorderTop(BorderStyle.THIN);
        borders.setBorderBottom(BorderStyle.THIN);
        borders.setBorderLeft(BorderStyle.THIN);
        borders.setBorderRight(BorderStyle.THIN);
        return borders;
    }

    private static void setColumnWidths(Sheet sheet) {
        sheet.setColumnWidth(0, 2800);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
    }

    private static void setEmployeeNames(Employee employee, Sheet sheet) {
        Row header = sheet.createRow(0);
        Cell headerCell = header.createCell(1);
        headerCell.setCellValue("Imię i nazwisko: " + employee.getName());
    }

    private static void writeRows(Employee employee, Sheet sheet, CellStyle borderStyle) {
        int rowIndex = 2;
        for (String date : dates) {
            Row row = sheet.createRow(rowIndex);
            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(date);
            dateCell.setCellStyle(borderStyle);

            ArrayList<String[]> entriesOfDate = new ArrayList<>();

            for (SingleEntry entry : employee.getEntries()) {
                if (date.equals(entry.getDate())) {
                    String[] timeAndTypes = {entry.getTime(), entry.getType()};
                    entriesOfDate.add(timeAndTypes);
                }
            }
//            for debugging
//            writeEntryCounts(row, entriesOfDate);

            if (entriesOfDate.size() >= 2) {
                entriesOfDate = simplifyEntries(entriesOfDate);
            }

            writeTimestamps(row, entriesOfDate);

            rowIndex++;
        }
    }

    private static void writeEntryCounts(Row row, ArrayList<String[]> entriesOfDate) {
        Cell entryNumber = row.createCell(4);
        entryNumber.setCellValue(entriesOfDate.size());
    }

    private static void writeTimestamps(Row row, ArrayList<String[]> entriesOfDate) {
        for (String[] entryOfDate : entriesOfDate) {
            Cell entryCell = getCell(row, entryOfDate);
            entryCell.setCellValue(entryOfDate[0]);
        }
    }

    private static ArrayList<String[]> simplifyEntries(ArrayList<String[]> entriesOfDate) {
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

    private static int countEntryType(ArrayList<String[]> entriesOfDate, String searchedEntryType) {
        int counter = 0;
        for (String[] entryOfDate : entriesOfDate) {
            if (entryOfDate[1].equals(searchedEntryType)) {
                counter++;
            }
        }
        return counter;
    }

    private static void estimateInaccurateTimes(ArrayList<String[]> entriesOfDate, ArrayList<String[]> simplifiedEntries, String entryType) {
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

    private static Cell getCell(Row row, String[] entryInfo) {
        Cell entryCell = null;
        if (entryInfo[1].equals("WEJŚCIE")) {
            entryCell = row.createCell(1);
        } else if (entryInfo[1].equals("WYJŚCIE")) {
            entryCell = row.createCell(2);
        }
        return entryCell;
    }

    private static void setColumnNames(Sheet sheet, CellStyle borderStyle) {
        Row columnNames = sheet.createRow(1);
        Cell in = columnNames.createCell(1);
        in.setCellValue("Wejście");
        in.setCellStyle(borderStyle);
        Cell out = columnNames.createCell(2);
        out.setCellValue("Wyjście");
        out.setCellStyle(borderStyle);
        Cell totalHours = columnNames.createCell(3);
        totalHours.setCellValue("Razem");
        totalHours.setCellStyle(borderStyle);
    }

    private static List<Employee> extractEmployees(List<SingleEvent> events) {
        List<Employee> employees = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (SingleEvent event : events) {
            if (!names.contains(event.getName()) && !event.getName().equals("U�ytkownik nieznany")
                    && !event.getName().equals("Harmonogram:") && !event.getName().isEmpty()) {
                names.add(event.getName());
                employees.add(new Employee(event.getName(), new ArrayList<>()));
            }
        }
        return employees;
    }

    private static void deleteDuplicates(List<SingleEvent> events) {
        Iterator<SingleEvent> iterator = events.iterator();
        SingleEvent old = iterator.next();
        while (iterator.hasNext()) {
            SingleEvent next = iterator.next();
            if (old.getName().equals(next.getName()) && old.getDate().equals(next.getDate())
                    && old.getTime().equals(next.getTime()) && old.getEntryType().equals(next.getEntryType())) {
                iterator.remove();
            }
            old = next;
        }
    }

    private static int extractIndex(String line) {
        StringBuilder sb = new StringBuilder(line);
        String result = sb.substring(7, 9);
        if (result.contains("=")) {
            result = sb.substring(7, 8);
        }
        return Integer.parseInt(result);
    }

    private static String formatToHoursAndMinutes(String time) {
        if (time.length() == 8) {
            return time.substring(0, time.length() - 3);
        }
        return time;
    }

}
