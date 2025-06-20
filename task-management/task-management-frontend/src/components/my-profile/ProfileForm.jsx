import { useForm, Controller } from "react-hook-form";
import {
  Card,
  CardHeader,
  CardContent,
  CardActions,
  Divider,
  Grid,
  FormControl,
  InputLabel,
  OutlinedInput,
  Select,
  MenuItem,
  Checkbox,
  ListItemText,
  Button,
} from "@mui/material";
import { SkillChip } from "../../components/task/skill";
import PropTypes from "prop-types";

const ProfileForm = ({
  user,
  skills,
  selectedSkills,
  onSkillsChange,
  onSave,
}) => {
  const { handleSubmit, control } = useForm();

  return (
    <Card sx={{ borderRadius: "20px" }}>
      <CardHeader subheader="Skills can be edited" title="Profile" />
      <Divider />
      <CardContent>
        <Grid container spacing={3}>
          <Grid item md={6} xs={12}>
            <FormControl fullWidth>
              <InputLabel>First name</InputLabel>
              <OutlinedInput
                defaultValue={user?.firstName}
                label="First name"
                name="firstName"
                readOnly
              />
            </FormControl>
          </Grid>
          <Grid item md={6} xs={12}>
            <FormControl fullWidth>
              <InputLabel>Last name</InputLabel>
              <OutlinedInput
                defaultValue={user?.lastName}
                label="Last name"
                name="lastName"
                readOnly
              />
            </FormControl>
          </Grid>
          <Grid item xs={12}>
            <FormControl fullWidth>
              <InputLabel>Email</InputLabel>
              <OutlinedInput
                defaultValue={user?.email}
                label="Email"
                name="email"
                readOnly
              />
            </FormControl>
          </Grid>
          <Grid item xs={12}>
            <Controller
              name="skillList"
              control={control}
              render={({ field }) => (
                <FormControl fullWidth>
                  <InputLabel id="skill-select">Skills</InputLabel>
                  <Select
                    {...field}
                    id="select-skill"
                    label="Skills"
                    labelId="skill-select"
                    multiple
                    value={selectedSkills}
                    onChange={onSkillsChange}
                    renderValue={(selected) => (
                      <div
                        style={{
                          display: "flex",
                          flexWrap: "wrap",
                          gap: "5px",
                        }}
                      >
                        {selected.map((value) => (
                          <SkillChip key={value.skillId} skill={value} />
                        ))}
                      </div>
                    )}
                    MenuProps={{
                      anchorOrigin: {
                        vertical: "top",
                        horizontal: "left",
                      },
                      transformOrigin: {
                        vertical: "bottom",
                        horizontal: "left",
                      },
                      getContentAnchorEl: null,
                      PaperProps: {
                        style: {
                          maxHeight: "50%",
                        },
                      },
                    }}
                  >
                    {skills.map((skill) => (
                      <MenuItem key={skill.skillId} value={skill}>
                        <Checkbox
                          checked={selectedSkills.some(
                            (selectedSkill) =>
                              selectedSkill.skillId === skill.skillId
                          )}
                        />
                        <ListItemText primary={skill.name} />
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              )}
            />
          </Grid>
        </Grid>
      </CardContent>
      <Divider />
      <CardActions sx={{ justifyContent: "flex-end" }}>
        <Button variant="contained" onClick={handleSubmit(onSave)}>
          Save
        </Button>
      </CardActions>
    </Card>
  );
};

ProfileForm.propTypes = {
  user: PropTypes.object,
  skills: PropTypes.array,
  selectedSkills: PropTypes.array,
  onSkillsChange: PropTypes.func,
  onSave: PropTypes.func,
};

export default ProfileForm;
