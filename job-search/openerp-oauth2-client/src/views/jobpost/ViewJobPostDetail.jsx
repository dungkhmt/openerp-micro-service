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
import { Card, CardContent } from '@mui/material';

const ViewJobPost = (props) => {

    const [JobPostForm, setJobPostForm] = useState([])

    return (
        <>
            <Card  sx={{ minWidth: 275 }}>
                <CardContent>
                    <Typography variant="h4" component="div">
                        your dream job here
                    </Typography>
                    <Typography variant="body2">
                        <strong>Job title</strong>: {props.title}
                    </Typography>
                    <Typography variant="body2">
                        <strong>Job location</strong>: {props.locations}
                    </Typography>
                    <Typography variant="body2">
                        <strong>Job salary</strong>: {props.salary}
                    </Typography>
                </CardContent>
            </Card>  
        </>

    )
}

export default ViewJobPost;