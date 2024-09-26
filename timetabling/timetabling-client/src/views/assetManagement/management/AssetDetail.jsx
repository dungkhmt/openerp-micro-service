import { makeStyles } from "@mui/styles";
import { useParams, useHistory } from "react-router-dom";
import React, { useEffect, useState } from "react";
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
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Modal,
  Select,
  TextField,
  Typography,
} from "@mui/material";
import { request } from "api";
import { BiDetail } from "react-icons/bi";
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

const AssetDetail = () => {
  const classes = useStyles();
  const params = useParams();
  const history = useHistory();

  const [assetDetail, setAssetDetail] = useState({});
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [openRepair, setOpenRepair] = useState(false);
  const [openDeprecated, setOpenDeprecated] = useState(false);
  const [openDelete, setOpenDelete] = useState(false);

  const [locations, setLocations] = useState([]);
  const [vendors, setVendors] = useState([]);
  const [types, setTypes] = useState([]);
  const [users, setUsers] = useState([]);

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [locationName, setLocationName] = useState("");
  const [vendorName, setVendorName] = useState("");
  const [typeName, setTypeName] = useState("");
  const [admin, setAdmin] = useState("");

  let status = "";
  let type_name = "";
  let vendor_name = "";
  let location_name = "";

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

  const getAssetDetail = async () => {
    setLoading(true);
    await request("get", `/asset/id/${params.id}`, (res) => {
      setAssetDetail(res.data);
    });
    setLoading(false);
  };

  const getAllLocations = async () => {
    await request("get", "/location/get-all", (res) => {
      setLocations(res.data);
    }).then();
  };

  const getAllVendors = async () => {
    await request("get", "/vendor/get-all", (res) => {
      setVendors(res.data);
    }).then();
  };

  const getAllTypes = async () => {
    await request("get", "/asset-type/get-all", (res) => {
      setTypes(res.data);
    }).then();
  };

  const getAllUsers = async () => {
    await request("get", "/user/get-all", (res) => {
      setUsers(res.data);
    });
  };

  const syncData = () => {
    if (
      Object.keys(assetDetail).length &&
      types.length &&
      locations.length &&
      vendors.length
    ) {
      const type = types.find((item) => item.id === assetDetail["type_id"]);
      const location = locations.find(
        (item) => item.id === assetDetail["location_id"]
      );
      const vendor = vendors.find(
        (item) => item.id === assetDetail["vendor_id"]
      );
      type_name = type["name"];
      location_name = location["name"];
      vendor_name = vendor["name"];
    }
  };

  const initData = () => {
    setName(assetDetail["name"]);
    setDescription(assetDetail["description"]);
    setAdmin(assetDetail["admin_id"]);
    setTypeName(type_name);
    setVendorName(vendor_name);
    setLocationName(location_name);
  };

  if (assetDetail["status_id"] === 1) {
    status = "AVAILABLE";
  } else if (assetDetail["status_id"] === 2) {
    status = "INUSE";
  } else if (assetDetail["status_id"] === 3) {
    status = "REPAIR";
  } else {
    status = "DEPRECATED";
  }

  const successHandler = () => {
    successNoti("EDIT ASSET SUCCESSFULLY", 3000);
    window.location.reload();
  };

  const errorHandlers = {
    onError: (e) => {
      console.log(e);
      errorNoti("FAILED", 3000);
    },
  };

  const successHandlerDelete = () => {
    successNoti("DELETE ASSET SUCCESSFULLY", 3000);
    history.push("/assets");
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleCloseRepair = () => {
    setOpenRepair(false);
  };

  const handleCloseDeprecated = () => {
    setOpenDeprecated(false);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const foundLocation = locations.find(
      (location) => location.name === locationName
    );
    const foundVendor = vendors.find((vendor) => vendor.name === vendorName);
    const foundType = types.find((item) => item.name === typeName);
    const foundAdmin = users.find((user) => user.id === admin);

    const body = {
      name: name,
      description: description,
      location_id: foundLocation ? foundLocation.id : 0,
      vendor_id: foundVendor ? foundVendor.id : 0,
      type_id: foundType ? foundType.id : 0,
      admin_id: foundAdmin.id,
    };

    request(
      "put",
      `/asset/edit/${params.id}`,
      successHandler,
      errorHandlers,
      body
    );
    handleClose();
  };

  const handleEdit = () => {
    initData();
    setOpen(true);
  };

  const handleRepair = () => {
    setOpenRepair(true);
  };

  const handleDeprecated = () => {
    setOpenDeprecated(true);
  };

  const handleDelete = () => {
    setOpenDelete(true);
  };

  const handleCloseDelete = () => {
    setOpenDelete(false);
  };

  const repairApi = async () => {
    let is_repair = true;
    if (assetDetail["status_id"] === 1 || assetDetail["status_id"] === 2) {
      await request(
        "put",
        `/asset/repair/${params.id}/${is_repair}`,
        successHandler,
        errorHandlers,
        {}
      );
    } else if (assetDetail["status_id"] === 3) {
      is_repair = false;
      await request(
        "put",
        `/asset/repair/${params.id}/${is_repair}`,
        successHandler,
        errorHandlers,
        {}
      );
    }

    setOpenRepair(false);
  };

  const deprecatedApi = async () => {
    await request(
      "put",
      `/asset/deprecated/${params.id}`,
      successHandler,
      errorHandlers,
      {}
    );
    setOpenDeprecated(false);
  };

  const deleteApi = () => {
    request(
      "delete",
      `/asset/delete/${params.id}`,
      successHandlerDelete,
      errorHandlers,
      {}
    );
    setOpenDelete(false);
  };

  syncData();

  useEffect(() => {
    getAssetDetail();
    getAllLocations();
    getAllVendors();
    getAllTypes();
    getAllUsers();
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
          title={<Typography variant="h5">Asset Detail</Typography>}
        />
        <div style={{ float: "right", paddingRight: "20px" }}>
          {/* {assetDetail["status_id"] === 1 && <Button>Assign</Button>} */}
          <Button onClick={handleEdit}>Edit</Button>
          <Button onClick={handleRepair}>Repair</Button>
          {assetDetail["status_id"] !== 4 && (
            <Button onClick={handleDeprecated}>Deprecated</Button>
          )}
          <Button onClick={handleDelete}>Delete</Button>
        </div>
        <CardContent style={{ paddingTop: "20px" }}>
          <Grid container className={classes.grid}>
            <Grid item md={3} sm={3} xs={3} direction={"column"}>
              <Typography>Asset Name</Typography>
              <Typography>Admin</Typography>
              <Typography>Assignee</Typography>
              <Typography>Code</Typography>
              <Typography>Type</Typography>
              <Typography>Location</Typography>
              <Typography>Vendor</Typography>
              <Typography>Status</Typography>
              <Typography>Description</Typography>
            </Grid>
            <Grid item md={8} sm={8} xs={8}>
              <Typography>
                <b>:</b> {assetDetail["name"]}
              </Typography>
              <Typography>
                <b>:</b> {assetDetail["admin_id"]}
              </Typography>
              <Typography>
                <b>:</b> {assetDetail["assignee_id"]}
              </Typography>
              <Typography>
                <b>:</b> {assetDetail["code"]}
              </Typography>
              <Typography>
                <b>:</b> {type_name}
              </Typography>
              <Typography>
                <b>:</b> {location_name}
              </Typography>
              <Typography>
                <b>:</b> {vendor_name}
              </Typography>
              <Typography>
                <b>:</b> {status}
              </Typography>
              <Typography>
                <b>:</b> {assetDetail["description"]}
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
          <div>EDIT ASSET</div>
          <hr />
          <form onSubmit={(e) => handleSubmit(e)}>
            <TextField
              label="Name"
              variant="outlined"
              fullWidth
              margin="normal"
              required
              name="name"
              placeholder="Asset name"
              onChange={(e) => setName(e.target.value)}
              value={name}
            />
            <TextField
              label="Description"
              variant="outlined"
              fullWidth
              margin="normal"
              name="description"
              placeholder="Asset description"
              onChange={(e) => setDescription(e.target.value)}
              value={description}
            />
            <FormControl sx={{ minWidth: 255, marginTop: "20px" }}>
              <InputLabel id="demo-simple-select-label">Type</InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={typeName}
                label="Type"
                onChange={(e) => setTypeName(e.target.value)}
                renderValue={() => typeName}
              >
                {types.map((type) => (
                  <MenuItem id={type.id} value={type.name}>
                    {type.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <FormControl
              sx={{ marginLeft: "20px", minWidth: 255, marginTop: "20px" }}
            >
              <InputLabel id="demo-simple-select-label">Admin</InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={admin}
                label="Admin"
                onChange={(e) => setAdmin(e.target.value)}
                renderValue={() => admin}
              >
                {users.map((user) => (
                  <MenuItem key={user.id} value={user.id}>
                    {user.id}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <FormControl sx={{ minWidth: 255, marginTop: "20px" }}>
              <InputLabel id="demo-simple-select-label">Location</InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={locationName}
                label="Location"
                onChange={(e) => setLocationName(e.target.value)}
              >
                {locations.map((location) => (
                  <MenuItem key={location.id} value={location.name}>
                    {location.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <FormControl
              sx={{ marginLeft: "20px", minWidth: 255, marginTop: "20px" }}
            >
              <InputLabel id="demo-simple-select-label">Vendor</InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={vendorName}
                label="Vendor"
                onChange={(e) => setVendorName(e.target.value)}
              >
                {vendors.map((vendor) => (
                  <MenuItem key={vendor.id} value={vendor.name}>
                    {vendor.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
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
        open={openRepair}
        onClose={handleCloseRepair}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{"REPAIR THIS ASSET"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            {assetDetail["status_id"] !== 3
              ? `Do you want to repair this asset. This asset will be repaired and cannot be used when finish repairing.`
              : `Finish repairing this asset? Now it will be available and can be use.`}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button variant="outlined" onClick={handleCloseRepair}>
            CANCEL
          </Button>
          <Button
            variant="outlined"
            color="success"
            onClick={repairApi}
            autoFocus
          >
            {assetDetail["status_id"] !== 3 ? `REPAIR` : `FINISH REPAIR`}
          </Button>
        </DialogActions>
      </Dialog>
      <Dialog
        open={openDeprecated}
        onClose={handleCloseDeprecated}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          {"DEPRECATE THIS ASSET"}
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Do you want to deprecate this asset. This asset will be deprecated
            and cannot be used anymore.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button variant="outlined" onClick={handleCloseDeprecated}>
            CANCEL
          </Button>
          <Button
            variant="outlined"
            color="success"
            onClick={deprecatedApi}
            autoFocus
          >
            DEPRECATE
          </Button>
        </DialogActions>
      </Dialog>
      <Dialog
        open={openDelete}
        onClose={handleCloseDelete}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{"DELETE THIS ASSET"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Do you want to delete this asset. It cannot be undone?
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

export default AssetDetail;
