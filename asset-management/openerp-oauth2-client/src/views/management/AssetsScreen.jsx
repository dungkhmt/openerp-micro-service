import React, {useEffect, useState} from "react";
import {request} from "../../api";
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
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import { errorNoti, successNoti } from "utils/notification";

const AssetsScreen = () => {    
    const [assetName, setAssetName] = useState("");
    const [description, setDescription] = useState("");
    const [locationName, setLocationName] = useState("");
    const [vendorName, setVendorName] = useState("");
    const [type, setType] = useState("");
    const [assignee, setAssignee] = useState("");

    const [title, setTitle] = useState("");
    const [assets, setAssets] = useState([]);
    const [locations, setLocations] = useState([]);
    const [vendors, setVendors] = useState([]);
    const [types, setTypes] = useState([]);
    const [users, setUsers] = useState([]);

    const [open, setOpen] = useState(false);
    const [currentId, setCurrentId] = useState(null);
    const [openDelete, setOpenDelete] = useState(false);



    const getAllLocations = async() => {
        request("get", "/location/get-all", (res) => {
            setLocations(res.data);
        }).then();
    };
    
    const getAllVendors = async() => {
        request("get", "/vendor/get-all", (res) => {
            setVendors(res.data);
        }).then();
    };

    const getAllUsers = async() => {
        request("get", "/user/get-all", (res) => {
            setUsers(res.data);
        }).then();
    };

    const getAllTypes = async() => {
        request("get", "/asset-type/get-all", (res) => {
            setTypes(res.data);
        }).then();
    };

    const  errorHandlers = {
        onError: (e) => {
            console.log(e);
            errorNoti("FAILED", 3000);
        },
    }; 

    const successHandler = (res) => {
        const msg = title === "CREATE NEW ASSET" ? "CREATE SUCCESSFULLY" : "EDIT SUCCESSFULLY";
        successNoti(msg, 3000);
        const assetIndex = assets.findIndex(asset => asset.id === res.data.id);

        if (assetIndex !== -1) {
            const updatedAssets = [...assets];
            updatedAssets[assetIndex] = res.data;
            console.log("res data", res.data);
            setAssets(updatedAssets);
        } else {
            setAssets(prevAsset => [...prevAsset, res.data]);
        }
    };

    const successHandlerDelete = () => {
        successNoti("DELETE SUCCESSFULLY", 3000);
        const updatedAssets = assets.filter(a => a.id !== currentId);
        setAssets(updatedAssets);
    };    

    const resetData = () => {
        setAssetName("");
        setDescription("");
        setAssignee("");
        setLocationName("");
        setVendorName("");
        setType("");
    }

    const handleCreate = () => {
        setTitle("CREATE NEW ASSET");
        resetData();
        handleOpen();
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const foundLocation = locations.find(location => location.name === locationName);
        const foundVendor = vendors.find(vendor => vendor.name === vendorName);
        const foundType = types.find(typ => typ.name === type);
        const foundAssignee = users.find(user => user.id === assignee);

        const body = {
            name: assetName,
            description: description,
            location_id: foundLocation ? foundLocation.id : 0,
            vendor_id: foundVendor ? foundVendor.id : 0,
            type_id: foundType ? foundType.id : 0,
            assignee_id: foundAssignee ? foundAssignee.id : 0
        };

        console.log("body shy", body);

        if(title === "CREATE NEW ASSET"){
            request("post", "/asset/add-new", successHandler, errorHandlers, body);
        } else if(title === "EDIT ASSET"){
            request("put", `/asset/edit/${currentId}`, successHandler, errorHandlers, body);
        }

        resetData();
        handleClose();
    };

    const handleEdit = (asset) => {
        setAssetName(asset.name);
        setDescription(asset.description);
        const foundLocation = locations.find(location => location.id === asset.location_id);
        const foundVendor = vendors.find(vendor => vendor.id === asset.vendor_id);
        const foundType = types.find(typ => typ.id === asset.type_id);
        const foundAssignee = asset.assignee_id;

        foundLocation ? setLocationName(foundLocation.name) : setLocationName("");
        foundVendor ? setVendorName(foundVendor.name) : setVendorName("");
        foundType ? setType(foundType.name) : setType("");
        foundAssignee ? setAssignee(foundAssignee) : setAssignee("");
        setCurrentId(asset.id);
        setTitle("EDIT ASSET");
        handleOpen();
    };

    const handleDelete = (asset) => {
        setOpenDelete(true);
        setCurrentId(asset.id);
    }    

    const handleCloseDelete = () => {
        setOpenDelete(false)
    };

    const deleteApi = () => {
        request("delete", `/location/delete/${currentId}`, successHandlerDelete, errorHandlers, {});
        setOpenDelete(false);
    };    

    useEffect(() => {
        getAllLocations();
        getAllVendors();
        getAllUsers();
        getAllTypes();
    }, []);

    useEffect(() => {
        request("get", "/asset/get-all", (res) => {
            setAssets(res.data);
        }).then();
    }, []);
    
    console.log("asset", assets);

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
        },
        {
            title: "Assignee",
            field: "assignee_id",
        },
        {
            title: "Code",
            field: "code",
        },
        {
            title: "Type",
            render: (rowData) => {
                let found = types.find(typ => typ.id === rowData.type_id);
                if(found){
                    return <div>{found.name}</div>
                } else return ``;
            }
        },
        {
            title: "Status",
            field: "status",
            render: (rowData) => (
            	<div>In use</div>
            )
        },
        {
            title: "Location",
            field: "location",
            render: (rowData) => {
                let found = locations.find(loc => loc.id === rowData.location_id);
                if(found){
                    return <div>{found.name}</div>
                } else return ``;
            }
        }, 
        {
            title: "Vendor",
            field: "vendor",
            render: (rowData) => {
                let found = vendors.find(v => v.id === rowData.vendor_id);
                if(found){
                    return <div>{found.name}</div>
                } else return ``;
            }
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
	};
    
    return (
        <div>
            <Button variant="contained" onClick={handleCreate}>Create</Button>
            <StandardTable
                title="Assets"
                columns={columns}
                data={assets}
                // hideCommandBar
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
                            placeholder='Asset name'
                            onChange={(e) => setAssetName(e.target.value)}
                            value={assetName}
                        />
                        <TextField
                            label="Description"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            name='description'
                            placeholder='Asset description'
                            onChange={(e) => setDescription(e.target.value)}
                            value={description}
                        />
                        <FormControl sx={{ minWidth: 255, marginTop: "20px" }}>
                            <InputLabel id="demo-simple-select-label">Type</InputLabel>
                            <Select
                                labelId="demo-simple-select-label"
                                id="demo-simple-select"
                                value={type}
                                label="Type"
                                onChange={(e) => setType(e.target.value)}
                                renderValue={() => type}
                            >
                                {types.map((type) => (
                                    <MenuItem id={type.id} value={type.name}>{type.name}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>    
                        <FormControl sx={{ marginLeft: "20px", minWidth: 255, marginTop: "20px" }}>
                            <InputLabel id="demo-simple-select-label">Assignee</InputLabel>
                            <Select
                                labelId="demo-simple-select-label"
                                id="demo-simple-select"
                                value={assignee}
                                label="Assignee"
                                onChange={(e) => setAssignee(e.target.value)}
                                renderValue={() => assignee}
                            >
                                {users.map((user) => (
                                    <MenuItem key={user.id} value={user.id}>{user.id}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>    
                        <FormControl sx={{ minWidth: 255, marginTop: "20px" }}>
                            <InputLabel id="demo-simple-select-label">Location</InputLabel>
                            <Select
                                labelId="demo-simple-select-label"
                                id="demo-simple-select"
                                value={locationName}
                                label="Location"
                                onChange={(e) => setLocationName(e.target.value)}
                                //renderValue={() => data.location_name}
                            >
                                {locations.map((location) => (
                                    <MenuItem key={location.id} value={location.name}>{location.name}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>   
                        <FormControl sx={{ marginLeft: "20px", minWidth: 255, marginTop: "20px" }}>
                            <InputLabel id="demo-simple-select-label">Vendor</InputLabel>
                            <Select
                                labelId="demo-simple-select-label"
                                id="demo-simple-select"
                                value={vendorName}
                                label="Vendor"
                                onChange={(e) => setVendorName(e.target.value)}
                                // renderValue={() => data.vendor_name}
                            >
                                {vendors.map((vendor) => (
                                    <MenuItem key={vendor.id} value={vendor.name}>{vendor.name}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>                           
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

export default AssetsScreen;
