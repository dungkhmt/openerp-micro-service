import { Box, Modal, Icon, Typography, Divider, TextField, Button, FormControl, Select, MenuItem, InputLabel } from "@mui/material";
import './styles.scss';
import React, { useEffect, useState } from "react";
import { getFacility, getFacilityById } from "api/FacilityAPI";
import { createTruck, updateTruck } from "api/TruckAPI";
import { trip } from "config/menuconfig/shipment";

const ModalTruck = ({ openModal, handleClose, truckId, truck, setToast, setToastType, setToastMsg }) => {
    const [facilityList, setFacilityList] = useState([]);
    const [facility, setFacility] = useState({});
    const [driverName, setDriverName] = useState('');
    const [licensePlates, setLicensePlates] = useState('');
    const [brandTruck, setBrandTruck] = useState('');

    useEffect(() => {
        getFacility({ type: "Truck" }).then((res) => {
            setFacilityList(res?.data.data.facilityModels);
            if(truckId) {
                setFacility(res?.data.data.facilityModels.find((item) => item.id === truck?.facilityResponsiveDTO.facilityId))
            }
        });
        if(truckId) {
            setBrandTruck(truck?.brandTruck);
            setLicensePlates(truck?.licensePlates);
            setDriverName(truck?.driverName);
        }
    }, []);

    const handleChange = (event) => {
        setFacility(event.target.value);
    };
    const handleSubmit = () => {
        const data = {
            facilityId: facility?.id,
            driverName: driverName,
            licensePlates: licensePlates,
            brandTruck: brandTruck
        }
        console.log("avv==========", data);
        if (truckId) {
            updateTruck(truckId, data).then((res) => {
                console.log(res);
                setToastMsg("Update Info Truck Success");
                setToastType("success");
                setToast(true);
                setTimeout(() => {
                    setToast(false);
                }, "2000");
                handleClose();
                clearData();
            })
        } else {
            createTruck(data).then((res) => {
                console.log(res);
                setToastMsg("Create Truck Success");
                setToastType("success");
                setToast(true);
                setTimeout(() => {
                    setToast(false);
                }, "2000");
                handleClose();
                clearData();
            })
        }
    }
    const clearData = () => {
        setFacility();
        setBrandTruck('');
        setDriverName('');
        setLicensePlates('');
    }
    return (
        <Modal
            open={openModal}
            onClose={handleClose}
            aria-labelledby="modal-modal-title"
            aria-describedby="modal-modal-description"
        >
            <Box className="modal">
                <Box className="header-modal">
                    <Typography className="header-modal-text">
                    {truckId ? "Update Truck" : "New Truck"}</Typography>
                </Box>
                <Divider sx={{ mb: 4, mt: 4 }} />
                <Box className="body-modal">
                    <Box className="body-modal-item">
                        <Box className="body-modal-item-text">
                            <Typography>Facility:</Typography>
                        </Box>
                        <Box className="body-modal-item-input">
                            <FormControl>
                                <InputLabel id="demo-simple-select-label">facility</InputLabel>
                                <Select
                                    value={facility}
                                    onChange={handleChange}
                                    label="facility"
                                >
                                    {facilityList ? (
                                        facilityList.map((item, key) => {
                                            return (
                                                <MenuItem key={key} value={item}>{item.facilityName}</MenuItem>
                                            );
                                        })
                                    ) : null}
                                </Select>
                            </FormControl>
                        </Box>
                    </Box>
                    <Box className="body-modal-item">
                        <Box className="body-modal-item-text">
                            <Typography>Driver:</Typography>
                        </Box>
                        <Box className="body-modal-item-input">
                            <TextField id="outlined-basic" label="driver" variant="outlined"
                                value={driverName}
                                onChange={(e) => setDriverName(e.target.value)} />
                        </Box>
                    </Box>
                    <Box className="body-modal-item">
                        <Box className="body-modal-item-text">
                            <Typography>License Plates:</Typography>
                        </Box>
                        <Box className="body-modal-item-input">
                            <TextField id="outlined-basic" label="license plates" variant="outlined"
                                value={licensePlates}
                                onChange={(e) => setLicensePlates(e.target.value)} />
                        </Box>
                    </Box>
                    <Box className="body-modal-item">
                        <Box className="body-modal-item-text">
                            <Typography>Brand:</Typography>
                        </Box>
                        <Box className="body-modal-item-input">
                            <TextField id="outlined-basic" label="brand" variant="outlined" type="search"
                                value={brandTruck}
                                onChange={(e) => setBrandTruck(e.target.value)} />
                        </Box>
                    </Box>
                </Box>
                <Divider />
                <Box className="footer-modal">
                    <Box className="btn-modal">
                        <Box>
                            <Button variant="outlined" color="error" onClick={handleClose}>Cancel</Button>
                        </Box>
                        <Box>
                            <Button variant="contained" onClick={handleSubmit}>Save</Button>
                        </Box>
                    </Box>
                </Box>
            </Box>
        </Modal>
    );
};
export default ModalTruck;