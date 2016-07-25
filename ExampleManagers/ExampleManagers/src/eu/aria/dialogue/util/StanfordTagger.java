package eu.aria.dialogue.util;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author psamp3
 */
public class StanfordTagger {

    private String tagged;
    private String sample;
    static MaxentTagger tagger;

    StanfordTagger(String taggerLocation) {
        tagged = "";
        sample = "";
        tagger = new MaxentTagger(taggerLocation);       
    }

    public ArrayList<String> tagFile(ArrayList<String> wordsList) {

        int i = 0;
        //we will now pick up sentences line by line from the file input.txt and store it in the string sample
        while (i < wordsList.size()) {
            //tag the string
            sample = wordsList.get(i);
            tagged = tagger.tagString(sample);
            wordsList.set(i, tagged);
            i++;
        }
        return wordsList;
    }
}
