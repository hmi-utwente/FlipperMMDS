package eu.aria.dialogue.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by adg on 18/01/2016.
 *
 */
public class Say {
    private static final AtomicLong ID = new AtomicLong(0);

    private boolean typed;
    private long id;
    private long timestamp;
    private String text;
    private String actorName;

    public Say(String text, String actorName, boolean typed) {
        id = ID.incrementAndGet();
        this.text = text.trim();
        this.timestamp = System.currentTimeMillis();
        this.typed = typed;
        this.actorName = actorName;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isTyped() {
        return typed;
    }
    
    public String getActorName(){
        return actorName;
    }

    @Override
    public String toString() {
        return "\""+getActorName()+"\":{ \"id\":" + getId() + ", \"timestamp\":" + getTimestamp() + ", \"text\":\"" + getText() + "\" }";
    }
}

