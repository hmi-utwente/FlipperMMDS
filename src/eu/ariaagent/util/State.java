package eu.ariaagent.util;

public class State {
    private String name;
    private String value;

    public State(String name, String value){
        this.name = name;
        this.value = value;
    }

    public void setName(String name){this.name = name;}

    public String getName(){return name;}

    public void setValue(String value){this.value = value;}

    public String getValue(){return value;}
}