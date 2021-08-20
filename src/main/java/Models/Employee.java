package Models;

import java.util.ArrayList;

public class Employee {

    private String name;
    private ArrayList<Date> dates;

    public Employee(String name, ArrayList<Date> dates) {
        this.name = name;
        this.dates = dates;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Date> getDates() {
        return dates;
    }
}
