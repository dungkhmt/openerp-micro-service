import { Box, FormControl, InputLabel, MenuItem, Select, TextField, Typography, Autocomplete, Divider, Switch, Alert, AlertTitle } from "@mui/material";
import PrimaryButton from "components/button/PrimaryButton";
import TertiaryButton from "components/button/TertiaryButton";
import CustomizedDialogs from "components/dialog/CustomizedDialogs";
import { AnimatePresence, motion } from "framer-motion";
import { grey } from "@mui/material/colors";
import React, { useEffect, useState } from "react";
import '../styles.scss';


const styles = {
    btn: { margin: "4px 8px" },
};

const FacilityModal = ({ open, setOpen }) => {
    const [type, setType] = useState();
    const [name, setName] = useState('');
    const [address, setAddress] = useState('');
    const [maxTruck, setMaxTruck] = useState();
    const [maxTrailer, setMaxTrailer] = useState();
    const [maxContainer, setMaxContainer] = useState();

    const typeConst = [
        { name: "Truck", id: "1" },
        { name: "Trailer", id: "2" },
        { name: "Container", id: "3" },
    ];
    const handleClose = () => {
        setOpen(false);
    }
    const handleSubmit = () => {
        const data = {
            facilityType: type,
            facilityName: name,
            address: address,
            maxNumberTruck: type === "Truck" ? maxTruck : null,
            maxNumberTrailer: type === "Trailer" ? maxTrailer : null,
            maxNumberContainer: type === "Container" ? maxContainer : null,
        }
        console.log("data", data);
        // request(
        //     "post",
        //     `/order/create`, {}, {}, data
        // ).then((res) => {
        //     console.log("res", res);
        //     if(!res) {
        //         setToastType("error");
        //     } else {
        //         setToastType("success");
        //     }
        //     handleClose();
        //     setToast(true);
        //     setTimeout(() => {
        //         setToast(false);
        //     }, "2000");
        // }).catch(err => {
        //     console.log("err", err);

        // })
    }
    return (
        <Box >
            <CustomizedDialogs
                open={open}
                handleClose={handleClose}
                contentTopDivider
                title="New Facility"
                className="modalFacility"
                content={
                    <AnimatePresence>
                        <motion.div
                        >
                            <motion.div
                                animate="center"
                                transition={{
                                    opacity: { duration: 0.1 },
                                }}
                            >
                                <Box pt="7px" pb={1} pl={1} pr={1} sx={{ width: "650px" }} className="contentModalFacility">
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Type:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <FormControl>
                                                <InputLabel id="demo-simple-select-label">Type</InputLabel>
                                                <Select
                                                    value={type}
                                                    onChange={(e) => setType(e.target.value)}
                                                    label="type"
                                                >
                                                    {typeConst ? (
                                                        typeConst.map((item) => {
                                                            return (
                                                                <MenuItem value={item.name}>{item.name}</MenuItem>
                                                            );
                                                        })
                                                    ) : null}
                                                </Select>
                                            </FormControl>
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Facility Name: </Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <TextField
                                                id="outlined-textarea"
                                                label="Name"
                                                placeholder="name"
                                                value={name}
                                                onChange={(e) => setName(e.target.value)}
                                            />
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Address:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <TextField
                                                id="outlined-textarea"
                                                label="Address"
                                                placeholder="address"
                                                multiline
                                                value={address}
                                                onChange={(e) => setAddress(e.target.value)}
                                            />
                                        </Box>
                                    </Box>
                                    {type === "Truck" ? (
                                        <Box className="contentModal-item">
                                            <Box className="contentModal-item-text">
                                                <Typography>Max Total Truck:</Typography>
                                            </Box>
                                            <Box className="contentModal-item-input">
                                                <TextField
                                                    id="outlined-textarea"
                                                    label="Address"
                                                    placeholder="address"
                                                    value={maxTruck}
                                                    onChange={(e) => setMaxTruck(e.target.value)}
                                                />
                                            </Box>
                                        </Box>
                                    ) : null}
                                    {type === "Trailer" ? (
                                        <Box className="contentModal-item">
                                            <Box className="contentModal-item-text">
                                                <Typography>Max Total Trailer:</Typography>
                                            </Box>
                                            <Box className="contentModal-item-input">
                                                <TextField
                                                    id="outlined-textarea"
                                                    label="Address"
                                                    placeholder="address"
                                                    value={maxTrailer}
                                                    onChange={(e) => setMaxTrailer(e.target.value)}
                                                />
                                            </Box>
                                        </Box>
                                    ) : null}
                                    {type === "Container" ? (
                                        <Box className="contentModal-item">
                                            <Box className="contentModal-item-text">
                                                <Typography>Max Total Container:</Typography>
                                            </Box>
                                            <Box className="contentModal-item-input">
                                                <TextField
                                                    id="outlined-textarea"
                                                    label="Address"
                                                    placeholder="address"
                                                    value={maxContainer}
                                                    onChange={(e) => setMaxContainer(e.target.value)}
                                                />
                                            </Box>
                                        </Box>
                                    ) : null}

                                    <Divider />
                                    <Box
                                        display="flex"
                                        justifyContent="flex-end"
                                        pt={2}
                                        ml={-1}
                                        mr={-1}
                                    >
                                        <TertiaryButton
                                            onClick={() => setOpen(false)}
                                            sx={styles.btn}
                                            style={{ width: 100 }}
                                        >
                                            Cancel
                                        </TertiaryButton>
                                        <PrimaryButton
                                            // disabled={
                                            //     isEmpty(feature) || isEmpty(detail) || isSubmitting
                                            // }
                                            sx={styles.btn}
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
export default FacilityModal;