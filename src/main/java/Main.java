import Models.Employee;
import Models.SingleEntry;
import Models.SingleEvent;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
            int[] indexes = new int[5];
            int actualIndex = 0;

            for (String[] singleLine : r) {
                if (singleLine[0].contains("Parametr 1 zdarzenia RCP - nazwa") || singleLine[0].contains("Data")
                        || singleLine[0].contains("Godzina") || singleLine[0].contains("Nazwa u�ytkownika")
                        || singleLine[0].contains("Nazwa zdarzenia")) {

                    indexes[actualIndex] = getColumnIndex(singleLine[0]);
                    actualIndex++;
                }

                if (actualIndex == indexes.length) {
                    break;
                }
            }

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
        for (Employee employee : employees) {
            Sheet sheet = workbook.createSheet(employee.getName());
            sheet.setColumnWidth(0,2800);

            Row header = sheet.createRow(0);
            Cell headerCell = header.createCell(1);
            headerCell.setCellValue("Imię i nazwisko: " + employee.getName());

            setColumnNames(sheet);

            int rowIndex = 2;
            for (String date : dates) {
                Row row = sheet.createRow(rowIndex);
                Cell dateCell = row.createCell(0);
                dateCell.setCellValue(date);

                ArrayList<String[]> entriesOfDate = new ArrayList<>();

//                int entryCounter = 0;
                for (SingleEntry entry : employee.getEntries()) {
                    if (date.equals(entry.getDate())) {
                        String[] timeAndTypes = {entry.getTime(), entry.getType()};
                        entriesOfDate.add(timeAndTypes);
//                        entryCounter++;
                    }
                }
                Cell entryNumber = row.createCell(3);
                entryNumber.setCellValue(entriesOfDate.size());

//                if (entryCounter == 1) {
//                    employee.
//                }

                rowIndex++;
            }
        }
        return workbook;
    }

    private static void setColumnNames(Sheet sheet) {
        Row columnNames = sheet.createRow(1);
        Cell in = columnNames.createCell(1);
        in.setCellValue("Wejście");
        Cell out = columnNames.createCell(2);
        out.setCellValue("Wyjście");
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

    private static int getColumnIndex(String line) {
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
