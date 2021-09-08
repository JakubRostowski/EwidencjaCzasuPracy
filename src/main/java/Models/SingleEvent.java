package Models;

import java.util.Objects;

public class SingleEvent {

    private String entryType;
    private final String date;
    private final String time;
    private final String name;

    public SingleEvent(String entryType, String date, String time, String name) {
        if (Objects.equals(entryType, "Wej�cie")) {
            this.entryType = "WEJŚCIE";
        } else if (Objects.equals(entryType, "Wyj�cie")) {
            this.entryType = "WYJŚCIE";
        }
        this.date = date;
        this.time = time;
        this.name = name;
    }

    public String getEntryType() {
        return entryType;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "SingleEvent{" +
                "isEntry=" + entryType +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
