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
import { request } from "../../api"
import { CircularProgress, Snackbar } from '@mui/material';
import Backdrop from '@mui/material/Backdrop';
import Swal from "sweetalert2";
import './styles.css';
import { Select, MenuItem, Checkbox, ListItemText, InputLabel } from '@mui/material';

const CreateJobPost = () => {
    const [loading, setLoading] = useState(false);
    const [openSnackbar, setOpenSnackbar] = useState(false);

    const [title, setTitle] = useState("")
    const [description, setDescription] = useState("")
    const [requirements, setRequirements] = useState("")
    const [locations, setLocations] = useState("")
    const [salary, setSalary] = useState(0)
    const [jobPostForm, setJobPostForm] = useState({
    })
    const [user, setUser] = useState({})
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
        }).then();
    }, [])
    const handleInputChange = (event) => {
        setJobPostForm({
            ...jobPostForm,
            [event.target.name]: event.target.value,
        });
    };
    const sleep = (milliseconds) => {
        return new Promise(resolve => setTimeout(resolve, milliseconds));
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        console.log(selectedrequirements)
        // Simulate a delay with setTimeout

        jobPostForm.salary = Number(jobPostForm.salary);
        jobPostForm.user = user
        jobPostForm.requirements = selectedrequirements.join(", ")
        console.log(jobPostForm)
        // Use SweetAlert2 for confirmation before submitting
        Swal.fire({
            title: 'Uploading...',
            text: 'Please wait while the file is being uploaded.',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });
        try {
            const res = await request("post", "/job-post", null, null, jobPostForm)
            // Show a success Swal if the form is submitted
            Swal.fire({
                title: 'Submitted!',
                text: 'Your information has been submitted.',
                icon: 'success',
                confirmButtonText: 'OK'
            });
        } catch (error) {
            console.error(error);
            // Show an error Swal if there's an issue with the upload or form submission
            Swal.fire({
                title: 'Error!',
                text: 'There was an issue with the upload or submission.',
                icon: 'error',
                confirmButtonText: 'OK'
            });
        }
    };

    const requirement = [
        "Agile",
        "Android",
        "Angular",
        "AngularJS",
        "ASP.NET",
        "Automation Test",
        "AWS",
        "Azure",
        "Blockchain",
        "Bridge Engineer",
        "Business Analyst",
        "Business Intelligence",
        "C#",
        "C++",
        "C language",
        "Cloud",
        "Cocos",
        "Crystal",
        "CSS",
        "Dart",
        "Data Analyst",
        "Database",
        "Designer",
        "DevOps",
        "DevSecOps",
        "Django",
        "Embedded",
        "Embedded C",
        "English",
        "ERP",
        "Flutter",
        "Games",
        "Golang",
        "HTML5",
        "iOS",
        "IT Support",
        "J2EE",
        "Japanese",
        "Java",
        "JavaScript",
        "JQuery",
        "JSON",
        "Kotlin",
        "Laravel",
        "Linux",
        "Magento",
        "Manager",
        "MongoDB",
        "MVC",
        "MySQL",
        ".NET",
        "Networking",
        "NodeJS",
        "NoSQL",
        "Objective C",
        "OOP",
        "Oracle",
        "PHP",
        "PostgreSql",
        "Presale",
        "Product Designer",
        "Product Manager",
        "Product Owner",
        "Project Manager",
        "Python",
        "QA QC",
        "ReactJS",
        "React Native",
        "Ruby",
        "Ruby on Rails",
        "Salesforce",
        "SAP",
        "Scala",
        "Scrum",
        "Security",
        "Sharepoint",
        "Software Architect",
        "Solidity",
        "Solution Architect",
        "Spring",
        "SQL",
        "Swift",
        "System Admin",
        "System Engineer",
        "Team Leader",
        "Tester",
        "TypeScript",
        "UI-UX",
        "Unity",
        "VueJS",
        "Wordpress",
        "Xamarin"
    ];
    const [selectedrequirements, setSelectedrequirements] = useState([]);
    const handleRequirementsChange = (event) => {
        setSelectedrequirements(event.target.value);
    };

    return (
        <>
            <Grid item xs={12} container spacing={4} style={{ backgroundColor: "#F7F7FF" }} display="flex" justifyContent="center" paddingTop={"50px"}>
                <Grid item xs={8} container spacing={2} style={{ backgroundColor: "white" }} display="flex" paddingTop={"50px"}>
                    <Grid item xs={12}>
                        <Typography variant="h3" marginLeft="20%">Create your Job Post</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Job title</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="Job Title" value={jobPostForm.title} variant="outlined" name="title" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Description</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="description" value={jobPostForm.description} variant="outlined" name="description" multiline rows={4} onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Requirements</Typography>
                    </Grid>
                    <Grid item xs={11}>
                    <InputLabel id="multi-select-label">Select required skill</InputLabel>
                        <Select
                            labelId="multi-select-label"
                            multiple
                            value={selectedrequirements}
                            onChange={handleRequirementsChange}
                            renderValue={(selected) => selected.join(', ')}
                            fullWidth
                        >
                            {requirement.map((option) => (
                                <MenuItem key={option} value={option}>
                                    <Checkbox checked={selectedrequirements.indexOf(option) > -1} />
                                    <ListItemText primary={option} />
                                </MenuItem>
                            ))}
                        </Select>
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Location</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="locations" value={jobPostForm.locations} variant="outlined" name="locations" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Salary</Typography>
                    </Grid>
                    <Grid item xs={11}>
                        <TextField fullWidth label="salary" value={jobPostForm.salary} variant="outlined" name="salary" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={24} />
                    <Grid item xs={24} />
                    <Grid container justifyContent="center" > {/* Add Grid container for centering */}
                        <Grid item >
                            <Button sx={{ backgroundColor: 'green', color: 'white' }} variant="contained" onClick={handleSubmit}>
                                Submit CV
                            </Button>
                        </Grid>
                    </Grid>
                    {/* <Snackbar
                        open={openSnackbar}
                        autoHideDuration={3000} // Duration in milliseconds
                        onClose={() => setOpenSnackbar(false)}
                        message="Form submitted successfully!"
                    /> */}
                    {/* <Backdrop open={loading} sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}>
                        <CircularProgress color="inherit" />
                        <div>
                            Please wait a few seconds...
                        </div>
                    </Backdrop> */}
                </Grid>
            </Grid>
        </>
    )
}

export default CreateJobPost;