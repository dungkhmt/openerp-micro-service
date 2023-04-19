import CloseIcon from "@mui/icons-material/Close";
import { Box, IconButton, Typography } from "@mui/material";
import { grey } from "@mui/material/colors";
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
        margin: "0px -16px 0 -16px",
        paddingX: 2,
        paddingY: 1,
        marginBottom: 3,
        position: "sticky",
        top: 0,
        backgroundColor: "white",
        zIndex: 1000,
        ...style,
      }}
    >
      <Typography
        id="modal-modal-title"
        variant="h6"
        textTransform="capitalize"
        letterSpacing={1}
        color={grey[800]}
        fontSize={17}
      >
        {title}
      </Typography>
      <IconButton onClick={onClose}>
        <CloseIcon />
      </IconButton>
    </Box>
  );
};
export default HeaderModal;
