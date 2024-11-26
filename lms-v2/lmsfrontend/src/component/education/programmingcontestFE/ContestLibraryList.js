import AddIcon from "@material-ui/icons/Add";
import {
  Box,
  LinearProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
} from "@mui/material";
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { toFormattedDateTime } from "../../../utils/dateutils";
import StandardTable from "../../table/StandardTable";
import { successNoti } from "utils/notification";

export default function ContestLibraryList() {
  const [libraries, setLibraries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [userLoginId, setUserLoginId] = useState("");
  const [openDialog, setOpenDialog] = useState(false);
  const [newLibrary, setNewLibrary] = useState({
    name: "",
    language: "",
    content: "",
    status: "",
    createdStamp: new Date().toISOString(),
    lastUpdatedStamp: new Date().toISOString(),
  });

  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [editLibraryContent, setEditLibraryContent] = useState("");
  const [currentLibrary, setCurrentLibrary] = useState(null);

  const columns = [
    {
      title: "Name",
      field: "name",
      render: (rowData) => (
        <Button onClick={() => handleOpenEditDialog(rowData)}>
          {rowData.name}
        </Button>
      ),
    },
    { title: "Language", field: "language" },
    {
      title: "Last Update Date",
      field: "lastUpdatedStamp",
      render: (rowData) => toFormattedDateTime(rowData["lastUpdatedStamp"]),
    },
    {
      title: "Actions",
      field: "actions",
      render: (rowData) => (
        <Button
          color="secondary"
          onClick={() => handleDeleteLibrary(rowData.id)}
        >
          Delete
        </Button>
      ),
    },
  ];
  
  const handleDeleteLibrary = (id) => {
    request("delete", `/code-library/delete-library/${id}`)
      .then(() => {
        successNoti("Library deleted successfully");
        setLibraries((prevLibraries) =>
          prevLibraries.filter((library) => library.id !== id)
        );
      })
      .catch((error) => {
        console.error("Error deleting library:", error);
        errorNoti("Failed to delete library");
      });
  };
  

  useEffect(() => {
    request("get", "/code-library/get-user-info")
      .then((res) => {
        setUserLoginId(res.data);
      })
      .catch((error) => {
        console.error("Error fetching user info:", error);
        setLoading(false);
      });
  }, []);

  useEffect(() => {
    if (userLoginId) {
      getLibraryList();
    }
  }, [userLoginId]);

  const getLibraryList = () => {
    request("get", `/code-library/student-list-library/${userLoginId}`)
      .then((res) => {
        const librariesWithFormattedDates = res.data.map((library) => ({
          ...library,
          createdStamp: toFormattedDateTime(library.createdStamp),
          lastUpdatedStamp: toFormattedDateTime(library.lastUpdatedStamp),
        }));
        setLibraries(librariesWithFormattedDates);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching library list:", error);
        setLoading(false);
      });
  };

  const handleOpenDialog = () => setOpenDialog(true);
  const handleCloseDialog = () => {
    setOpenDialog(false);
    setNewLibrary({
      name: "",
      language: "",
      content: "",
      status: "",
      createdStamp: new Date().toISOString(),
      lastUpdatedStamp: new Date().toISOString(),
    });
  };

  const handleChange = (e) => {
    setNewLibrary({ ...newLibrary, [e.target.name]: e.target.value });
  };

  const handleSaveLibrary = () => {
    const currentDateTime = new Date().toISOString();
    const libraryData = {
      ...newLibrary,
      createdStamp: currentDateTime,
      lastUpdatedStamp: currentDateTime,
    };

    request("post", "/code-library/create-library", (res) => {
      successNoti("Library created successfully");
      setLibraries([...libraries, res.data]);
      handleCloseDialog();
    }, {}, libraryData).catch((error) => {
      console.error("Error creating library:", error);
      errorNoti("Failed to create library");
    });
  };

  const handleOpenEditDialog = (library) => {
    setCurrentLibrary(library);
    setEditLibraryContent(library.content);
    setOpenEditDialog(true);
  };

  const handleCloseEditDialog = () => {
    setOpenEditDialog(false);
    setEditLibraryContent("");
  };

  const handleSaveEdit = () => {
    if (!currentLibrary) {
      console.error("No library selected for editing");
      return;
    }
  
    const { createdStamp, ...restOfLibrary } = currentLibrary;
  
    const updatedLibrary = {
      ...restOfLibrary,
      content: editLibraryContent,
      lastUpdatedStamp: new Date().toISOString(),
    };
  
    console.log("Updating library with data:", updatedLibrary);
  
    request("put", `/code-library/edit-library/${currentLibrary.id}`, (res) => {
      successNoti("Library updated successfully");
      
      setLibraries((prevLibraries) =>
        prevLibraries.map((lib) =>
          lib.id === currentLibrary.id ? { ...lib, ...updatedLibrary } : lib
        )
      );
  
      handleCloseEditDialog();
    }, {}, updatedLibrary).catch((error) => {
      console.error("Error updating library:", error.response ? error.response.data : error);
      errorNoti("Failed to update library");
    });
  };

  return (
    <Box mb={2}>
      {loading && <LinearProgress />}
      <StandardTable
        title="Library List"
        columns={columns}
        data={libraries}
        hideCommandBar
        options={{
          pageSize: 5,
          selection: false,
          search: true,
          sorting: true,
        }}
        actions={[
          {
            icon: () => <AddIcon fontSize="large" />,
            tooltip: "Add new Library",
            isFreeAction: true,
            onClick: handleOpenDialog,
          },
        ]}
      />

      <Dialog open={openDialog} onClose={handleCloseDialog} fullWidth>
        <DialogTitle>Create New Library</DialogTitle>
        <DialogContent>
          <TextField
            label="Name"
            name="name"
            value={newLibrary.name}
            onChange={handleChange}
            fullWidth
            sx={{ mb: 2 }}
          />
          <TextField
            label="Language"
            name="language"
            value={newLibrary.language}
            onChange={handleChange}
            fullWidth
            sx={{ mb: 2 }}
          />
          <TextField
            label="Content"
            name="content"
            value={newLibrary.content}
            onChange={handleChange}
            multiline
            rows={4}
            fullWidth
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button onClick={handleSaveLibrary}>Save</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openEditDialog} onClose={handleCloseEditDialog} fullWidth>
        <DialogTitle>Edit Library Content</DialogTitle>
        <DialogContent sx={{ width: "100%", minHeight: "300px", display: "flex", flexDirection: "column" }}>
          <TextField
            label="Content"
            multiline
            rows={30}
            value={editLibraryContent}
            onChange={(event) => setEditLibraryContent(event.target.value)}
            sx={{ width: "100%", flexGrow: 1, minHeight: "200px" }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseEditDialog}>Cancel</Button>
          <Button onClick={handleSaveEdit}>Save</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
