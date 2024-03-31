import React, {useEffect, useState} from "react";
import {request} from "../../api";
import {StandardTable} from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import Switch from '@mui/material/Switch';
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
import { Image } from "@mui/icons-material";

export const VendorScreen = () => {
    const [vendors, setVendors] = useState([]);
    const INITIAL_STATE = {
        name: "",
        address: "",
        description: "",
        phone: "",
        email: "",
        url: ""
    };

    const [open, setOpen] = useState(false);
    const [data, setData] = useState(INITIAL_STATE);
    const [title, setTitle] = useState("");
    const [currentId, setCurrentId] = useState(null);
    const [openDelete, setOpenDelete] = useState(false);

    const  errorHandlers = {
        onError: (e) => {
            console.log(e);
            errorNoti("FAILED", 3000);
        },
    };

    const successHandler = (res) => {
        const msg = title === "CREATE NEW VENDOR" ? "CREATE SUCCESSFULLY" : "EDIT SUCCESSFULLY";
        successNoti(msg, 3000);
        console.log("res", res);
        const vendorIndex = vendors.findIndex(vendor => vendor.id === res.data.id);

        if (vendorIndex !== -1) {
            const updatedVendors = [...vendors];
            updatedVendors[vendorIndex] = res.data;
            setVendors(updatedVendors);
        } else {
            setVendors(prevVendors => [...prevVendors, res.data]);
        }
    };

    const successHandlerDelete = () => {
        successNoti("DELETE SUCCESSFULLY", 3000);
        const updatedVendors = vendors.filter(vendor => vendor.id !== currentId);
        setVendors(updatedVendors);
    };


    const handleInputChange = (e) => {
        e.preventDefault();
        setData({...data, [e.target.name]: e.target.value});
    };

    const handleCreate = () => {
    		setTitle("CREATE NEW VENDOR");
    		setData(INITIAL_STATE);
		setOpen(true);
	};

	const handleClose = () => {
		setOpen(false);
	}

    const handleSubmit = async(e) => {
        e.preventDefault();
        if(title === "CREATE NEW VENDOR"){
            console.log("data", data);
            request("post", "/vendor/add-new", successHandler, errorHandlers, data);
        } else if(title === "EDIT VENDOR"){
            request("put", `/vendor/edit/${currentId}`, successHandler, errorHandlers, data);
        }
        setData(INITIAL_STATE);
        handleClose();
    };

    useEffect(() => {
        request("get", "/vendor/get-all", (res) => {
            setVendors(res.data);
        }).then();
    }, []);

    const handleEdit = (vendor) => {
        setData({
            name: vendor.name,
            address: vendor.address,
            phone: vendor.phone,
            email: vendor.email,
            url: vendor.url
        });
        setCurrentId(vendor.id);
        setTitle("EDIT VENDOR");
        setOpen(true);
    };

    const handleDelete = (vendor) => {
        setOpenDelete(true);
        setCurrentId(vendor.id);
    };

    const deleteApi = () => {
        request("delete", `/vendor/delete/${currentId}`, successHandlerDelete, errorHandlers, data);
        setOpenDelete(false);
    };

    const handleCloseDelete = () => {
        setOpenDelete(false)
    };

    

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
	};

    const columns = [
        {
            title: "Name",
            field: "name",
        },
        {
            title: "Image",
            field: "image",
            render: (rowData) => (
                <img
                    src="https://vcdn-vnexpress.vnecdn.net/2022/05/10/DHBKHN-7506-1652177227.jpg"
                    alt="Dai hoc Bach khoa Ha Noi"
                    fit="contain"
                    width={70}
                    height={70}
                />
            )
        },
        {
            title: "Address",
            field: "address",
        },
        {
            title: "Email",
            field: "email",
        },
        {
            title: "Phone number",
            field: "phone",
        },
        {
            title: "URL",
            field: "url",
        },
        {
            title: "Status",
            sorting: true,
            render: (rowData) => (
                <Switch {...label} defaultChecked />
            )
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
                        demoFunction(rowData)
                    }}
                    variant="contained"
                    color="error"
                >
                    <DeleteIcon/>
                </IconButton>
            ),
        },
    ];

    const demoFunction = (user) => {
        alert("You clicked on User: " + user.id)
    }

    return (
        <div>
            <Button variant="contained" onClick={handleCreate}>Create</Button>
            <StandardTable
                title="Vendor List"
                columns={columns}
                data={vendors}
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
                    <div>CREATE LOCATION</div>
                    <hr/>
                    <form onSubmit={handleSubmit}>
                        <TextField
                            label="Name"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            required
                            name='name'
                            placeholder='Location name'
                            onChange={handleInputChange}
                        />
                        <TextField
                            label="Description"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            name='description'
                            placeholder='Location description'
                            onChange={handleInputChange}
                        />
                        <TextField
                            label="Email"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            required
                            name='email'
                            placeholder='Location email'
                            onChange={handleInputChange}
                        />                        
                        <div style={{display: "flex", gap: "20px"}}>
                            <TextField
                                label="Phone"
                                variant="outlined"
                                fullWidth
                                margin="normal"
                                required
                                name='phone'
                                placeholder='Location phone'
                                onChange={handleInputChange}
                            />
                            <TextField
                                label="URL"
                                variant="outlined"
                                fullWidth
                                margin="normal"
                                required
                                name='url'
                                placeholder='Location url'
                                onChange={handleInputChange}
                            />
                        </div>
                        <TextField
                            label="Address"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            required
                            name='address'
                            placeholder='Location address'
                            onChange={handleInputChange}
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
                    {"DELETE THIS VENDOR"}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        DO you want to delete this vendor. It cannot be undone!!!
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
