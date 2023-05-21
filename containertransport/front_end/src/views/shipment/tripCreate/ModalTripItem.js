import { Box, Modal, Icon, Typography, Divider, TextField, Button, FormControl, Select, MenuItem, InputLabel } from "@mui/material";
import '../styles.scss';
import React, { useEffect, useState } from "react";
import { request } from "api";

const ModalTripItem = ({ openModal, handleModal, truckInfo }) => {
    const [facilityList, setFacilityList] = useState([]);
    const [facility, setFacility] = useState();
    const [trailer, setTrailer] = useState('');
    const [action, setAction] = useState('');
    const [type, setType] = useState('');
    const actionConst = [
        { name: "PICK TRAILER", id: "1" },
        { name: "DROP TRAILER", id: "2" },
        { name: "STOP", id: "3" },
    ];
    const typeConst = [
        { name: "Trailer", id: "1" },
        { name: "Truck", id: "2" },
    ];

    useEffect(() => {
        request(
            "post",
            `/facility/`, {}, {}, {}, {},
        ).then((res) => {
            setFacilityList(res.data.data);
        })
    }, []);

    const handleChange = (event) => {
        // setFacility(event.target.value);
    };
    const handleChangeType = (event) => {
        setType(event.target.value);
    };
    const handleChangeTrailer = (event) => {
        setTrailer(event.target.value);
    };
    const handleChangeAction = (event) => {
        setAction(event.target.value);
    };
    const handleSubmit = () => {
        
    }
console.log("type", type)
    return (
        <Modal
            open={openModal}
            onClose={handleModal}
            aria-labelledby="modal-modal-title"
            aria-describedby="modal-modal-description"
        >
            <Box className="modal">
                <Box className="header-modal">
                    <Typography className="header-modal-text">Add TripItem</Typography>
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
                            <Typography>Type:</Typography>
                        </Box>
                        <Box className="body-modal-item-input">
                            <FormControl>
                                <InputLabel id="demo-simple-select-label">type</InputLabel>
                                <Select
                                    value={type}
                                    onChange={handleChangeType}
                                    label="facility"
                                    inputProps={{ 'aria-label': 'Without label' }}
                                >
                                    {typeConst ? (
                                        typeConst.map((item, key) => {
                                            return (
                                                <MenuItem value={item.name}>{item.name}</MenuItem>
                                            );
                                        })
                                    ) : null}
                                </Select>
                            </FormControl>
                        </Box>
                    </Box>
                    {type && type === "Trailer" ? (<Box className="body-modal-item">
                        <Box className="body-modal-item-text">
                            <Typography>Trailer:</Typography>
                        </Box>
                        <Box className="body-modal-item-input">
                            <FormControl>
                                <InputLabel id="demo-simple-select-label">trailer</InputLabel>
                                <Select
                                    value={facility}
                                    onChange={handleChangeTrailer}
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
                    </Box>) : null}
                    <Box className="body-modal-item">
                        <Box className="body-modal-item-text">
                            <Typography>Action:</Typography>
                        </Box>
                        <Box className="body-modal-item-input">
                            <FormControl>
                                <InputLabel id="demo-simple-select-label">action</InputLabel>
                                <Select
                                    value={action}
                                    onChange={handleChangeAction}
                                    label="facility"
                                    inputProps={{ 'aria-label': 'Without label' }}
                                >
                                    {actionConst ? (
                                        actionConst.map((item, key) => {
                                            return (
                                                <MenuItem value={item.name}>{item.name}</MenuItem>
                                            );
                                        })
                                    ) : null}
                                </Select>
                            </FormControl>
                        </Box>
                    </Box>
                </Box>
                <Divider />
                <Box className="footer-modal">
                    <Box className="btn-modal">
                        <Box>
                            <Button variant="outlined" color="error" onClick={handleModal}>Cancel</Button>
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
export default ModalTripItem;