import React, { useEffect, useState } from 'react';
import axios from 'axios';

const VMList = () => {
  const [vms, setVms] = useState([]);
  const [error, setError] = useState(''); // To hold error message

  useEffect(() => {
    // Initial VM fetch
    fetchVMs();
  }, []);

  const fetchVMs = () => {
    axios.get('http://localhost:8080/vms')
      .then(response => setVms(response.data))
      .catch(error => setError('Error fetching VMs: ' + error.message));
  };

  const startVM = (id) => {
    axios.post(`http://localhost:8080/vms/${id}/start`)
      .then(() => {
        // Update the local state without fetching all VMs again
        setVms(vms.map(vm => (vm.id === id ? { ...vm, status: 'Running' } : vm)));
      })
      .catch(error => setError('Error starting VM: ' + error.message));
  };

  const stopVM = (id) => {
    axios.post(`http://localhost:8080/vms/${id}/stop`)
      .then(() => {
        // Update the local state without fetching all VMs again
        setVms(vms.map(vm => (vm.id === id ? { ...vm, status: 'Stopped' } : vm)));
      })
      .catch(error => setError('Error stopping VM: ' + error.message));
  };

  const deleteVM = (id) => {
    axios.post(`http://localhost:8080/vms/${id}/delete`)
      .then(() => {
        // Remove the deleted VM from the list
        setVms(vms.filter(vm => vm.id !== id));
      })
      .catch(error => setError('Error deleting VM: ' + error.message));
  };

  return (
    <div>
      <h2>VM List</h2>

      {/* Display error message */}
      {error && <div className="error-message">{error}</div>}

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {vms.map((vm) => (
            <tr key={vm.id}>
              <td>{vm.id}</td>
              <td>{vm.name}</td>
              <td>{vm.status}</td>
              <td>
                <button onClick={() => startVM(vm.id)} disabled={vm.status === 'Running'}>
                  Start
                </button>
                <button onClick={() => stopVM(vm.id)} disabled={vm.status === 'Stopped'}>
                  Stop
                </button>
                <button onClick={() => deleteVM(vm.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default VMList;
