import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from '@mui/material';
import { request } from "../../../api";
import { DataGrid } from '@mui/x-data-grid';

const GroupListScreen = ({ open, handleClose, existingData, handleRefreshData }) => {
  const [selectedItem, setSelectedItem] = useState(null);

  const handleSelectItem = (item) => {
    setSelectedItem(item);

    const groupName = item.groupName

    const requestUpdateClassOpened = {
      ids: existingData[1],
      groupName: groupName
    };
    request("post", "/class-opened/update", (res) => {
      // Call handleRefreshData to refresh the data 
      handleRefreshData();
      //close dialog
      handleClose();
    },
      {},
      requestUpdateClassOpened
    ).then();
  };

  const columns = [
    {
      field: 'select',
      headerName: 'Action',
      width: 100,
      renderCell: (params) => (
        <Button
          variant="contained"
          color="primary"
          onClick={() => handleSelectItem(existingData[0].find((item) => item.id === params.row.id))}
        >
          Select
        </Button>
      ),
    },
    { field: 'id', headerName: 'ID', width: 70 },
    { field: 'groupName', headerName: 'Nhóm', width: 120 },
    { field: 'studyClass', headerName: 'Lớp học', width: 150 },
    { field: 'moduleName', headerName: 'Tên học phần', width: 150 },
    { field: 'mass', headerName: 'Khối lượng', width: 150 },
    { field: 'crew', headerName: 'Kíp', width: 100 },
  ];

  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>Thêm vào nhóm đã có</DialogTitle>
      <DialogContent>
        <div style={{ height: 400, width: '100%' }}>
          <DataGrid
            rows={existingData[0]}
            columns={columns}
            pageSize={5}
          />
        </div>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="secondary">
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default GroupListScreen;
