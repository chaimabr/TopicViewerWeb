
package com.timeline;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Timeline {

    @Expose
    private Timeline_ timeline;

    /**
     * 
     * @return
     *     The timeline
     */
    public Timeline_ getTimeline() {
        return timeline;
    }

    /**
     * 
     * @param timeline
     *     The timeline
     */
    public void setTimeline(Timeline_ timeline) {
        this.timeline = timeline;
    }

}
