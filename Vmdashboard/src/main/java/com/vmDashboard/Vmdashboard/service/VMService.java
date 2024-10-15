package com.vmDashboard.Vmdashboard.service;

import com.vmDashboard.Vmdashboard.model.VM;
import com.vmDashboard.Vmdashboard.repository.VMRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class VMService {

    private static final Logger logger = LoggerFactory.getLogger(VMService.class);

    // Ensure that VBoxManage path is correctly set with backslashes and spaces handled
    @Value("${vbox.manage.path:C:\\Program Files\\Oracle\\VirtualBox\\VBoxManage.exe}")
    private String vboxManagePath;

    private final VMRepository vmRepository;

    public VMService(VMRepository vmRepository) {
        this.vmRepository = vmRepository;
    }

    // Fetch all VMs
    public List<VM> getAllVMs() {
        logger.info("Fetching all VMs");
        return vmRepository.findAll();
    }

    // Create a new VM
    public VM createVM(VM vm) {
        logger.info("Creating VM: {}", vm);

        try {
            validateVMParams(vm);

            // VBoxManage createvm command with quoted VM name to handle spaces
            String createCommand = String.format(
                    "\"%s\" createvm --name \"%s\" --ostype %s --register",
                    vboxManagePath, vm.getName(), vm.getOsType());
            executeCommand(createCommand);

            // VBoxManage modifyvm command
            String modifyCommand = String.format(
                    "\"%s\" modifyvm \"%s\" --cpus %d --memory %d",
                    vboxManagePath, vm.getName(), vm.getCpu(), vm.getRam());
            executeCommand(modifyCommand);

            // VBoxManage createhd command
            String storageCommand = String.format(
                    "\"%s\" createhd --filename \"%s.vdi\" --size %d",
                    vboxManagePath, vm.getName(), vm.getStorage());
            executeCommand(storageCommand);

            vm.setStatus("Stopped");
            VM savedVM = vmRepository.save(vm);

            logger.info("VM created and saved: {}", savedVM);
            return savedVM;

        } catch (Exception e) {
            logger.error("Error creating VM", e);
            throw new RuntimeException("Error creating VM: " + e.getMessage(), e);
        }
    }

    // Start VM
    public void startVM(Long id) {
        VM vm = vmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VM not found"));
        try {
            logger.info("Starting VM with ID: {}", id);
            executeCommand(String.format("\"%s\" startvm \"%s\" --type headless", vboxManagePath, vm.getName()));
            vm.setStatus("Running");
            vmRepository.save(vm);
            logger.info("VM started successfully: {}", vm);
        } catch (Exception e) {
            logger.error("Error starting VM", e);
            throw new RuntimeException("Error starting VM: " + e.getMessage(), e);
        }
    }

    // Stop VM
    public void stopVM(Long id) {
        VM vm = vmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VM not found"));
        try {
            logger.info("Stopping VM with ID: {}", id);
            executeCommand(String.format("\"%s\" controlvm \"%s\" poweroff", vboxManagePath, vm.getName()));
            vm.setStatus("Stopped");
            vmRepository.save(vm);
            logger.info("VM stopped successfully: {}", vm);
        } catch (Exception e) {
            logger.error("Error stopping VM", e);
            throw new RuntimeException("Error stopping VM: " + e.getMessage(), e);
        }
    }

    // Restart VM
    public void restartVM(Long id) {
        try {
            stopVM(id);
            startVM(id);
        } catch (Exception e) {
            logger.error("Error restarting VM", e);
            throw new RuntimeException("Error restarting VM: " + e.getMessage(), e);
        }
    }

    // Delete VM
    public void deleteVM(Long id) {
        VM vm = vmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VM not found"));
        try {
            logger.info("Deleting VM with ID: {}", id);
            executeCommand(String.format("\"%s\" unregistervm \"%s\" --delete", vboxManagePath, vm.getName()));
            vmRepository.delete(vm);
            logger.info("VM deleted successfully: {}", vm);
        } catch (Exception e) {
            logger.error("Error deleting VM", e);
            throw new RuntimeException("Error deleting VM: " + e.getMessage(), e);
        }
    }

    // Helper method to execute commands
    private void executeCommand(String command) {
        logger.info("Executing command: {}", command);
        try {
            // Check if VBoxManage exists at the given path
            if (!isVBoxManageValid()) {
                logger.error("Invalid VBoxManage path: {}", vboxManagePath);
                throw new RuntimeException("VBoxManage executable not found at path: " + vboxManagePath);
            }

            Process process = Runtime.getRuntime().exec(command);
            captureOutput(process);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.error("Command failed with exit code: {}", exitCode);
                throw new RuntimeException("VBoxManage command failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error executing command", e);
            throw new RuntimeException("Error executing command: " + command, e);
        }
    }

    // Helper method to capture standard output and error streams
    private void captureOutput(Process process) throws IOException {
        try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            String line;
            while ((line = stdInput.readLine()) != null) {
                logger.info("OUTPUT: {}", line);
            }
            while ((line = stdError.readLine()) != null) {
                logger.error("ERROR: {}", line);
            }
        }
    }

    // Validate VM parameters
    private void validateVMParams(VM vm) {
        if (vm.getName() == null || vm.getName().isEmpty()) {
            throw new IllegalArgumentException("VM name cannot be empty");
        }
        if (vm.getOsType() == null || vm.getOsType().isEmpty()) {
            throw new IllegalArgumentException("VM OS Type cannot be empty");
        }
        if (vm.getCpu() <= 0) {
            throw new IllegalArgumentException("VM must have at least one CPU");
        }
        if (vm.getRam() <= 0) {
            throw new IllegalArgumentException("VM must have some RAM");
        }
        if (vm.getStorage() <= 0) {
            throw new IllegalArgumentException("VM must have storage allocated");
        }
    }

    // Check if VBoxManage exists at the given path
    private boolean isVBoxManageValid() {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "where", "VBoxManage");
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            logger.error("Error checking VBoxManage path", e);
            return false;
        }
    }
}
