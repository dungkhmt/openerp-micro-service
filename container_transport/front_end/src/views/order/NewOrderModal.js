import { Box, FormControl, InputLabel, MenuItem, Select, TextField, Typography, Autocomplete, Divider, Switch, Alert, AlertTitle } from "@mui/material";
import PrimaryButton from "components/button/PrimaryButton";
import TertiaryButton from "components/button/TertiaryButton";
import CustomizedDialogs from "components/dialog/CustomizedDialogs";
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { AnimatePresence, motion } from "framer-motion";
import { grey } from "@mui/material/colors";
import React, { useEffect, useState } from "react";
import { request } from "api";
import './styles.scss';
import { LocalizationProvider } from "@mui/x-date-pickers";


const styles = {
    backButton: {
        width: 40,
        height: 40,
        position: "absolute",
        top: 8 * 1.5,
        left: 16,
        color: "rgba(0, 0, 0, 0.5)",
        background: grey[300],
        "&:hover": {
            background: grey[400],
        },
    },
    avatarIcon: {
        width: 60,
        height: 60,
        backgroundColor: grey[300],
        margin: "8px 12px 8px 0px",
    },
    list: {
        paddingTop: 0.5,
        paddingBottom: 0,
    },
    listItem: {
        borderRadius: 2,
        padding: "0 8px",
        "&:hover": {
            background: grey[100],
        },
    },
    listItemTextPrimary: {
        fontWeight: 500,
        fontSize: 17,
    },
    btn: { margin: "4px 8px" },
};

const NewOrderModal = ({ open, setOpen, setToast, setToastType }) => {
    const [type, setType] = useState();
    const [facilities, setFacilities] = useState([]);
    const [fromFacility, setFromFacility] = useState();
    const [toFacility, setToFaciity] = useState();
    const [containers, setContainers] = useState([]);
    const [containerSelect, setContainerSelect] = useState([]);
    const [earlyDeliveryTime, setEarlyDeliveryTime] = useState();
    const [lateDeliveryTime, setLateDeliveryTime] = useState();
    const [earlyPickUpTime, setEarlyPickUpTime] = useState();
    const [latePickUpTime, setLatePickUpTime] = useState();
    const [isBreakRomooc, setIsBreakRomooc] = useState(false);

    useEffect(() => {
        request(
            "post",
            `/facility/`, {}, {}, {}, {},
        ).then((res) => {
            console.log("facility==========", res.data)
            setFacilities(res.data.data);
        });
        request(
            "post",
            `/container/`, {}, {}, {}, {},
        ).then((res) => {
            console.log("container==========", res.data)
            setContainers(res.data);
        });
    }, []);
    const typeConst = [
        { name: "Inbound Empty", id: "IE" },
        { name: "Inbound Full", id: "IF" },
        { name: "Outbound Empty", id: "OE" },
        { name: "Outbound Full", id: "OF" }
    ];
    const typeFull = ["IF", "IE", "OF"];

    const handleClose = () => {
        setOpen(false);
    }
    const handleChangeBreakRomooc = (e) => {
        setIsBreakRomooc(e.target.checked);
    }
    const handleSubmit = () => {
        const data = {
            type: type,
            fromFacilityId: fromFacility,
            toFacilityId: toFacility,
            earlyDeliveryTime: earlyDeliveryTime,
            lateDeliveryTime: lateDeliveryTime,
            earlyPickupTime: earlyPickUpTime,
            latePickupTime: latePickUpTime,
            isBreakRomooc: isBreakRomooc,
            containerIds: containerSelect
        }
        console.log("data", data);
        request(
            "post",
            `/order/create`, {}, {}, data
        ).then((res) => {
            console.log("res", res);
            if(!res) {
                setToastType("error");
            } else {
                setToastType("success");
            }
            handleClose();
            setToast(true);
            setTimeout(() => {
                setToast(false);
            }, "2000");
        }).catch(err => {
            console.log("err", err);
            
        })
    }
    const handleSelectContainer = (event, values) => {
        console.log("values", values);
        let containerIds = [];
        if(values.length > 0) {
            values.map((item) => containerIds.push(item.id))
        }
        setContainerSelect(containerIds);
    }
    return (
        <Box >
            <CustomizedDialogs
                open={open}
                handleClose={handleClose}
                contentTopDivider
                title="New Order"
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
                                <Box pt="7px" pb={1} pl={1} pr={1} sx={{ width: "650px" }} className="contentModal">
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
                                                                <MenuItem value={item.id}>{item.name}</MenuItem>
                                                            );
                                                        })
                                                    ) : null}
                                                </Select>
                                            </FormControl>
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>From Facility: </Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <FormControl>
                                                <InputLabel id="demo-simple-select-label">From facility</InputLabel>
                                                <Select
                                                    value={fromFacility}
                                                    onChange={(e) => setFromFacility(e.target.value)}
                                                    label="from facility"
                                                >
                                                    {facilities ? (
                                                        facilities.map((item) => {
                                                            return (
                                                                <MenuItem value={item.id}>{item.facilityName}</MenuItem>
                                                            );
                                                        })
                                                    ) : null}
                                                </Select>
                                            </FormControl>
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>To Facility:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <FormControl>
                                                <InputLabel id="demo-simple-select-label">To facility</InputLabel>
                                                <Select
                                                    value={toFacility}
                                                    onChange={(e) => setToFaciity(e.target.value)}
                                                    label="to facility"
                                                >
                                                    {facilities ? (
                                                        facilities.map((item) => {
                                                            return (
                                                                <MenuItem value={item.id}>{item.facilityName}</MenuItem>
                                                            );
                                                        })
                                                    ) : null}
                                                </Select>
                                            </FormControl>
                                        </Box>
                                    </Box>
                                    {
                                        typeFull.includes(type) ? (<Box className="contentModal-item">
                                            <Box className="contentModal-item-text">
                                                <Typography>Containers:</Typography>
                                            </Box>
                                            <Box className="contentModal-item-input">
                                                <Autocomplete
                                                    multiple
                                                    id="tags-outlined"
                                                    options={containers}
                                                    getOptionLabel={(option) => option.containerCode}
                                                    // defaultValue={[containers[0]]}
                                                    onChange={handleSelectContainer}
                                                    filterSelectedOptions
                                                    renderInput={(params) => (
                                                        <TextField
                                                            {...params}
                                                            label="Containers"
                                                            placeholder="containers"
                                                        />
                                                    )}
                                                />
                                            </Box>
                                        </Box>) : null
                                    }
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Break Romooc:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <Switch
                                                checked={isBreakRomooc}
                                                onChange={handleChangeBreakRomooc}
                                                inputProps={{ 'aria-label': 'controlled' }}
                                            />
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Early Delivery Time:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <LocalizationProvider dateAdapter={AdapterDayjs}>
                                                <DemoContainer components={['DateTimePicker']}>
                                                    <DateTimePicker label="Early Delivery Time"
                                                        onChange={(e) => setEarlyDeliveryTime((new Date(e)).getTime())} />
                                                </DemoContainer>
                                            </LocalizationProvider>
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Late Delivery Time:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <LocalizationProvider dateAdapter={AdapterDayjs}>
                                                <DemoContainer components={['DateTimePicker']}>
                                                    <DateTimePicker label="Late Delivery Time"
                                                        onChange={(e) => setLateDeliveryTime((new Date(e)).getTime())} />
                                                </DemoContainer>
                                            </LocalizationProvider>
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Early PickUp Time:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <LocalizationProvider dateAdapter={AdapterDayjs}>
                                                <DemoContainer components={['DateTimePicker']}>
                                                    <DateTimePicker label="Early PickUp Time"
                                                        onChange={(e) => setEarlyPickUpTime((new Date(e)).getTime())} />
                                                </DemoContainer>
                                            </LocalizationProvider>
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Late PickUp Time:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <LocalizationProvider dateAdapter={AdapterDayjs}>
                                                <DemoContainer components={['DateTimePicker']}>
                                                    <DateTimePicker label="Late PickUp Time"
                                                        onChange={(e) => setLatePickUpTime((new Date(e)).getTime())} />
                                                </DemoContainer>
                                            </LocalizationProvider>
                                        </Box>
                                    </Box>
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
                className="modalOrder"
            />

        </Box>
    )
}
export default NewOrderModal;