import {Box, Card, CardContent, TextField, Typography,} from "@material-ui/core/";
import {makeStyles} from "@material-ui/core/styles";
import _ from "lodash";
import React, {useState} from "react";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import {useForm} from "react-hook-form";
import {useHistory} from "react-router-dom";
import {request} from "../../api";
import PrimaryButton from "../button/PrimaryButton";
import TertiaryButton from "../button/TertiaryButton";
import AlertDialog from "../common/AlertDialog";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: "100%",
      minWidth: 120,
    },
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
    maxWidth: 300,
  },
}));

let reDirect = null;

function SendMail2Users() {
  const classes = useStyles();

  //
  const { register, errors, watch, handleSubmit } = useForm();

  const [alertMessage, setAlertMessage] = useState({
    title: "Vui lòng nhập đầy đủ thông tin cần thiết",
    content:
      "Một số thông tin yêu cầu cần phải được điền đầy đủ. Vui lòng kiểm tra lại.",
  });
  const [alertSeverity, setAlertSeverty] = useState("info");
  const [openAlert, setOpenAlert] = useState(false);
  const history = useHistory();

  const handleCloseAlert = () => {
    setOpenAlert(false);
  };

  const onClickAlertBtn = () => {
    setOpenAlert(false);
    if (reDirect != null) {
      history.push(reDirect);
    }
  };

  function onSubmit(data) {
    request("post", "/send-mail-to-all-users", undefined, {}, data);
  }

  return (
    <>
      <Card>
        <CardContent>
          <Typography variant="h5" component="h2">
            Gửi mail
          </Typography>
          <form
            className={classes.root}
            onSubmit={handleSubmit(onSubmit)}
            noValidate
            autoComplete="off"
          >
            <div>
              <TextField
                autoFocus
                label="Tiêu đề*"
                name="mailTitle"
                // variant="outlined"
                value={watch("mailTitle")}
                size="small"
                error={!!errors.mailTitle}
                helperText={errors.mailTitle?.message}
                inputRef={register({
                  required: "Trường này được yêu cầu",
                  validate: (name) => {
                    if (_.isEmpty(name.trim()))
                      return "Vui lòng chọn tiêu đề hợp lệ";
                  },
                })}
              />
            </div>
            <div>
              <TextField
                label="Nội dung"
                multiline
                name="mailContent"
                // variant="outlined"
                value={watch("mailContent")}
                size="small"
                // error={!!errors.mailContent}
                // helperText={errors.mailContent?.message}
                inputRef={register}
              />
            </div>
            <Box>
              <TertiaryButton
                style={{ marginLeft: "45px" }}
                onClick={() => history.push("contestprogramming")}
              >
                Hủy
              </TertiaryButton>
              <PrimaryButton type="submit">Gửi</PrimaryButton>
            </Box>
          </form>
        </CardContent>
      </Card>

      <AlertDialog
        open={openAlert}
        onClose={handleCloseAlert}
        severity={alertSeverity}
        {...alertMessage}
        buttons={[
          {
            onClick: onClickAlertBtn,
            color: "primary",
            autoFocus: true,
            text: "OK",
          },
        ]}
      />
    </>
  );
}

export default SendMail2Users;
