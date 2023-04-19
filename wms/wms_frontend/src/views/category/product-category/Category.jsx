import MenuIcon from "@mui/icons-material/Menu";
import {
  AppBar,
  Box,
  Button,
  IconButton,
  Toolbar,
  Typography,
} from "@mui/material";
import withScreenSecurity from "../../../components/common/withScreenSecurity";
function ProductCategoryScreen({ screenAuthorization }) {
  return (
    <Box sx={{ flexGrow: 1, background: "red" }}>
      <AppBar position="static" sx={{ background: "" }}>
        <Toolbar>
          <IconButton
            size="large"
            edge="end"
            color="inherit"
            aria-label="menu"
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            News
          </Typography>
          <Button color="inherit">Login</Button>
        </Toolbar>
      </AppBar>
    </Box>
  );
}

const SCR_ID = "SCR_PRODUCT_CATEGORY";
export default withScreenSecurity(ProductCategoryScreen, SCR_ID, true);
