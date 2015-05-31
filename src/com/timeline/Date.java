
package com.timeline;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Date {

    @Expose
    private String startDate;
    @Expose
    private String headline;
    @Expose
    private String text;
    @Expose
    private Asset_ asset;

    /**
     * 
     * @return
     *     The startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * 
     * @param startDate
     *     The startDate
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * 
     * @return
     *     The headline
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * 
     * @param headline
     *     The headline
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     * 
     * @return
     *     The text
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * @param text
     *     The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 
     * @return
     *     The asset
     */
    public Asset_ getAsset() {
        return asset;
    }

    /**
     * 
     * @param asset
     *     The asset
     */
    public void setAsset(Asset_ asset) {
        this.asset = asset;
    }

}
