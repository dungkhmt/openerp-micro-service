import { useState, useEffect } from "react"
import { TextField, Button, Grid, Typography, IconButton, Box } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import {request} from "../../api"

const UserSkill = () => {

    let [skills, setSkills] = useState([
        { skillName: '', certLink: '', score : 0 },
    ]);
    const handleAddSkill = () => {
        setSkills([...skills, { skillName: '', certLink: '', score: 0 }]);
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
    
    const handleSubmit = (e) => {
        e.preventDefault()
        setSkills([
            ...skills
        ])

        console.log(skills)
        request("post", "/skill",(res) => {
            return 0;
        }, (res) => {
            return 0;
        }, skills[0]).then();
    };

return (
    <Grid item xs={12}>
        <Typography variant="h4">Skill Details</Typography>
        {skills.map((skill, index) => (
            <Box key={index} xs={12} sx={{ display: 'flex', flexDirection: 'column', mb: 2 }}>
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
                    label="score"
                    name="score"
                    value={skill.score}
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
        <Grid container justifyContent="center" > {/* Add Grid container for centering */}
                        <Grid item >
                            <Button sx={{ backgroundColor: 'green', color: 'white' }}  variant="contained" onClick={handleSubmit}>
                                Submit CV
                            </Button>
        </Grid>
        </Grid>
    </Grid>
    )
}

export default UserSkill;