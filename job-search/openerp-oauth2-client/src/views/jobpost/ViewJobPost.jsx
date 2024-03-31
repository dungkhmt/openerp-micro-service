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

const ViewJobPost = () => {
    
    const [title, setTitle] = useState("Thực tập sinh dot net")
    const [description, setDescription] = useState("không có lương đâu")
    const [requirements, setRequirements] = useState("10 năm kinh nghiệm")
    const [location, setLocation] = useState("Hà Nội")
    const [salary, setSalary] = useState(0)
    const [jobPostForm, setJobPostForm] = useState({
        title: "title",
        description: "description",
        requirements: "requirements",
        locations: "location",
        salary: "salary"
    })

    const handleInputChange = (event) => {
        setJobPostForm({
            ...jobPostForm,
            [event.target.name]: event.target.value,
        });
    };


    return (
        <>
            <Grid item xs={12}>
                <Typography variant="h4">Job title</Typography>
            </Grid>
            <Grid item xs={4}>
                <Typography variant="outlined" fontSize={16} name="title"> {title} </Typography>
            </Grid>
            <Grid item xs={12}>
                <Typography variant="h4">Description</Typography>
            </Grid>
            <Grid item xs={4}>
            <Typography variant="outlined" name="description" fontSize={16}> {description} </Typography>
            </Grid>
            <Grid item xs={12}>
                <Typography variant="h4">Requirements</Typography>
            </Grid>
            <Grid item xs={4}>
                <Typography variant="outlined" name="requirements" fontSize={16}> {requirements} </Typography>
            </Grid>
            <Grid item xs={12}>
                <Typography variant="h4">Location</Typography>
            </Grid>
            <Grid item xs={4}>
            <Typography variant="outlined" name="location" fontSize={16}> {location} </Typography>
            </Grid>
            <Grid item xs={12}>
                <Typography variant="h4" >Salary</Typography>
            </Grid>
            <Grid item xs={4}>
            <Typography variant="outlined" name="salary" fontSize={16}> {salary} </Typography>
            </Grid>
        </>
    )
}

export default ViewJobPost;