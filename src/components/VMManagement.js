import React, { useState } from 'react';
import CreateVMForm from './CreateVMForm';

const VMManagement = () => {
  const [showCreateForm, setShowCreateForm] = useState(false);

  const handleVmCreated = () => {
    
    console.log('VM created, reloading VM list...');
  };

  return (
    <div>
      <button onClick={() => setShowCreateForm(true)}>Create VM</button>

      {showCreateForm && (
        <CreateVMForm
          onClose={() => setShowCreateForm(false)}
          onVmCreated={handleVmCreated}
        />
      )}
    </div>
  );
};

export default VMManagement;
