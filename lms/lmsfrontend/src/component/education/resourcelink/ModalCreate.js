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
import Alert from "@mui/material/Alert";
import {request} from "api";
import {useState} from "react";

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

export default function ModalCreate({ open, handleClose }) {
  const classes = useStyles();

  const [name, setName] = useState("");
  const [alert, setAlert] = useState(false);
  const [alertContent, setAlertContent] = useState("");

  // const toastId = React.useRef(null);

  const createDomain = () => {
    request(
      "post",
      "/domain",
      (res) => {
        console.log("crean, domain ", res.data);
        if (res.data == true) {
          setAlertContent("Create susscessed");
          setAlert(true);
        }
      },
      {
        onError: (error) => {
          setAlertContent("Create failed");
          setAlert(true);
        },
      },
      { name: name }
    );
  };

  const handleSubmit = () => {
    createDomain();
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
            <CardHeader title="Thêm nguồn tham khảo" />
            <CardContent>
              <Box
                display="flex"
                flexDirection="column"
                justifyContent="center"
              >
                <TextField
                  id="name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  label="Tên nguồn tham khảo"
                />
              </Box>
            </CardContent>
            <CardActions className={classes.action}>
              <Button type="submit" variant="contained" color="primary">
                Thêm
              </Button>
              {alert ? <Alert severity="error">{alertContent}</Alert> : <></>}
            </CardActions>
          </Card>
        </form>
      </Fade>
    </Modal>
  );
}
