package com.krs.service;

import com.krs.entity.TOrganization;
import com.krs.entity.Workspace;
import com.krs.model.*;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.springframework.web.client.RestTemplate;

import java.io.*;

@Service
public class MachineWorkspaceService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private MongoTemplate mongoTemplate;
    String uploadUrl = "https://archivist.terraform.io/v1/"; // this needs to be coming from db

    // Register Terraform organization
    public TOrganization registerOrganization(TOrganization tOrganization) {
        mongoTemplate.save(tOrganization);
        return tOrganization;
    }

    // Get Terraform organization
    public TOrganization getTerraformOrganization() {
        return getOrganization();
    }

    // Update Terraform organization
    public TOrganization updateTerraformOrganization(TOrganization tOrganization) {
        Query query = new Query();
        query.addCriteria(Criteria.where("organizationName").is(tOrganization.getOrganizationName()));
        TOrganization oldOrg = mongoTemplate.findOne(query, TOrganization.class);
        if (oldOrg != null) {
            Update update = new Update();
            update.set("organizationName", tOrganization.getOrganizationName());
            update.set("accessToken", tOrganization.getAccessToken());
            UpdateResult updateResult = mongoTemplate.updateFirst(query, update, TOrganization.class);
            return updateResult.getModifiedCount() > 0 ? tOrganization : null;
        }
        return null;
    }

    private TOrganization getOrganization() {
        return mongoTemplate.findAll(TOrganization.class).get(0);
    }

/*    ==============================================================================================    */

    // Creating a Terraform workspace
    public Workspace createWorkspace(CreateWorkspaceRequest createWorkspaceRequest) {
        TOrganization tOrganization = getOrganization();
        ResponseEntity<Workspace> response = restTemplate.postForEntity(String.format(TerraformAPIs.WORKSPACE_CREATE, tOrganization.getOrganizationName()), getEntity(buildWorkspaceRequest(createWorkspaceRequest), tOrganization.getAccessToken()), Workspace.class);
        return response.getBody() != null ? mongoTemplate.save(response.getBody()) : null;
    }

    private CreateWorkspaceRequestBuilder buildWorkspaceRequest(CreateWorkspaceRequest createWorkspaceRequest) {
        CreateWorkspaceRequestBuilder request = new CreateWorkspaceRequestBuilder();
        WorkspaceData data = new WorkspaceData();
        data.setType("workspaces");
        WorkspaceDataAttributes attributes = new WorkspaceDataAttributes();
        attributes.setName(createWorkspaceRequest.getWorkspaceName());
        attributes.setResourceCount(0);
        data.setAttributes(attributes);
        request.setData(data);
        return request;
    }

    /*    ==============================================================================================    */

    public Object createVariable(CreateWorkspaceVariableRequest createWorkspaceVariableRequest) {
        ResponseEntity<Object> response = restTemplate.postForEntity(TerraformAPIs.WORKSPACE_VARIABLE_CREATE, getEntity(variableRequestBuilder(createWorkspaceVariableRequest), getOrganization().getAccessToken()), Object.class);
        return response.getBody() != null ? mongoTemplate.save(response.getBody(), "variables") : null;
    }

    private CreateWorkspaceRequestBuilder variableRequestBuilder(CreateWorkspaceVariableRequest createWorkspaceVariableRequest) {
        CreateWorkspaceRequestBuilder createWorkspaceRequestBuilder = new CreateWorkspaceRequestBuilder();
        WorkspaceData data = new WorkspaceData();
        CreateWorkspaceVariableAttributes attributes = new CreateWorkspaceVariableAttributes();
        WorkspaceDataRelationships relationships = new WorkspaceDataRelationships();
        WorkspaceDataRelationshipsWorkspace workspace = new WorkspaceDataRelationshipsWorkspace();
        WorkspaceDataRelationshipsWorkspaceData workspaceData = new WorkspaceDataRelationshipsWorkspaceData();

        workspaceData.setType("workspaces");
        workspaceData.setId(createWorkspaceVariableRequest.getWorkspaceId());
        workspace.setData(workspaceData);

        relationships.setWorkspace(workspace);

        attributes.setKey(createWorkspaceVariableRequest.getKey());
        attributes.setValue(createWorkspaceVariableRequest.getValue());
        attributes.setHcl(false);
        attributes.setSensitive(createWorkspaceVariableRequest.isSensitive());
        attributes.setDescription(createWorkspaceVariableRequest.getDescription());
        attributes.setCategory("terraform");

        data.setType("vars");
        data.setAttributes(attributes);
        data.setRelationships(relationships);

        createWorkspaceRequestBuilder.setData(data);

        return createWorkspaceRequestBuilder;
    }

    /*    ==============================================================================================    */


    public Object createConfigurationVersion(CreateConfigurationVersionRequest createConfigurationVersionRequest) {
        CreateWorkspaceRequestBuilder request = new CreateWorkspaceRequestBuilder();
        WorkspaceData data = new WorkspaceData();
        data.setType("configuration-versions");
        request.setData(data);
        ResponseEntity<Workspace> response = restTemplate.postForEntity(String.format(TerraformAPIs.WORKSPACE_CREATE_CONFIGURATION_VERSION, createConfigurationVersionRequest.getWorkspaceId()), getEntity(request, getOrganization().getAccessToken()), Workspace.class);
        return response.getBody() != null ? mongoTemplate.save(configurationVersionResponseBuilder(response.getBody()), "configurationVersions") : null;
    }

    private Workspace configurationVersionResponseBuilder(Workspace workspace) {
        CreateConfigurationVersionAttributes attributes = (CreateConfigurationVersionAttributes) workspace.getData().getAttributes();
        workspace.getData().setAttributes(attributes);
        return workspace;
    }

    /*    ==============================================================================================    */

    public Object createConfiguration() {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/octet-stream");
        headers.set("Content-Disposition", "attachment; filename=\"terraform.tar.gz\"");

        ResponseEntity<Object> response = restTemplate.exchange(uploadUrl, HttpMethod.PUT, new HttpEntity<>(generateTerraformZip(), headers), Object.class);

        return response.getBody();
    }

    public byte[] generateTerraformZip() {

        // Create the main.tf file and write the content
        File mainTfFile = new File("main.tf");
        String terraformContent = "terraform {\n" +
                "  required_providers {\n" +
                "    aws = {\n" +
                "      source  = \"hashicorp/aws\"\n" +
                "      version = \"3.58.0\"\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "variable \"aws_access_key\" {\n" +
                "    description = \"AWS access key ID\"\n" +
                "}\n" +
                "variable \"aws_secret_access_key\" {\n" +
                "    description = \"AWS secret access key\"\n" +
                "}\n" +
                "provider \"aws\" {\n" +
                "    region          = \"us-east-1\"\n" +
                "    access_key      = var.aws_access_key\n" +
                "    secret_key      = var.aws_secret_access_key\n" +
                "}\n" +
                "resource \"aws_instance\" \"example\" {\n" +
                "    ami           = \"ami-0261755bbcb8c4a84\" \n" +
                "    instance_type = \"t2.micro\"\n" +
                "}\n" +
                "output \"instance_id\" {\n" +
                "    value = aws_instance.example.id\n" +
                "}";

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(mainTfFile);
            fileOutputStream.write(terraformContent.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            return null; // Error writing the file
        }

        // Create the zip file and add the config directory
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (TarArchiveOutputStream tarOutputStream = new TarArchiveOutputStream(new GzipCompressorOutputStream(byteArrayOutputStream))) {
            addDirectoryToZip(mainTfFile, tarOutputStream);
        } catch (IOException e) {
            return null; // Error creating the zip file
        }

        // Return the zip file
        return byteArrayOutputStream.toByteArray();
    }

    private void addDirectoryToZip(File directory, TarArchiveOutputStream tarOutputStream) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(directory);
        tarOutputStream.putArchiveEntry(new TarArchiveEntry(directory));

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer)) > 0) {
            tarOutputStream.write(buffer, 0, length);
        }

        fileInputStream.close();
        tarOutputStream.closeArchiveEntry();
    }

    /*    ==============================================================================================    */

    public Object destroyWorkspace(DestroyWorkspaceRequest destroyWorkspaceRequest) {
        ResponseEntity<Object> response = restTemplate.postForEntity(TerraformAPIs.WORKSPACE_DESTROY, getEntity(buildDestroyRequest(destroyWorkspaceRequest), getOrganization().getAccessToken()), Object.class);
        return response.getBody();
    }

    public CreateWorkspaceRequestBuilder buildDestroyRequest(DestroyWorkspaceRequest destroyWorkspaceRequest) {
        CreateWorkspaceRequestBuilder request = new CreateWorkspaceRequestBuilder();
        WorkspaceData data = new WorkspaceData();
        DestroyWorkspaceAttributes attributes = new DestroyWorkspaceAttributes();
        WorkspaceDataRelationships relationships = new WorkspaceDataRelationships();
        WorkspaceDataRelationshipsWorkspace workspace = new WorkspaceDataRelationshipsWorkspace();
        WorkspaceDataRelationshipsWorkspaceData workspaceData = new WorkspaceDataRelationshipsWorkspaceData();
        workspaceData.setId(destroyWorkspaceRequest.getWorkspaceId());
        workspaceData.setType("workspaces");
        workspace.setData(workspaceData);
        relationships.setWorkspace(workspace);

        attributes.setDestroy(true);
        attributes.setMessage(destroyWorkspaceRequest.getMessage());
        data.setType("runs");
        data.setRelationships(relationships);
        data.setAttributes(attributes);

        request.setData(data);

        return request;

    }

    /*    ==============================================================================================    */

    public HttpEntity<CreateWorkspaceRequestBuilder> getEntity(CreateWorkspaceRequestBuilder createWorkspaceRequest, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/vnd.api+json");
        headers.add("Authorization", "Bearer " + token);
        return new HttpEntity<>(createWorkspaceRequest, headers);
    }

}
