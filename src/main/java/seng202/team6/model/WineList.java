package seng202.team6.model;

public record WineList(long id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
