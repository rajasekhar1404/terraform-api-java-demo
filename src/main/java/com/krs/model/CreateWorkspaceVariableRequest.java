package com.krs.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWorkspaceVariableRequest {

    private String key;
    private String value;
    private String description;
    private boolean sensitive;
    private String workspaceId;

}
