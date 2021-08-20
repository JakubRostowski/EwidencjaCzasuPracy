import Models.Employee;
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
                String time = infoForSingleEvent[indexes[3] - 1];
                String name = infoForSingleEvent[indexes[4] - 1];

                if (!name.contains("Linia wej�ciowa") && accessType.contains("001")) {
                    SingleEvent singleEvent = new SingleEvent(entryType, date, time, name);
                    events.add(singleEvent);
                }
            }

            deleteDuplicates(events);

            List<Employee> employees = extractEmployees(events);
            List<String> dates = extractDates(events);



        } catch (IOException | CsvException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> extractEmployees(List<SingleEvent> events) {
        List<Employee> employees = new ArrayList<>();
        for (SingleEvent event : events) {
            if (!employees.contains(event.getName()) && !event.getName().equals("U�ytkownik nieznany")
                    && !event.getName().equals("Harmonogram:") && !event.getName().isEmpty()) {
                employees.add(new Employee(event.getName(), new ArrayList<>()));
            }
        }
        return employees;
    }

    private static List<String> extractDates(List<SingleEvent> events) {
        List<String> dates = new ArrayList<>();
        for (SingleEvent event : events) {
            if (!dates.contains(event.getDate())) {
                dates.add(event.getDate());
            }
        }
        return dates;
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

}
