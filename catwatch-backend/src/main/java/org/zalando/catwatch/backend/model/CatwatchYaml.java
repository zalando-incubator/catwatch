package org.zalando.catwatch.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
