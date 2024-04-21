import { useState, useEffect } from "react"
import {
    TextField, Button, Grid, Typography, IconButton, Box,
    Divider,
    Link,
    Container,
    Chip,
} from '@mui/material';
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
import { useParams } from 'react-router-dom';
import { ApplyJobPost } from './ApplyJobPost'
import JobTitleCard from "components/JobTitleCard";
import CompanyProfile from "components/CompanyProfile";
import JobDescription from "components/JobDescription";
import { light } from "@mui/material/styles/createPalette";
import { grey } from "@mui/material/colors";

const ViewJobPostDetail = () => {

    const { id } = useParams();
    const [jobPostForm, setJobPostForm] = useState({

    })
    const [user, setUser] = useState({})
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
        }).then();
    }, [])
    useEffect(() => {
        request("get", `/job-post/${id}`, (res) => {
            setJobPostForm(res.data)
            console.log(res.data)
        }).then();
    }, [])

    function goToUrl(url) {
        window.location.href = url;
    }

    const [open, setOpen] = useState(false);

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };
    return (
        <>
            <Grid container spacing={2} style={{backgroundColor:  "#F7F7FF"}}>
                <Grid item xs={7} style={{ boxShadow: '0px 0px 1px 1px rgba(0,0,0,0.5)', margin: '10px', borderRadius: '20px', backgroundColor: "white"}}>
                    <JobTitleCard jobListing={jobPostForm } open={open} onClose={handleClose} jobId={id} jobName={jobPostForm.jobPost?.title} handleClick={handleClickOpen}></JobTitleCard>
                </Grid>
                <Grid item xs={4} style={{ boxShadow: '0px 0px 1px 1px rgba(0,0,0,0.5)', margin: '10px', borderRadius: '20px', backgroundColor: "white"}}>
                    <CompanyProfile company={jobPostForm}></CompanyProfile>
                </Grid>
                <Grid item xs={7} style={{ boxShadow: '0px 0px 1px 1px rgba(0,0,0,0.5)', margin: '10px', borderRadius: '20px', backgroundColor: "white"}}>
                    <JobDescription jobDescription={jobPostForm}></JobDescription>
                </Grid>
            </Grid>
            {/* <Card sx={{ minWidth: 275 }}>
                <CardContent>
                    <Typography variant="h4" component="div">
                        Job Detail
                    </Typography>
                    <Typography variant="body2">
                        <strong>Job title</strong>: {JobPostForm.jobPost?.title}
                    </Typography>
                    <Typography variant="body2">
                        <strong>Job location</strong>: {JobPostForm.jobPost?.locations}
                    </Typography>
                    <Typography variant="body2">
                        <strong>Job salary</strong>: {JobPostForm.jobPost?.salary}
                    </Typography>
                    <Typography variant="body2">
                        <strong>About company</strong>: {JobPostForm.company?.about}
                    </Typography>
                </CardContent>
                <CardActions>
                    <Button variant="outlined" onClick={handleClickOpen}>
                        apply this job
                    </Button>
                    <ApplyJobPost open={open} onClose={handleClose} jobId={id} jobName={JobPostForm.jobPost?.title} />
                </CardActions>
            </Card> */}
        </>

    )
}


export default ViewJobPostDetail;