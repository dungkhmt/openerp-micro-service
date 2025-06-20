import {
  Card,
  CardContent,
  CardActions,
  Button,
  Stack,
  Typography,
  Divider,
} from "@mui/material";
import { SkillChip } from "../../components/task/skill";
import { UserAvatar } from "../../components/common/avatar/UserAvatar";
import PropTypes from "prop-types";

const ProfileCard = ({ user, userSkills, onUploadImage }) => {
  const fullName =
    `${user?.firstName ?? ""} ${user?.lastName ?? ""}`.trim() || "-";
  const initials = `${user?.firstName?.charAt(0) ?? ""}${
    user?.lastName?.charAt(0) ?? ""
  }`;

  return (
    <Card sx={{ borderRadius: "20px" }}>
      <CardContent>
        <Stack spacing={2} alignItems="center">
          <UserAvatar
            skin="light"
            user={user}
            sx={{ height: 150, width: 150, fontSize: "3rem" }}
          >
            {initials}
          </UserAvatar>
          <Stack spacing={1} textAlign="center">
            <Typography variant="h5">{fullName}</Typography>
            <Typography color="text.secondary" variant="body2">
              {user?.email}
            </Typography>
          </Stack>
        </Stack>
      </CardContent>
      <CardContent>
        <Stack
          direction="row"
          sx={{ flexWrap: "wrap", justifyContent: "center", gap: 1 }}
        >
          {userSkills.map((s) => (
            <SkillChip key={s.skill.skillId} skill={s.skill} />
          ))}
        </Stack>
      </CardContent>
      <Divider />
      <CardActions sx={{ justifyContent: "center" }}>
        <Button variant="text" sx={{ height: 5 }} onClick={onUploadImage}>
          Upload picture
        </Button>
      </CardActions>
    </Card>
  );
};

ProfileCard.propTypes = {
  user: PropTypes.object,
  userSkills: PropTypes.array,
  onUploadImage: PropTypes.func,
};

export default ProfileCard;
