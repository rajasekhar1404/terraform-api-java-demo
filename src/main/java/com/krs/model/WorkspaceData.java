package com.krs.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceData {
    private WorkspaceDataAttributes attributes;
    private String type;
    private WorkspaceDataRelationships relationships;
}