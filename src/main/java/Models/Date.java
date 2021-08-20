package Models;

import java.util.ArrayList;

public class Date {

    private String day;
    private ArrayList<SingleEntry> timeOfEntry;

    public Date(String day, ArrayList timeOfEntry) {
        this.day = day;
        this.timeOfEntry = timeOfEntry;
    }

    public String getDay() {
        return day;
    }

    public ArrayList<SingleEntry> getTimeOfEntry() {
        return timeOfEntry;
    }
}
