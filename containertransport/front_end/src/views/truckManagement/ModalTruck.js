import { Box, Modal, Icon, Typography, Divider, TextField, Button, FormControl, Select, MenuItem, InputLabel } from "@mui/material";
import './styles.scss';
import React, { useEffect, useState } from "react";
import { request } from "api";

const ModalTruck = ({ openModal, handleClose, type, truckInfo }) => {
    const [facilityList, setFacilityList] = useState([]);
    const [facility, setFacility] = useState();
    const [driverName, setDriverName] = useState('');
    const [licensePlates, setLicensePlates] = useState('');
    const [brandTruck, setBrandTruck] = useState('');

    useEffect(() => {
        request(
            "post",
            `/facility/`, {}, {}, {}, {},
        ).then((res) => {
            setFacilityList(res.data.data);
        })
    }, []);

    const handleChange = (event) => {
        setFacility(event.target.value);
    };
    const handleSubmit = () => {
        const data = {
            facilityId: facility,
            driverName: driverName,
            licensePlates: licensePlates,
            brandTruck: brandTruck
        }
        console.log("avv==========", data);
        request(
            "post",
            `/truck/create`, {}, {}, data
        ).then((res) => {
            console.log(res);
            handleClose();
            setFacility();
            setBrandTruck('');
            setDriverName('');
            setLicensePlates('');
        })
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
                    <Typography className="header-modal-text">New Truck</Typography>
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
                                    inputProps={{ 'aria-label': 'Without label' }}
                                >
                                    {facilityList ? (
                                        facilityList.map((item, key) => {
                                            return (
                                                <MenuItem value={item.id}>{item.facilityName}</MenuItem>
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
                            onChange={(e) => setDriverName(e.target.value)} />
                        </Box>
                    </Box>
                    <Box className="body-modal-item">
                        <Box className="body-modal-item-text">
                            <Typography>License Plates:</Typography>
                        </Box>
                        <Box className="body-modal-item-input">
                            <TextField id="outlined-basic" label="license plates" variant="outlined" 
                            onChange={(e) => setLicensePlates(e.target.value)}/>
                        </Box>
                    </Box>
                    <Box className="body-modal-item">
                        <Box className="body-modal-item-text">
                            <Typography>Brand:</Typography>
                        </Box>
                        <Box className="body-modal-item-input">
                            <TextField id="outlined-basic" label="brand" variant="outlined" type="search"
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