package com.krs.controller;

import com.krs.entity.Workspace;
import com.krs.model.CreateWorkspaceRequest;
import com.krs.model.WorkspaceData;
import com.krs.model.WorkspaceDataAttributes;
import com.krs.service.MachineWorkspaceService;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class MachineWorkspaceController {

    private final MachineWorkspaceService machineWorkspaceService;

    public MachineWorkspaceController(final  MachineWorkspaceService machineWorkspaceService) {
        this.machineWorkspaceService = machineWorkspaceService;
    }

    @PostMapping("/create")
    public Workspace createWorkspace() {
        return machineWorkspaceService.createWorkspace(testing());
    }

    @PostMapping("/configure")
    public Object createConfiguration() {
        return machineWorkspaceService.createConfiguration();
    }

    @PostMapping("/configurationVersion")
    public Object createConfigurationVersion() {
        return machineWorkspaceService.createConfigurationVersion();
    }


    public Object createVariable() {
        return machineWorkspaceService.createVariable();
    }








    private CreateWorkspaceRequest testing() {
        CreateWorkspaceRequest request = new CreateWorkspaceRequest();

        WorkspaceData data = new WorkspaceData();
        data.setType("workspaces");


        WorkspaceDataAttributes attributes = new WorkspaceDataAttributes();
        attributes.setName("java-terraform");
        attributes.setResourceCount(0);

        data.setAttributes(attributes);

        request.setData(data);

        return request;
    }












    @GetMapping(value = "/generate-terraform-zip", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
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
