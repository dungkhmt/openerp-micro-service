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

const VendorDetail = () => {
  const classes = useStyles();
  const params = useParams();
  const history = useHistory();

  const [vendorDetail, setVendorDetail] = useState({});
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [openDelete, setOpenDelete] = useState(false);

  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [email, setEmail] = useState("");
  const [address, setAddress] = useState("");
  const [url, setUrl] = useState("");
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

  const getVendorDetail = async () => {
    setLoading(true);
    await request("get", `/vendor/get/${params.id}`, (res) => {
      setVendorDetail(res.data);
    });
    setLoading(false);
  };

  const initData = () => {
    setName(vendorDetail["name"]);
    setPhone(vendorDetail["phone"]);
    setEmail(vendorDetail["email"]);
    setAddress(vendorDetail["address"]);
    setUrl(vendorDetail["url"]);
    setDescription(vendorDetail["description"]);
  };

  const successHandler = () => {
    successNoti("EDIT VENDOR SUCCESSFULLY", 3000);
    window.location.reload();
  };

  const errorHandler = {
    onError: (e) => {
      console.log(e);
      errorNoti("FAILED", 3000);
    },
  };

  const successHandlerDelete = () => {
		successNoti("DELETE VENDOR SUCCESSFULLY", 3000);
		history.push("/vendors");
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

  const handleCloseDelete = ()  => {
		setOpenDelete(false);
	};

  const deleteApi = async() => {
    await request("delete", `/vendor/delete/${params.id}`, successHandlerDelete, errorHandler, {});
    setOpenDelete(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const body = {
      name: name,
      phone: phone,
      email: email,
      address: address,
      url: url,
      description: description
    };

    await request("put", `/vendor/edit/${params.id}`, successHandler, errorHandler, body);
    handleClose();
  };

  useEffect(() => {
    getVendorDetail();
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
          title={<Typography variant="h5">Vendor Detail</Typography>}
        />
        <div style={{ float: "right", paddingRight: "20px" }}>
          <Button onClick={handleEdit}>Edit</Button>
          <Button onClick={handleDelete}>Delete</Button>
        </div>
        <CardContent style={{ paddingTop: "20px" }}>
          <Grid container className={classes.grid}>
            <Grid item md={3} sm={3} xs={3} direction={"column"}>
              <Typography>Name</Typography>
              <Typography>Phone</Typography>
              <Typography>Email</Typography>
              <Typography>Address</Typography>
              <Typography>URL</Typography>
              <Typography>Number of Assets</Typography>
              <Typography>Description</Typography>
            </Grid>
            <Grid item md={8} sm={8} xs={8}>
              <Typography>
                <b>:</b> {vendorDetail["name"]}
              </Typography>
              <Typography>
                <b>:</b> {vendorDetail["phone"]}
              </Typography>
              <Typography>
                <b>:</b> {vendorDetail["email"]}
              </Typography>
              <Typography>
                <b>:</b> {vendorDetail["address"]}
              </Typography>
              <Typography>
                <b>:</b> {vendorDetail["url"]}
              </Typography>
              <Typography>
                <b>:</b> 10
              </Typography>
              <Typography>
                <b>:</b> {vendorDetail["description"]}
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
          <div>EDIT VENDOR</div>
          <hr />
          <form onSubmit={(e) => handleSubmit(e)}>
            <TextField
              label="Name"
              variant="outlined"
              fullWidth
              name="name"
              margin="normal"
              placeholder="Vendor Name"
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
            <TextField
              label="Description"
              variant="outlined"
              fullWidth
              name="description"
              margin="normal"
              placeholder="Vendor Description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
            <TextField
              label="Email"
              variant="outlined"
              fullWidth
              name="email"
              margin="normal"
              placeholder="Vendor Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <div style={{ display: "flex", gap: "20px" }}>
              <TextField
                label="Phone"
                variant="outlined"
                fullWidth
                margin="normal"
                required
                name="phone"
                placeholder="Vendor phone"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
              />
              <TextField
                label="URL"
                variant="outlined"
                fullWidth
                margin="normal"
                required
                name="url"
                placeholder="Vendor url"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
              />
            </div>
            <TextField
              label="Address"
              variant="outlined"
              fullWidth
              margin="normal"
              required
              name="address"
              placeholder="Vendor Address"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
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
            Do you want to delete this vendor. It cannot be undone?
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

export default VendorDetail;
