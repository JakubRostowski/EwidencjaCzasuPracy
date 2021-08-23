package Models;

public class SingleEntry {

    private final String type;
    private final String date;
    private final String time;

    public SingleEntry(String type, String date, String time) {
        this.type = type;
        this.date = date;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return this.time.substring(0, this.time.length() - 3);
    }

    @Override
    public String toString() {
        return "SingleEntry{" +
                "type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
