import { useState, useEffect } from "react"
import { TextField, Button, Grid, Typography, IconButton, Box } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import { request } from "../api"
import {
    Form,
    Col,
    FormGroup,
    FormLabel,
    FormControl,
    Row,
} from "react-bootstrap";
import { Card, CardContent, CardActions } from '@mui/material';
import JobCard from "components/JobCard";
import { Dashboard } from "@mui/icons-material";

const DashboardOverView = () => {
    const [allJobPostForm, setAllJobPostForm] = useState([])
    const [allJob, setAllJob] = useState([])
    const [user, setUser] = useState({})
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
          }).then();
    }, [])
    useEffect(() => {
        request("get", "/job-post", (res) => {
            setAllJobPostForm(res.data.reverse().slice(0, 4))
            setAllJob(res.data)
        }).then();
    }, [])

    function goToUrl(url) {
        window.location.href = url;
      }

    return (
        <>
        <h1 style={{textAlign: 'center', backgroundColor:  "#F7F7FF", paddingTop: '24px', fontSize: "48px",  paddingBottom: '24px'}}>Latest jobs for you</h1>
        <Grid container spacing={4} style={{backgroundColor:  "#F7F7FF"}} display="flex" justifyContent="center" paddingTop={"50px"}>
        {allJobPostForm.map((jobPost, index) => (
            <Grid item xs={5} style={{ boxShadow: '1px 1px 1px 1px rgba(0.5,0.5,0.5,0.5)', margin: '20px', borderRadius: '20px', backgroundColor: "white"}} onClick={() => goToUrl(`/view-job-post/${jobPost.id}`)}>
            <JobCard job={jobPost}></JobCard>
            </Grid>
            // <Card  sx={{ minWidth: 275 }}>
            //     <CardContent>
            //         <Typography variant="h4" component="div">
            //             your dream job here
            //         </Typography>
            //         <Typography variant="body2">
            //             <strong>Job title</strong>: {jobPost.title}
            //         </Typography>
            //         <Typography variant="body2">
            //             <strong>Job location</strong>: {jobPost.locations}
            //         </Typography>
            //         <Typography variant="body2">
            //             <strong>Job salary</strong>: {jobPost.salary ? jobPost.salary : "thương lượng"}
            //         </Typography>
            //         <CardActions>
            //             <Button size="small" onClick={() => goToUrl(`/view-job-post/${jobPost.id}`)}>More Detail</Button>
            //         </CardActions>
            //     </CardContent>
            // </Card>
        ))
        }    
        </Grid>
        {(allJob.length - 4 )> 0 && (<h4 style={{textAlign: 'center', backgroundColor:  "#F7F7FF", paddingTop: '24px',  paddingBottom: '60px' }}>And {allJob.length - 4} more are waiting for you !</h4>)}
        </>

    )
}

export default DashboardOverView;