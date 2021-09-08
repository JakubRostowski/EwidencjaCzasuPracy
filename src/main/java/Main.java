import Models.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String pathToCsv = "C:\\Users\\User\\Desktop\\Jakub\\EventsTest.csv";

    public static void main(String[] args) {

        try (CSVReader reader = new CSVReader(new FileReader(pathToCsv))) {
            List<String[]> linesOfCsv = reader.readAll();
            EventsList events = new EventsList(linesOfCsv);
            events.deleteDuplicates();

            List<Employee> employees = extractEmployees(events.getEvents());
            bindEventsToEmployees(events.getEvents(), employees);

            ExcelFile excelFile = new ExcelFile(getDates(events.getEvents()));
            excelFile.create(employees);
            excelFile.export();

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



}
