package com.krs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestroyWorkspaceAttributes {
    @JsonProperty("is-destroy")
    private boolean isDestroy;
    private String message;
}
