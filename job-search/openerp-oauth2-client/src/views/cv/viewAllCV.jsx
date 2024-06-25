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
import CVCard from "components/CVCard";
const ViewAllCV = () => {
    
    const [allCV, setAllCV] = useState([])
    const [user, setUser] = useState({})
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
            request("get", `/employee-cv/user/${res.data.id}`, (res) => {
                setAllCV(res.data)
            }).then();
        }).then();
    }, []) 
    
    function goToUrl(url) {
        window.location.href = url;
    }
    
    return (
        <>
        <Typography variant="h4" component="div">
        Your CV List: 
        </Typography>
        <Grid container spacing={4} style={{ backgroundColor: "#F7F7FF" }} display="flex" justifyContent="center" paddingTop={"50px"}>
        {allCV.map((cv, index) => (
            <Grid item xs={6} style={{ boxShadow: '1px 1px 1px 1px rgba(0.5,0.5,0.5,0.5)', margin: '20px', borderRadius: '20px', backgroundColor: "white", display: 'flex', alignItems: 'stretch' }} >
            <CVCard cvData={cv}></CVCard>
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
            //             <Button size="small" onClick={() => goToUrl(`/view-job-post-applicant/${jobPost.id}`)}>View All CV Applicant</Button>
            //         </CardActions>
            //     </CardContent>
            // </Card>
        ))
    }
    </Grid>
    {/* {allCV.map((cv, index) => (
        <Card  sx={{ minWidth: 275 }}>
        <CardContent>
        <Typography variant="h4" component="div">
        {cv.employeeCV?.title}
        </Typography>
        <Typography variant="body2">
        <strong>cv description</strong>: {cv.employeeCV?.description}
        </Typography>
        <Typography variant="body2">
        <strong>skill</strong>: {cv.skills[0]?.skillName}
        </Typography>
        <Typography variant="body2">
        <strong>education</strong>: {cv.educations[0]?.schoolName}
        </Typography>
        <Typography variant="body2">
        <strong>experience</strong>: {cv.experiences[0]?.experience}
        </Typography>
        <CardActions>
        <Button size="small" onClick={() => goToUrl(`/view-job-post`)}>More Detail</Button>
        </CardActions>
        </CardContent>
        </Card>
        ))
        }     */}
        </>
        
    )
}

export default ViewAllCV;