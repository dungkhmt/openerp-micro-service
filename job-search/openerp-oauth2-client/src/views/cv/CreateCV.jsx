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

const CreateNewCvForm = () => {

    const [showCVDemo, setShowCVDemo] = useState(false)
    const [resumeTitle, setResumeTitle] = useState("")
    // experiences
    const [experiences, setExperiences] = useState([
        { position: '', companyName: '', startingDate: '', endingDate: '', description: '' },
    ]);

    const handleAddExperience = () => {
        setExperiences([...experiences, { position: '', companyName: '', startingDate: '', endingDate: '', description: '' }]);
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
        { major: '', schoolName: '', startingDate: '', grade: 0, endingDate: '', description: '' },
    ]);

    const handleAddEducation = () => {
        setEducations([...educations, { major: '', schoolName: '', startingDate: '', endingDate: '', description: '' }]);
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
        { skill: '', linkCert: '' },
    ]);

    const handleAddSkill = () => {
        setSkills([...skills, { skill: '', linkCert: '' }]);
    };

    const handleSkillChange = (index, event) => {
        const newSkills = [...skills];
        newSkills[index][event.target.name] = event.target.value;
        setSkills(newSkills);
    };

    const handleDeleteSkill = (index) => {
        const newSkills = [...skills];
        newSkills.splice(index, 1);
        setSkills(newSkills);
    };
    // all user info
    const [userInfoForm, setUserInfoForm] = useState({
        name: "Lê Minh Thiện",
        gender: "Nam",
        profession: "Cloud engineer",
        location: "Ha Noi",
        mobilePhone: "01647997611",
        email: "xxx@gmail.com",
        profileDescription: "tôi yêu  gái đẹp",
        linkedInLink: "https://www.linkedin.com/checkpoint/login-submit?_l=en_US",
        githubLink: "javhd.pro",
        experiences: experiences,
        educations: educations,
        skills: skills
    })

    const handleInputChange = (event) => {
        setUserInfoForm({
            ...userInfoForm,
            [event.target.name]: event.target.value,
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault()
        setUserInfoForm({
            ...userInfoForm, experiences, educations, skills
        })
        setShowCVDemo(true)
        const newJsonData = { ...userInfoForm, experiences, educations, skills };
        console.log(newJsonData)
        // Do something with newJsonData (e.g., send to server, store locally)
    };

    // const createNewCv = async () => {
    //     const element = document.getElementById("fileToPrint");
    //     if (element) {
    //       // Convert the HTML element to canvas
    //       const file = await convertHtmlElementToPdfFile(element, cvTitle);
    //       const uploadFileData = await uploadFile(file);
    //       console.log(uploadFileData);
    //       if (isErrorHttpResponse(uploadFileData)) {
    //         return Swal.fire(
    //           "Oops...",
    //           `There is some thing wrong when upload your CV ${uploadFileData.message.join(
    //             ", "
    //           )}`,
    //           "error"
    //         );
    //       } else {
    //         const createResumeData = uploadResume({
    //           title: resumeTitle,
    //           link: uploadFileData.data,
    //           employeeId: employee?.id || "",
    //           name: userInfoForm.name,
    //           gender: userInfoForm.gender,
    //           profession: userInfoForm.profession,
    //           location: userInfoForm.location,
    //           mobilePhone: userInfoForm.mobilePhone,
    //           email: userInfoForm.email,
    //           profileDescription: userInfoForm.profileDescription,
    //           facebookLink: userInfoForm.facebookLink,
    //           linkedInLink: userInfoForm.linkedInLink,
    //           gitHubLink: userInfoForm.gitHubLink,
    //           education: userEducations,
    //           skill: userSkills,
    //           workingExperience: userWorkingExperiences,
    //           skillDescription: userInfoForm.skillDescription,
    //           method: EResumeMethod.CREATE,
    //         });
    //         if (isErrorHttpResponse(createResumeData)) {
    //           return Swal.fire(
    //             "Oops...",
    //             "There is some thing wrong when upload your CV",
    //             "error"
    //           );
    //         } else {
    //           notify("Create CV successfully", () =>
    //             navigate(`/employee/profile/${employee?.userId}`)
    //           );
    //         }
    //       }
    //     }
    //   };
    return (
        <div>
            {!showCVDemo && (
                <Grid container spacing={3}>

                    <Grid item xs={12}>
                        <Typography variant="h4">Personal Information</Typography>
                    </Grid>
                    <Grid item xs={4}>
                        <TextField fullWidth label="Full Name" value={userInfoForm.name} variant="outlined" name="name" onChange={handleInputChange} />
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
                        <TextField fullWidth label="Location" value={userInfoForm.location} variant="outlined" name="location" onChange={handleInputChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField fullWidth label="Describe Yourself" value={userInfoForm.profileDescription} variant="outlined" multiline rows={4} name="profileDescription" onChange={handleInputChange} />
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
                                            name="position"
                                            value={experience.position}
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
                                            name="startingDate"
                                            type="date"
                                            InputLabelProps={{ shrink: true }}
                                            value={experience.startingDate}
                                            onChange={(event) => handleExperienceChange(index, event)}
                                            sx={{ mb: 1 }}
                                        />
                                    </Grid>
                                    <Grid item xs={4}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="Ending Date"
                                            name="endingDate"
                                            type="date"
                                            InputLabelProps={{ shrink: true }}
                                            value={experience.endingDate}
                                            onChange={(event) => handleExperienceChange(index, event)}
                                            sx={{ mb: 1 }}
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="Describe your responsibility"
                                            name="description"
                                            multiline
                                            rows={4}
                                            value={experience.description}
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
                                            name="startingDate"
                                            type="date"
                                            InputLabelProps={{ shrink: true }}
                                            value={education.startingDate}
                                            onChange={(event) => handleEducationChange(index, event)}
                                            sx={{ mb: 1 }}
                                        />
                                    </Grid>
                                    <Grid item xs={4}>
                                        <TextField
                                            fullWidth variant="outlined"
                                            label="Ending Date"
                                            name="endingDate"
                                            type="date"
                                            InputLabelProps={{ shrink: true }}
                                            value={education.endingDate}
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
                                    label="skill"
                                    name="skill"
                                    value={skill.position}
                                    onChange={(event) => handleSkillChange(index, event)}
                                    sx={{ mb: 1 }}
                                />

                                <TextField
                                    fullWidth variant="outlined"
                                    label="link to cert"
                                    name="linkCert"
                                    value={skill.companyName}
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
                        <TextField fullWidth label="enter your job title" variant="outlined" />
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
                        name={userInfoForm.name}
                        gender={userInfoForm.gender}
                        profession={userInfoForm.profession}
                        location={userInfoForm.location}
                        mobilePhone={userInfoForm.mobilePhone}
                        email={userInfoForm.email}
                        profileDescription={userInfoForm.profileDescription}
                        linkedInLink={userInfoForm.linkedInLink}
                        gitHubLink={userInfoForm.githubLink}
                        education={userInfoForm.educations}
                        skill={userInfoForm.skills}
                        workingExperience={userInfoForm.experiences}
                        handleBackToEditButton={() => setShowCVDemo(false)}
                    // handleSaveCVButton={() => createNewCv()}
                    />
                </div>
            )}
        </div>
    )

}

export default CreateNewCvForm;