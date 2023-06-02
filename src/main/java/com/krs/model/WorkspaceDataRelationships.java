package com.krs.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceDataRelationships {
    private WorkspaceDataRelationshipsWorkspace workspace;
}

/*
{
  "data": {
    "attributes": {
      "is-destroy":true,
      "message": "workspace destoryed"
    },
    "type":"runs",
    "relationships": {
      "workspace": {
        "data": {
          "type": "workspaces",
          "id": "ws-8uEYdoVEP5xvh2kJ"
        }
      }
    }
  }
}

 */