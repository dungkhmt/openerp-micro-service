import { request } from 'api';
import React, { useEffect, useState } from 'react';
import {StandardTable} from "erp-hust/lib/StandardTable";
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Modal from '@mui/material/Modal';
import TextField from '@mui/material/TextField';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import dayjs from 'dayjs';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { errorNoti, successNoti } from 'utils/notification';
import "./requestTable.css";

const RequestTable = () => {
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");

    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);

    const [assets, setAssets] = useState([]);
    const [requests, setRequests] = useState([]);
    const [assetName, setAssetName] = useState("");

    const [open, setOpen] = useState(false);
    const [currentId, setCurrentId] = useState(null);
    const [title, setTitle] = useState("");

    const getAllAvailableAssets = async() => {
        request("get", "/asset/get-all", (res) => {
            setAssets(res.data);
        }).then();
    };

    const getAllRequests = async() => {
        request("get", "/request/get-all", (res) => {
            setRequests(res.data);
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

    const resetData = () => {
        setName("");
        setDescription("");
        setAssetName("");
        setStartDate(null);
        setEndDate(null);
    }


    const handleClose = () => {
        setOpen(false);
        resetData();
    };

    const handleSubmit = (e) => {
    	e.preventDefault();
        const foundAsset = assets.find(a => a.name === assetName);
        console.log("dataaa", name);
        const asset_id = foundAsset ? foundAsset.id : 0;

        const body = {
            name: name,
            description: description,
            start_date: startDate.format(),
            end_date: endDate.format(),
            asset_id: asset_id
        };

        console.log("body", body);

        request("post", "/request/add-new", successHandler, errorHandlers, body);

        resetData();
        setOpen(false);
    };

    useEffect(() => {
        getAllAvailableAssets();
        getAllRequests();
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
            title: "StartDate",
            field: "startDate",
        },
        {
            title: "EndDate",
            field: "endDate",
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
                            value={name}
                            name='name'
                            placeholder='Request name'
                            onChange={(e) => setName(e.target.value)}
                        />
                        <TextField
                            label="Description"
                            variant="outlined"
                            fullWidth
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            margin="normal"
                            name='description'
                            placeholder='Request description'
                        /> 
                        <FormControl sx={{ minWidth: 730, marginTop: "20px" }}>
                            <InputLabel id="demo-simple-select-label">Asset</InputLabel>
                            <Select
                                labelId="demo-simple-select-label"
                                id="demo-simple-select"
                                value={assetName}
                                label="Asset"
                                onChange={(e) => setAssetName(e.target.value)}
                            >
                                {assets.map((asset) => (
                                    <MenuItem id={asset.id} value={asset.name}>{asset.name}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>    
                        <div style={{marginTop: "20px"}}>
                            <LocalizationProvider dateAdapter={AdapterDayjs} className='date-adapter'>
                            <DemoContainer components={['DatePicker', 'DatePicker']}>
                                <DatePicker className='date-picker-request' label="Start Date" value={startDate} onChange={(newValue) => setStartDate(newValue)} />
                                <DatePicker className='date-picker-request' label="End Date" value={endDate} onChange={(newValue) => setEndDate(newValue)} />
                            </DemoContainer>
                            </LocalizationProvider>
                        </div>
                        {/* <FormControl sx={{ width: "100%", marginTop: "20px" }}>
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
                        </FormControl>                          */}
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
