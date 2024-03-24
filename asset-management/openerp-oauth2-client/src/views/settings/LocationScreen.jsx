import React, {useEffect, useState} from "react";
import {request} from "../../api";
import { errorNoti, successNoti } from "utils/notification";
import {StandardTable} from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import Switch from '@mui/material/Switch';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Modal from '@mui/material/Modal';
import TextField from '@mui/material/TextField';
import "./location.css";

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

    const  errorHandlers = {
        onError: (e) => {
            console.log(e);
            errorNoti("FAILED", 3000);
        },
    };

    const successHandler = () => {
        successNoti("SUCESSFULLY", 3000);
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
            request("post", "/location/add-new", successHandler, errorHandlers, data, (res) => {
                setLocations([...locations, res.data]);
            }).then();
        } else if(title === "EDIT LOCATION"){
            console.log("loca id", currentId);
            console.log("data", data);
            request("put", `/location/edit/${currentId}`, successHandler, errorHandlers, data, (res) => {
                setLocations([...locations, res.data]);
            }).then();
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
    }

    const handleDelete = async(location) => {

    }

    useEffect(() => {
        request("get", "/location/get-all", (res) => {
            setLocations(res.data);
        }).then();
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
            title: "Description",
            field: "description",
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

	const handleOpen = () => {
		setOpen(true);
	};

	const handleClose = () => {
		setOpen(false);
	}

    return (
        <div className="location-container">
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
                            isRequired
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
        </div>

    );
};
