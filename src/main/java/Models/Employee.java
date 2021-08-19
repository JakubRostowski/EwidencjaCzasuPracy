package Models;

import java.util.ArrayList;

public class Employee {

    private String name;
    private ArrayList<String[]> logs;

    public Employee(String name, ArrayList<String[]> logs) {
        this.name = name;
        this.logs = logs;
    }
}
