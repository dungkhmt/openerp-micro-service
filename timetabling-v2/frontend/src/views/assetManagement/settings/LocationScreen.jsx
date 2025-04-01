import React, {useEffect, useState} from "react";
import { request } from "api";
import { errorNoti, successNoti } from "utils/notification";
import { Link as RouterLink } from "react-router-dom";
import { Link } from "@mui/material";
import {StandardTable} from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Modal from '@mui/material/Modal';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';

export const LocationScreen = () => {
    const INITIAL_STATE = {
        name: "",
        description: "",
        address: ""
    };

    const [locations, setLocations] = useState([]);
    const [open, setOpen] = useState(false);
    const [data, setData] = useState(INITIAL_STATE);
    const [currentId, setCurrentId] = useState(null);
    const [title, setTitle] = useState("");
    const [openDelete, setOpenDelete] = useState(false);

    const  errorHandlers = {
        onError: (e) => {
            console.log(e);
            errorNoti("FAILED", 3000);
        },
    };

    const successHandler = (res) => {
        const msg = title === "CREATE NEW LOCATION" ? "CREATE SUCCESSFULLY" : "EDIT SUCCESSFULLY";
        successNoti(msg, 3000);
        window.location.reload();
    };

    const successHandlerDelete = () => {
        successNoti("DELETE SUCCESSFULLY", 3000);
        const updatedLocations = locations.filter(location => location.id !== currentId);
        setLocations(updatedLocations);
    };

    const callApi = () => {
        request("get", "/location/get-all", (res) => {
            setLocations(res.data);
        }).then();
    };

    const handleInputChange = (e) => {
        e.preventDefault();
        setData({...data, [e.target.name]: e.target.value});
    };

    const handleCreate = () => {
        setTitle("CREATE NEW LOCATION");
        setData(INITIAL_STATE);
        handleOpen();
    }

    const handleSubmit = async(e) => {
        e.preventDefault();
        if(title === "CREATE NEW LOCATION"){
            request("post", "/location/add-new", successHandler, errorHandlers, data);
        } else if(title === "EDIT LOCATION"){
            request("put", `/location/edit/${currentId}`, successHandler, errorHandlers, data);
        }
        setData(INITIAL_STATE);
        handleClose();
    };

    const handleEdit = (location) => {
        setData({
        	name: location.name,
        	description: location.description,
        	address: location.address
        });
        setCurrentId(location.id);
        setTitle("EDIT LOCATION");
        handleOpen();
    };

    const handleCloseDelete = () => {
        setOpenDelete(false)
    };

    const handleDelete = (location) => {
        setOpenDelete(true);
        setCurrentId(location.id);
    }

    const deleteApi = () => {
        request("delete", `/location/delete/${currentId}`, successHandlerDelete, errorHandlers, data);
        setOpenDelete(false);
    };

    useEffect(() => {
        callApi();
    }, []);

    const label = { inputProps: { 'aria-label': 'Size switch demo' } };

	const style = {
		position: 'absolute',
		top: '50%',
		left: '50%',
		transform: 'translate(-50%, -50%)',
		width: 600,
		bgcolor: 'background.paper',
		border: '2px solid #000',
		boxShadow: 24,
		p: 4,
        gap: "30px"
	};

    const columns = [
        {
            title: "Name",
            field: "name",
            render: (rowData) => (
                <Link
                    component={RouterLink}
                    to={`/location/${rowData["id"]}`}
                >
                    {rowData["name"]}
                </Link>
            )
        },
        {
            title: "Address",
            field: "address",
        },
        {
            title: "Description",
            field: "description",
        },
        {
            title: "Edit",
            sorting: false,
            render: (rowData) => (
                <IconButton
                    onClick={() => {
                        handleEdit(rowData)
                    }}
                    variant="contained"
                    color="success"
                >
                    <EditIcon/>
                </IconButton>
            ),
        },
        {
            title: "Delete",
            sorting: false,
            render: (rowData) => (
                <IconButton
                    onClick={() => {
                        handleDelete(rowData)
                    }}
                    variant="contained"
                    color="error"
                >
                    <DeleteIcon/>
                </IconButton>
            ),
        },
    ];

	const handleOpen = () => {
		setOpen(true);
	};

	const handleClose = () => {
		setOpen(false);
	}

    return (
        <div className="">
            <Button variant="contained" onClick={handleCreate}>Create</Button>
            <StandardTable
                title="Location List"
                columns={columns}
                data={locations}
                options={{
                    selection: false,
                    pageSize: 20,
                    search: true,
                    sorting: true,
                }}
            />

			<Modal
				open={open}
				aria-labelledby="modal-modal-title"
				aria-describedby="modal-modal-description"
				onClose={handleClose}
			>
				<Box sx={style}>
                    <div>{title}</div>
                    <hr/>
                    <form onSubmit={(e) => handleSubmit(e)}>
                        <TextField
                            label="Name"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            required
                            name='name'
                            placeholder='Location name'
                            onChange={handleInputChange}
                            value={data.name}
                        />
                        <TextField
                            label="Description"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            required
                            name='description'
                            placeholder='Location description'
                            onChange={handleInputChange}
                            value={data.description}
                        />
                        <TextField
                            label="Address"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            required
                            name='address'
                            placeholder='Location address'
                            onChange={handleInputChange}
                            value={data.address}
                        />           
                        <div style={{display: "flex", justifyContent: "space-between", marginTop: "20px"}}>
                            <Button
                                variant="outlined"
                                color="primary"
                                type="cancel"
                                onClick={handleClose}
                            >
                                Cancel
                            </Button>
                            <Button
                                variant="contained"
                                color="primary"
                                type="submit"
                            >
                                Submit
                            </Button>
                        </div>
                    </form>
				</Box>
      		</Modal>
            <Dialog
                open={openDelete}
                onClose={handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                    {"DELETE THIS LOCATION"}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        DO you want to delete this location. It cannot be undone?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button variant="outlined" onClick={handleCloseDelete}>CANCEL</Button>
                    <Button variant="outlined" color="error" onClick={deleteApi} autoFocus>
                        DELETE
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
};
