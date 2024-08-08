import {
  Avatar,
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Grid,
  Modal,
  TextField,
  Typography,
} from "@mui/material";
import { makeStyles } from "@mui/styles";
import { request } from "api";
import { useEffect } from "react";
import { useState } from "react";
import { BiDetail } from "react-icons/bi";
import { useParams, useHistory } from "react-router-dom";
import { errorNoti, successNoti } from "utils/notification";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
  },
  grid: {
    paddingLeft: 56,
  },
  divider: {
    width: "91.67%",
    marginTop: 16,
    marginBottom: 16,
  },
  rootDivider: {
    backgroundColor: "black",
  },
  tabs: {
    backgroundColor: theme.palette.background.paper,
  },
}));

const TypeDetail = () => {
  const classes = useStyles();
  const params = useParams();
  const history = useHistory();

  const [typeDetail, setTypeDetail] = useState({});
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [openDelete, setOpenDelete] = useState(false);

  const [name, setName] = useState("");
  const [codePrefix, setCodePrefix] = useState("");
  const [description, setDescription] = useState("");

  const style = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: 600,
    bgcolor: "background.paper",
    border: "2px solid #000",
    boxShadow: 24,
    p: 4,
    gap: "30px",
  };

  const getTypeDetail = async () => {
    setLoading(true);
    await request("get", `/asset-type/get/${params.id}`, (res) => {
      setTypeDetail(res.data);
    });
    setLoading(false);
  };

  const initData = () => {
    setName(typeDetail["name"]);
    setCodePrefix(typeDetail["code_prefix"]);
    setDescription(typeDetail["description"]);
  };

  const successHandler = () => {
    successNoti("EDIT TYPE SUCCESSFULLY", 3000);
    window.location.reload();
  };

  const successHandlerDelete = () => {
		successNoti("DELETE LOCATION SUCCESSFULLY", 3000);
		history.push("/types");
	};

  const errorHandler = {
    onError: (e) => {
      console.log(e);
      errorNoti("FAILED", 3000);
    },
  };

  const handleEdit = () => {
    initData();
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleDelete = () => {
    setOpenDelete(true);
  };

  const handleCloseDelete = () => {
    setOpenDelete(true);
  };

  const deleteApi = async() => {
    await request("delete", `/asset-type/delete/${params.id}`, successHandlerDelete, errorHandler, {});
    setOpenDelete(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const body = {
      name: name,
      code_prefix: codePrefix,
      description: description
    };

    await request("put", `/asset-type/edit/${params.id}`, successHandler, errorHandler, body);
    handleClose();
  };

  useEffect(() => {
    getTypeDetail();
  }, []);

  if (loading) {
    return <p>Loading...</p>;
  }

  return (
    <div>
      <Card>
        <CardHeader
          avatar={
            <Avatar style={{ background: "#ff7043" }}>
              <BiDetail size={32} />
            </Avatar>
          }
          title={<Typography variant="h5">Asset Type Detail</Typography>}
        />
        <div style={{ float: "right", paddingRight: "20px" }}>
          <Button onClick={handleEdit}>Edit</Button>
          <Button onClick={handleDelete}>Delete</Button>
        </div>
        <CardContent style={{ paddingTop: "20px" }}>
          <Grid container className={classes.grid}>
            <Grid item md={3} sm={3} xs={3} direction={"column"}>
              <Typography>Name</Typography>
              <Typography>Code Prefix</Typography>
              <Typography>Number of Assets</Typography>
              <Typography>Description</Typography>
            </Grid>
            <Grid item md={8} sm={8} xs={8}>
              <Typography>
                <b>:</b> {typeDetail["name"]}
              </Typography>
              <Typography>
                <b>:</b> {typeDetail["code_prefix"]}
              </Typography>
              <Typography>
                <b>:</b> {typeDetail["num_assets"]}
              </Typography>
              <Typography>
                <b>:</b> {typeDetail["description"]}
              </Typography>
            </Grid>
          </Grid>
        </CardContent>
      </Card>
      <Modal
        open={open}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
        onClose={handleClose}
      >
        <Box sx={style}>
          <div>EDIT ASSET TYPE</div>
          <hr />
          <form onSubmit={(e) => handleSubmit(e)}>
            <TextField
              label="Name"
              variant="outlined"
              fullWidth
              margin="normal"
              required
              value={name}
              placeholder="Asset Type Name"
              onChange={(e) => setName(e.target.value)}
            />
            <TextField
              label="Code Prefix"
              variant="outlined"
              fullWidth
              margin="normal"
              name="code_prefix"
              required
              value={codePrefix}
              placeholder="Asset Type Code Prefix"
              onChange={(e) => setCodePrefix(e.target.value)}
            />
            <TextField
              label="Description"
              variant="outlined"
              fullWidth
              margin="normal"
              name="description"
              placeholder="Asset Type Description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                marginTop: "20px",
              }}
            >
              <Button
                variant="outlined"
                color="primary"
                type="cancel"
                onClick={handleClose}
              >
                Cancel
              </Button>
              <Button variant="contained" color="primary" type="submit">
                Submit
              </Button>
            </div>
          </form>
        </Box>
      </Modal>
      <Dialog
        open={openDelete}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{"DELETE THIS ASSET"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Do you want to delete this type. It cannot be undone?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button variant="outlined" onClick={handleCloseDelete}>
            CANCEL
          </Button>
          <Button
            variant="outlined"
            color="error"
            onClick={deleteApi}
            autoFocus
          >
            DELETE
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default TypeDetail;
