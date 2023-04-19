import { LoadingButton } from "@mui/lab";
import { Button, Stack } from "@mui/material";
import { grey } from "@mui/material/colors";
/**
 * @typedef Prop
 * @property {boolean} loading
 * @property {Function} onCancel
 * @property {Function} onSubmit
 * @property {boolean} disabled
 * @property {string} titleSubmit
 * @property {import("@mui/material").SxProps} style
 * @param {Prop} props
 */
const FooterModal = (props) => {
  const { onCancel, disabled, onSubmit, style, loading, titleSubmit } = props;
  return (
    <Stack
      direction="row"
      spacing={2}
      sx={{
        justifyContent: "flex-end",
        boxShadow: 6,
        margin: "0 -16px -16px -16px",
        paddingX: 2,
        paddingY: 2,
        mt: 3,
        position: "sticky",
        bottom: 0,
        backgroundColor: "white",
        zIndex: 1000,
        ...style,
      }}
    >
      <Button
        onClick={onCancel}
        variant="text"
        sx={{ color: grey[700], fontWeight: 700 }}
      >
        Hủy
      </Button>
      <LoadingButton
        onClick={onSubmit}
        variant="contained"
        sx={{ mt: 2, fontWeight: 700 }}
        disabled={disabled}
        loading={loading}
      >
        {titleSubmit ? titleSubmit : "hoàn thành"}
      </LoadingButton>
    </Stack>
  );
};
export default FooterModal;
