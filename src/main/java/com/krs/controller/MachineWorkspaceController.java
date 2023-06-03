package com.krs.controller;

import com.krs.entity.TOrganization;
import com.krs.entity.Workspace;
import com.krs.model.*;
import com.krs.service.MachineWorkspaceService;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
public class MachineWorkspaceController {

    private final MachineWorkspaceService machineWorkspaceService;

    public MachineWorkspaceController(final  MachineWorkspaceService machineWorkspaceService) {
        this.machineWorkspaceService = machineWorkspaceService;
    }

    // Register Terraform organization
    @PostMapping("/org-reg")
    public ResponseEntity<TOrganization> registerOrganization(@RequestBody TOrganization tOrganization) {
        return new ResponseEntity<>(machineWorkspaceService.registerOrganization(tOrganization), HttpStatus.CREATED);
    }

    // Get Terraform organization
    @GetMapping("/org")
    public ResponseEntity<TOrganization> geOrganization() {
        return new ResponseEntity<>(machineWorkspaceService.getTerraformOrganization(), HttpStatus.OK);
    }

    // Update Terraform organization
    @PutMapping("/org-update")
    public ResponseEntity<TOrganization> updateOrganization(@RequestBody TOrganization tOrganization) {
        return new ResponseEntity<>(machineWorkspaceService.updateTerraformOrganization(tOrganization), HttpStatus.CREATED);
    }

    // Create Terraform workspace
    @PostMapping("/create")
    public Workspace createWorkspace(@RequestBody CreateWorkspaceRequest createWorkspaceRequest) {
        return machineWorkspaceService.createWorkspace(createWorkspaceRequest);
    }

    // Create Terraform workspace variables
    @PostMapping("/var-create")
    public Object createWorkspaceVariable(@RequestBody CreateWorkspaceVariableRequest createWorkspaceVariableRequest) {
        return machineWorkspaceService.createVariable(createWorkspaceVariableRequest);
    }

    // Create Terraform configuration version
    @PostMapping("/configurationVersion")
    public Object createConfigurationVersion(@RequestBody CreateConfigurationVersionRequest createConfigurationVersionRequest) {
        return machineWorkspaceService.createConfigurationVersion(createConfigurationVersionRequest);
    }

    // Create terraform configuration
    @PostMapping("/configure")
    public Object createConfiguration() {
        return machineWorkspaceService.createConfiguration();
    }

    // Destroy terraform workspace

    @PostMapping("/destroy")
    public Object destroyWorkspace(@RequestBody DestroyWorkspaceRequest destroyWorkspaceRequest) {
        return machineWorkspaceService.destroyWorkspace(destroyWorkspaceRequest);
    }

}