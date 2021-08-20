package Models;

public class SingleEntry {

    private String type;
    private String date;
    private String time;

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
        return time;
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
