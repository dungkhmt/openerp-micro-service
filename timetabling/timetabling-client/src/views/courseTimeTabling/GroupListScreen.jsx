// AddToExistingGroupDialog.js
import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, Button } from '@mui/material';

const AddToExistingGroupDialog = ({ open, handleClose, existingData, handleSelect }) => {
  const [selectedItem, setSelectedItem] = useState(null);

  const handleSelectItem = (item) => {
    setSelectedItem(item);
  };

  const handleConfirm = () => {
    handleSelect(selectedItem);
    handleClose();
  };

  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>Add to Existing Group</DialogTitle>
      <DialogContent>
        <ul>
          {existingData.map((item) => (
            <li key={item.id}>
              {item.name}
              <Button onClick={() => handleSelectItem(item)}>Select</Button>
            </li>
          ))}
        </ul>
        <Button onClick={handleConfirm}>Confirm</Button>
      </DialogContent>
    </Dialog>
  );
};

export default AddToExistingGroupDialog;
