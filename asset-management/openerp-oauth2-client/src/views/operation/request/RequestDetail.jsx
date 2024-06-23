import { makeStyles } from "@mui/styles";
import { useParams, useHistory } from "react-router-dom";
import React, { useEffect, useState } from "react";
import { request } from "api";
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
  Divider,
  Grid,
  Modal,
  TextField,
  Typography,
} from "@mui/material";
import { BiDetail } from "react-icons/bi";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DemoContainer } from "@mui/x-date-pickers/internals/demo";
import { errorNoti, successNoti } from "utils/notification";
import { convertToDate } from "utils/date";
import dayjs from "dayjs";

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

const RequestDetail = () => {
  const classes = useStyles();
  const params = useParams();
  const history = useHistory();

  const [requestDetail, setRequestDetail] = useState({});
  const [asset, setAsset] = useState({});
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [openDelete, setOpenDelete] = useState(false);

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);

  let status = "";

  const getRequestDetail = async () => {
    setLoading(true);
    await request("get", `/request/id/${params.id}`, (res) => {
      setRequestDetail(res.data);
    });
    setLoading(false);
  };

  const getAsset = async () => {
    await request("get", `/asset/id/${requestDetail["asset_id"]}`, (res) => {
      setAsset(res.data);
    });
  };

  const initData = () => {
    setName(requestDetail["name"]);
    setDescription(requestDetail["description"]);
    setStartDate(requestDetail["start_date"]);
    setEndDate(requestDetail["end_date"]);
  };

  if (requestDetail["status"] === 1) {
    status = "APPROVED";
  } else if (requestDetail["status" === -1]) {
    status = "REJECTED";
  } else {
    status = "PENDING";
  }

  const style = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: 800,
    bgcolor: "background.paper",
    border: "2px solid #000",
    boxShadow: 24,
    p: 4,
    gap: "30px",
  };

  const successHandler = () => {
    successNoti("EDIT REQUEST SUCCESSFULLY", 3000);
    window.location.reload();
  };

  const errorHandlers = {
    onError: (e) => {
      console.log(e);
      errorNoti("FAILED", 3000);
    },
  };

  const successHandlerDelete = () => {
    successNoti("DELETE REQUEST SUCCESSFULLY", 3000);
    history.push("/requests");
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleEdit = () => {
    initData();
    setOpen(true);
  };

  const handleDelete = () => {
    setOpenDelete(true);
  };

  const handleCloseDelete = () => {
    setOpenDelete(false);
  };

  const deleteApi = () => {
    request(
      "delete",
      `/request/delete/${params.id}`,
      successHandlerDelete,
      errorHandlers,
      {}
    );
    setOpenDelete(false);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const body = {
      name: name,
      description: description,
      start_date: startDate,
      end_date: endDate,
    };

    request(
      "put",
      `/request/edit/${params.id}`,
      successHandler,
      errorHandlers,
      body
    );
  };

  useEffect(() => {
    getRequestDetail();
  }, []);

  useEffect(() => {
    getAsset();
  }, [requestDetail]);

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
          title={<Typography variant="h5">Request Detail</Typography>}
        />
        <div style={{ float: "right", paddingRight: "20px" }}>
          <Button onClick={handleEdit}>Edit</Button>
          <Button onClick={handleDelete}>Remove</Button>
        </div>
        <CardContent style={{ paddingTop: "20px" }}>
          <Grid container className={classes.grid}>
            <Grid item md={3} sm={3} xs={3} direction={"column"}>
              <Typography>Request Name</Typography>
              <Typography>Request By</Typography>
              <Typography>Asset</Typography>
              <Typography>Approver</Typography>
              <Typography>Request Description</Typography>
              <Typography>Start Date</Typography>
              <Typography>End Date</Typography>
              <Typography>STATUS</Typography>
              <Typography>Description</Typography>
            </Grid>
            <Grid item md={8} sm={8} xs={8}>
              <Typography>
                <b>:</b> {requestDetail["name"]}
              </Typography>
              <Typography>
                <b>:</b> {requestDetail["user_id"]}
              </Typography>
              <Typography>
                <b>:</b> {asset["name"]}
              </Typography>
              <Typography>
                <b>:</b> {requestDetail["admin_id"]}
              </Typography>
              <Typography>
                <b>:</b> {requestDetail["description"]}
              </Typography>
              <Typography>
                <b>:</b> {convertToDate(requestDetail["start_date"])}
              </Typography>
              <Typography>
                <b>:</b> {convertToDate(requestDetail["end_date"])}
              </Typography>
              <Typography>
                <b>:</b> {status}
              </Typography>
              <Typography>
                <b>:</b> {requestDetail["description"]}
              </Typography>
            </Grid>

            <div className={classes.divider}>
              <Divider
                variant="fullWidth"
                classes={{ root: classes.rootDivider }}
              />
            </div>
          </Grid>
          {requestDetail["status"] === 0 && (
            <Grid item md={5} sm={5} xs={5}>
              <div
                style={{ display: "flex", alignItems: "center", gap: "20px" }}
              >
                <Button variant="contained" color="success">
                  Approved
                </Button>
                <Button variant="contained" color="error">
                  Reject
                </Button>
              </div>
            </Grid>
          )}
        </CardContent>
      </Card>
      <Modal
        open={open}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
        onClose={handleClose}
      >
        <Box sx={style}>
          <div>EDIT REQUEST</div>
          <hr />
          <form onSubmit={handleSubmit}>
            <TextField
              label="Name"
              variant="outlined"
              fullWidth
              margin="normal"
              required
              value={name}
              name="name"
              placeholder="Request name"
              onChange={(e) => setName(e.target.value)}
            />
            <TextField
              label="Description"
              variant="outlined"
              fullWidth
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              margin="normal"
              name="description"
              placeholder="Request description"
            />
            <TextField
              disabled
              sx={{ minWidth: 730, marginTop: "20px" }}
              id="outlined-disabled"
              label="Asset"
              value={name}
            />
            <div style={{ marginTop: "20px" }}>
              <LocalizationProvider
                dateAdapter={AdapterDayjs}
                className="date-adapter"
              >
                <DemoContainer components={["DatePicker", "DatePicker"]}>
                  <DatePicker
                    className="date-picker-request"
                    label="Start Date"
                    value={dayjs(convertToDate(startDate))}
                    onChange={(newValue) => setStartDate(newValue)}
                  />
                  <DatePicker
                    className="date-picker-request"
                    label="End Date"
                    value={dayjs(convertToDate(endDate))}
                    onChange={(newValue) => setEndDate(newValue)}
                  />
                </DemoContainer>
              </LocalizationProvider>
            </div>
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
        aria-description="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          {"DELETE THIS REQUEST"}
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Do you want to delete this request. It cannot be undone?
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

export default RequestDetail;
