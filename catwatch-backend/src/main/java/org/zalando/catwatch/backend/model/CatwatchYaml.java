package org.zalando.catwatch.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hjacobs on 09.02.16.
 */
public class CatwatchYaml {
    @JsonProperty
    private String title;

    @JsonProperty
    private String image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
