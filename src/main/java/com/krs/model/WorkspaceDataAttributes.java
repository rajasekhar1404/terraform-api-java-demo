package com.krs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class WorkspaceDataAttributes {
    private String name;
    @JsonProperty("resource-count")
    private int resourceCount;
    @JsonProperty("updated-at")
    private Date updatedAt = new Date();
}
