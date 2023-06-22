import CloseIcon from "@mui/icons-material/Close";
import { Box, IconButton, Modal, Slide, Typography } from "@mui/material";
import { AppColors } from "../../shared/AppColors";
const styleDefault = {
  maxHeight: "80vh",
  overflowY: "auto",
  width: 700,
  bgcolor: "background.paper",
  boxShadow: 24,
  px: 2,
  borderRadius: 1,
};
/**
 * @typedef Prop
 * @property {boolean} open
 * @property {any} children
 * @property {any} toggle
 * @property {"xs" | "sm" | "md" | "lg" | "xl"} size
 * @property {string} title
 * @property {import("@mui/material").SxProps} style
 * @param {Prop} props
 */

const CustomModal = (props) => {
  const { open, style, children, toggle, size, title } = props;
  return (
    <Modal
      onClose={() => (toggle ? toggle() : undefined)}
      open={open}
      aria-labelledby="modal-modal-title"
      aria-describedby="modal-modal-description"
      sx={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      <Slide direction="up" in={open} mountOnEnter unmountOnExit>
        <Box
          sx={{
            ...styleDefault,
            ...style,
            width:
              size === "sm"
                ? "50vw"
                : size === "md"
                ? "60vw"
                : size === "lg"
                ? "75vw"
                : "90vw",
          }}
        >
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              // boxShadow: 3,
              paddingLeft: 2,
              margin: "10px -16px 10px -16px",
              position: "sticky",
              backgroundColor: "white",
              zIndex: 1000,
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
            <IconButton onClick={() => (toggle ? toggle() : undefined)}>
              <CloseIcon color={"secondary"} />
            </IconButton>
          </Box>
          {children}
        </Box>
      </Slide>
    </Modal>
  );
};
export default CustomModal;
