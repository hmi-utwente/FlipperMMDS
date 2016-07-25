/**
 * Created by Andry Chowanda on 18/12/2015.
 */
package eu.aria.dialogue.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class RulesReader {
    private ArrayList<Rules> rules;
    private String filename;

    public RulesReader(String filename) {
        rules = new ArrayList<>();
        this.filename = filename;
    }

    public boolean readData() {
        try{
            File XMLFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(XMLFile);
            doc.getDocumentElement().normalize();
            NodeList nListRules = doc.getElementsByTagName("rule");

            for (int i = 0; i < nListRules.getLength(); i++) {
                Node nRule = nListRules.item(i);
                if (nRule.getNodeType() == Node.ELEMENT_NODE) {
                    Element eRule = (Element) nRule;
                    Rules rule = new Rules();

                    rule.setID(eRule.getAttribute("id"));

                    Node nWords = eRule.getElementsByTagName("words").item(0);
                    if(nWords.getNodeType() == Node.ELEMENT_NODE){
                        Element eWord = (Element) nWords;
                        ArrayList<String> words = new ArrayList<>();

                        NodeList nListWord = eWord.getElementsByTagName("word");
                        for (int j = 0; j < nListWord.getLength(); j++) {
                            Node nWord = nListWord.item(j);
                            if (nWord.getNodeType() == Node.ELEMENT_NODE) {
                                Element eWordItem = (Element) nWord;
                                words.add(eWordItem.getAttribute("value"));
                            }
                        }
                        rule.setWords(words);
                    }

                    Node nStates = eRule.getElementsByTagName("states").item(0);
                    if(nStates.getNodeType() == Node.ELEMENT_NODE){
                        Element eState = (Element) nStates;
                        ArrayList<State> states = new ArrayList<>();

                        NodeList nListState = eState.getElementsByTagName("state");
                        for (int j = 0; j < nListState.getLength(); j++) {
                            Node nState = nListState.item(j);
                            if (nState.getNodeType() == Node.ELEMENT_NODE) {
                                Element eStateItem = (Element) nState;
                                State state = new State(eStateItem.getAttribute("name"), eStateItem.getAttribute("value"));
                                states.add(state);
                            }
                        }
                        rule.setStates(states);
                    }
                    this.rules.add(rule);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public ArrayList<Rules> getRules() {
        return rules;
    }
}

