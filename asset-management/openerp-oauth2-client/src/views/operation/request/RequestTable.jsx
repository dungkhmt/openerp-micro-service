import { request } from 'api';
import React, { useEffect, useState } from 'react';
import {StandardTable} from "erp-hust/lib/StandardTable";
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Modal from '@mui/material/Modal';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Checkbox from "@mui/material/Checkbox";
import ListItemText from "@mui/material/ListItemText";
import OutlinedInput from "@mui/material/OutlinedInput";
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { errorNoti, successNoti } from 'utils/notification';

const RequestTable = () => {
    const INITIAL_DATA = {
        name: "",
        description: ""
    };

    const [data, setData] = useState(INITIAL_DATA);
    const [users, setUsers] = useState([]);
    const [requests, setRequests] = useState([]);
    const [approvers, setApprovers] = useState([]);

    const [open, setOpen] = useState(false);
    const [currentId, setCurrentId] = useState(null);
    const [title, setTitle] = useState("");


    const getAllUsers = async() => {
        request("get", "/user/get-all", (res) => {
            setUsers(res.data);
        }).then();
    };
   
    const style = {
		position: 'absolute',
		top: '50%',
		left: '50%',
		transform: 'translate(-50%, -50%)',
		width: 800,
		bgcolor: 'background.paper',
		border: '2px solid #000',
		boxShadow: 24,
		p: 4,
        gap: "30px"
	};

    const ITEM_HEIGHT = 48;
    const ITEM_PADDING_TOP = 8;
    const MenuProps = {
        PaperProps: {
            style: {
            maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
            width: 250,
            },
        },
    };
    
    const errorHandlers = {
        onError: (e) => {
            console.log(e);
            errorNoti("FAILED", 3000);
        },
    };

        const successHandler = (res) => {
        const msg = title === "CREATE NEW ASSET" ? "CREATE SUCCESSFULLY" : "EDIT SUCCESSFULLY";
        successNoti(msg, 3000);
        setRequests(prevAsset => [...prevAsset, res.data]);
    };

    const handleCreate = () => {
        setTitle("CREATE NEW REQUEST");
        setOpen(true);
    };


    const handleClose = () => {
        setOpen(false);
        setData(INITIAL_DATA);
        setApprovers([]);
    };

    const handleChangeApprovers = (event) => {
        const {
            target: {value},
        } = event;

        setApprovers(
            typeof value === "string" ? value.split(",") : value
        );
    };

    const handleSubmit = (e) => {
    	e.preventDefault();
        console.log("dataaa", data);
        console.log("appro", approvers);
        const arr = ["aa", "bb", "cc"];
        console.log("mix mix", {...data, approvers: approvers});
        request("post", "/request/add-new", successHandler, errorHandlers, {...data, ...approvers});
    };

    const handleInputChange = (e) => {
        e.preventDefault();
        setData({...data, [e.target.name]: e.target.value});
    };

    useEffect(() => {
        getAllUsers();
    });

    useEffect(() => {
        request("get", "/request/get-all", (res) => {
            setRequests(res.data);
        }).then();
    }, []);

    const columns = [
        {
            title: "Request",
            field: "id",
        },
        {
            title: "Creator",
            field: "firstName",
        },
        {
            title: "Description",
            field: "lastName",
        },
        {
            title: "Status",
            field: "email",
        },
        {
            title: "Approvers",
            field: "approvers",
        },
        {
            title: "Edit",
            sorting: false,
            render: (rowData) => (
                <IconButton
                    onClick={() => {
                        demoFunction(rowData)
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
                title="Requests List"
                columns={columns}
                data={requests}
                // hideCommandBar
                options={{
                    selection: false,
                    pageSize: 10,
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
                    <form onSubmit={handleSubmit}>
                        <TextField
                            label="Name"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            required
                            value={data.name}
                            name='name'
                            placeholder='Asset name'
                            onChange={handleInputChange}
                        />
                        <TextField
                            label="Description"
                            variant="outlined"
                            fullWidth
                            value={data.description}
                            onChange={handleInputChange}
                            margin="normal"
                            name='description'
                            placeholder='Asset description'
                        />  
                        <FormControl sx={{ width: "100%", marginTop: "20px" }}>
                            <InputLabel id="demo-multiple-checkbox-label">Approval</InputLabel>
                            <Select
                                labelId="demo-multiple-checkbox-label"
                                id="demo-multiple-checkbox"
                                multiple
                                value={approvers}
                                onChange={handleChangeApprovers}
                                input={<OutlinedInput label="Tag"/>}
                                renderValue={(selected) => selected.join(', ')}
                                MenuProps={MenuProps}
                            >
                                {users.map((ele, index) => (
                                    <MenuItem key={ele.id} value={ele.id}>
                                    <Checkbox checked={approvers.indexOf(ele.id) > -1}/>
                                    <ListItemText primary={ele.id}/>
                                    </MenuItem>
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
        </div>
    );
};

export default RequestTable;
