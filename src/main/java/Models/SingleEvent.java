package Models;

import java.util.Objects;

public class SingleEvent {
    private Entry entryType;
    private String date;
    private String time;
    private String name;

    private enum Entry{
        WEJŚCIE, WYJŚCIE
    }

    public SingleEvent(String entryType, String date, String time, String name) {
        if (Objects.equals(entryType, "Wej�cie")) {
            this.entryType = Entry.WEJŚCIE;
        } else if (Objects.equals(entryType, "Wyj�cie")){
            this.entryType = Entry.WYJŚCIE;
        }
        this.date = date;
        this.time = time;
        this.name = name;
    }

    public Entry getEntryType() {
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
