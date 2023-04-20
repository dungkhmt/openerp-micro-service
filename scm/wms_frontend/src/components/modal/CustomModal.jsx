import { Box, Modal, Slide } from "@mui/material";

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
 * @property {import("@mui/material").SxProps} style
 * @param {Prop} props
 */

const CustomModal = (props) => {
  const { open, style, children, toggle, size } = props;
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
        padding: 20,
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
          {children}
        </Box>
      </Slide>
    </Modal>
  );
};
export default CustomModal;
