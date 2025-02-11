import {
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Divider,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Select,
  ListItemText,
  Checkbox,
  Typography,
  Stack,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from "@mui/material";
import { Helmet } from "react-helmet";
import { useCallback, useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchUserSkills,
  updateUser,
  updateUserSkills,
} from "../../store/my-profile";
import toast from "react-hot-toast";
import { Controller, useForm } from "react-hook-form";
import { CircularProgressLoading } from "../../components/common/loading/CircularProgressLoading";
import { SkillChip } from "../../components/task/skill";
import { UserAvatar } from "../../components/common/avatar/UserAvatar";
import { FileService } from "../../services/api/file.service";
import { config } from "../../config/config";
import { Icon } from "@iconify/react";
import CustomAvatar from "../../components/mui/avatar/CustomAvatar";
import { getRandomColorSkin } from "../../utils/color.util";

const MyProfile = () => {
  const dispatch = useDispatch();
  const { skill } = useSelector((state) => state);
  const { user, userSkills } = useSelector((state) => state.myProfile);
  const [selectedSkills, setSelectedSkills] = useState([]);
  const [firstName, setFirstName] = useState(null);
  const [lastName, setLastName] = useState(null);
  const [email, setEmail] = useState(null);
  const [loading, setLoading] = useState(true);
  const { handleSubmit, control } = useForm();
  const [updateLoading, setUpdateLoading] = useState(false);
  const inputRef = useRef(null);

  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [avatarPreview, setAvatarPreview] = useState(user?.avatarUrl);

  const handleChooseImage = (e) => {
    const file = e.target.files[0];
    if (file) {
      setAvatarPreview(URL.createObjectURL(file));
    }
  };

  const handleDeleteImage = () => {
    setAvatarPreview(null);
  };

  const handleCancelImage = () => {
    setIsDialogOpen(false);
    setAvatarPreview(user?.avatarUrl);
  };

  const getUserSkills = useCallback(async () => {
    try {
      await dispatch(fetchUserSkills());
    } catch (error) {
      console.error(error);
      toast.error("Lỗi khi lấy dữ liệu");
    }
  }, [dispatch]);

  const setUserSkills = useCallback(() => {
    if (userSkills.length > 0 && skill.skills.length > 0) {
      const selectedSkills = userSkills.map((userSkill) => {
        return (
          skill.skills.find(
            (skill) => skill.skillId === userSkill.skill.skillId
          ) || null
        );
      });
      setSelectedSkills(selectedSkills);
    }
  }, [userSkills, skill.skills]);

  const handleChange = (event) => {
    setSelectedSkills(event.target.value);
  };

  const onUpdate = async () => {
    try {
      setUpdateLoading(true);
      const skillList = selectedSkills.map((skill) => skill.skillId);
      await dispatch(updateUserSkills(skillList));
      toast.success("Cập nhật thông tin thành công");
    } catch (e) {
      console.error(e);
      toast.error("Cập nhật thất bại. Vui lòng thử lại sau.");
    } finally {
      setUpdateLoading(false);
    }
  };

  const handleUploadImage = () => {
    setIsDialogOpen(true);
  };

  const handleSaveImage = async () => {
    let avatarUrl = "";
    if (avatarPreview) {
      const file = inputRef.current.files[0];
      const data = await FileService.uploadFile(file);
      if (data?.id) {
        avatarUrl = `${config.url.API_URL}/content/img/${data.id}`;
      }
    }
    try {
      await dispatch(updateUser({ avatarUrl: avatarUrl }));
      toast.success("Ảnh đại diện đã được cập nhật");
    } catch (error) {
      console.error(error);
      toast.error("Tải lên ảnh thất bại");
    } finally {
      setIsDialogOpen(false);
    }
  };

  useEffect(() => {
    getUserSkills();
  }, [getUserSkills]);

  useEffect(() => {
    if (user) {
      setFirstName(user.firstName);
      setLastName(user.lastName);
      setEmail(user.email);
      setUserSkills();
      setLoading(false);
    }
  }, [setUserSkills, user]);

  if (loading || updateLoading) {
    return <CircularProgressLoading />;
  }

  return (
    <>
      <Helmet>
        <title>Thông tin tài khoản | Task management</title>
      </Helmet>
      <Box sx={{ pr: 3, overflow: "auto", maxHeight: "92%" }}>
        <Grid container spacing={3}>
          <Grid item lg={5} md={6} xs={12}>
            <Card sx={{ borderRadius: "20px" }}>
              <CardContent>
                <Stack spacing={2} sx={{ alignItems: "center" }}>
                  <UserAvatar
                    skin="light"
                    user={user}
                    sx={{ height: 150, width: 150, fontSize: "3rem" }}
                  >
                    {`${firstName?.charAt(0) ?? ""}${
                      lastName?.charAt(0) ?? ""
                    }`}
                  </UserAvatar>
                  <Stack spacing={1} sx={{ textAlign: "center" }}>
                    <Typography variant="h5">
                      {firstName || lastName
                        ? `${firstName ?? ""} ${lastName ?? ""}`
                        : " - "}
                    </Typography>
                    <Typography color="text.secondary" variant="body2">
                      {email}
                    </Typography>
                  </Stack>
                </Stack>
              </CardContent>
              <CardContent>
                <Stack
                  direction="row"
                  sx={{
                    flexWrap: "wrap",
                    justifyContent: "center",
                    rowGap: 2,
                    columnGap: 1,
                  }}
                >
                  {userSkills.map((skill) => (
                    <SkillChip key={skill.skill.skillId} skill={skill.skill} />
                  ))}
                </Stack>
              </CardContent>
              <Divider />
              <CardActions sx={{ justifyContent: "center" }}>
                <Button
                  variant="text"
                  sx={{ height: 5 }}
                  onClick={handleUploadImage}
                >
                  Upload picture
                </Button>
              </CardActions>
            </Card>
          </Grid>

          <Grid item lg={7} md={6} xs={12}>
            <Card sx={{ borderRadius: "20px" }}>
              <CardHeader subheader="Skills can be edited" title="Profile" />
              <Divider />
              <CardContent>
                <Grid container spacing={3}>
                  <Grid item md={6} xs={12}>
                    <FormControl fullWidth>
                      <InputLabel>First name</InputLabel>
                      <OutlinedInput
                        defaultValue={firstName}
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
                        defaultValue={lastName}
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
                        defaultValue={email}
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
                            onChange={handleChange}
                            renderValue={(selected) => (
                              <div
                                style={{
                                  display: "flex",
                                  flexWrap: "wrap",
                                  gap: "5px",
                                }}
                              >
                                {selected.map((value) => (
                                  <SkillChip
                                    key={value.skillId}
                                    skill={value}
                                  />
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
                            {skill.skills.map((skill) => (
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
                <Button variant="contained" onClick={handleSubmit(onUpdate)}>
                  Save
                </Button>
              </CardActions>
            </Card>
          </Grid>
        </Grid>

        <Dialog
          open={isDialogOpen}
          onClose={handleCancelImage}
          maxWidth="sm"
          fullWidth
        >
          <DialogTitle>Thay đổi ảnh đại diện</DialogTitle>
          <DialogContent sx={{ textAlign: "center", padding: 4 }}>
            <Box sx={{ display: "flex", justifyContent: "center", mb: 5 }}>
              {avatarPreview ? (
                <img
                  src={avatarPreview}
                  alt="Avatar Preview"
                  style={{
                    width: 250,
                    height: 250,
                    borderRadius: "50%",
                    objectFit: "cover",
                  }}
                />
              ) : (
                <CustomAvatar
                  skin="light"
                  color={getRandomColorSkin(user.id)}
                  sx={{
                    width: 250,
                    height: 250,
                    fontSize: "3.5rem",
                  }}
                >
                  {`${user.firstName?.charAt(0) ?? ""}${
                    user.lastName?.charAt(0) ?? ""
                  }`}
                </CustomAvatar>
              )}
            </Box>
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "center",
                alignItems: "center",
                gap: 5,
              }}
            >
              <IconButton
                color="primary"
                onClick={() => inputRef.current.click()}
                sx={{
                  flexDirection: "row",
                  alignItems: "center",
                  "&:hover": {
                    backgroundColor: "rgba(25, 118, 210, 0.1)",
                    ".text": { color: "#1976d2" },
                  },
                  padding: "8px 10px",
                  borderRadius: "4px",
                }}
              >
                <Icon
                  className="icon"
                  fontSize={24}
                  icon="material-symbols:upload-rounded"
                />
                <Typography
                  className="text"
                  variant="subtitle1"
                  sx={{ fontSize: "0.875rem", color: "#696969", ml: 1 }}
                >
                  Upload
                </Typography>
                <input
                  type="file"
                  hidden
                  ref={inputRef}
                  onChange={handleChooseImage}
                  accept="image/*"
                />
              </IconButton>
              <IconButton
                color="error"
                onClick={handleDeleteImage}
                sx={{
                  flexDirection: "row",
                  alignItems: "center",
                  "&:hover": {
                    backgroundColor: "rgba(211, 47, 47, 0.1)",
                    ".text": { color: "#d32f2f" },
                  },
                  padding: "8px 10px",
                  borderRadius: "4px",
                }}
              >
                <Icon
                  className="icon"
                  fontSize={24}
                  icon="material-symbols:delete-outline-rounded"
                />
                <Typography
                  className="text"
                  variant="subtitle1"
                  sx={{ fontSize: "0.875rem", color: "#696969", ml: 1 }}
                >
                  Remove
                </Typography>
              </IconButton>
            </Box>
          </DialogContent>
          <DialogActions
            sx={{ justifyContent: "flex-end", pr: 8, pb: 5, gap: 3 }}
          >
            <Button
              onClick={handleCancelImage}
              color="primary"
              variant="outlined"
            >
              Cancel
            </Button>
            <Button
              onClick={handleSaveImage}
              color="primary"
              variant="contained"
            >
              Save
            </Button>
          </DialogActions>
        </Dialog>
      </Box>
    </>
  );
};
export default MyProfile;
