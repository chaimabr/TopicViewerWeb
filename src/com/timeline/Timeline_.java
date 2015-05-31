
package com.timeline;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Timeline_ {

    @Expose
    private String headline;
    @Expose
    private String type;
    @Expose
    private String startDate;
    @Expose
    private String text;
    @Expose
    private Asset asset;
    @Expose
    private List<Date> date = new ArrayList<Date>();

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
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

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
    public Asset getAsset() {
        return asset;
    }

    /**
     * 
     * @param asset
     *     The asset
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    /**
     * 
     * @return
     *     The date
     */
    public List<Date> getDate() {
        return date;
    }

    /**
     * 
     * @param date
     *     The date
     */
    public void setDate(List<Date> date) {
        this.date = date;
    }

}
