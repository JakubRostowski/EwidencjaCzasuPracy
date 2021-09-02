import Models.Employee;
import Models.ExcelFile;
import Models.SingleEntry;
import Models.SingleEvent;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    private static final String pathToCsv = "C:\\Users\\User\\Desktop\\Jakub\\EventsTest.csv";

    public static void main(String[] args) {

        try (CSVReader reader = new CSVReader(new FileReader(pathToCsv))) {
            List<String[]> linesOfCsv = reader.readAll();
            List<SingleEvent> events = new ArrayList<>();

            populateEvents(linesOfCsv, events);
            deleteDuplicates(events);
            List<Employee> employees = extractEmployees(events);
            bindEventsToEmployees(events, employees);

            ExcelFile excelFile = new ExcelFile(getDates(events));
            excelFile.createExcel(employees);
            excelFile.exportExcelFile();

        } catch (IOException | CsvException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static void bindEventsToEmployees(List<SingleEvent> events, List<Employee> employees) {
        for (Employee employee : employees) {
            for (SingleEvent event : events) {
                if (employee.getName().equals(event.getName())) {
                    employee.getEntries().add(new SingleEntry(event.getEntryType(), event.getDate(), event.getTime()));
                }
            }
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
