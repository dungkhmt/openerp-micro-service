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
import EducationCard from "components/EducationCard";

const UserEducation = () => {

    const [allEducation, setAllEducation] = useState([])
    const [user, setUser] = useState({})

    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
            request("get", `/education/user/${res.data.id}`, (res) => {
                setAllEducation(res.data)
            }).then();
        }).then();
    }, [])

    function goToUrl(url) {
        window.location.href = url;
    }

    return (
        <>
            <Typography variant="h4" component="div">
                Your Education List:
            </Typography>
            <Grid container spacing={4} style={{ backgroundColor: "#F7F7FF" }} display="flex" justifyContent="center" paddingTop={"50px"}>
                {allEducation.map((education, index) => (
                    <Grid item xs={6} style={{ boxShadow: '1px 1px 1px 1px rgba(0.5,0.5,0.5,0.5)', margin: '20px', borderRadius: '20px', backgroundColor: "white", display: 'flex', alignItems: 'stretch' }} >
                        <EducationCard education={education}></EducationCard>
                    </Grid>
                ))
                }
            </Grid>
        </>

    )
}

export default UserEducation;