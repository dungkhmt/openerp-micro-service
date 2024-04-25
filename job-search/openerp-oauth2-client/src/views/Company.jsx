import { useState, useEffect } from "react"
import { TextField, Button, Grid, Typography, IconButton, Box } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import {
    Form,
    Col,
    FormGroup,
    FormLabel,
    FormControl,
    Row,
} from "react-bootstrap";
import { request } from "../api"
import { CircularProgress, Snackbar } from '@mui/material';
import Backdrop from '@mui/material/Backdrop';

const UserCompany = () => {
    const [loading, setLoading] = useState(false);
    const [openSnackbar, setOpenSnackbar] = useState(false);

    const [title, setTitle] = useState("")
    const [description, setDescription] = useState("")
    const [requirements, setRequirements] = useState("")
    const [location, setLocation] = useState("")
    const [salary, setSalary] = useState(0)
    const [jobPostForm, setJobPostForm] = useState({
    })
    const [company, setCompany] = useState({
    })

    const [user, setUser] = useState({})
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
            request("get", `/company/user/${res.data.id}`, (res) => {
            setCompany(res.data)
        }).then();
        }).then();
    }, [])


    const handleInputChange = (event) => {
        setCompany({
            ...company,
            [event.target.name]: event.target.value,
        });
    };
    const sleep = (milliseconds) => {
        return new Promise(resolve => setTimeout(resolve, milliseconds));
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setLoading(true);
        await sleep(2000);
        company["user"] = user
        console.log(company)
        request("post", "/company", (res) => {
            return 0;
        }, (res) => {
            return 0;
        }, company).then();
        setLoading(false);
        setOpenSnackbar(true);
        await sleep(1500);
    };

    return (
        <>
            <Grid item xs={12} container spacing={4} style={{ backgroundColor: "#F7F7FF" }} display="flex" justifyContent="center" paddingTop={"50px"}>
                <Grid item xs={8} container spacing={2} style={{ backgroundColor: "white" }} display="flex" paddingTop={"50px"}>
                    <Grid item xs={12} display="flex" justifyContent="center">
                        <Typography variant="h3" >Your company</Typography>
                    </Grid>
                    <Grid item xs={12} display="flex" justifyContent="center">
                        <img src={company.companyLogoLink} alt="Logo"/>
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Company Name</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="" value={company.companyName} variant="filled" name="companyName" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Description</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="" value={company.about} variant="filled" name="about" multiline rows={4} onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Company Location</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="" value={company.companyLocation} variant="filled" name="companyLocation" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Country</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="" value={company.country} variant="filled" name="country" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Company Type</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="" value={company.companyType} variant="filled" name="companyType" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Company Size</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="" value={company.companySize} variant="filled" name="companySize" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">OT policy</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="" value={company.overtimePolicy} variant="filled" name="overtimePolicy" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Logo link</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="" value={company.companyLogoLink} variant="filled" name="companyLogoLink" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Working days</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="" value={company.workingDays} variant="filled" name="workingDays" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={24} />
                    <Grid item xs={24} />
                    <Grid container justifyContent="center" > {/* Add Grid container for centering */}
                        <Grid item >
                            <Button sx={{ backgroundColor: 'green', color: 'white' }} variant="contained" onClick={handleSubmit}>
                                Cập nhật thông tin công ty
                            </Button>
                        </Grid>
                    </Grid>
                    <Snackbar
                        open={openSnackbar}
                        autoHideDuration={3000} // Duration in milliseconds
                        onClose={() => setOpenSnackbar(false)}
                        message="Form submitted successfully!"
                    />
                    <Backdrop open={loading} sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}>
                        <CircularProgress color="inherit" />
                        <div>
                            Please wait a few seconds...
                        </div>
                    </Backdrop>
                </Grid>
            </Grid>
        </>
    )
}

export default UserCompany;