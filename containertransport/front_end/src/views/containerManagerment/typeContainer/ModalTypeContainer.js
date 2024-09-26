import React, { useContext, useEffect, useState } from "react";
import '../styles.scss';
import { Box, FormControl, InputLabel, MenuItem, Select, TextField, Typography, Autocomplete, Divider, Switch, Alert, AlertTitle } from "@mui/material";
import CustomizedDialogs from "components/dialog/CustomizedDialogs";
import { AnimatePresence, motion } from "framer-motion";
import { getFacility } from "api/FacilityAPI";
import TertiaryButton from "components/button/TertiaryButton";
import PrimaryButton from "components/button/PrimaryButton";
import { createContainer, createTypeContainer, getTypeContainer, updateContainer } from "api/ContainerAPI";


const ModalTypeContainer = ({ open, handleClose, container, setToast, setToastType, setToastMsg }) => {
    const [facilityList, setFacilityList] = useState([]);
    const [facility, setFacility] = useState('');
    const [size, setSize] = useState('');
    const [description, setDescription] = useState('');

    useEffect(() => {
        getFacility({ type: "Depot" }).then((res) => {
            setFacilityList(res?.data.data.facilityModels);
            // if(truckId) {
            //     setFacility(res?.data.data.facilityModels.find((item) => item.id === truck?.facilityResponsiveDTO.facilityId))
            // }
        });
        if (container) {
            setFacility(container?.facilityResponsiveDTO.facilityId);
            setDescription(container?.containerCode);
        }
    }, []);

    const handleChange = (event) => {
        setFacility(event.target.value);
    };
    const handleChangeSize = (event) => {
        setSize(event.target.value);
    };
    const handleSubmit = () => {
        if (container) {
            let data = {
                id: container.id,
                facilityId: facility,
                typeContainerCode: size?.typeContainerCode,
            };
            console.log("data", data)
            updateContainer(data).then((res) => {
                setToastMsg("Update Container Success");
                setToastType("success");
                setToast(true);
                setTimeout(() => {
                    setToast(false);
                }, "2000");
                handleClose();
                clearData();
            });
        }
        else {
            let data = {
                size: size,
                description: description,
            };
            console.log("data", data)
            createTypeContainer(data).then((res) => {
                setToastMsg("Create Type Container Success");
                setToastType("success");
                setToast(true);
                setTimeout(() => {
                    setToast(false);
                }, "2000");
                handleClose();
                clearData();
            });
        }
    }
    const clearData = () => {
        setFacility('');
        setSize('');
        setFacilityList([]);
    }
    return (
        <Box>
            <CustomizedDialogs
                open={open}
                handleClose={handleClose}
                contentTopDivider
                title={container ? "Update Container" : "New Type Container"}
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
                                            <Typography>Size:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <TextField
                                                id="outlined-textarea"
                                                label="Size"
                                                placeholder="size"
                                                value={size}
                                                size="small"
                                                onChange={(e) => setSize(e.target.value)}
                                            />
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Description: </Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <TextField
                                                id="outlined-textarea"
                                                label="Description"
                                                placeholder="description"
                                                value={description}
                                                size="small"
                                                onChange={(e) => setDescription(e.target.value)}
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
                                            onClick={handleSubmit}
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
export default ModalTypeContainer;