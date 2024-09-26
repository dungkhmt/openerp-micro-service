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

export default function ModalCreateResource({ open, handleClose, domainId }) {
  const classes = useStyles();

  const [link, setLink] = useState(null);
  const [description, setDescription] = useState(null);
  const [alert, setAlert] = useState(false);
  const [alertContent, setAlertContent] = useState("");

  // const toastId = React.useRef(null);

  const createResource = () => {
    request(
      "post",
      `/domains/${domainId}/resource`,
      (res) => {
        console.log("create, resource ", res.data);
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
      {
        link: link,
        description: description,
        statusId: "RESOURCE_CREATED",
      }
    );
  };

  const handleSubmit = () => {
    createResource();
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
                  id="link"
                  value={link}
                  onChange={(e) => setLink(e.target.value)}
                  label="Đường dẫn tham khảo"
                />
                <TextField
                  id="description"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  label="Mô tả"
                />
                <TextField
                  id="status"
                  value="RESOURCE_CREATED"
                  // onChange={(e) => setStatus("RESOURCE_CREATED")}
                  label="Trạng thái"
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
