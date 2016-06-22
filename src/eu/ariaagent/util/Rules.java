/**
 * Created by Andry Chowanda on 18/12/2015.
 */

package eu.ariaagent.util;

import java.util.ArrayList;

public class Rules {
    private String ID;
    private ArrayList<String> words;
    private ArrayList<State> states;

    public Rules(){
        ID = "";
        words = new ArrayList<>();
        states = new ArrayList<>();
    }

    public Rules(String ID, ArrayList<String> words, ArrayList<State> states){
        this.ID = ID;
        this.words = words;
        this.states = states;
    }

    public void setID(String ID){this.ID = ID;}

    public String getID(){return ID;}

    public void setWords(ArrayList<String> words){this.words = words;}

    public ArrayList<String> getWords(){return words;}

    public void setStates(ArrayList<State> states){this.states = states;}

    public ArrayList<State> getStates(){return states;}

}
