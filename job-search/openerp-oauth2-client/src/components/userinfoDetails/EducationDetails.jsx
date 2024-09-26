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
import Swal from "sweetalert2";
import './styles.css';

const EducatonDetail = ({ Education,  open, onClose }) => {

    const [loading, setLoading] = useState(false);
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [education, setEducation] = useState(Education)
    const [user, setUser] = useState({})

    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
        }).then();
    }, [])

    const handleClose = () => {
        onClose();
    };

    const sleep = (milliseconds) => {
        return new Promise(resolve => setTimeout(resolve, milliseconds));
    }

    // const handleSubmit = async (e) => {
    //     e.preventDefault()
    //     setLoading(true);
    //     await sleep(2000);
    //     let submitToServerForm = {...Education, ...education}
    //     submitToServerForm.user = user
    //     console.log(submitToServerForm)
    //     console.log(user)
    //     request("post", `/education`, (res) => {
    //         console.log(res.data);
    //     }, (err) => {
    //         console.log(err);
    //     }, submitToServerForm).then();
    //     setLoading(false);
    //     setOpenSnackbar(true);
    //     await sleep(1500);
    //     handleClose()
    // }
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
    
        // Replace the sleep function with a more conventional setTimeout
        // await sleep(2000); // Not recommended
    
        let submitToServerForm = {...Education, ...education};
        submitToServerForm.user = user;
    
        // Here you can replace console.log with SweetAlert2
        Swal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, submit it!'
        }).then((result) => {
            if (result.isConfirmed) {
                request("post", `/education`, (res) => {
                    Swal.fire(
                        'Submitted!',
                        'Your information has been submitted.',
                        'success'
                    );
                    handleClose();
                }, (err) => {
                    return {"onError" : (e) => { Swal.fire(
                        'Failed!',
                        'There was a problem submitting your information.',
                        'error'
                    );
                    handleClose();
                }
                }
                }, submitToServerForm).then();
            }
        });
    
        setLoading(false);
        // setOpenSnackbar(true);
        
    }
    const handleInputChange = (event) => {
        setEducation({
            ...education,
            [event.target.name]: event.target.value,
        });
    };
    function goToUrl(url) {
        window.location.href = url;
    }

    return (<>
        <div>
            <Dialog onClose={handleClose} open={open} maxWidth="md" fullWidth={true} sx={{ maxHeight: 'lg' }} fullHeight={true}>
                <h1 fullHeight>Your Education: </h1>
                <form noValidate autoComplete="off">
                    <Grid item xs={12} container spacing={2}  display="flex" justifyContent="center" paddingTop={"50px"}>
                        <Grid item xs={11} container  style={{ backgroundColor: "white" }}  paddingLeft={"50px"}>
                            <Typography>School name</Typography>
                        </Grid>
                        <Grid item xs={11}>
                            <TextField fullWidth label="School name" value={education.schoolName} variant="outlined" name="schoolName" onChange={handleInputChange} />
                        </Grid>
                        <Grid item xs={11} container  style={{ backgroundColor: "white" }}  paddingLeft={"50px"}>
                            <Typography >Major</Typography>
                        </Grid>
                        <Grid item xs={11}>
                            <TextField fullWidth label="Major" value={education.major} variant="outlined" name="major" onChange={handleInputChange} />
                        </Grid>
                        <Grid item xs={11} container  style={{ backgroundColor: "white" }} display="flex" paddingTop={"50px"}>
                            <Typography >Description</Typography>
                        </Grid>
                        <Grid item xs={11}>
                            <TextField fullWidth label="Description" value={education.description} variant="outlined" name="description" onChange={handleInputChange} />
                        </Grid>
                        <Grid item xs={11} container  style={{ backgroundColor: "white" }} display="flex" paddingTop={"50px"}>
                            <Typography>Grade</Typography>
                        </Grid>
                        <Grid item xs={11}>
                            <TextField fullWidth label="Grade" value={education.grade} variant="outlined" name="grade" onChange={handleInputChange} />
                        </Grid>
                        <Grid item xs={11} container  style={{ backgroundColor: "white" }} display="flex" paddingTop={"50px"}>
                            <Typography >Starting time</Typography>
                        </Grid>
                        <Grid item xs={11}>
                            <TextField fullWidth label="Starting time" value={education.startingTime} variant="outlined" name="startingTime" onChange={handleInputChange} />
                        </Grid>
                        <Grid item xs={11} container  style={{ backgroundColor: "white" }} display="flex" paddingTop={"50px"}>
                            <Typography >Ending time</Typography>
                        </Grid>
                        <Grid item xs={11}>
                            <TextField fullWidth label="Ending time" value={education.endingTime} variant="outlined" name="endingTime" onChange={handleInputChange} />
                        </Grid>

                        {/* <Snackbar
                            open={openSnackbar}
                            autoHideDuration={3000} // Duration in milliseconds
                            onClose={() => setOpenSnackbar(false)}
                            message="Form submitted successfully!"
                        /> */}

                        {/* This button should submit the form */}
                        <Box display="flex" justifyContent="center" paddingTop={'50px'}>
                            <Button type="submit" variant="contained" color="primary" onClick={handleSubmit} disabled={loading} sx={{ marginBottom: '16px' }}>
                                {'Submit'}
                            </Button>
                        </Box>
                        {/* <Backdrop open={loading} sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}>
                            <CircularProgress color="inherit" />
                            <div>
                                Please wait a few seconds...
                            </div>
                        </Backdrop> */}
                    </Grid>
                </form>
            </Dialog>
        </div>
    </>
    )
}


export { EducatonDetail };