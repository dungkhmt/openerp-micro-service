import CloseIcon from "@mui/icons-material/Close";
import { Box, IconButton, Typography } from "@mui/material";
import { AppColors } from "../../shared/AppColors";
/**
 * @typedef Prop
 * @property {Function} onClose
 * @property {string} title
 * @property {import("@mui/material").SxProps} style
 * @param {Prop} props
 */
const HeaderModal = (props) => {
  const { title, onClose, style } = props;
  return (
    <Box
      sx={{
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        boxShadow: 3,
        paddingLeft: 2,
        margin: "0px -16px 0 -16px",
        position: "sticky",
        backgroundColor: "white",
        zIndex: 1000,
        ...style,
      }}
    >
      <Typography
        id="modal-modal-title"
        variant="h6"
        textTransform="uppercase"
        letterSpacing={1.5}
        color={AppColors.primary}
        fontSize={18}
      >
        {title}
      </Typography>
      <IconButton onClick={onClose}>
        <CloseIcon color={"secondary"} />
      </IconButton>
    </Box>
  );
};
export default HeaderModal;
