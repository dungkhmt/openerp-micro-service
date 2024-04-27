import { useState, useEffect } from "react"
import { TextField, Button, Grid, Typography, IconButton, Box, InputLabel, Select } from '@mui/material';
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
import Autocomplete from '@mui/material/Autocomplete';
// import { Select, MenuItem, InputLabel } from '@material-ui/core';
import Dialog from '@mui/material/Dialog';
import { useHookstate } from '@hookstate/core';
import fetchUserState from "state/userState";
import { CircularProgress, Snackbar } from '@mui/material';
import Backdrop from '@mui/material/Backdrop';

const SkillDetail = ({ Skill,  open, onClose }) => {
    const [loading, setLoading] = useState(false);
    const [openSnackbar, setOpenSnackbar] = useState(false);

    const [allCV, setAllCV] = useState([])
    const [selectedCVName, setSelectedCVName] = useState()
    const [skill, setSkill] = useState({})
    const [user, setUser] = useState({})
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
        }).then();
    }, [])

    const handleClose = () => {
        onClose();
    };

    const [submitForm, setSubmitForm] = useState({})

    useEffect(() => {
        request("get", "/employee-cv/user/dungpq", (res) => {
            setAllCV(res.data)
        }).then();
    }, [])

    const sleep = (milliseconds) => {
        return new Promise(resolve => setTimeout(resolve, milliseconds));
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setLoading(true);
        await sleep(2000);
        let submitToServerForm = skill
        let data = fetchUserState()

        console.log(submitToServerForm)
        console.log(user)
        request("post", `/cv-application/user/dungpq}`, (res) => {
            console.log(res);
        }, (err) => {
            console.log(err);
        }, submitToServerForm).then();
        setLoading(false);
        setOpenSnackbar(true);
        await sleep(1500);
        handleClose()
    }

    const handleInputChange = (event) => {
        setSkill({
            ...skill,
            [event.target.name]: event.target.value,
        });
    };
    function goToUrl(url) {
        window.location.href = url;
    }
    let allCVArray = allCV.map(e => { return { title: e.employeeCV.title, id: e.employeeCV.id } })
    return (<>
        <div>
            <Dialog onClose={handleClose} open={open} maxWidth="md" fullWidth={true} sx={{ maxHeight: 'lg' }} fullHeight={true}>
                <h1 fullHeight>Your Skill: </h1>
                <form noValidate autoComplete="off">
                    <Grid item xs={12} container spacing={2}  display="flex" justifyContent="center" paddingTop={"50px"}>
                        <Grid item xs={11} container  style={{ backgroundColor: "white" }}  paddingLeft={"50px"}>
                            <Typography>Skill name</Typography>
                        </Grid>
                        <Grid item xs={11}>
                            <TextField fullWidth label="Skill name" value={Skill.skillName} variant="outlined" name="skillName" onChange={handleInputChange} />
                        </Grid>
                        <Grid item xs={11} container  style={{ backgroundColor: "white" }}  paddingLeft={"50px"}>
                            <Typography >Cert link</Typography>
                        </Grid>
                        <Grid item xs={11}>
                            <TextField fullWidth label="Cert link" value={Skill.certLink} variant="outlined" name="certLink" onChange={handleInputChange} />
                        </Grid>
                        <Grid item xs={11} container  style={{ backgroundColor: "white" }} display="flex" paddingTop={"50px"}>
                            <Typography >Score</Typography>
                        </Grid>
                        <Grid item xs={11}>
                            <TextField fullWidth label="Skill score" value={Skill.score} variant="outlined" name="score" onChange={handleInputChange} />
                        </Grid>
                        <Snackbar
                            open={openSnackbar}
                            autoHideDuration={3000} // Duration in milliseconds
                            onClose={() => setOpenSnackbar(false)}
                            message="Form submitted successfully!"
                        />

                        {/* This button should submit the form */}
                        <Box display="flex" justifyContent="center" paddingTop={'50px'}>
                            <Button type="submit" variant="contained" color="primary" onClick={handleSubmit} disabled={loading} sx={{ marginBottom: '16px' }}>
                                {'Submit'}
                            </Button>
                        </Box>
                        <Backdrop open={loading} sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}>
                            <CircularProgress color="inherit" />
                            <div>
                                Please wait a few seconds...
                            </div>
                        </Backdrop>
                    </Grid>
                </form>
            </Dialog>
        </div>
    </>
    )
}


export { SkillDetail };