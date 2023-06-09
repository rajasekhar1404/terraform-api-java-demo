package com.krs.entity;

import com.krs.model.WorkspaceData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "tWorkspaces")
public class Workspace {
    private WorkspaceData data;
}
