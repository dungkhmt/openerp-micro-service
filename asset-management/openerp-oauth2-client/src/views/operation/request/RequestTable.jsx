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
import DoneIcon from '@mui/icons-material/Done';
import CloseIcon from '@mui/icons-material/Close';
import dayjs from 'dayjs';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { errorNoti, successNoti } from 'utils/notification';
import "./requestTable.css";
import {Link as RouterLink} from "react-router-dom";
import { Chip, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Link } from '@mui/material';
import { GiSandsOfTime } from 'react-icons/gi';
import { FcApproval, FcDisapprove } from "react-icons/fc";

const RequestTable = ({ request123 }) => {
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [requestName, setRequestName] = useState("");

    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const [paybackDate, setPaybackDate] = useState(null);

    const [assets, setAssets] = useState([]);
    const [allAssets, setAllAssets] = useState([]);
    const [requests, setRequests] = useState([]);
    const [assetName, setAssetName] = useState("");

    const [open, setOpen] = useState(false);
    const [currentId, setCurrentId] = useState(null);
    const [title, setTitle] = useState("");
    const [parentId, setParentId] = useState(0);

    const [openDelete, setOpenDelete] = useState(false);
    const [openApprove, setOpenApprove] = useState(false);
    const [openReject, setOpenReject] = useState(false);
    const [openPayback, setOpenPayback] = useState(false);

    const getAllAvailableAssets = async() => {
        request("get", "/asset/get-all-available", (res) => {
            setAssets(res.data);
        }).then();
    };

    const getAllAssets = async() => {
        await request("get", "/asset/get-all", (res) => {
            setAllAssets(res.data);
        }).then();
    };

    const getAllRequests = async() => {
        await request("get", "/request/get-all", (res) => {
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

    const convertToDate = (date_time) => {
        const dateString = date_time;
        const dateObj = new Date(dateString);
        const options = { month: '2-digit', day: '2-digit', year: 'numeric' };
        return dateObj.toLocaleDateString('en-US', options);
    };

    const errorHandlers = {
        onError: (e) => {
            console.log(e);
            errorNoti("FAILED", 3000);
        },
    };

    const successHandler = () => {
        const msg = title === "CREATE NEW REQUEST" ? "CREATE SUCCESSFULLY" : "EDIT SUCCESSFULLY";
        successNoti(msg, 3000);
        window.location.reload();
    };

    const successHandlerDelete = () => {
        successNoti("DELETE SUCCESSFULLY", 3000);
        window.location.reload();
    };

    const successHandlerApprove = () => {
        successNoti("APPROVE REQUEST SUCCESSFULLY", 3000);
        window.location.reload();
    };

    const successHandlerReject = () => {
        successNoti("REJECT REQUEST SUCCESSFULLY", 3000);
        window.location.reload();
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
        setPaybackDate(null);
    };

    const handleClose = () => {
        setOpen(false);
        resetData();
    };

    const handleCloseDelete = () => {
        setOpenDelete(false)
    };

    const handleCloseApprove = () => {
        setOpenApprove(false);
    };

    const handleCloseReject = () => {
        setOpenReject(false);
    };

    const handleSubmit = (e) => {
    	e.preventDefault();
        if(title === "CREATE NEW REQUEST"){
            const foundAsset = assets.find(a => a.name === assetName);
            const asset_id = foundAsset ? foundAsset.id : 0;
    
            const body = {
                name: name,
                description: description,
                start_date: startDate.format(),
                end_date: endDate.format(),
                asset_id: asset_id,
                type: 1
            };
    
            request("post", "/request/add-new", successHandler, errorHandlers, body);
        } else if(title === "EDIT REQUEST"){
            const body = {
                name: name,
                description: description,
                start_date: startDate,
                end_date: endDate
            };
            request("put", `/request/edit/${currentId}`, successHandler, errorHandlers, body);
        }

        resetData();
        setOpen(false);
    };

    const handleSubmitPayback = (e) => {
        e.preventDefault();
        const parent_request = requests.find(item => item.id === parentId);

        const body = {
            name: name,
            description: description,
            parent_id: parentId,
            asset_id: parent_request?.asset_id,
            type: 2,
            payback_date: paybackDate.format()
        };

        request("post", "/request/add-new", successHandler, errorHandlers, body);
        resetData();
        setOpenPayback(false);
    };


    const handleDelete = (request) => {
        setOpenDelete(true);
        setCurrentId(request.id);
    };

    const deleteApi = () => {
        request("delete", `/request/delete/${currentId}`, successHandlerDelete, errorHandlers, {});
        setOpenDelete(false);
    };

    const handleApprove = (request) => {
        setOpenApprove(true);
        setCurrentId(request.id);
    };

    const approveApi = () => {
        request("put", `/request/approve/${currentId}`, successHandlerApprove, errorHandlers, {});
        setOpenApprove(false);
    };

    const handleReject = (request) => {
        setOpenReject(true);
        setCurrentId(request.id);
    };

    const rejectApi = () => {
        request("put", `/request/reject/${currentId}`, successHandlerReject, errorHandlers, {});
        setOpenReject(false);
    };

    const handlePayback = (request) => {
        setOpenPayback(true);
        setCurrentId(request?.id);
        setTitle("CREATE PAYBACK REQUEST");
        setRequestName(request?.name);
        const asset = allAssets.find(item => item.id === request.asset_id);
        setAssetName(asset?.name);
        setParentId(request?.id);
    };

    const closePayback = () => {
        setOpenPayback(false);
    };

    const handleEdit = (req) => {
        if(req.status === 0){ // pending
            setName(req.name);
            setDescription(req.description);
            setStartDate(req.start_date);
            setEndDate(req.end_date);
            setCurrentId(req.id);
            setTitle("EDIT REQUEST");
            const asset = allAssets.find(a => a.id === req.asset_id);
            setAssetName(asset["name"]);
            setOpen(true);
        }
    };

    useEffect(() => {
        getAllAvailableAssets();
        getAllAssets();
        getAllRequests();
    }, []);

    const columns = [
        {
            title: "Request",
            field: "name",
            render: (rowData) => (
                <Link
                    component={RouterLink}
                    to={`/request/${rowData["id"]}`}
                >
                    {rowData["name"]}
                </Link>
            ),
        },
        {
            title: "Creator",
            field: "user_id",
        },
        {
            title: "Asset",
            render: (rowData) => {
                const asset = allAssets.find(item => item.id === rowData.asset_id);
                return (
                    <div>{asset?.name}</div>
                )
            }
        },
        {
            title: "Status",
            sorting: true,
            render: (rowData) => {
                if(rowData.status === 0){
                    return (
                        <Chip
                            icon={<GiSandsOfTime size={24} />}
                            label="PENDING"
                            color="primary"
                            variant="outlined"
                            style={{fontSize: "1rem"}}
                        />
                    )
                } else if(rowData.status === 1){
                    return (
                        <Chip
                            icon={<FcApproval size={24} />}
                            label="APPROVED"
                            color="primary"
                            variant="outlined"
                            style={{fontSize: "1rem", color: "green", borderColor: "green"}}
                        />
                    )
                } else if(rowData.status === 2){
                    return (
                        <Chip
                            icon={<FcDisapprove size={24} />}
                            label="REJECTED"
                            color="primary"
                            variant="outlined"
                            style={{fontSize: "1rem", color: "red", borderColor: "red"}}
                        />
                    )
                } else if(rowData.status === 3){
                    return (
                        <Chip
                            icon={<FcApproval size={24} />}
                            label="DONE"
                            color="primary"
                            variant="outlined"
                            style={{fontSize: "1rem", color: "green", borderColor: "green"}}
                        />
                    )
                }
            }
        },
        {
            title: "Approver",
            field: "admin_id"
        },
        {
            title: "StartDate",
            render: (rowData) => {
                if(rowData.start_date){
                    return <div>{convertToDate(rowData.start_date)}</div>;
                }
                return <div></div>;
            }
        },
        {
            title: "EndDate",
            render: (rowData) => {
                if(rowData.end_date){
                    return <div>{convertToDate(rowData.end_date)}</div>;
                }
                return <div></div>;
            }
        },
        {
            title: "PaybackDate",
            render: (rowData) => {
                if(rowData.payback_date){
                    return <div>{convertToDate(rowData.payback_date)}</div>;
                }
                return <div></div>;
            }
        },
        {
            title: "Approve",
            sorting: false,
            render: (rowData) => {
                if(rowData.status === 0){
                    return <IconButton
                        onClick={() => {
                            handleApprove(rowData)
                        }}
                        variant="contained"
                        color="success"
                    >
                        <DoneIcon/>
                    </IconButton>
                } else {
                    return ``;
                }

            }
        },
        {
            title: "Reject",
            render: (rowData) => {
                if(rowData.status === 0){
                    return <IconButton
                        onClick={() => {
                            handleReject(rowData)
                        }}
                        variant="contained"
                        color="error"
                    >
                        <CloseIcon/>
                    </IconButton>
                } else {
                    return ``;
                }
            }
        },
        {
            title: "Edit",
            sorting: false,
            render: (rowData) => {
                if(rowData.status === 0){
                    return <IconButton
                        onClick={() => {
                            handleEdit(rowData)
                        }}
                        variant="contained"
                        color="success"
                    >
                        <EditIcon/>
                    </IconButton>
                } else {
                    return ``;
                }
            },
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
        {
            title: "Payback",
            sorting: false,
            render: (rowData) => {
                if(rowData["status"] === 1 && !rowData.parent_id){
                    return (
                        <Button
                            variant="contained"
                            onClick={() => handlePayback(rowData)}
                        >
                            PAYBACK
                        </Button>
                    )
                } else {
                    return ``;
                }
            }
        }
    ];

    return (
        <div>
            <Button variant="contained" onClick={handleCreate}>Create</Button>
            <StandardTable
                title="Requests List"
                columns={columns}
                data={requests}
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
                        {title === "CREATE NEW REQUEST" ? (<FormControl sx={{ minWidth: 730, marginTop: "20px" }}>
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
                        </FormControl>) : (
                            <TextField
                                disabled
                                sx={{ minWidth: 730, marginTop: "20px" }}
                                id="outlined-disabled"
                                label="Asset"
                                value={assetName}
                            />
                        )}
                        {title === "CREATE NEW REQUEST" ? (<div style={{marginTop: "20px"}}>
                            <LocalizationProvider dateAdapter={AdapterDayjs} className='date-adapter'>
                            <DemoContainer components={['DatePicker', 'DatePicker']}>
                                <DatePicker className='date-picker-request' label="Start Date" value={startDate} onChange={(newValue) => setStartDate(newValue)} />
                                <DatePicker className='date-picker-request' label="End Date" value={endDate} onChange={(newValue) => setEndDate(newValue)} />
                            </DemoContainer>
                            </LocalizationProvider>
                        </div>) : (
                            <LocalizationProvider dateAdapter={AdapterDayjs} className='date-adapter'>
                            <DemoContainer components={['DatePicker', 'DatePicker']}>
                                <DatePicker className='date-picker-request' label="Start Date" value={dayjs(convertToDate(startDate))} onChange={(newValue) => setStartDate(newValue)} />
                                <DatePicker className='date-picker-request' label="End Date" value={dayjs(convertToDate(endDate))} onChange={(newValue) => setEndDate(newValue)} />
                            </DemoContainer>
                            </LocalizationProvider>
                        )}
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
                    {"DELETE THIS REQUEST"}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Do you want to delete this request. It cannot be undone?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button variant="outlined" onClick={handleCloseDelete}>CANCEL</Button>
                    <Button variant="outlined" color="error" onClick={deleteApi} autoFocus>
                        DELETE
                    </Button>
                </DialogActions>
            </Dialog>
            <Dialog
                open={openApprove}
                onClose={handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                    {"APPROVE THIS REQUEST"}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Do you want to approve this request. Asset in request will be assign for user
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button variant="outlined" onClick={handleCloseApprove}>CANCEL</Button>
                    <Button variant="outlined" color="error" onClick={approveApi} autoFocus>
                        APPROVE
                    </Button>
                </DialogActions>
            </Dialog>
            <Dialog
                open={openReject}
                onClose={handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                    {"REJECT THIS REQUEST"}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Do you want to reject this request. Asset in request will not be assign for user
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button variant="outlined" onClick={handleCloseReject}>CANCEL</Button>
                    <Button variant="outlined" color="error" onClick={rejectApi} autoFocus>
                        REJECT
                    </Button>
                </DialogActions>
            </Dialog>
            <Modal
                open={openPayback}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
                onClose={closePayback}
            >
                <Box sx={style}>
                    <div>{title}</div>
                    <hr/>
                    <form onSubmit={handleSubmitPayback}>
                        <TextField
                            label="Name"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            required
                            value={name}
                            name="name"
                            placeholder="Request name"
                            onChange={(e) => setName(e.target.value)}
                        />
                        <TextField
                            label="Description"
                            variant="outlined"
                            fullWidth
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            margin="normal"
                            name="description"
                            placeholder="Request description"
                        />
                        <TextField
                            disabled
                            sx={{ minWidth: 730, marginTop: "20px" }}
                            id="outlined-disabled"
                            label="Parent Request"
                            value={requestName}
                        />
                        <TextField
                            disabled
                            sx={{ minWidth: 730, marginTop: "20px", marginBottom: "20px" }}
                            id="outlined-disabled"
                            label="Asset Payback"
                            value={assetName}
                        />
                        <LocalizationProvider dateAdapter={AdapterDayjs} className="date-adapter">
                            <DemoContainer components={['DatePicker']}>
                                <DatePicker className='date-picker-payback' label="Payback Date" value={paybackDate} onChange={(newValue) => setPaybackDate(newValue)}/>
                            </DemoContainer>
                        </LocalizationProvider>
                        <div style={{display: "flex", justifyContent: "space-between", marginTop: "20px" }}>
                            <Button
                                variant="outlined"
                                color="primary"
                                type="cancel"
                                onClick={closePayback}
                            >
                                Cancel
                            </Button>
                            <Button
                                variant="contained"
                                color="primary"
                                type="submit"
                            >
                                Create
                            </Button>
                        </div>
                    </form>
                </Box>
            </Modal>
        </div>
    );
};

export default RequestTable;
