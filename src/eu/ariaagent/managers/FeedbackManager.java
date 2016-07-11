package eu.ariaagent.managers;

import hmi.flipper.defaultInformationstate.DefaultRecord;

/**
 * The feedback manager is responsible for providing feedback to what the user is saying,
 * as well as provide feedback on its own words. Likewise, it tries to determine what
 * type of feedback the user is providing.
 * @author WaterschootJB
 */
public class FeedbackManager extends DefaultManager{
    
    public FeedbackManager(DefaultRecord is, long interval) {
        super(is, interval);
    }    
}
