package engine.enums;

public enum Themes {
    MATH("Math"), GEOGRAPHY("Geography"), HISTORY("History");

    private String title;

    Themes(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
