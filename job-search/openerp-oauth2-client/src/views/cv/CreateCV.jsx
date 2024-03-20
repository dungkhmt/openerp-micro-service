import {useState, useEffect} from "react"
import { TextField, Button, Grid, Typography, IconButton, Box  } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';

const CreateNewCvForm = () => {

    const [resumeTitle, setResumeTitle] = useState("")
// experiences
    const [experiences, setExperiences] = useState([
        { position: '', companyName: '', startDate: '', endingDate: '', description: '' },
      ]);
    
    const handleAddExperience = () => {
    setExperiences([...experiences, { position: '', companyName: '', startDate: '', endingDate: '', description: '' }]);
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
        { major: '', schoolName: '', startDate: '', endingDate: '', description: '' },
    ]);

    const handleAddEducation = () => {
        setEducations([...educations, { major: '', schoolName: '', startDate: '', endingDate: '', description: '' }]);
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
        setSkills([...skills,  { skill: '', linkCert: '' }]);
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
        name: "",
        gender: "",
        profression: "IT",
        location: "Ha Noi",
        mobilePhone: "",
        email: "",
        profileDescription: "",
        linkedInLink: "",
        githubLink: "",
        experiences: experiences,
        educations: educations,
        skills: skills
    })

    const handleInputChange = (event) => {
        setUserInfoForm({
          ...userInfoForm,
          [event.target.name]: event.target.value,
        });
        setUserInfoForm({
            ...userInfoForm, experiences, educations, skills
        })
      };

    const handleSubmit = () => {
        const newJsonData = { ...userInfoForm, experiences, educations, skills };
        console.log(newJsonData)
        // Do something with newJsonData (e.g., send to server, store locally)
    };
      
    return (
        <div>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Typography variant="h4">Personal Information</Typography>
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth label="Full Name" variant="outlined" name="name" onChange={handleInputChange} />
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth label="Gender" variant="outlined"  name="gender" onChange={handleInputChange}/>
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth label="Mobile Phone" variant="outlined"  name="mobilephone" onChange={handleInputChange} />
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth label="Email" variant="outlined"  name="mobile" onChange={handleInputChange}/>
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth label="Profession" variant="outlined"  name="profession" onChange={handleInputChange} />
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth label="Location" variant="outlined"  name="location" onChange={handleInputChange}/>
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth label="Describe Yourself" variant="outlined" multiline rows={4} name="profileDescription" onChange={handleInputChange}/>
                </Grid>

                <Grid item xs={12}>
                    <Typography variant="h4">Social Detail</Typography>
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth label="Github Link" variant="outlined" />
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth label="LinkedIn Link" variant="outlined" />
                </Grid>

                <Grid item xs={12}>
                    <Typography variant="h4">Working Experiences</Typography>
                    {experiences.map((experience, index) => (
                        <Box key={index} xs = {12}  sx={{ display: 'flex', flexDirection: 'column', mb: 2 }}>
                        <TextField
                            label="Position"
                            name="position"
                            value={experience.position}
                            onChange={(event) => handleExperienceChange(index, event)}
                            sx={{ mb: 1 }}
                        />
                        <TextField
                            label="Company Name"
                            name="companyName"
                            value={experience.companyName}
                            onChange={(event) => handleExperienceChange(index, event)}
                            sx={{ mb: 1 }}
                        />
                        <TextField
                            label="Starting Date"
                            name="startDate"
                            type="date"
                            InputLabelProps={{ shrink: true }}
                            value={experience.startDate}
                            onChange={(event) => handleExperienceChange(index, event)}
                            sx={{ mb: 1 }}
                        />
                        <TextField
                            label="Ending Date"
                            name="endingDate"
                            type="date"
                            InputLabelProps={{ shrink: true }}
                            value={experience.endingDate}
                            onChange={(event) => handleExperienceChange(index, event)}
                            sx={{ mb: 1 }}
                        />
                        <TextField
                            label="Describe your responsibility"
                            name="description"
                            multiline
                            rows={4}
                            value={experience.description}
                            onChange={(event) => handleExperienceChange(index, event)}
                        />
                        <IconButton onClick={() => handleDeleteExperience(index)} aria-label="delete">
                            <DeleteIcon />
                        </IconButton>
                        </Box>
                    ))}
                    <Button variant="contained" onClick={handleAddExperience}>
                        Add another working experience
                    </Button>
                </Grid>

                <Grid item xs={12}>
                    <Typography variant="h4">Educational Details</Typography>
                    {educations.map((education, index) => (
                        <Box key={index} xs = {12}  sx={{ display: 'flex', flexDirection: 'column', mb: 2 }}>
                        <TextField
                            label="major"
                            name="major"
                            value={education.position}
                            onChange={(event) => handleEducationChange(index, event)}
                            sx={{ mb: 1 }}
                        />
                        <TextField
                            label="school Name"
                            name="schoolName"
                            value={education.companyName}
                            onChange={(event) => handleEducationChange(index, event)}
                            sx={{ mb: 1 }}
                        />
                        <TextField
                            label="Starting Date"
                            name="startDate"
                            type="date"
                            InputLabelProps={{ shrink: true }}
                            value={education.startDate}
                            onChange={(event) => handleEducationChange(index, event)}
                            sx={{ mb: 1 }}
                        />
                        <TextField
                            label="Ending Date"
                            name="endingDate"
                            type="date"
                            InputLabelProps={{ shrink: true }}
                            value={education.endingDate}
                            onChange={(event) => handleAddEducation(index, event)}
                            sx={{ mb: 1 }}
                        />
                        <TextField
                            label="Describe your oppotunity"
                            name="description"
                            multiline
                            rows={4}
                            value={education.description}
                            onChange={(event) => handleEducationChange(index, event)}
                        />
                        <IconButton onClick={() => handleDeleteEducation(index)} aria-label="delete">
                            <DeleteIcon />
                        </IconButton>
                        </Box>
                    ))}
                    <Button variant="contained" onClick={handleAddEducation}>
                        Add another education
                    </Button>
                </Grid>

                <Grid item xs={12}>
                    <Typography variant="h4">Skill Details</Typography>
                    {skills.map((skill, index) => (
                        <Box key={index} xs = {12}  sx={{ display: 'flex', flexDirection: 'column', mb: 2 }}>
                        <TextField
                            label="skill"
                            name="skill"
                            value={skill.position}
                            onChange={(event) => handleSkillChange(index, event)}
                            sx={{ mb: 1 }}
                        />
                        <TextField
                            label="link to cert"
                            name="linkCert"
                            value={skill.companyName}
                            onChange={(event) => handleSkillChange(index, event)}
                            sx={{ mb: 1 }}
                        />
                        <IconButton onClick={() => handleDeleteSkill(index)} aria-label="delete">
                            <DeleteIcon />
                        </IconButton>
                        </Box>
                    ))}
                    <Button variant="contained" onClick={handleAddSkill}>
                        Add another skill
                    </Button>
                </Grid>
                <Grid item xs={12}>
                    <Typography variant="h4">Resume Title</Typography>
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth label="enter your job title" variant="outlined"   />
                </Grid>
            </Grid>
            <Button variant="contained" onClick={handleSubmit}>
            Submit
            </Button>
        </div>
    )

}

export default CreateNewCvForm;