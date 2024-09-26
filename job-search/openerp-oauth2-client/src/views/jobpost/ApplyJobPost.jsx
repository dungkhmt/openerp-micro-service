import { useState, useEffect } from "react"
import { TextField, Button, Grid, Typography, IconButton, Box, InputLabel, Select } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import { request } from "../../api"
import {
    Form,
    Col,
    FormGroup,
    FormLabel,
    FormControl,
    Row,
} from "react-bootstrap";
import { Card, CardContent, CardActions } from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
// import { Select, MenuItem, InputLabel } from '@material-ui/core';
import Dialog from '@mui/material/Dialog';
import { useHookstate } from '@hookstate/core';
import fetchUserState from "state/userState";
import { CircularProgress, Snackbar } from '@mui/material';
import Backdrop from '@mui/material/Backdrop';

const ApplyJobPost = ({ open, onClose, jobId, jobName }) => {
    const [loading, setLoading] = useState(false);
    const [openSnackbar, setOpenSnackbar] = useState(false);

    const [allCV, setAllCV] = useState([])
    const [selectedCVName, setSelectedCVName] = useState()
    const [selectedCV, setSelectedCV] = useState({})
    const [user, setUser] = useState({})
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
        }).then();
    }, [])

    const handleClose = () => {
        onClose();
    };

    const [submitForm, setSubmitForm] = useState({})

    useEffect(() => {
        request("get", `/employee-cv/user/${user.id}`, (res) => {
            setAllCV(res.data)
        }).then();
    }, [user])

    const sleep = (milliseconds) => {
        return new Promise(resolve => setTimeout(resolve, milliseconds));
      }

    const handleSubmit = async  (e) => {
        console.log("selected cv: ", selectedCV)
        e.preventDefault()
        setLoading(true);
        await sleep(2000);
        let submitToServerForm = {
            "status": "pending",
            "cvId": selectedCV.employeeCV?.id
        };
        let data = fetchUserState()
        submitToServerForm.user = user
        console.log(submitToServerForm)
        console.log(user)
        request("post", `/cv-application/user/${user.id}/${jobId}`, (res) => {
            console.log(res);
        }, (err) => {
            console.log(err);
        }, submitToServerForm).then();
        setLoading(false);
        setOpenSnackbar(true);
        await sleep(1500);
        handleClose()
    }

    function goToUrl(url) {
        window.location.href = url;
    }
    let allCVArray = allCV.map(e => { return { title: e.employeeCV.title, id: e.employeeCV.id } })
    return (<>
        <div>
            <Dialog onClose={handleClose} open={open} maxWidth="md" fullWidth={true} sx={{ maxHeight: 'lg' }} fullHeight={true}>
                <h1>Apply for:  {jobName}</h1>
                <form noValidate autoComplete="off">
                    <InputLabel id="cv-label" sx={{ marginBottom: '16px' }}><strong>Attach your CV</strong></InputLabel>
                    <Autocomplete
                        disablePortal
                        id="combo-box-demo"
                        options={allCV.map(e => { return "Name: " + e.employeeCV?.title + " - Description: " + e.employeeCV?.description })}
                        sx={{ width: 300, margin: '16px' }}
                        renderInput={(params) => <TextField {...params} label="select your cv" />}
                        value={selectedCVName}
                        onChange={(event, value) => {
                            event.preventDefault()
                            setSelectedCVName(value)
                            let cvExtracted = extractValuesFromString(value.toString())
                            let cvName = cvExtracted.name
                            let index = cvExtracted.id
                            let idx = allCVArray.findIndex(e => { return e.title == cvName && e.id == index })
                            setSelectedCV(allCV.find(e => e.id == index))
                        }}
                    />
                    {/* Add more MenuItems here */}

                    {/* This button should trigger a file picker dialog */}
                    <Snackbar
                        open={openSnackbar}
                        autoHideDuration={3000} // Duration in milliseconds
                        onClose={() => setOpenSnackbar(false)}
                        message="Form submitted successfully!"
                    />
                    <InputLabel id="create-cv-label" sx={{ padding: '16px' }}><a href="https://xnxx.com">or create new one</a></InputLabel>
                    <TextField
                        id="message"
                        label="Message to employer"
                        multiline
                        rows={5}
                        variant="outlined"
                        fullWidth
                        sx={{ margin: '16px', maxWidth: '865px' }}
                    />

                    {/* This button should submit the form */}
                    <Box display="flex" justifyContent="center">
                        <Button type="submit" variant="contained" color="primary" onClick={handleSubmit} disabled={loading}  sx={{ marginBottom: '16px' }}>
                            {'Submit'}
                        </Button>
                    </Box>
                    <Backdrop open={loading} sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}>
                        <CircularProgress color="inherit" />
                        <div>
                            Please wait a few seconds...
                        </div>
                    </Backdrop>
                </form>
            </Dialog>
        </div>
    </>
    )
}

function extractValuesFromString(str) {
    // Regular expression to capture field name and value pairs, handling potential spaces
    const regex = /([^:]+):\s*(.*?)\s*(?=id:|$)/g;

    const results = {};
    let match;
    while ((match = regex.exec(str)) !== null) {
        const fieldName = match[1].trim();
        const value = match[2].trim();
        results[fieldName] = value;
    }
    return results;
}

export { ApplyJobPost };