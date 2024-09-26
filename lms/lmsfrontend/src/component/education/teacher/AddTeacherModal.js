import {
  Backdrop,
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Fade,
  Modal,
  TextField,
} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import {request} from "api";
import React, {useEffect, useState} from "react";
import {infoNoti} from "utils/notification";
import * as yup from "yup";

const useStyles = makeStyles((theme) => ({
  modal: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  card: {
    minWidth: 400,
  },
  action: {
    display: "flex",
    justifyContent: "center",
  },
  error: {
    textAlign: "center",
    color: "red",
    marginTop: theme.spacing(2),
  },
}));

let schema = yup.object().shape({
  name: yup.string().required(),
  email: yup.string().email("Email invalid").required(),
  userLogin: yup.string(),
});

export default function AddTeacherModal({ open, handleClose }) {
  const classes = useStyles();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [userLogin, setUserLogin] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  // const toastId = React.useRef(null);

  useEffect(() => {
    setName("");
    setEmail("");
    setUserLogin("");
    setError("");
    setLoading(false);
  }, [open]);

  const handleSubmit = (e) => {
    e.preventDefault();
    setLoading(true);
    schema
      .validate({ name, email, userLogin })
      .then((data) => {
        setError("");
        const reqData = {
          teacherName: name,
          teacherId: email,
          userLoginId: userLogin,
        };
        request(
          "post",
          "add-teacher",
          (data) => {
            setLoading(false);
            handleClose();
            infoNoti("Thêm giảng viên thành công");
          },
          {
            400: (res) => {
              // console.log('check', res.response)
              setError(res.response.data);
              setLoading(false);
            },
          },
          reqData
        );
        console.log(reqData);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  };

  return (
    <Modal
      className={classes.modal}
      open={open}
      onClose={handleClose}
      closeAfterTransition
      BackdropComponent={Backdrop}
      BackdropProps={{
        timeout: 500,
      }}
    >
      <Fade in={open}>
        <form onSubmit={handleSubmit}>
          <Card className={classes.card}>
            <CardHeader title="Thêm giảng viên" />
            <CardContent>
              <Box
                display="flex"
                flexDirection="column"
                justifyContent="center"
              >
                <TextField
                  id="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  label="Email"
                />
                <TextField
                  id="name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  label="Họ tên giảng viên"
                />
                <TextField
                  id="userLogin"
                  value={userLogin}
                  onChange={(e) => setUserLogin(e.target.value)}
                  label="Tài khoản đăng nhập"
                />
              </Box>
              <div className={classes.error}>{error}</div>
            </CardContent>
            <CardActions className={classes.action}>
              <Button
                disabled={loading}
                type="submit"
                variant="contained"
                color="primary"
              >
                Thêm
              </Button>
            </CardActions>
          </Card>
        </form>
      </Fade>
    </Modal>
  );
}
