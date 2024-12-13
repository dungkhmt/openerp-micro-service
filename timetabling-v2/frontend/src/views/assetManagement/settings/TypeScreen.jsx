import React, {useEffect, useState} from "react";
import { request } from "api";
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
import { errorNoti, successNoti } from "utils/notification";

export const TypeScreen = () => {
    const [types, setTypes] = useState([]);
        const INITIAL_STATE = {
        name: "",
        code_prefix: "",
        description: "",
    };

    const [open, setOpen] = useState(false);
    const [data, setData] = useState(INITIAL_STATE);
    const [title, setTitle] = useState("");
    const [currentId, setCurrentId] = useState(null);
    const [openDelete, setOpenDelete] = useState(false);

    const successHandler = (res) => {
        const msg = title === "CREATE NEW ASSET TYPE" ? "CREATE SUCCESSFULLY" : "EDIT SUCCESSFULLY";
        successNoti(msg, 3000);
        window.location.reload();
    };    

    const successHandlerDelete = () => {
        successNoti("DELETE SUCCESSFULLY", 3000);
        const updatedTypes = types.filter(type => type.id !== currentId);
        setTypes(updatedTypes);
    };

    const  errorHandlers = {
        onError: (e) => {
            console.log(e);
            errorNoti("FAILED", 3000);
        },
    };

    const handleInputChange = (e) => {
        e.preventDefault();
        setData({...data, [e.target.name]: e.target.value});
    };

    const handleCreate = () => {
    	setTitle("CREATE NEW ASSET TYPE");
    	setData(INITIAL_STATE);
		setOpen(true);
	};

	const handleClose = () => {
		setOpen(false);
	};    

    const handleSubmit = async(e) => {
        e.preventDefault();
        if(title === "CREATE NEW ASSET TYPE"){            
            request("post", "/asset-type/add-new", successHandler, errorHandlers, data);
        } else if(title === "EDIT ASSET TYPE"){
            request("put", `/asset-type/edit/${currentId}`, successHandler, errorHandlers, data);
        }
        setData(INITIAL_STATE);
        handleClose();
    };

    const handleEdit = (type) => {
        setData({
            name: type.name,
            description: type.description,
            code_prefix: type.code_prefix
        });
        setCurrentId(type.id);
        setTitle("EDIT ASSET TYPE");
        setOpen(true);
    };    

    const handleDelete = (type) => {
        setOpenDelete(true);
        setCurrentId(type.id);
    };

    const deleteApi = () => {
        request("delete", `/asset-type/delete/${currentId}`, successHandlerDelete, errorHandlers, data);
        setOpenDelete(false);
    };

    const handleCloseDelete = () => {
        setOpenDelete(false)
    };

    useEffect(() => {
        request("get", "/asset-type/get-all", (res) => {
            setTypes(res.data);
        }).then();
    }, []);  
    
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
	};
    
    const columns = [
        {
            title: "Name",
            field: "name",
            render: (rowData) => (
                <Link
                    component={RouterLink}
                    to={`/type/${rowData["id"]}`}
                >
                    {rowData["name"]}
                </Link>
            )
        },
        {
            title: "Description",
            field: "description",
        },
        {
            title: "Code Prefix",
            field: "code_prefix",
        },
        {
            title: "Num Assets",
            field: "num_assets"
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

    return (
        <div>
            <Button variant="contained" onClick={handleCreate}>Create</Button>
            <StandardTable
                title="Type List"
                columns={columns}
                data={types}
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
                            placeholder='Type name'
                            onChange={handleInputChange}
                            value={data.name}
                        />
                        <TextField
                            label="Description"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            name='description'
                            placeholder='Type description'
                            onChange={handleInputChange}
                            value={data.description}
                        />
                        <TextField
                            label="Code Prefix"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            required
                            name='code_prefix'
                            placeholder='Type code prefix'
                            onChange={handleInputChange}
                            value={data.code_prefix}
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
                    {"DELETE THIS TYPE"}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        DO you want to delete this type. It cannot be undone!!!
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
