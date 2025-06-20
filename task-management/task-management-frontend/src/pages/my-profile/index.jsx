import { Box, Grid } from "@mui/material";
import { Helmet } from "react-helmet";
import { useCallback, useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchMySkills,
  updateMyProfile,
  updateMySkills,
} from "../../store/my-profile";
import toast from "react-hot-toast";
import { CircularProgressLoading } from "../../components/common/loading/CircularProgressLoading";
import { FileService } from "../../services/api/file.service";
import { config } from "../../config/config";
import { usePreventOverflow } from "../../hooks/usePreventOverflow";
import ProfileCard from "../../components/my-profile/ProfileCard";
import ProfileForm from "../../components/my-profile/ProfileForm";
import AvatarDialog from "../../components/my-profile/AvatarDialog";

const MyProfile = () => {
  const dispatch = useDispatch();
  const { skill, fetchLoading: skillFetchLoading } = useSelector(
    (state) => state
  );
  const {
    user,
    userSkills,
    fetchLoading: userFetchLoading,
  } = useSelector((state) => state.myProfile);
  const [selectedSkills, setSelectedSkills] = useState([]);
  const [updateLoading, setUpdateLoading] = useState(false);
  const inputRef = useRef(null);
  const { ref, updateMaxHeight } = usePreventOverflow();
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [avatarPreview, setAvatarPreview] = useState(null);
  const [uploadLoading, setUploadLoading] = useState(false);

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
      await dispatch(fetchMySkills()).unwrap();
    } catch (error) {
      console.error(error);
      toast.error(error?.message || "Lỗi khi lấy dữ liệu");
    }
  }, [dispatch]);

  const setUserSkills = useCallback(() => {
    if (userSkills.length > 0 && skill.skills.length > 0) {
      const selectedSkills = userSkills
        .map(
          (userSkill) =>
            skill.skills.find(
              (skill) => skill.skillId === userSkill.skill.skillId
            ) || null
        )
        .filter(Boolean);
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
      await dispatch(updateMySkills(skillList)).unwrap();
      toast.success("Cập nhật thông tin thành công");
    } catch (e) {
      console.error(e);
      toast.error(error?.message || "Cập nhật thất bại. Vui lòng thử lại sau.");
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
      setUploadLoading(true);
      const file = inputRef.current.files[0];
      const data = await FileService.uploadFile(file);
      if (data?.id) {
        avatarUrl = `${config.url.API_URL}/content/img/${data.id}`;
      }
    }
    try {
      await dispatch(updateMyProfile({ avatarUrl: avatarUrl })).unwrap();
      toast.success("Ảnh đại diện đã được cập nhật");
    } catch (error) {
      console.error(error);
      toast.error(error?.message || "Tải lên ảnh thất bại");
    } finally {
      setUploadLoading(false);
      setIsDialogOpen(false);
    }
  };

  useEffect(() => {
    getUserSkills();
  }, [getUserSkills]);

  useEffect(() => {
    setUserSkills();
  }, [setUserSkills]);

  useEffect(() => {
    if (user?.avatarUrl) {
      setAvatarPreview(user.avatarUrl);
    }
  }, [user]);

  useEffect(() => {
    updateMaxHeight();
    window.addEventListener("resize", updateMaxHeight);
    return () => window.removeEventListener("resize", updateMaxHeight);
  }, [updateMaxHeight]);

  if (
    skillFetchLoading ||
    userFetchLoading ||
    updateLoading ||
    !user ||
    !skill
  ) {
    return <CircularProgressLoading />;
  }

  return (
    <>
      <Helmet>
        <title>Thông tin tài khoản | Task management</title>
      </Helmet>
      <Box ref={ref} sx={{ pr: 3, overflow: "auto" }}>
        <Grid container spacing={3}>
          <Grid item lg={5} md={6} xs={12}>
            <ProfileCard
              user={user}
              userSkills={userSkills}
              onUploadImage={handleUploadImage}
            />
          </Grid>

          <Grid item lg={7} md={6} xs={12}>
            <ProfileForm
              user={user}
              skills={skill.skills}
              selectedSkills={selectedSkills}
              onSkillsChange={handleChange}
              onSave={onUpdate}
            />
          </Grid>
        </Grid>

        <AvatarDialog
          open={isDialogOpen}
          avatarPreview={avatarPreview}
          user={user}
          inputRef={inputRef}
          onClose={handleCancelImage}
          onChooseImage={handleChooseImage}
          onDeleteImage={handleDeleteImage}
          onSave={handleSaveImage}
          loading={uploadLoading}
        />
      </Box>
    </>
  );
};
export default MyProfile;
