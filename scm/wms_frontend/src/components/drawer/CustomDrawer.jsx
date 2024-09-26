import { Box, Drawer } from "@mui/material";

/**
 * @typedef Prop
 * @property {boolean} open
 * @property {string} title
 * @property {boolean} loading
 * @property {"sm" | "other"} size
 * @property {Function} onClose
 * @property {import("@mui/material").SxProps} style
 * @param {Prop} props
 */
const CustomDrawer = (props) => {
  const {
    onClose,
    onCancel,
    onSubmit,
    disabled,
    title,
    children,
    open,
    style,
    hideFooter,
    size,
    loading,
  } = props;
  return (
    <Drawer anchor="right" open={open} onClose={onClose}>
      <Box
        sx={{
          width:
            size === "xl"
              ? "100vw"
              : size === "lg"
              ? "90vw"
              : size === "md"
              ? "80vw"
              : size === "sm"
              ? "70vw"
              : "50vw",
          minHeight: "100%",
          px: 2,
          ...style,
        }}
      >
        {children}
      </Box>
    </Drawer>
  );
};
export default CustomDrawer;
