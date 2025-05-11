import { Box, Typography } from "@mui/material";
import Drawer from "@mui/material/Drawer";
import List from "@mui/material/List";
import { styled } from "@mui/material/styles";
import { useKeycloak } from "@react-keycloak/web";
import PrimaryButton from "../../components/button/PrimaryButton";
import { MENUS } from "../../config/menuconfig.jsx";
import PropTypes from "prop-types";
import { useEffect } from "react";
import SimpleBar from "simplebar-react";
import "simplebar/dist/simplebar.min.css";
import { fetchMenu } from "../../state/MenuState";
import GroupMenuItem, { menuItemBaseStyle } from "./GroupMenuItem";
import { blackColor, whiteColor } from "./MenuItem";

export const drawerWidth = 300;
export const miniDrawerWidth = 50;

const Background = styled("div")(({ theme }) => ({
  position: "absolute",
  zIndex: "1",
  height: "100%",
  width: "100%",
  display: "block",
  top: "0",
  left: "0",
  backgroundSize: "cover",
  backgroundPosition: "center center",
  "&:after": {
    position: "absolute",
    zIndex: "3",
    width: "100%",
    height: "100%",
    content: '""',
    display: "block",
    background: blackColor,
    opacity: ".8",
  },
}));

const styles = {
  drawerPaper: {
    height: "calc(100% - 64px)",
    marginTop: 8,
    overflowX: "hidden",
    width: drawerWidth,
    flexShrink: 0,
    border: "none",
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
    whiteSpace: "nowrap",
  },
  signInText: (theme) => ({
    ...menuItemBaseStyle(theme).menuItemText,
    fontSize: "1rem",
    whiteSpace: "break-spaces",
    color: whiteColor,
    textAlign: "center",
    paddingBottom: 2,
  }),
  signInContainer: (theme) => ({
    width: drawerWidth - 20,
    zIndex: theme.zIndex.drawer + 1,
    paddingTop: theme.spacing(3),
    paddingBottom: theme.spacing(6),
  }),
};

export default function SideBar(props) {
  const { open, image, color: bgColor } = props;

  const { keycloak } = useKeycloak();

  useEffect(() => {
    if (keycloak.authenticated) fetchMenu();
  }, [keycloak.authenticated]);

  return (
    <Drawer
      variant="persistent"
      anchor="left"
      open={open}
      sx={styles.drawer}
      PaperProps={{ sx: styles.drawerPaper }}
    > {keycloak.authenticated && (
      <SimpleBar
        style={{
          marginBottom: 16,
          position: "relative",
          height: "100%",
          zIndex: "4",
          overflowX: "hidden",
          overscrollBehaviorY: "none", 
        }}
      >
        <List component="nav">
          {MENUS.map((group) => (
            <GroupMenuItem key={group.text} group={group} color={bgColor} />
          ))}
        </List>
      </SimpleBar>
    )}
      {!keycloak.authenticated && open && (
        <Box
          sx={styles.signInContainer}
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          ml="auto"
          mr="auto"
        >
          <Typography sx={styles.signInText}>
            Đăng nhập ngay để sử dụng những tính năng dành riêng cho bạn
          </Typography>
          <PrimaryButton
            onClick={() => keycloak.login()}
            style={{ width: 160, borderRadius: 25 }}
          >
            Đăng nhập
          </PrimaryButton>
        </Box>
      )}
      {image && (
        <Background
          sx={styles.background}
          style={{ backgroundImage: "url(" + image + ")" }}
        />
      )}
    </Drawer>
  );
}

SideBar.propTypes = {
  bgColor: PropTypes.oneOf(["purple", "blue", "green", "orange", "red"]),
  image: PropTypes.string,
  open: PropTypes.bool,
};
