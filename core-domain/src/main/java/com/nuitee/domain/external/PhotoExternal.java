package com.nuitee.domain.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PhotoExternal(
    String url,

    @JsonProperty("hd_url")
    String hdUrl,

    @JsonProperty("image_description")
    String description,

    @JsonProperty("image_class1")
    String class1,

    @JsonProperty("image_class2")
    String class2,

    @JsonProperty("main_photo")
    boolean mainPhoto,

    double score,

    @JsonProperty("class_id")
    int classId,

    @JsonProperty("class_order")
    int classOrder
) {}
