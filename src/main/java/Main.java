import Models.SingleEvent;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Main {

    private static final String pathToCsv = "C:\\Users\\User\\Desktop\\Jakub\\EventsTest.csv";

    public static void main(String[] args) {

        try (CSVReader reader = new CSVReader(new FileReader(pathToCsv))) {
            List<String[]> r = reader.readAll();
            List<SingleEvent> events = new ArrayList<SingleEvent>();
            int[] indexes = new int[4];
            int actualIndex = 0;

            for (String[] singleLine : r) {
                if (singleLine[0].contains("Parametr 1 zdarzenia RCP - nazwa") || singleLine[0].contains("Data")
                        || singleLine[0].contains("Godzina") || (singleLine[0].contains("Nazwa u�ytkownika"))) {

                    indexes[actualIndex] = getColumnIndex(singleLine[0]);
                    actualIndex++;
                }

                if (actualIndex == 4) {
                    break;
                }
            }

            for (String[] singleLine : r) {
                if (singleLine[0].startsWith("#")) {
                    continue;
                }
                String[] infoForSingleEvent = singleLine[0].split(";");

                String entryType = infoForSingleEvent[indexes[0]-1];
                String date = infoForSingleEvent[indexes[1]-1];
                String time = infoForSingleEvent[indexes[2]-1];
                String name = infoForSingleEvent[indexes[3]-1];

                SingleEvent singleEvent = new SingleEvent(entryType, date, time, name);

                if (!singleEvent.getName().contains("Linia wej�ciowa")) {
                    events.add(singleEvent);
                }
            }

            deleteDuplicates(events);

            for (SingleEvent event : events) {
                if (event.getName().equals("Ruszczyk Bo�ena")) {
                    System.out.println(event);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static void deleteDuplicates(List<SingleEvent> events) {
        Iterator<SingleEvent> iterator = events.iterator();
        SingleEvent old = iterator.next();
        while (iterator.hasNext()) {
            SingleEvent next = iterator.next();
            if (old.getName().equals(next.getName()) && old.getDate().equals(next.getDate())
                    && old.getTime().equals(next.getTime()) && old.getEntryType() == next.getEntryType()) {
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
        int columnIndex = Integer.parseInt(result);
        return columnIndex;
    }

}
