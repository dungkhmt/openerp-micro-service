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

const CreateJobPost = () => {
    const [loading, setLoading] = useState(false);
    const [openSnackbar, setOpenSnackbar] = useState(false);

    const [title, setTitle] = useState("")
    const [description, setDescription] = useState("")
    const [requirements, setRequirements] = useState("")
    const [location, setLocation] = useState("")
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
        e.preventDefault()
        setLoading(true);
        await sleep(2000);
        jobPostForm.salary = Number(jobPostForm.salary)
        console.log(jobPostForm)
        request("post", "/job-post", (res) => {
            return 0;
        }, (res) => {
            return 0;
        }, jobPostForm).then();
        setLoading(false);
        setOpenSnackbar(true);
        await sleep(1500);
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
                        <TextField fullWidth label="location" value={jobPostForm.locations} variant="outlined" name="location" onChange={handleInputChange} />
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
                    <Snackbar
                        open={openSnackbar}
                        autoHideDuration={3000} // Duration in milliseconds
                        onClose={() => setOpenSnackbar(false)}
                        message="Form submitted successfully!"
                    />
                    <Backdrop open={loading} sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}>
                        <CircularProgress color="inherit" />
                        <div>
                            Please wait a few seconds...
                        </div>
                    </Backdrop>
                </Grid>
            </Grid>
        </>
    )
}

export default CreateJobPost;