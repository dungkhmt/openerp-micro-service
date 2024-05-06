import { useState, useEffect } from "react"
import { TextField, Button, Grid, Typography, IconButton, Box } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import CvTemplate1 from "./cv_template/CVTemplate1";
import {
    Form,
    Col,
    FormGroup,
    FormLabel,
    FormControl,
    Row,
} from "react-bootstrap";
import {request} from "../../api"
import { convertHtmlElementToPdfFile } from "../../utils/ConvertHTMLToFile/convertHtmlElementToPdfFile";
import Swal from "sweetalert2";

const CreateNewCvForm = () => {

    const [showCVDemo, setShowCVDemo] = useState(false)
    const [resumeTitle, setResumeTitle] = useState("")
    // experiences
    const [experiences, setExperiences] = useState([
        { workingPosition: '', companyName: '', startingTime: '', endingTime: '', responsibility: '' },
    ]);
    const [user, setUser] = useState({})
    useEffect(() => {
        request("get", "/user/get-user-data", (res) => {
            setUser(res.data)
          }).then();
    }, [])

    const handleAddExperience = () => {
        setExperiences([...experiences, { workingPosition: '', companyName: '', startingTime: '', endingTime: '', responsibility: '' }]);
    };

    const handleExperienceChange = (index, event) => {
        const newExperiences = [...experiences];
        newExperiences[index][event.target.name] = event.target.value;
        setExperiences(newExperiences);
    };

    const handleDeleteExperience = (index) => {
        const newExperiences = [...experiences];
        newExperiences.splice(index, 1);
        setExperiences(newExperiences);
    };
    // education
    const [educations, setEducations] = useState([
        { major: '', schoolName: '', startingTime: '', grade: 0, endingTime: '', description: '' },
    ]);

    const handleAddEducation = () => {
        setEducations([...educations, { major: '', schoolName: '', startingTime: '', endingTime: '', description: '' }]);
    };

    const handleEducationChange = (index, event) => {
        const newEducations = [...educations];
        newEducations[index][event.target.name] = event.target.value;
        setEducations(newEducations);
    };

    const handleDeleteEducation = (index) => {
        const newEducations = [...educations];
        newEducations.splice(index, 1);
        setEducations(newEducations);
    };
    // skill
    const [skills, setSkills] = useState([
        { skillName: '', certLink: '', score : 0},
    ]);

    const handleAddSkill = () => {
        setSkills([...skills, { skillName: '', certLink: '', score : 0 }]);
    };

    const handleSkillChange = (index, event) => {
        const newSkills = [...skills];
        newSkills[index][event.target.name] = event.target.value;
        newSkills[index].score = parseFloat(newSkills[index].score)
        setSkills(newSkills);
    };

    const handleDeleteSkill = (index) => {
        const newSkills = [...skills];
        newSkills.splice(index, 1);
        setSkills(newSkills);
    };
    // all user info
    const [userInfoForm, setUserInfoForm] = useState({
        userName: "Lê Minh Thiện",
        gender: "Nam",
        profession: "Cloud engineer",
        userLocation: "Ha Noi",
        cvLink: "fuck",
        mobilePhone: "01647997611",
        email: "xxx@gmail.com",
        description: "tôi yêu  gái đẹp",
        linkedInLink: "https://www.linkedin.com/checkpoint/login-submit?_l=en_US",
        githubLink: "javhd.pro",
        title: "hacker"
    })

    const handleInputChange = (event) => {
        setUserInfoForm({
            ...userInfoForm,
            [event.target.name]: event.target.value,
            
        });
        setSubmitForm({
            employeeCV: userInfoForm, experiences, educations, skills
        })
    };
    let [submitForm, setSubmitForm] = useState({})
    const handleSubmit = (e) => {
        e.preventDefault()
        setShowCVDemo(true)
        console.log(submitForm)
        // request("post", `/employee-cv`, (res)=> {
        //     console.log(res);
        //   }, (err)=>{
        //     console.log(err);
        //   }, submitForm).then();
    }

    // const handleGenerateResume = async (e) => {
    //     e.preventDefault();
    //     setUserCVInfo({
    //       name: userInfoForm.name,
    //       gender: userInfoForm.gender,
    //       profession: userInfoForm.profession,
    //       location: userInfoForm.location,
    //       mobilePhone: userInfoForm.mobilePhone,
    //       email: userInfoForm.email,
    //       profileDescription: userInfoForm.profileDescription,
    //       facebookLink: userInfoForm.facebookLink,
    //       linkedInLink: userInfoForm.linkedInLink,
    //       gitHubLink: userInfoForm.gitHubLink,
    //       education: userEducations,
    //       skill: userSkills,
    //       workingExperience: userWorkingExperiences,
    //       skillDescription: userInfoForm.skillDescription,
    //     });
    //     setShowCVDemo(true);
    //   };
    const uploadFile = async (file) => {
        const formData = new FormData()
        formData.append("file", file);
        await request("post", `/employee-cv/file-upload`, (res)=> {
            console.log(res);
            console.log("xnxx: ", submitForm)           
            submitForm.employeeCV.cvLink = res.data
            console.log("xnxx: ", submitForm)
            request("post", `/employee-cv`, (res)=> {
                console.log(res);
              }, (err)=>{
                console.log(err);
              }, submitForm).then();
          }, (err)=>{
            console.log(err);
          }, formData).then();
    }

    const createNewCv = async () => {
        const element = document.getElementById("fileToPrint");
        if (element) {
          // Convert the HTML element to canvas
          const file = await convertHtmlElementToPdfFile(element, userInfoForm.title);
          const uploadFileData = await uploadFile(file);
          if (uploadFileData == null) {
            return Swal.fire(
                      "Oops...",
                      `There is some thing wrong when upload your CV`,
                      "error"
                    );
                  } 
        }
      };
    return (
        <div>
            {!showCVDemo && (
                <Grid container spacing={3}>

                    <Grid item xs={12}>
                        <Typography variant="h4">Personal Information</Typography>
                    </Grid>
                    <Grid item xs={4}>
                        <TextField fullWidth label="Full Name" value={userInfoForm.userName} variant="outlined" name="userName" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={4}>
                        <TextField fullWidth label="Gender" value={userInfoForm.gender} variant="outlined" name="gender" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={4}>
                        <TextField fullWidth label="Mobile Phone" value={userInfoForm.mobilePhone} variant="outlined" name="mobilephone" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={4}>
                        <TextField fullWidth label="Email" value={userInfoForm.email} variant="outlined" name="mobile" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={4}>
                        <TextField fullWidth label="Profession" value={userInfoForm.profession} variant="outlined" name="profession" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={4}>
                        <TextField fullWidth label="User Location" value={userInfoForm.userLocation} variant="outlined" name="userLocation" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField fullWidth label="Describe Yourself" value={userInfoForm.description} variant="outlined" multiline rows={4} name="description" onChange={handleInputChange} />
                    </Grid>

                    <Grid item xs={12}>
                        <Typography variant="h4">Social Detail</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <TextField fullWidth value={userInfoForm.githubLink} label="Github Link" variant="outlined" />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField fullWidth value={userInfoForm.linkedInLink} label="LinkedIn Link" variant="outlined" />
                    </Grid>

                    <Grid item xs={12}>
                        <Typography variant="h4">Working Experiences</Typography>
                        {experiences.map((experience, index) => (
                            <Box key={index} xs={6} sx={{ display: 'flex', flexDirection: 'column', mb: 2 }}>
                                <Grid container spacing={3}>
                                    <Grid item xs={6}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="Position"
                                            name="workingPosition"
                                            value={experience.workingPosition}
                                            onChange={(event) => handleExperienceChange(index, event)}

                                        />
                                    </Grid>
                                    <Grid item xs={6}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="Company Name"
                                            name="companyName"
                                            value={experience.companyName}
                                            onChange={(event) => handleExperienceChange(index, event)}
                                            sx={{ mb: 1 }}
                                        />
                                    </Grid>
                                    <Grid item xs={4}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="Starting Date"
                                            name="startingTime"
                                            type="date"
                                            InputLabelProps={{ shrink: true }}
                                            value={experience.startingTime}
                                            onChange={(event) => handleExperienceChange(index, event)}
                                            sx={{ mb: 1 }}
                                        />
                                    </Grid>
                                    <Grid item xs={4}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="Ending Date"
                                            name="endingTime"
                                            type="date"
                                            InputLabelProps={{ shrink: true }}
                                            value={experience.endingTime}
                                            onChange={(event) => handleExperienceChange(index, event)}
                                            sx={{ mb: 1 }}
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="Describe your responsibility"
                                            name="responsibility"
                                            multiline
                                            rows={4}
                                            value={experience.responsibility}
                                            onChange={(event) => handleExperienceChange(index, event)}
                                        />
                                    </Grid>
                                    <Grid container justifyContent="right"> {/* Add Grid container for centering */}
                                        <Grid item>
                                            <IconButton onClick={() => handleDeleteExperience(index)} aria-label="delete">
                                                <DeleteIcon />
                                            </IconButton>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Box>
                        ))}
                        <Grid container justifyContent="center"> {/* Add Grid container for centering */}
                            <Grid item>
                                <Button variant="contained" onClick={handleAddExperience}>
                                    Add another working experience
                                </Button>
                            </Grid>
                        </Grid>
                    </Grid>

                    <Grid item xs={12}>
                        <Typography variant="h4">Educational Details</Typography>
                        {educations.map((education, index) => (
                            <Box key={index} xs={12} sx={{ display: 'flex', flexDirection: 'column', mb: 2 }}>
                                <Grid container spacing={3}>
                                    <Grid item xs={6}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="major"
                                            name="major"
                                            value={education.position}
                                            onChange={(event) => handleEducationChange(index, event)}
                                            sx={{ mb: 1 }}
                                        />
                                    </Grid>
                                    <Grid item xs={6}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="school Name"
                                            name="schoolName"
                                            value={education.schoolName}
                                            onChange={(event) => handleEducationChange(index, event)}
                                            sx={{ mb: 1 }}
                                        />
                                    </Grid>
                                    <Grid item xs={6}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="Grade"
                                            name="grade"
                                            value={education.grade}
                                            onChange={(event) => handleEducationChange(index, event)}
                                            sx={{ mb: 1 }}
                                        />
                                    </Grid>
                                    <Grid item xs={4}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="Starting Date"
                                            name="startingTime"
                                            type="date"
                                            InputLabelProps={{ shrink: true }}
                                            value={education.startingTime}
                                            onChange={(event) => handleEducationChange(index, event)}
                                            sx={{ mb: 1 }}
                                        />
                                    </Grid>
                                    <Grid item xs={4}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="Ending Date"
                                            name="endingTime"
                                            type="date"
                                            InputLabelProps={{ shrink: true }}
                                            value={education.endingTime}
                                            onChange={(event) => handleEducationChange(index, event)}
                                            sx={{ mb: 1 }}
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="Describe your oppotunity"
                                            name="description"
                                            multiline
                                            rows={4}
                                            value={education.description}
                                            onChange={(event) => handleEducationChange(index, event)}
                                        />
                                    </Grid>
                                    <Grid container justifyContent="right">
                                        <Grid item>
                                            <IconButton onClick={() => handleDeleteEducation(index)} aria-label="delete">
                                                <DeleteIcon />
                                            </IconButton>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Box>
                        ))}
                        <Grid container justifyContent="center"> {/* Add Grid container for centering */}
                            <Grid item>
                                <Button variant="contained" onClick={handleAddEducation}>
                                    Add another education
                                </Button>
                            </Grid>
                        </Grid>
                    </Grid>

                    <Grid item xs={12}>
                        <Typography variant="h4">Skill Details</Typography>
                        {skills.map((skill, index) => (
                            <Box key={index} xs={12} sx={{ display: 'flex', flexDirection: 'column', mb: 2 }}>
                                <TextField
                                    fullWidth variant="outlined"
                                    label="score"
                                    name="score"
                                    value={skill.score}
                                    onChange={(event) => handleSkillChange(index, event)}
                                    sx={{ mb: 1 }}
                                />
                                <TextField
                                    fullWidth variant="outlined"
                                    label="skillName"
                                    name="skillName"
                                    value={skill.skillName}
                                    onChange={(event) => handleSkillChange(index, event)}
                                    sx={{ mb: 1 }}
                                />
                                <TextField
                                    fullWidth variant="outlined"
                                    label="link to cert"
                                    name="certLink"
                                    value={skill.certLink}
                                    onChange={(event) => handleSkillChange(index, event)}
                                    sx={{ mb: 1 }}
                                />
                                <Grid container justifyContent="right"> {/* Add Grid container for centering */}
                                    <Grid item>
                                        <IconButton onClick={() => handleDeleteSkill(index)} aria-label="delete">
                                            <DeleteIcon />
                                        </IconButton>
                                    </Grid>
                                </Grid>
                            </Box>
                        ))}
                        <Grid container justifyContent="center"> {/* Add Grid container for centering */}
                            <Grid item>
                                <Button variant="contained" onClick={handleAddSkill}>
                                    Add another skill
                                </Button>
                            </Grid>
                        </Grid>
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="h4">Resume Title</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <TextField fullWidth label="enter your job title" value={userInfoForm.title} variant="outlined"  name="title" onChange={handleInputChange} />
                    </Grid>
                    {/* spacing */}
                    <Grid item xs={24} />
                    <Grid item xs={24} />
                    <Grid container justifyContent="center" > {/* Add Grid container for centering */}
                        <Grid item >
                            <Button sx={{ backgroundColor: 'green', color: 'white' }}  variant="contained" onClick={handleSubmit}>
                                Submit CV
                            </Button>
                        </Grid>
                    </Grid>
                </Grid>
            )}

            {/* cv template */}
            {showCVDemo && (
                <div className="w-100">
                    <CvTemplate1
                        name={userInfoForm.userName}
                        gender={userInfoForm.gender}
                        profession={userInfoForm.profession}
                        location={userInfoForm.userLocation}
                        mobilePhone={userInfoForm.mobilePhone}
                        email={userInfoForm.email}
                        description={userInfoForm.responsibility}
                        linkedInLink={userInfoForm.linkedInLink}
                        gitHubLink={userInfoForm.githubLink}
                        education={educations}
                        skill={skills}
                        workingExperience={experiences}
                        handleBackToEditButton={() => setShowCVDemo(false)}
                        handleSaveCVButton={() => createNewCv()}
                    />
                </div>
            )}
        </div>
    )

}

export default CreateNewCvForm;