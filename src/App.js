import React, { useState } from 'react';
import CreateVMForm from './components/CreateVMForm';
import VMList from './components/VMList';
import './App.css';

const App = () => {
  const [showCreateForm, setShowCreateForm] = useState(false);

  const openCreateForm = () => {
    setShowCreateForm(true);
  };

  const closeCreateForm = () => {
    setShowCreateForm(false);
  };

  return (
    <div className="container">
      <h1>Virtual Machine Dashboard</h1>
      <button className="btn" onClick={openCreateForm}>Create New VM</button>
      {showCreateForm && <CreateVMForm onClose={closeCreateForm} />}
      <VMList />
    </div>
  );
};

export default App;
