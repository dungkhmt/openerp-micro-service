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
import UserApplicantCard from "components/UserApplicantCard";
const ViewAllApplicantStatus = () => {

    let id = "4"
    const [cvApplication, setCVApplication] = useState([])
    const [allCV, setAllCV] = useState([])
    const [user, setUser] = useState({})
    
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
            request("get", `/cv-application/user/${res.data.id}`, (res) => {
            setCVApplication(res.data)
        }).then();
        }).then();
    }, [])

    useEffect(() => {
        request("get", "/employee-cv", (res) => {
            setAllCV(res.data)
        }).then();
    }, [])


    return (
        <>
            <Typography variant="h4" component="div">
                Your Applicant
            </Typography>
            <Grid container spacing={4} style={{ backgroundColor: "#F7F7FF" }} display="flex" justifyContent="center" paddingTop={"50px"}>
                {cvApplication.map((cv, index) => (
                    <Grid item xs={5} style={{ boxShadow: '1px 1px 1px 1px rgba(0.5,0.5,0.5,0.5)', margin: '20px', borderRadius: '20px', backgroundColor: "white", display: 'flex', alignItems: 'stretch' }} >
                        <UserApplicantCard applicant={cv}></UserApplicantCard>
                    </Grid>
                ))
                }
            </Grid>
            {/* {cvApplication.map((cv, index) => (
            <Card  sx={{ minWidth: 275 }}>
                <CardContent>
                    <Typography variant="h4" component="div">
                        Candidate Applicant
                    </Typography>
                    <Typography variant="body2">
                        <strong>Applicant name</strong>: {cv.user.firstName + " " +cv.user.lastName}
                    </Typography>
                    <Typography variant="body2">
                        <strong>Applicant CV</strong>: <a href="http://xnxx.com" target="_blank" >applicant CV link</a>
                    </Typography>
                    <Typography variant="body2">
                        <strong>status</strong>: {cv.status}
                    </Typography>
                    <Typography variant="body2">
                        <strong>Uploaded at</strong>: {cv.createdTime}
                    </Typography>
                    <CardActions>
                        <Button size="small" onClick={() => goToUrl(`/view-job-post/${id}`)}>Usder Detail</Button>
                    </CardActions>
                </CardContent>
            </Card>
        ))
        }     */}
        </>

    )
}

export default ViewAllApplicantStatus;