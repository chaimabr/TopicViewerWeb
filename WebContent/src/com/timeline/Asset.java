
package com.timeline;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Asset {

    @Expose
    private String media;

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

}
