
package com.timeline;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Asset_ {

    @Expose
    private String media;
    @Expose
    private String caption;

    /**
     * 
     * @return
     *     The media
     */
    public String getMedia() {
        return media;
    }

    /**
     * 
     * @param media
     *     The media
     */
    public void setMedia(String media) {
        this.media = media;
    }

    /**
     * 
     * @return
     *     The caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * 
     * @param caption
     *     The caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

}
