import { Box, Modal, Icon, Typography, Divider, TextField, Button, FormControl, Select, MenuItem, InputLabel } from "@mui/material";
import './styles.scss';
import React, { useEffect, useState } from "react";
import { getFacility, getFacilityById } from "api/FacilityAPI";
import { createTruck, getAllDriver, getTrucks, updateTruck } from "api/TruckAPI";
import { trip } from "config/menuconfig/shipment";

const ModalTruck = ({ openModal, handleClose, truckId, truck, setToast, setToastType, setToastMsg }) => {
    const [facilityList, setFacilityList] = useState([]);
    const [facility, setFacility] = useState({});
    const [driverName, setDriverName] = useState('');
    const [driverId, setDriverId] = useState('');
    const [licensePlates, setLicensePlates] = useState('');
    const [brandTruck, setBrandTruck] = useState('');
    const [yearOfManufacture, setYearOfManufacture] = useState('');

    const [flagTruck, setFlagTruck] = useState(false);
    const [driverNotUses, setDriverNotUses] = useState([]);
    const [allDriver, setAllDriver] = useState([]);
    const [driverSelect, setDriverSelect] = useState({});

    useEffect(() => {
        getFacility({ type: "Truck" }).then((res) => {
            setFacilityList(res?.data.data.facilityModels);
            if (truckId) {
                setFacility(res?.data.data.facilityModels.find((item) => item.id === truck?.facilityResponsiveDTO.facilityId))
            }
        });
        getAllDriver().then((res) => {
            setAllDriver(res?.data);
            setFlagTruck(!flagTruck);
        });

        if (truckId) {
            setBrandTruck(truck?.brandTruck);
            setLicensePlates(truck?.licensePlates);
            setDriverName(truck?.driverName);
            setYearOfManufacture(truck?.yearOfManufacture);
        }
    }, []);

    useEffect(() => {
        let driverIds = [];
        let driverNotChose = [];
        getTrucks({}).then((res) => {
            if (res?.data?.truckModels?.length > 0) {
                res?.data?.truckModels.forEach(element => {
                    driverIds.push(element?.driverId)
                })
                allDriver?.forEach(element => {
                    if (!driverIds.includes(element?.username) || (truck && truck?.driverId === element?.username)) {
                        driverNotChose.push(element);
                        if(truck && truck?.driverId === element?.username) {
                            setDriverSelect(element);
                        }
                    }
                })
                setDriverNotUses(driverNotChose);
            }
        })
    }, [flagTruck]);
    const handleChange = (event) => {
        setFacility(event.target.value);
    };
    const handleChangeDriver = (event) => {
        setDriverId(event.target.value.username);
        setDriverName(event.target.value.firstName + " " + event.target.value.lastName);
        setDriverSelect(event.target.value);
    };
    const handleSubmit = () => {
        const data = {
            facilityId: facility?.id,
            driverName: driverName,
            driverId: driverId,
            licensePlates: licensePlates,
            brandTruck: brandTruck,
            yearOfManufacture: yearOfManufacture
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
    console.log("driverNotUses", driverNotUses)
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
                            <FormControl>
                                <InputLabel id="demo-simple-select-label">Driver</InputLabel>
                                <Select
                                    value={driverSelect}
                                    onChange={handleChangeDriver}
                                    label="driver"
                                >
                                    {driverNotUses ? (
                                        driverNotUses.map((item, key) => {
                                            return (
                                                <MenuItem key={key} value={item}>{item.username}</MenuItem>
                                            );
                                        })
                                    ) : null}
                                </Select>
                            </FormControl>
                        </Box>
                        {/* <Box className="body-modal-item-input">
                            <TextField id="outlined-basic" label="driver" variant="outlined"
                                value={driverName}
                                onChange={(e) => setDriverName(e.target.value)} />
                        </Box> */}
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
                    <Box className="body-modal-item">
                        <Box className="body-modal-item-text">
                            <Typography>Year of manufacture:</Typography>
                        </Box>
                        <Box className="body-modal-item-input">
                            <TextField id="outlined-basic" label="year of manufacture" variant="outlined" type="search"
                                value={yearOfManufacture}
                                onChange={(e) => setYearOfManufacture(e.target.value)} />
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