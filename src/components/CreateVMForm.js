import React, { useState } from 'react';
import axios from 'axios';

const CreateVMForm = ({ onClose, onVmCreated }) => {
  const [vmData, setVmData] = useState({
    name: '',
    osType: '',
    cpu: 1,
    ram: 1,
    storage: 1,
    status: 'Stopped',
  });

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Handle form field changes
  const handleChange = (e) => {
    const { name, value } = e.target;
    setVmData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  // Handle form validation
  const validateForm = () => {
    if (!vmData.name.trim()) return 'Name is required';
    if (!vmData.osType.trim()) return 'OS Type is required';
    if (vmData.cpu <= 0) return 'CPU must be greater than 0';
    if (vmData.ram <= 0) return 'RAM must be greater than 0';
    if (vmData.storage <= 0) return 'Storage must be greater than 0';
    return null;
  };

  // Handle form submit
  const handleSubmit = (e) => {
    e.preventDefault();

    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      return;
    }

    setError('');
    setLoading(true); // Start loading state

    // Make POST request to create VM
    axios
      .post('http://localhost:8080/vms', vmData)
      .then((response) => {
        alert('VM Created Successfully');
        setVmData({
          name: '',
          osType: '',
          cpu: 1,
          ram: 1,
          storage: 1,
          status: 'Stopped',
        }); // Reset form data
        onVmCreated(); // Notify parent to reload the VM list
        onClose(); // Close the form after successful creation
      })
      .catch((error) => {
        // Check for server-specific errors if any
        setError(error.response?.data?.message || 'Error creating VM. Please check the server or logs.');
      })
      .finally(() => {
        setLoading(false); // Stop loading state
      });
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <span className="close" onClick={onClose}>&times;</span>
        <h2>Create Virtual Machine</h2>

        {/* Display error message */}
        {error && <div className="error-message">{error}</div>}

        {/* Form to create a VM */}
        <form onSubmit={handleSubmit}>
          <label htmlFor="name">Name:</label>
          <input
            type="text"
            name="name"
            id="name"
            value={vmData.name}
            onChange={handleChange}
            aria-label="Virtual Machine Name"
          />

          <label htmlFor="osType">OS Type:</label>
          <input
            type="text"
            name="osType"
            id="osType"
            value={vmData.osType}
            onChange={handleChange}
            aria-label="Operating System Type"
          />

          <label htmlFor="cpu">CPU:</label>
          <input
            type="number"
            name="cpu"
            id="cpu"
            value={vmData.cpu}
            onChange={handleChange}
            min="1"
            aria-label="Number of CPUs"
          />

          <label htmlFor="ram">RAM (GB):</label>
          <input
            type="number"
            name="ram"
            id="ram"
            value={vmData.ram}
            onChange={handleChange}
            min="1"
            aria-label="RAM in GB"
          />

          <label htmlFor="storage">Storage (GB):</label>
          <input
            type="number"
            name="storage"
            id="storage"
            value={vmData.storage}
            onChange={handleChange}
            min="1"
            aria-label="Storage in GB"
          />

          {/* Loading Spinner or Button Disabled */}
          <button type="submit" disabled={loading}>
            {loading ? 'Creating VM...' : 'Create VM'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default CreateVMForm;
