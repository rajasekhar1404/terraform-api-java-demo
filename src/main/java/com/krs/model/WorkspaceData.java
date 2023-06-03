package com.krs.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceData {
    private String id;
    private String type;
    private Object attributes;
    private WorkspaceDataRelationships relationships;
}