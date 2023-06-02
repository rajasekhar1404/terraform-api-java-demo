package com.krs.service;

import com.krs.entity.Workspace;
import com.krs.model.CreateWorkspaceRequest;
import com.krs.model.TerraformAPIs;
import com.krs.model.WorkspaceData;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
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

    private final String orgName = "mrrajasekhar09";
    String WORKSPACE_ID = "ws-4PWeN6F7XaeMahZk";
      String uploadUrl = "https://archivist.terraform.io/v1/object/";
    public Workspace createWorkspace(CreateWorkspaceRequest createWorkspaceRequest) {
        ResponseEntity<Workspace> response = restTemplate.postForEntity(String.format(TerraformAPIs.WORKSPACE_CREATE, orgName), getEntity(createWorkspaceRequest), Workspace.class);
        return response.getBody();
    }

    public Object createVariable() {

        return "";
    }

    public HttpEntity<CreateWorkspaceRequest> getEntity(CreateWorkspaceRequest createWorkspaceRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/vnd.api+json");
        headers.add("Authorization", "Bearer <Bearer token>");
        return new HttpEntity<>(createWorkspaceRequest, headers);
    }


    public Object createConfiguration() {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/octet-stream");
        headers.set("Content-Disposition", "attachment; filename=\"terraform.tar.gz\"");

        ResponseEntity<Object> response = restTemplate.exchange(uploadUrl, HttpMethod.PUT, new HttpEntity<>(generateTerraformZip().getBody(), headers), Object.class);

        return response.getBody();
    }

    public Object createConfigurationVersion() {
        CreateWorkspaceRequest request = new CreateWorkspaceRequest();
        WorkspaceData data = new WorkspaceData();
        data.setType("configuration-versions");
        request.setData(data);

        ResponseEntity<Object> response = restTemplate.postForEntity(String.format(TerraformAPIs.WORKSPACE_CREATE_CONFIGURATION_VERSION, WORKSPACE_ID), getEntity(request), Object.class);

        return response.getBody();
    }


    public ResponseEntity<byte[]> generateTerraformZip() {

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
            return ResponseEntity.status(500).body(null); // Error writing the file
        }

        // Create the zip file and add the config directory
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (TarArchiveOutputStream tarOutputStream = new TarArchiveOutputStream(new GzipCompressorOutputStream(byteArrayOutputStream))) {
            addDirectoryToZip(mainTfFile, tarOutputStream);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null); // Error creating the zip file
        }

        // Return the zip file
        byte[] zipBytes = byteArrayOutputStream.toByteArray();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"terraform.tar.gz\"")
                .body(zipBytes);
    }

    private void addDirectoryToZip(File directory, TarArchiveOutputStream tarOutputStream) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(directory);
//        ZipEntry zipEntry = new ZipEntry(directory.getName());
//        tarOutputStream.putNextEntry(zipEntry);
        tarOutputStream.putArchiveEntry(new TarArchiveEntry(directory));

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer)) > 0) {
            tarOutputStream.write(buffer, 0, length);
        }

        fileInputStream.close();
        tarOutputStream.closeArchiveEntry();
    }

}
