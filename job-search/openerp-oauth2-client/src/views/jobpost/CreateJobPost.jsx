import { useState, useEffect } from "react"
import { TextField, Button, Grid, Typography, IconButton, Box } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import {
    Form,
    Col,
    FormGroup,
    FormLabel,
    FormControl,
    Row,
} from "react-bootstrap";
import { request } from "../../api"
import { CircularProgress, Snackbar } from '@mui/material';
import Backdrop from '@mui/material/Backdrop';
import Swal from "sweetalert2";
import './styles.css';

const CreateJobPost = () => {
    const [loading, setLoading] = useState(false);
    const [openSnackbar, setOpenSnackbar] = useState(false);

    const [title, setTitle] = useState("")
    const [description, setDescription] = useState("")
    const [requirements, setRequirements] = useState("")
    const [locations, setLocations] = useState("")
    const [salary, setSalary] = useState(0)
    const [jobPostForm, setJobPostForm] = useState({
    })
    const [user, setUser] = useState({})
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
        }).then();
    }, [])
    const handleInputChange = (event) => {
        setJobPostForm({
            ...jobPostForm,
            [event.target.name]: event.target.value,
        });
    };
    const sleep = (milliseconds) => {
        return new Promise(resolve => setTimeout(resolve, milliseconds));
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
    
        // Simulate a delay with setTimeout
    
        jobPostForm.salary = Number(jobPostForm.salary);
        jobPostForm.user = user
        console.log(jobPostForm)
        // Use SweetAlert2 for confirmation before submitting
        try {
        Swal.fire({
            title: 'Confirm Submission',
            text: "Are you sure you want to submit this job post?",
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Submit',
            cancelButtonText: 'Cancel'
        }).then((result) => {
            if (result.isConfirmed) {
                request("post", "/job-post", (res) => {
                    // Handle success
                    console.log("success: ", res)
                    Swal.fire(
                        'Submitted!',
                        'The job post has been submitted.',
                        'success'
                    );
                    setLoading(false);
                }, (err) => {
                    // Handle error
                    console.log("error: ", err)
                    Swal.fire(
                        'Failed!',
                        'There was a problem submitting the job post.',
                        'error'
                    );
                    setLoading(false);
                }, jobPostForm).then();
            } else {
                // If cancelled, reset loading
                setLoading(false);
            }
        }); }
        catch (error) {
            console.error(error);
            // Show an error Swal if there's an issue with the upload or form submission
            Swal.fire({
                title: 'Error!',
                text: 'There was an issue with the upload or submission.',
                icon: 'error',
                confirmButtonText: 'OK'
            });
        }
    };

    return (
        <>
            <Grid item xs={12} container spacing={4} style={{ backgroundColor: "#F7F7FF" }} display="flex" justifyContent="center" paddingTop={"50px"}>
                <Grid item xs={8} container spacing={2} style={{ backgroundColor: "white" }} display="flex" paddingTop={"50px"}>
                    <Grid item xs={12}>
                        <Typography variant="h3" marginLeft="20%">Create your Job Post</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Job title</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="Job Title" value={jobPostForm.title} variant="outlined" name="title" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Description</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="description" value={jobPostForm.description} variant="outlined" name="description" multiline rows={4} onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Requirements</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="requirements" value={jobPostForm.requirements} variant="outlined" name="requirements" multiline rows={4} onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Location</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="locations" value={jobPostForm.locations} variant="outlined" name="locations" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Salary</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="salary" value={jobPostForm.salary} variant="outlined" name="salary" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={24} />
                    <Grid item xs={24} />
                    <Grid container justifyContent="center" > {/* Add Grid container for centering */}
                        <Grid item >
                            <Button sx={{ backgroundColor: 'green', color: 'white' }} variant="contained" onClick={handleSubmit}>
                                Submit CV
                            </Button>
                        </Grid>
                    </Grid>
                    {/* <Snackbar
                        open={openSnackbar}
                        autoHideDuration={3000} // Duration in milliseconds
                        onClose={() => setOpenSnackbar(false)}
                        message="Form submitted successfully!"
                    /> */}
                    {/* <Backdrop open={loading} sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}>
                        <CircularProgress color="inherit" />
                        <div>
                            Please wait a few seconds...
                        </div>
                    </Backdrop> */}
                </Grid>
            </Grid>
        </>
    )
}

export default CreateJobPost;