package com.krs.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWorkspaceRequest {
    private WorkspaceData data;
}
/*
{
        "data": {
        "attributes": {
        "name": "workspace-1",
        "resource-count": 0,
        "updated-at": "2017-11-29T19:18:09.976Z"
        },
        "type": "workspaces"
        }
        }
 */