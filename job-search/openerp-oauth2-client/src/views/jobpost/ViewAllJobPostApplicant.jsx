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
import ApplicantCard from "components/ApplicantCard";
const ViewAllJobPostApplicant = () => {
    let id = "4"
    const [title, setTitle] = useState("Thực tập sinh dot net")
    const [description, setDescription] = useState("không có lương đâu")
    const [requirements, setRequirements] = useState("10 năm kinh nghiệm")
    const [location, setLocation] = useState("Hà Nội")
    const [salary, setSalary] = useState(0)

    const [cv, selectedCV] = useState({})

    const [allCV, setAllCV] = useState([])
    const [user, setUser] = useState({})
    const [cvApplication, setCVApplication] = useState([])    
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
          }).then();
    }, [])
    useEffect(() => {
        request("get", `/cv-application/${id}`, (res) => {
            setCVApplication(res.data)
        }).then();
    }, [])

    useEffect(() => {
        request("get", "/employee-cv", (res) => {
            setAllCV(res.data)
        }).then();
    }, [])

    function goToUrl(url) {
        window.location.href = url;
      }

    const handleSubmit = (status, index) => {
        let cv = cvApplication[index]
        console.log(cv)
        let submitToServerForm = {
            ...cv,
            "status": status,
            
        };
        console.log(submitToServerForm)
        request("put", `/cv-application/user/dungpq/${id}`, (res)=> {
            console.log(res);
          }, (err)=>{
            console.log(err);
          }, submitToServerForm).then();
    }

    return (
        <>
        {cvApplication.map((cv, index) => (
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
                    <CardActions>
                        <Button size="small" onClick={() => handleSubmit('reject', index)} name="reject" value="reject" cv={cv}>Reject CV</Button>
                        <Button size="small" onClick={() => handleSubmit('accept', index)}  name="accept" value="accept" cv={cv}>Accept CV</Button>
                    </CardActions>
                </CardContent>
            </Card>
        ))
        }    
        </>

    )
}

export default ViewAllJobPostApplicant;