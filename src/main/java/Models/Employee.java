package Models;

import java.util.ArrayList;

public class Employee {

    private String name;
    private ArrayList<SingleEntry> entries;

    public Employee(String name, ArrayList<SingleEntry> dates) {
        this.name = name;
        this.entries = dates;
    }

    public void printEntries() {
        System.out.println(this.name);
        String savedDate = "";
        for (SingleEntry entry : this.entries) {
            if (!savedDate.equals(entry.getDate())) {
                System.out.println(entry.getDate());
            }
            System.out.println(entry.getType() + " o " + entry.getTime());
            savedDate = entry.getDate();
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<SingleEntry> getEntries() {
        return entries;
    }
}
