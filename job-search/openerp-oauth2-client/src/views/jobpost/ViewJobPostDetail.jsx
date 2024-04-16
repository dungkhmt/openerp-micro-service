import { useState, useEffect } from "react"
import { TextField, Button, Grid, Typography, IconButton, Box } from '@mui/material';
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

const ViewJobPostDetail = () => {

    const { id } = useParams();
    const [JobPostForm, setJobPostForm] = useState({

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
            <Card sx={{ minWidth: 275 }}>
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
            </Card>
        </>

    )
}

export default ViewJobPostDetail;