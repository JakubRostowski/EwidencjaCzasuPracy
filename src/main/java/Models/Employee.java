package Models;

import java.util.ArrayList;

public class Employee {

    private final String name;
    private ArrayList<SingleEntry> entries;

    public Employee(String name, ArrayList<SingleEntry> entries) {
        this.name = name;
        this.entries = entries;
    }

    public String getName() {
        return name;
    }

    public ArrayList<SingleEntry> getEntries() {
        return entries;
    }
}
