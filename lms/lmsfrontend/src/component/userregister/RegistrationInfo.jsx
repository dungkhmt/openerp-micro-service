import {Box, Typography} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import React from "react";

const useStyles = makeStyles((theme) => ({
  title: {
    paddingBottom: theme.spacing(2),
  },

  fieldNameWrapper: {
    flex: "0 0 120px",
    marginRight: 32,
    // textAlign: "right",
  },
  fieldNameText: {
    fontWeight: theme.typography.fontWeightBold,
  },
  fieldValue: {
    wordBreak: "break-word",
    wordWrap: "break-word",
  },
}));

function RegistrationInfo(props) {
  const classes = useStyles();
  const { id, fullName, email, requestedRoleNames } = props;

  const info = [
    { key: "Tên đăng nhập", value: id },
    { key: "Tên đầy đủ", value: fullName },
    { key: "Email", value: email },
    { key: "Vai trò đăng ký", value: requestedRoleNames },
  ];

  return (
    <>
      <Typography variant="h6" className={classes.title}>
        Thông tin đăng ký
      </Typography>

      {info.map((inf) => (
        <Box key={inf.key} mb={1} width="100%" display="flex">
          <div className={classes.fieldNameWrapper}>
            <Typography className={classes.fieldNameText}>{inf.key}</Typography>
          </div>
          <div className={classes.fieldValue}>
            <Typography>{inf.value}</Typography>
          </div>
        </Box>
      ))}
    </>
  );
}

export default React.memo(RegistrationInfo);

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
