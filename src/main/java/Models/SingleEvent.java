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

    public SingleEvent(String[] singleLine, int[] indexes) {
        if (Objects.equals(singleLine[indexes[0] - 1], "Wej�cie")) {
            this.entryType = Entry.WEJŚCIE;
        } else if (Objects.equals(singleLine[indexes[0] - 1], "Wyj�cie")){
            this.entryType = Entry.WYJŚCIE;
        }
        this.date = singleLine[indexes[1]-1];
        this.time = singleLine[indexes[2]-1];
        this.name = singleLine[indexes[3]-1];
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
