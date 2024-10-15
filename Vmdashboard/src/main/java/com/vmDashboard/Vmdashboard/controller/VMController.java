package com.vmDashboard.Vmdashboard.controller;

import com.vmDashboard.Vmdashboard.model.VM;
import com.vmDashboard.Vmdashboard.service.VMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/vms")
@CrossOrigin(origins = "*") // Adjust CORS in production for security
public class VMController {

    private static final Logger logger = LoggerFactory.getLogger(VMController.class);

    @Autowired
    private VMService vmService;

    // GET /vms: Retrieve all VMs
    @GetMapping
    public List<VM> getAllVMs() {
        logger.info("Fetching all VMs");
        return vmService.getAllVMs();
    }

    // POST /vms: Create a new VM
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VM createVM(@RequestBody VM vm) {
        logger.info("Creating new VM: {}", vm);
        return vmService.createVM(vm);
    }

    // PUT /vms/{id}/start: Start a VM
    @PutMapping("/{id}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void startVM(@PathVariable Long id) {
        logger.info("Starting VM with ID: {}", id);
        vmService.startVM(id);
    }

    // PUT /vms/{id}/stop: Stop a VM
    @PutMapping("/{id}/stop")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void stopVM(@PathVariable Long id) {
        logger.info("Stopping VM with ID: {}", id);
        vmService.stopVM(id);
    }

    // PUT /vms/{id}/restart: Restart a VM
    @PutMapping("/{id}/restart")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restartVM(@PathVariable Long id) {
        logger.info("Restarting VM with ID: {}", id);
        vmService.restartVM(id);
    }

    // DELETE /vms/{id}: Delete a VM
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVM(@PathVariable Long id) {
        logger.info("Deleting VM with ID: {}", id);
        vmService.deleteVM(id);
    }

    // You may also consider adding global error handling (Optional):
    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<String> handleException(Exception e) {
    //    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    // }
}
