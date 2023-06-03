package com.krs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties
public class CreateConfigurationVersionAttributes {
    @JsonProperty("upload-url")
    private String uploadUrl;
}
