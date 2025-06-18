import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Box,
  IconButton,
  Button,
  Typography,
} from "@mui/material";
import { Icon } from "@iconify/react";
import CustomAvatar from "../../components/mui/avatar/CustomAvatar";
import { getRandomColorSkin } from "../../utils/color.util";
import PropTypes from "prop-types";

const AvatarDialog = ({
  open,
  avatarPreview,
  user,
  inputRef,
  onClose,
  onChooseImage,
  onDeleteImage,
  onSave,
  loading,
}) => {
  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
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
            disabled={loading}
            sx={{
              flexDirection: "row",
              alignItems: "center",
              "&:hover": {
                backgroundColor: "primary.background",
                ".text": { color: "primary.main" },
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
              sx={{ fontSize: "0.875rem", color: "grey.700", ml: 1 }}
            >
              Upload
            </Typography>
            <input
              type="file"
              hidden
              ref={inputRef}
              onChange={onChooseImage}
              accept="image/*"
            />
          </IconButton>
          <IconButton
            color="error"
            onClick={onDeleteImage}
            disabled={loading}
            sx={{
              flexDirection: "row",
              alignItems: "center",
              "&:hover": {
                backgroundColor: "error.background",
                ".text": { color: "error.main" },
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
              sx={{ fontSize: "0.875rem", color: "grey.700", ml: 1 }}
            >
              Remove
            </Typography>
          </IconButton>
        </Box>
      </DialogContent>
      <DialogActions sx={{ justifyContent: "flex-end", pr: 8, pb: 5, gap: 3 }}>
        <Button
          onClick={onClose}
          color="primary"
          variant="outlined"
          disabled={loading}
        >
          Cancel
        </Button>
        <Button
          onClick={onSave}
          color="primary"
          variant="contained"
          disabled={loading}
        >
          {loading ? "Saving..." : "Save"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

AvatarDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  avatarPreview: PropTypes.string,
  user: PropTypes.object,
  inputRef: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onChooseImage: PropTypes.func.isRequired,
  onDeleteImage: PropTypes.func.isRequired,
  onSave: PropTypes.func.isRequired,
  loading: PropTypes.bool.isRequired,
};

export default AvatarDialog;
