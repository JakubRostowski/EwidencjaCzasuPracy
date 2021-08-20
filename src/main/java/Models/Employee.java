package Models;

import java.util.ArrayList;

public class Employee {

    private String name;
    private ArrayList<SingleEntry> entries;

    public Employee(String name, ArrayList<SingleEntry> dates) {
        this.name = name;
        this.entries = dates;
    }

    public String getName() {
        return name;
    }

    public ArrayList<SingleEntry> getEntries() {
        return entries;
    }
}
