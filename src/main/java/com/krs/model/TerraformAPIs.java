package com.krs.model;

public class TerraformAPIs {
    public static final String BASE_URL = "https://app.terraform.io/api/v2/";

    public static final String WORKSPACE_CREATE = BASE_URL + "organizations/%s/workspaces";

    public static final String WORKSPACE_CREATE_CONFIGURATION_VERSION = BASE_URL + "workspaces/%s/configuration-versions";

    public static final String WORKSPACE_VARIABLE_CREATE = BASE_URL + "vars";
}
