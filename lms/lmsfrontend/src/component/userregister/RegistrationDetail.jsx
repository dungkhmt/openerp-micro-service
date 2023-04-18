import {
  Avatar,
  Collapse,
  Divider,
  Grid,
  ListItem,
  ListItemAvatar,
  ListItemIcon,
  ListItemText,
  Typography,
} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import ChevronRightRoundedIcon from "@material-ui/icons/ChevronRightRounded";
import {Skeleton} from "@material-ui/lab";
import clsx from "clsx";
import TertiaryButton from "component/button/TertiaryButton";
import {useEffect, useState} from "react";
import {request} from "../../api";
import {getRandomIntInclusive} from "../../layout/notification/Notification";
import {errorNoti} from "../../utils/notification";
import PrimaryButton from "../button/PrimaryButton";
import GrantRole from "./GrantRole";
import RegistrationInfo from "./RegistrationInfo";

// const theme = createMuiTheme({
//   overrides: {
//     MuiMenuItem: {
//       root: {
//         marginBottom: 2,
//         "&$selected, &$selected:focus, &$selected:hover": {
//           // This is to refer to the prop provided by M-UI
//           color: "white",
//           backgroundColor: "#1976d2", // updated backgroundColor
//           marginBottom: 2,
//         },
//       },
//     },
//   },
// });

const dividerStyle = { marginTop: 16, marginBottom: 16 };
const approveBtnStyle = { marginTop: 32 };

const useStyles = makeStyles((theme) => ({
  container: {
    background: "#ffffff",
    paddingLeft: theme.spacing(9),
    paddingTop: theme.spacing(3),
    paddingBottom: theme.spacing(3),
  },
  title: {
    paddingBottom: theme.spacing(2),
  },
  roles: {
    width: 300,
    background: "white",
  },

  icon: {
    minWidth: 24,
  },
  open: { transform: "rotate(90deg)", transition: "0.3s" },
  close: { transition: "0.3s" },
  listItem: {
    background: "#ffffff",
    "&:hover": {
      background: "#ffffff",
    },
    // borderRadius: 8,
  },
  avatar: {
    marginLeft: 16,
    width: 32,
    height: 32,
  },
  collapse: {
    borderBottom: "1px solid rgba(0, 0, 0, 0.12)",
  },
  createdStamp: {
    float: "right",
    [theme.breakpoints.down("xs")]: {
      float: "left",
      color: theme.palette.text.secondary,
      fontSize: "0.875rem",
    },
  },
}));

const formatDate = (strDate) => {
  let date = new Date(strDate);

  return `${date.getDate()} Tháng ${
    date.getMonth() + 1
  }, ${date.getFullYear()}`;
};

function RegistrationDetail(props) {
  const classes = useStyles();
  const { data, rolesList } = props;

  //
  const [roles, setRoles] = useState(); // To collect granted roles

  const [grantedRoles, setGrantedRoles] = useState(); // Keep final granted roles after approving tp display
  const [disabled, setDisabled] = useState(false);

  //
  const handleApprove = () => {
    setDisabled(true);

    // Collect granted roles
    const grantedItems = roles.filter((role) => role.granted);
    let grantedRolesId = [];
    let grantedRolesName = [];

    grantedItems.forEach((role) => {
      if (role.granted) {
        grantedRolesId.push(role.id);
        grantedRolesName.push(role.name);
      }
    });

    // Send request
    request(
      "post",
      //`/user/approve-registration`,
      `/user/approve-registration-send-email-for-activation`,
      () => {
        setGrantedRoles(grantedRolesName);
      },
      {
        onError: () => setDisabled(false),
        400: (e) => {
          if ("approved" === e.response.data.error) {
            errorNoti("Tài khoản đã được phê duyệt trước đó.");
          } else {
            errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
          }
        },
        rest: () => {
          errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
        },
      },
      { userLoginId: data.id, roles: grantedRolesId }
    );
  };
  const handleDisable = () => {
    setDisabled(true);

    // Send request
    request(
      "post",
      `/user/disable-registration`,
      () => {},
      {
        onError: () => setDisabled(false),
        400: (e) => {
          if ("approved" === e.response.data.error) {
            errorNoti("Tài khoản đã được phê duyệt trước đó.");
          } else {
            errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
          }
        },
        rest: () => {
          errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
        },
      },
      { userLoginId: data.id }
    );
  };

  //
  useEffect(() => {
    // data undefined when display loading screen
    if (rolesList && data) {
      const requestedRolesId = new Set(data.requestedRoleIds);
      const roles = JSON.parse(JSON.stringify(rolesList)); //  To deeply clone rolesList

      roles.forEach((role) => {
        if (requestedRolesId.has(role.id)) role.granted = true;
      });

      setRoles([...roles]);
    }
  }, [rolesList]);

  //
  const [open, setOpen] = useState(true);

  const handleClick = () => {
    setOpen(!open);
  };

  return data ? (
    <>
      <ListItem
        divider={!open}
        button
        onClick={handleClick}
        className={classes.listItem}
      >
        <ListItemIcon className={classes.icon}>
          <ChevronRightRoundedIcon
            className={clsx(!open && classes.close, open && classes.open)}
          />
        </ListItemIcon>
        <ListItemAvatar>
          <Avatar
            className={classes.avatar}
            style={{
              background: data.avatarBgColor,
            }}
          >
            {data.fullName
              .substring(data.fullName.lastIndexOf(" ") + 1)
              .substring(0, 1)
              .toLocaleUpperCase()}
          </Avatar>
        </ListItemAvatar>
        <ListItemText
          primary={
            <>
              <Typography style={{ float: "left" }}>{data.fullName}</Typography>
              <Typography className={classes.createdStamp}>
                {`Đăng ký ngày ${formatDate(data.createdStamp)}`}
              </Typography>
            </>
          }
        />
      </ListItem>
      <Collapse
        in={open}
        timeout="auto"
        unmountOnExit
        className={classes.collapse}
      >
        <div className={classes.container}>
          <Grid container spacing={4}>
            <Grid item md={5} xs={12} sm={12}>
              <RegistrationInfo
                id={data.id}
                fullName={data.fullName}
                email={data.email}
                requestedRoleNames={data.requestedRoleNames}
              />
            </Grid>

            <Divider
              orientation="vertical"
              flexItem
              md={2}
              sm={0}
              xs={0}
              style={dividerStyle}
            />

            <Grid item md={5} xs={12} sm={12}>
              <GrantRole
                key={data.id}
                roles={roles}
                setRoles={setRoles}
                grantedRoles={grantedRoles}
              />
            </Grid>
          </Grid>

          <div>
            <TertiaryButton
              disabled={disabled}
              style={approveBtnStyle}
              onClick={handleDisable}
              sx={{ mr: 1 }}
            >
              Hủy
            </TertiaryButton>
            <PrimaryButton
              disabled={disabled}
              style={approveBtnStyle}
              onClick={handleApprove}
            >
              Phê duyệt
            </PrimaryButton>
          </div>
        </div>
      </Collapse>
    </>
  ) : (
    <ListItem divider button className={classes.listItem}>
      <Skeleton
        variant="circle"
        width={32}
        height={32}
        style={{ marginRight: 8 }}
      />

      <ListItemText
        primary={
          <>
            <Typography style={{ float: "left" }}>
              <Skeleton
                width={getRandomIntInclusive(120, 160)}
                style={{ borderRadius: 8 }}
              />
            </Typography>
            <Typography style={{ float: "right" }}>
              <Skeleton width={220} style={{ borderRadius: 8 }} />
            </Typography>
          </>
        }
      />
    </ListItem>
  );
}

export default RegistrationDetail;
