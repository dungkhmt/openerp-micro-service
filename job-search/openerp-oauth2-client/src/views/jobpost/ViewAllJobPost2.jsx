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
import JobCard from "components/JobCard";
import SearchBar from "components/SearchBar";
import Pagination from '@mui/material/Pagination';

const ViewAllJobPost2 = () => {
    const [page, setPage] = useState(1);

    const [title, setTitle] = useState("Thực tập sinh dot net")
    const [description, setDescription] = useState("không có lương đâu")
    const [requirements, setRequirements] = useState("10 năm kinh nghiệm")
    const [location, setLocation] = useState("Hà Nội")
    const [salary, setSalary] = useState(0)
    let [resultSearch, setResultSearch] = useState([])
    const [allJobPostForm, setAllJobPostForm] = useState([])
    const [user, setUser] = useState({})
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
        }).then();
    }, [])
    useEffect(() => {
        request("get", "/job-post", (res) => {
            setAllJobPostForm(res.data)
            setResultSearch(res.data.reverse().slice(0, 6))
        }).then();
    }, [])

    function goToUrl(url) {
        window.location.href = url;
    }

    allJobPostForm.reverse()
    const handleChange = (event, value) => {
        setPage(value);
        setResultSearch(allJobPostForm.slice((value-1)*6, (value) * 6))
        console.log(allJobPostForm.slice((value-1)*6, (value) * 6))
    };

    return (
        <>
            <Grid container spacing={4} style={{ backgroundColor: "#F7F7FF" }} display="flex" justifyContent="center" paddingTop={"50px"}>
                <SearchBar items={allJobPostForm} setResult={setResultSearch}></SearchBar>
            </Grid>
            <Grid container spacing={4} style={{ backgroundColor: "#F7F7FF" }} display="flex" justifyContent="center" paddingTop={"50px"}>
                {resultSearch.map((jobPost, index) => (
                    <Grid index={index} item xs={5} style={{ boxShadow: '1px 1px 1px 1px rgba(0.5,0.5,0.5,0.5)', margin: '20px', borderRadius: '20px', backgroundColor: "white" }} onClick={() => goToUrl(`/view-job-post/${jobPost.id}`)}>
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
                {((resultSearch.length == 6 || page == Math.ceil(allJobPostForm.length / 6))  && <Pagination count={Math.ceil(allJobPostForm.length / 6)} page={page} onChange={handleChange} /> )}
            </Grid>

        </>

    )
}

export default ViewAllJobPost2;