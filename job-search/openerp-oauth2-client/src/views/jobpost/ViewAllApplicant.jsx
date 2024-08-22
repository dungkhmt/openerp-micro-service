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
import ApplicantCard from "components/application/ApplicantCard";

const ViewAllApplicant = () => {

    let { id } = useParams();
    id = "4"
    const [title, setTitle] = useState("Thực tập sinh dot net")
    const [description, setDescription] = useState("không có lương đâu")
    const [requirements, setRequirements] = useState("10 năm kinh nghiệm")
    const [location, setLocation] = useState("Hà Nội")
    const [salary, setSalary] = useState(0)

    const [allJobPostForm, setAllJobPostForm] = useState([])
    const [allApplicant, setAllApplicant] = useState([])
    const [allJobPostForm2, setAllJobPostForm2] = useState([])
    const [user, setUser] = useState({})
    const [allJobPost, setAllJobPost] = useState([])
    let hotestId = 0
    let hotestLength = 0
    let hotestData = {}
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)

            request("get", `/cv-application/${res.data.id}`, (res) => {
                setAllJobPostForm2(res.data)
                console.log(res.data)
            }).then();
            request("get", `/job-post/user/${res.data.id}`, (res) => {
                
                for(let i = 0; i < res.data.length; i++) {
                    let UID = res.data[i].id
                    request("get", `/cv-application/${UID}`, (res) => {
                        if(hotestLength < res.data.length) {
                            // console.log(res.data)
                            // console.log("uid of hotest job", UID)
                            hotestLength = res.data.length
                            hotestId = UID
                            hotestData = res.data
                            // console.log("xnxx", hotestData)
                            setAllApplicant(hotestData)
                        }
                    }).then();
                    
                }
            }).then();
        }).then();
    }, [])
    // useEffect(() => {
    //     request("get", `/getAllJobPost/${user.id}`, (res) => {
    //         let hotestId = 0
    //         let hotestLength = 0
    //         console.log("xxxxxxxxx", res.data)
    //         for(let i = 0; i < res.data.length; i++) {
    //             request("get", `/cv-application/${res.data[i].id}`, (res) => {
    //                 console.log("xxxxxxxxx", res.data)
    //             }).then();
    //         }

    //     }).then();
    // }, [])

    function goToUrl(url) {
        window.location.href = url;
    }

    return (
        <>
            <Typography variant="h4" component="div">
                Candidate Applicant
            </Typography>
            <Grid container spacing={4} style={{ backgroundColor: "#F7F7FF" }} display="flex" justifyContent="center" paddingTop={"50px"}>
                {allApplicant.map((applicant, index) => (
                    <Grid item xs={6} style={{ boxShadow: '1px 1px 1px 1px rgba(0.5,0.5,0.5,0.5)', margin: '20px', borderRadius: '20px', backgroundColor: "white", display: 'flex', alignItems: 'stretch' }} >
                        <ApplicantCard applicant={applicant} index={index}></ApplicantCard>
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
        </>

    )
}

export default ViewAllApplicant;