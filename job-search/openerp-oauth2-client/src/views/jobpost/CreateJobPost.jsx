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
import {request} from "../../api"

const CreateJobPost = () => {

    const [title, setTitle] = useState("")
    const [description, setDescription] = useState("")
    const [requirements, setRequirements] = useState("")
    const [location, setLocation] = useState("")
    const [salary, setSalary] = useState(0)
    const [jobPostForm, setJobPostForm] = useState({
        title: "title",
        description: "description",
        requirements: "requirements",
        locations: "location",
        salary: 4.0
    })

    const handleInputChange = (event) => {
        setJobPostForm({
            ...jobPostForm,
            [event.target.name]: event.target.value,
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault()
        jobPostForm.salary = Number(jobPostForm.salary)
        console.log(jobPostForm)
        request("post", "/job-post",(res) => {
            return 0;
        }, (res) => {
            return 0;
        }, jobPostForm).then();
    };

    return (
        <>
            <Grid item xs={12}>
                <Typography variant="h4">Job title</Typography>
            </Grid>
            <Grid item xs={4}>
                <TextField fullWidth label="Job Title" value={jobPostForm.title} variant="outlined" name="title"  onChange={handleInputChange} />
            </Grid>
            <Grid item xs={12}>
                <Typography variant="h4">Description</Typography>
            </Grid>
            <Grid item xs={4}>
                <TextField fullWidth label="description" value={jobPostForm.description} variant="outlined" name="description" multiline rows={4}  onChange={handleInputChange} />
            </Grid>
            <Grid item xs={12}>
                <Typography variant="h4">Requirements</Typography>
            </Grid>
            <Grid item xs={4}>
                <TextField fullWidth label="requirements" value={jobPostForm.requirements} variant="outlined" name="requirements"  multiline rows={4}  onChange={handleInputChange} />
            </Grid>
            <Grid item xs={12}>
                <Typography variant="h4">Location</Typography>
            </Grid>
            <Grid item xs={4}>
                <TextField fullWidth label="location" value={jobPostForm.locations} variant="outlined" name="location"  onChange={handleInputChange} />
            </Grid>
            <Grid item xs={12}>
                <Typography variant="h4">Salary</Typography>
            </Grid>
            <Grid item xs={4}>
                <TextField fullWidth label="salary" value={jobPostForm.salary} variant="outlined" name="salary"  onChange={handleInputChange} />
            </Grid>
            <Grid item xs={24} />
            <Grid item xs={24} />
            <Grid container justifyContent="center" > {/* Add Grid container for centering */}
                <Grid item >
                    <Button sx={{ backgroundColor: 'green', color: 'white' }}  variant="contained" onClick={handleSubmit}>
                        Submit CV
                    </Button>
                </Grid>
            </Grid>
        </>
    )
}

export default CreateJobPost;