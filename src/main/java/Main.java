import Models.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {

    private static final String pathToCsv = "C:\\Users\\User\\Desktop\\Jakub\\EventsTest.csv";

    public static void main(String[] args) {

        try (CSVReader reader = new CSVReader(new FileReader(pathToCsv))) {
            List<String[]> linesOfCsv = reader.readAll();
            EventsList events = new EventsList(linesOfCsv);
            events.deleteDuplicates();

            List<Employee> employees = events.extractEmployees();
            events.bindEventsToEmployees(employees);

            ExcelFile excelFile = new ExcelFile(events.getDates());
            excelFile.create(employees);
            excelFile.export();

        } catch (IOException | CsvException | NullPointerException e) {
            e.printStackTrace();
        }
    }

}
