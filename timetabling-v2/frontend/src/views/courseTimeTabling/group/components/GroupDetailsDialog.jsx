import { Dialog, DialogTitle, DialogContent, Button } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { useGroupData } from "services/useGroupData";
import CreateNewGroupScreen from "../CreateNewGroupScreen";
import { useState, useMemo, useEffect } from "react";
import { useQueryClient } from "react-query";

export default function GroupDetailsDialog({ open, onClose, groupId, parentGroupName }) {
  const queryClient = useQueryClient();
  const { groups: detailData, isLoading, deleteGroup } = useGroupData(groupId);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [addNewDialogOpen, setAddNewDialogOpen] = useState(false);

  // Update useEffect to use queryClient instead of refetch
  useEffect(() => {
    if (groupId) {
      queryClient.refetchQueries(["groupByGroupId", groupId]);
    }
  }, [groupId, queryClient]);

  const handleEdit = (group) => {
    setSelectedGroup({
      ...group,
      id: groupId // Replace the index-based id with the actual groupId
    });
    setEditDialogOpen(true);
  };

  const handleEditClose = () => {
    setEditDialogOpen(false);
    setSelectedGroup(null);
  };

  const handleEditSuccess = async () => {
    // Use queryClient to refetch instead of refetch function
    await queryClient.refetchQueries(["groupByGroupId", groupId]);
    handleEditClose();
  };

  const handleDeleteClick = async (row) => {
    try {
      await deleteGroup({
        id: groupId,
        roomId: row.roomName,
      });
      await queryClient.refetchQueries(["groupByGroupId", groupId]);
    } catch (error) {
      console.error(error);
    }
  };

  const handleAddNewClose = () => {
    setAddNewDialogOpen(false);
  };

  const handleAddNewSuccess = async () => {
    await queryClient.refetchQueries(["groupByGroupId", groupId]);
    handleAddNewClose();
  };

  const columns = [
    {
      headerName: "STT",
      field: "index",
      width: 80,
      renderCell: (params) => params.row.index,
    },
    {
      headerName: "Tên nhóm",
      field: "groupName",
      width: 320,
    },
    {
      headerName: "Phòng học",
      field: "roomName",
      width: 120,
    },
    {
      headerName: "Độ ưu tiên",
      field: "priority",
      width: 120,
    },
    {
      headerName: "Hành động",
      field: "actions",
      width: 200,
      renderCell: (params) => (
        <div>
          <Button 
            variant="outlined"
            color="primary"
            onClick={() => handleEdit(params.row)}
            style={{ marginRight: "8px" }}
          >
            Sửa
          </Button>
          <Button
            variant="outlined"
            color="secondary"
            onClick={() => handleDeleteClick(params.row)}
          >
            Xóa
          </Button>
        </div>
      ),
    },
  ];

  // Preserve groupId when mapping rows
  const rows = useMemo(() => {
    return (detailData || []).map((item, index) => ({
      ...item,
      index: index + 1,
      id: index,
      groupId: groupId // Keep reference to original groupId
    }));
  }, [detailData, groupId]);

  return (
    <>
      <Dialog 
        open={open} 
        onClose={onClose} 
        maxWidth="md" 
        fullWidth
        disableEscapeKeyDown
      >
        <DialogTitle style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span>Chi tiết nhóm</span>
          <Button
            variant="contained"
            color="primary"
            onClick={() => setAddNewDialogOpen(true)}
          >
            Thêm mới
          </Button>
        </DialogTitle>
        <DialogContent>
          <div style={{ height: 400, width: "100%" }}>
            {isLoading ? (
              <div>Loading...</div>
            ) : (
              <DataGrid
                rows={rows}
                columns={columns}
                pageSize={5}
                rowsPerPageOptions={[5]}
                disableSelectionOnClick
              />
            )}
          </div>
        </DialogContent>
      </Dialog>

      <CreateNewGroupScreen
        open={editDialogOpen}
        handleClose={handleEditClose}
        onSuccess={handleEditSuccess}
        selectedGroup={{
          ...selectedGroup,
          groupId: groupId // Ensure groupId is passed correctly
        }}
      />

      <CreateNewGroupScreen
        open={addNewDialogOpen}
        handleClose={handleAddNewClose}
        onSuccess={handleAddNewSuccess}
        selectedGroup={{
          groupName: parentGroupName,
          id: groupId, // Pass the current groupId for new group creation
          isNewGroup: true
        }}
      />
    </>
  );
}
