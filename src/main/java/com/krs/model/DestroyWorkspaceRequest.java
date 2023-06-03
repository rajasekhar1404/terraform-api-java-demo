package com.krs.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestroyWorkspaceRequest {
    private String workspaceId;
    private String message;
}
