package main.java.app.messages;

public class SimpleNameMessage {
    private String name;

    public SimpleNameMessage () {}

    public SimpleNameMessage (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }
}