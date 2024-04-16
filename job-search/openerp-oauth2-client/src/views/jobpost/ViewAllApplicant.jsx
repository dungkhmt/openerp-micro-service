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

const ViewAllApplicant = () => {
    
    let { id } = useParams();
    id = "dungpq"
    const [title, setTitle] = useState("Thực tập sinh dot net")
    const [description, setDescription] = useState("không có lương đâu")
    const [requirements, setRequirements] = useState("10 năm kinh nghiệm")
    const [location, setLocation] = useState("Hà Nội")
    const [salary, setSalary] = useState(0)

    const [allJobPostForm, setAllJobPostForm] = useState([])
    const [user, setUser] = useState({})
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
          }).then();
    }, [])
    useEffect(() => {
        request("get", `/job-post/user/${id}`, (res) => {
            setAllJobPostForm(res.data)
        }).then();
    }, [])

    function goToUrl(url) {
        window.location.href = url;
      }

    return (
        <>
        {allJobPostForm.map((jobPost, index) => (
            <Card  sx={{ minWidth: 275 }}>
                <CardContent>
                    <Typography variant="h4" component="div">
                        your dream job here
                    </Typography>
                    <Typography variant="body2">
                        <strong>Job title</strong>: {jobPost.title}
                    </Typography>
                    <Typography variant="body2">
                        <strong>Job location</strong>: {jobPost.locations}
                    </Typography>
                    <Typography variant="body2">
                        <strong>Job salary</strong>: {jobPost.salary ? jobPost.salary : "thương lượng"}
                    </Typography>
                    <CardActions>
                        <Button size="small" onClick={() => goToUrl(`/view-job-post-applicant/${jobPost.id}`)}>View All CV Applicant</Button>
                    </CardActions>
                </CardContent>
            </Card>
        ))
        }    
        </>

    )
}

export default ViewAllApplicant;