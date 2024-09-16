package seng202.team0.database;

public record WineList(long id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
