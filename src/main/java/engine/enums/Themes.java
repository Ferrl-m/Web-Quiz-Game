package engine.enums;

public enum Themes {
    SCIENCE("Science"), GEOGRAPHY("Geography"), HISTORY("History"),
    FOOD("Food"), SPORTS("Sports"), TECHNOLOGY("Technology"),
    LITERATURE("Literature"), POP("Pop Culture"), LOGIC("Logic");

    private String title;

    Themes(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
