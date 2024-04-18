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

const ViewAllCV = () => {

    const [allCV, setAllCV] = useState([])
    const [user, setUser] = useState({})
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
          }).then();
    }, [])
    useEffect(() => {
        request("get", "/employee-cv/user/dungpq", (res) => {
            setAllCV(res.data)
        }).then();
    }, [])

    function goToUrl(url) {
        window.location.href = url;
      }

    return (
        <>
        {allCV.map((cv, index) => (
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
        }    
        </>

    )
}

export default ViewAllCV;