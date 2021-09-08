package Models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventsList {

    private ArrayList<SingleEvent> events;

    public EventsList(List<String[]> CsvFile) {
        this.events = extractEvents(CsvFile);
    }

    private static ArrayList<SingleEvent> extractEvents(List<String[]> linesOfCsv) {
        int[] indexes = getColumnIndexes(linesOfCsv);
        ArrayList<SingleEvent> events = new ArrayList<>();
        for (String[] singleLine : linesOfCsv) {
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
        return events;
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

    public void deleteDuplicates() {
        Iterator<SingleEvent> iterator = this.events.iterator();
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

    public List<Employee> extractEmployees() {
        List<Employee> employees = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (SingleEvent event : this.events) {
            if (!names.contains(event.getName()) && !event.getName().equals("U�ytkownik nieznany")
                    && !event.getName().equals("Harmonogram:") && !event.getName().isEmpty()) {
                names.add(event.getName());
                employees.add(new Employee(event.getName(), new ArrayList<>()));
            }
        }
        return employees;
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

    public ArrayList<SingleEvent> getEvents() {
        return events;
    }
}
