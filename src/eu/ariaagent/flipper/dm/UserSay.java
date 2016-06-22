package eu.ariaagent.flipper.dm;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by adg on 18/01/2016.
 *
 */
public class UserSay {
    private static final AtomicLong ID = new AtomicLong(0);

    private boolean typed;
    private long id;
    private long timestamp;
    private String text;

    public UserSay(String text, boolean typed) {
        id = ID.incrementAndGet();
        this.text = text.trim();
        this.timestamp = System.currentTimeMillis();
        this.typed = typed;
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

    @Override
    public String toString() {
        return "\"UserSay\":{ \"id\":" + id + ", \"timestamp\":" + timestamp + ", \"text\":\"" + text + "\" }";
    }
}
