/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.aria.dialogue.gui;

import eu.aria.dialogue.util.Say;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import hmi.flipper.informationstate.Record;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

/**
 *
 * @author Siewart
 */
public class GuiController extends Observable
{
    private static HashMap<Record, GuiController> instances = new HashMap<>();
    private final Record is;
    public synchronized static GuiController getInstance(Record record) {
        
        GuiController inst = instances.get(record);
        if ( inst == null) {
            inst = new GuiController(record);
            instances.put(record, inst);
            UserAndAgentGui gui = UserAndAgentGui.getInstance(inst);
            inst.addObserver(gui);
        }
        return inst;
    }
    
    private GuiController(Record is){
        this.is = is;
    }

    private final LinkedList<Say> agentSays = new LinkedList<>();
    private final LinkedList<Say> userSays = new LinkedList<>();
    private final LinkedList<Say> allSays = new LinkedList<>();

    private String valenceISPath = "$userstates.emotion.valence";
    private String interestISPath = "$userstates.emotion.interest";
    private String arousalISPath = "$userstates.emotion.arousal";
    private String angerISPath = "$userstates.emotion.anger";
    private String disgustISPath = "$userstates.emotion.disgust";
    private String fearISPath = "$userstates.emotion.fear";
    private String happinessISPath = "$userstates.emotion.happiness";
    private String neutraISPath = "$userstates.emotion.neutral";
    private String surprisedISPath = "$userstates.emotion.surpised";
    
    private String userUtterancePath = "$userstates.utterance";
    private String agentUtterancePath = "$agentstates.utterance";
    
    private Say bufferedUserSay = null;
    public synchronized void addAgentSay(Say say, boolean updateIS) {
        agentSays.addLast(say);
        allSays.addLast(say);
        
        if(updateIS){
            Record utterance = is.getRecord(agentUtterancePath);
            if(utterance == null){
                utterance = new DefaultRecord();
                is.set(agentUtterancePath, utterance);
            }
            utterance.set("timestamp", "t:"+say.getTimestamp());
            utterance.set("text", say.getText());
        }
        this.notifyChanged(new DataChanged(DataChangedType.AgentSay, say));
    }

    public synchronized void addUserSay(Say say, boolean updateIS) {
        userSays.addLast(say);
        allSays.addLast(say);
        if(updateIS){
            Record utterance = is.getRecord(userUtterancePath);
            if(utterance == null){
                utterance = new DefaultRecord();
                is.set(userUtterancePath, utterance);
            }
            utterance.set("consumed", "false");
            utterance.set("timestamp", "t:"+say.getTimestamp());
            utterance.set("text", say.getText());
        }
        
        bufferedUserSay = say;
        
        this.notifyChanged(new DataChanged(DataChangedType.UserSay, say));
    }
    
    public synchronized void updateLastSayInIs(){
        Say say = bufferedUserSay;
        if(say != null){
            Record utterance = is.getRecord(userUtterancePath);
            if(utterance == null){
                utterance = new DefaultRecord();
                is.set(userUtterancePath, utterance);
            }
            utterance.set("consumed", "false");
            utterance.set("timestamp", "t:"+say.getTimestamp());
            utterance.set("text", say.getText());
            bufferedUserSay = null;
        }
    }
    public synchronized Say[] getAgentSays() {
        Say[] result = new Say[agentSays.size()];
        return agentSays.toArray(result);
    }

    public synchronized Say[] getUserSays() {
        Say[] result = new Say[userSays.size()];
        return userSays.toArray(result);
    }

    public synchronized Say[] getAllSays() {
        Say[] result = new Say[allSays.size()];
        return allSays.toArray(result);
    }

    public synchronized void setValence(double value) {
        is.set(valenceISPath, value);
        notifyChanged(new DataChanged(DataChangedType.EmotionChange, new EmotionKV("valence", value)));
    }
    
    public synchronized double getValence(){
        return is.getDouble(valenceISPath);
    }
    
    public synchronized void setValencePath(String path) {
        valenceISPath = path;
    }
    
    public synchronized void setInterest(double value) {
        is.set(interestISPath, value);
        notifyChanged(new DataChanged(DataChangedType.EmotionChange, new EmotionKV("interest", value)));
    }
    
    public synchronized double getInterest(){
        return is.getDouble(interestISPath);
    }
    
    public synchronized void setInterestPath(String path) {
        interestISPath = path;
    }
    
    public synchronized void setArousal(double value) {
        is.set(arousalISPath, value);
        notifyChanged(new DataChanged(DataChangedType.EmotionChange, new EmotionKV("arousal", value)));
    }
    
    public synchronized double getArousal(){
        return is.getDouble(arousalISPath);
    }
    
    public synchronized void setArousalPath(String path) {
        arousalISPath = path;
    }
    
    public synchronized void setAnger(double value) {
        is.set(angerISPath, value);
        notifyChanged(new DataChanged(DataChangedType.EmotionChange, new EmotionKV("anger", value)));
    }
    public synchronized double getAnger(){
        return is.getDouble(angerISPath);
    }
    public synchronized void setAngerPath(String path) {
        angerISPath = path;
    }
    public synchronized void setDisgust(double value) {
        is.set(disgustISPath, value);
        notifyChanged(new DataChanged(DataChangedType.EmotionChange, new EmotionKV("disgust", value)));
    }
    public synchronized double getDisgust(){
        return is.getDouble(disgustISPath);
    }
    public synchronized void setDisgustPath(String path) {
        disgustISPath = path;
    }
    public synchronized void setFear(double value) {
        is.set(fearISPath, value);
        notifyChanged(new DataChanged(DataChangedType.EmotionChange, new EmotionKV("fear", value)));
    }
    
    public synchronized double getFear(){
        return is.getDouble(fearISPath);
    }
    public synchronized void setFearPath(String path) {
        fearISPath = path;
    }
    
    public synchronized void setHappiness(double value) {
        is.set(happinessISPath, value);
        notifyChanged(new DataChanged(DataChangedType.EmotionChange, new EmotionKV("happiness", value)));
    }
    
    public synchronized double getHappiness(){
        return is.getDouble(happinessISPath);
    }
    public synchronized void setHappinessPath(String path) {
        happinessISPath = path;
    }
    public synchronized void setNeutral(double value) {
        is.set(neutraISPath, value);
        notifyChanged(new DataChanged(DataChangedType.EmotionChange, new EmotionKV("neutral", value)));
    }

    public synchronized double getNeutral(){
        return is.getDouble(neutraISPath);
    }
     public synchronized void setNeutralPath(String path) {
        neutraISPath = path;
    }   
    public synchronized void setSurpised(double value) {
        is.set(surprisedISPath, value);
        notifyChanged(new DataChanged(DataChangedType.EmotionChange, new EmotionKV("surprised", value)));
    }
    public synchronized void setSurprisedPath(String path) {
        surprisedISPath = path;
    }    
    public synchronized double getSurprised(){
        return is.getDouble(surprisedISPath);
    }

    public synchronized void setUserUtterancePath(String path) {
        userUtterancePath = path;
    }

    public synchronized void setAgentUtterancePath(String path) {
        agentUtterancePath = path;
    }

    public class DataChanged {

        private final DataChangedType type;
        private final Object argument;
        
        public DataChangedType getType() {
            return type;
        }

        public Object getArgument() {
            return argument;
        }
        
        public DataChanged(DataChangedType type, Object argument) {
            this.type = type;
            this.argument = argument;
        }
    }

    public enum DataChangedType {
        AgentSay,
        UserSay,
        EmotionChange
    }

    public class EmotionKV {

        private String emotionName;
        private Double emotionValue;
        EmotionKV(String name, Double value){
            emotionName = name;
            emotionValue = value;
        }
        public String getEmotionName() {
            return emotionName;
        }

        public void setEmotionName(String emotionName) {
            this.emotionName = emotionName;
        }

        public Double getEmotionValue() {
            return emotionValue;
        }

        public void setEmotionValue(Double emotionValue) {
            this.emotionValue = emotionValue;
        }
    }
    private void notifyChanged(DataChanged object){
        setChanged();
        notifyObservers(object);
    }
}
