import React, { useContext, useEffect, useState } from "react";
import '../styles.scss';
import { Box, FormControl, InputLabel, MenuItem, Select, TextField, Typography, Autocomplete, Divider, Switch, Alert, AlertTitle } from "@mui/material";
import CustomizedDialogs from "components/dialog/CustomizedDialogs";
import { AnimatePresence, motion } from "framer-motion";
import { getFacility } from "api/FacilityAPI";
import TertiaryButton from "components/button/TertiaryButton";
import PrimaryButton from "components/button/PrimaryButton";


const ModalContainer = ({ open, handleClose }) => {
    const [facilityList, setFacilityList] = useState([]);
    const [facility, setFacility] = useState('');

    useEffect(() => {
        getFacility({ type: "Depot" }).then((res) => {
            setFacilityList(res?.data.data.facilityModels);
            // if(truckId) {
            //     setFacility(res?.data.data.facilityModels.find((item) => item.id === truck?.facilityResponsiveDTO.facilityId))
            // }
        });
        // if(truckId) {
        //     setBrandTruck(truck?.brandTruck);
        //     setLicensePlates(truck?.licensePlates);
        //     setDriverName(truck?.driverName);
        // }
    }, []);

    const handleChange = (event) => {
        setFacility(event.target.value);
    };


    return (
        <Box>
            <CustomizedDialogs
                open={open}
                handleClose={handleClose}
                contentTopDivider
                title="New Container"
                className="modalContainer"
                content={
                    <AnimatePresence>
                        <motion.div>
                            <motion.div
                                animate="center"
                                transition={{
                                    opacity: { duration: 0.1 },
                                }}
                            >
                                <Box pt="7px" pb={1} pl={1} pr={1} 
                                sx={{ width: "650px" }}
                                 className="contentModal">
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Facility:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
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
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Size:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <TextField
                                                id="outlined-textarea"
                                                label="size"
                                                placeholder="size"
                                            />
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Break Romooc:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <Switch
                                                // value={isBreakRomooc}
                                                // checked={isBreakRomooc}
                                                // onChange={handleChangeBreakRomooc}
                                                inputProps={{ 'aria-label': 'controlled' }}
                                            />
                                        </Box>
                                    </Box>

                                    <Divider />
                                    <Box
                                        display="flex"
                                        justifyContent="flex-end"
                                        pt={2} ml={-1} mr={-1}
                                    >
                                        <TertiaryButton
                                            onClick={() => handleClose()}
                                            sx={{ margin: "4px 8px" }}
                                            style={{ width: 100 }}
                                        >
                                            Cancel
                                        </TertiaryButton>
                                        <PrimaryButton
                                            // disabled={
                                            //     isEmpty(feature) || isEmpty(detail) || isSubmitting
                                            // }
                                            sx={{ margin: "4px 8px" }}
                                            style={{ width: 100 }}
                                            // onClick={handleSubmit}
                                        >
                                            {false ? "Đang gửi..." : "Submit"}
                                        </PrimaryButton>
                                    </Box>
                                </Box>
                            </motion.div>
                        </motion.div>
                    </AnimatePresence>
                }
            />
        </Box>
    )
}
export default ModalContainer;