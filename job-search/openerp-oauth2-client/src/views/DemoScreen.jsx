import React, { useState, useEffect } from "react";
import { request } from "../api";
import { StandardTable } from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import Button from "@mui/material/Button";
import DeleteIcon from "@mui/icons-material/Delete";
import {
    Grid,
    Typography,
} from '@mui/material';

function DemoScreen() {
    const [users, setUsers] = useState([]);
    const [openPopup, setOpenPopup] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);

    useEffect(() => {
        request("get", "/user/get-all", (res) => {
            setUsers(res.data);
            console.log(res.data)
        }).then();
    }, []);

    const columns = [
        {
            title: "User",
            field: "id",
        },
        {
            title: "Creation time",
            field: "createdOn",
        },
        {
            title: "Show",
            sorting: false,
            render: (rowData) => (
                <IconButton
                    onClick={() => {
                        setSelectedUser(rowData);
                        setOpenPopup(true);
                    }}
                    variant="contained"
                    color="success"
                >
                    <EditIcon />
                </IconButton>
            ),
        },
        {
            title: "Delete",
            sorting: false,
            render: (rowData) => (
                <IconButton
                    onClick={() => {
                        deleteFunction(rowData)
                    }}
                    variant="contained"
                    color="error"
                >
                    <DeleteIcon />
                </IconButton>
            ),
        },
    ];

    const handlePopupClose = () => {
        setOpenPopup(false);
        setSelectedUser(null);
    };

    const deleteFunction = (user) => {
        alert("you don't have privilege to perform this action")
    }

    const handlePopupAction = () => {
        // Implement your desired action on the selected user data (e.g., navigate, update)
        console.log("Perform action on user:", selectedUser);
        handlePopupClose();
    };

    return (
        <div>
            <StandardTable
                title="User List"
                columns={columns}
                data={users}
                options={{
                    selection: false,
                    pageSize: 20,
                    search: true,
                    sorting: true,
                }}
            />

            <Dialog open={openPopup} onClose={handlePopupClose} maxWidth="xs" fullWidth={true} alignItems="center">
                <Grid container justifyContent="center" alignItems="center">
                    <Grid item>
                        <Grid container justifyContent="center" alignItems="center">
                            <Grid item>
                                <DialogTitle>User Details</DialogTitle>
                            </Grid>
                        </Grid>
                        <Typography style={{ borderBottom: '1px dashed rgba(0, 0, 0, 0.3)' ,paddingBottom: "10px"}}><strong>Id:</strong> {selectedUser?.id}</Typography>
                        <Typography style={{ borderBottom: '1px dashed rgba(0, 0, 0, 0.3)' ,paddingBottom: "10px"}}><strong>Email:</strong> {selectedUser?.email}</Typography>
                        <Typography style={{ borderBottom: '1px dashed rgba(0, 0, 0, 0.3)' ,paddingBottom: "10px"}}><strong>First name:</strong> {selectedUser?.firstName}</Typography>
                        <Typography style={{ borderBottom: '1px dashed rgba(0, 0, 0, 0.3)' ,paddingBottom: "10px"}}><strong>Last name:</strong> {selectedUser?.lastName}</Typography>
                        <Typography style={{ borderBottom: '1px dashed rgba(0, 0, 0, 0.3)' ,paddingBottom: "10px"}}><strong>Enabled:</strong> {selectedUser?.enabled ? "true" : false}</Typography>
                        <DialogContent>
                            {/* Display relevant user information here (optional) */}
                        </DialogContent>
                        <DialogActions>
                            <Grid container justifyContent="center" alignItems="center"> {/* Optional for vertical centering */}
                                <Grid item>
                                    <Button variant="outlined" onClick={handlePopupClose}>
                                        Cancel
                                    </Button>
                                </Grid>
                            </Grid>
                        </DialogActions>
                    </Grid>
                </Grid>
            </Dialog>
        </div>
    );
}

export default DemoScreen;