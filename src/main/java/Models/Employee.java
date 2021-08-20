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
        for (SingleEntry entry : this.entries) {
            System.out.println(entry.getDate());
            System.out.println(entry.getType() + " o " + entry.getTime());
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<SingleEntry> getEntries() {
        return entries;
    }
}
