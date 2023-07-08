import { Box, FormControl, InputLabel, MenuItem, Select, TextField, Typography, Autocomplete, Divider, Switch, Alert, AlertTitle, List, ListItem, ListItemIcon, ListItemText, InputAdornment, IconButton, OutlinedInput } from "@mui/material";
import PrimaryButton from "components/button/PrimaryButton";
import TertiaryButton from "components/button/TertiaryButton";
import CustomizedDialogs from "components/dialog/CustomizedDialogs";
import { AnimatePresence, motion } from "framer-motion";
import { grey } from "@mui/material/colors";
import React, { useCallback, useContext, useEffect, useState } from "react";
import '../styles.scss';
import { MyContext } from "contextAPI/MyContext";
import { facilityType, roles } from "config/menuconfig";
import debounce from 'lodash.debounce';
import SearchIcon from '@mui/icons-material/Search';
import PlaceIcon from '@mui/icons-material/Place';
import { createFacility, updateFacility } from "api/FacilityAPI";


const styles = {
    btn: { margin: "4px 8px" },
};
const NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/search?";

const FacilityModal = ({ open, handleClose, facility, setToast, setToastType, setToastMsg }) => {
    const [type, setType] = useState('');
    const [typeOwner, setTypeOwner] = useState('');
    const [name, setName] = useState('');
    const [address, setAddress] = useState('');
    const [maxTruck, setMaxTruck] = useState('');
    const [maxTrailer, setMaxTrailer] = useState('');
    const [maxContainer, setMaxContainer] = useState('');
    const [timePickup, setTimePickup] = useState('');
    const [timeDrop, setTimeDrop] = useState('');
    const [acreage, setAcreage] = useState('');

    const [listPlace, setListPlace] = useState([]);
    const [selectPosition, setSelectPosition] = useState({});

    const { role, preferred_username } = useContext(MyContext);

    useEffect(() => {
        if (facility) {
            setName(facility?.facilityName);
            setAddress(facility?.address);
            setAcreage(facility?.acreage);
            setType(facility?.facilityType);
            setTimePickup(facility?.processingTimePickUp);
            setTimeDrop(facility?.processingTimeDrop);
            setMaxContainer(facility?.maxNumberContainer);
            setMaxTrailer(facility?.maxNumberTrailer);
            setMaxTruck(facility?.maxNumberTruck);
        }
        if (role === roles.get("Customer")) {
            setType(facilityType.get("Container"));
            setTypeOwner("CUSTOMER");
        }
    }, [])
    const typeConst = [
        { name: "Truck", id: "1" },
        { name: "Trailer", id: "2" },
        { name: "Container", id: "3" },
    ];
    const handleSubmit = () => {
        const data = {
            facilityType: type,
            facilityName: name,
            address: address,
            longitude: Number(selectPosition?.lng),
            latitude: Number(selectPosition?.lat),
            processingTimePickUp: timePickup,
            processingTimeDrop: timeDrop,
            acreage: acreage,
            maxNumberTruck: type === "Truck" ? maxTruck : null,
            maxNumberTrailer: type === "Trailer" ? maxTrailer : null,
            maxNumberContainer: type === "Container" ? maxContainer : null,
            typeOwner: typeOwner
        }
        console.log("data", data);
        if (facility) {
            updateFacility(facility?.uid, data).then((res) => {
                if (!res) {
                    setToastType("error");
                    setToastMsg("Update Facility Fail !!!");
                } else {
                    setToastType("success");
                    setToastMsg("Update Facility Succsess !!!")
                }
                handleClose();
                setToast(true);
                setTimeout(() => {
                    setToast(false);
                }, "3000");
            })
                .catch(err => {
                    console.log("err", err);
                })
        }
        else {
            createFacility(data).then((res) => {
                console.log("res", res);
                if (!res) {
                    setToastType("error");
                    setToastMsg("Create Facility Fail !!!");
                } else {
                    setToastType("success");
                    setToastMsg("Create Facility Succsess !!!")
                }
                handleClose();
                setToast(true);
                setTimeout(() => {
                    setToast(false);
                }, "3000");
            }).catch(err => {
                console.log("err", err);

            })
        }
    }

    const onChangeAddress = (event) => {
        console.log("start", event.target.value)
        setAddress(event.target.value);
        getListPlace(event.target.value)
    }
    const debouncedOnChangeAddress = useCallback(
        debounce(() => onChangeAddress, 500)
        , []);
    const getListPlace = (searchText) => {
        const params = {
            q: searchText,
            format: "json",
            addressdetails: 1,
            polygon_geojson: 0,
        };
        const queryString = new URLSearchParams(params).toString();
        const requestOptions = {
            method: "GET",
            redirect: "follow",
        };
        fetch(`${NOMINATIM_BASE_URL}${queryString}`, requestOptions)
            .then((response) => response.text())
            .then((result) => {
                console.log(JSON.parse(result));
                setListPlace(JSON.parse(result));
            })
            .catch((err) => console.log("err: ", err));
    }
    const searchAddress = () => {
        getListPlace(address);
    }

    return (
        <Box >
            <CustomizedDialogs
                open={open}
                handleClose={handleClose}
                contentTopDivider
                title={facility ? "Update Facility" : "New Facility"}
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
                                    {role === roles.get("Customer") ? null : (
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
                                                        size="small"
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
                                        </Box>)}
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
                                                size="small"
                                                onChange={(e) => setName(e.target.value)}
                                            />
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Acreage: </Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <OutlinedInput
                                                id="outlined-textarea"
                                                label="Acreage"
                                                placeholder="acreage"
                                                value={acreage}
                                                endAdornment={<InputAdornment position="end">(m2)</InputAdornment>}
                                                size="small"
                                                onChange={(e) => setAcreage(e.target.value)}
                                            />
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Address:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <FormControl variant="outlined">
                                                <InputLabel htmlFor="outlined-textarea">Address</InputLabel>
                                                <OutlinedInput
                                                    id="outlined-textarea"
                                                    label="Address"
                                                    placeholder="address"
                                                    size="small"
                                                    // multiline
                                                    value={address}
                                                    onChange={(e) => setAddress(e.target.value)}
                                                    endAdornment={
                                                        <InputAdornment position="end">
                                                            <IconButton
                                                                aria-label="toggle password visibility"
                                                                onClick={searchAddress}
                                                                // onMouseDown={handleMouseDownPassword}
                                                                edge="end"
                                                            >
                                                                <SearchIcon />
                                                            </IconButton>
                                                        </InputAdornment>
                                                    }
                                                />
                                            </FormControl>
                                            <Box className="list-place">
                                                <List component="nav" aria-label="main mailbox folders">
                                                    {listPlace?.map((item) => {
                                                        return (
                                                            <div key={item?.place_id}>
                                                                <ListItem
                                                                    sx={{ cursor: 'pointer' }}
                                                                    onClick={() => {
                                                                        setAddress(item?.display_name);
                                                                        setSelectPosition({
                                                                            lat: item?.lat,
                                                                            lng: item?.lon,
                                                                        });
                                                                        setListPlace([]);
                                                                    }}
                                                                >
                                                                    <ListItemIcon>
                                                                        <PlaceIcon />
                                                                    </ListItemIcon>
                                                                    <ListItemText primary={item?.display_name} />
                                                                </ListItem>
                                                                <Divider />
                                                            </div>
                                                        );
                                                    })}
                                                </List>
                                            </Box>
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
                                                    label="Max total truck"
                                                    placeholder="max total truck"
                                                    value={maxTruck}
                                                    size="small"
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
                                                    label="Max total trailer"
                                                    placeholder="max total trailer"
                                                    value={maxTrailer}
                                                    size="small"
                                                    onChange={(e) => setMaxTrailer(e.target.value)}
                                                />
                                            </Box>
                                        </Box>
                                    ) : null}
                                    {type === "Container" ? (
                                        <>
                                            <Box className="contentModal-item">
                                                <Box className="contentModal-item-text">
                                                    <Typography>Processing Time Pick Up:</Typography>
                                                </Box>
                                                <Box className="contentModal-item-input">
                                                    <FormControl>
                                                        <InputLabel htmlFor="outlined-text-time">processing time pickup</InputLabel>
                                                        <OutlinedInput
                                                            id="outlined-text-time"
                                                            label="Processing Time Pickup"
                                                            placeholder="processing time pickup"
                                                            value={timePickup}
                                                            endAdornment={<InputAdornment position="end">(s)</InputAdornment>}
                                                            size="small"
                                                            onChange={(e) => setTimePickup(e.target.value)}
                                                        />
                                                    </FormControl>
                                                </Box>
                                            </Box>
                                            <Box className="contentModal-item">
                                                <Box className="contentModal-item-text">
                                                    <Typography>Processing Time Drop Off:</Typography>
                                                </Box>
                                                <Box className="contentModal-item-input">
                                                    <FormControl>
                                                        <InputLabel htmlFor="outlined-text-time">processing time drop off</InputLabel>
                                                        <OutlinedInput
                                                            id="outlined-text-time"
                                                            label="Processing Time Drop Off"
                                                            placeholder="processing time drop off"
                                                            value={timeDrop}
                                                            endAdornment={<InputAdornment position="end">(s)</InputAdornment>}
                                                            size="small"
                                                            onChange={(e) => setTimeDrop(e.target.value)}
                                                        />
                                                    </FormControl>
                                                </Box>
                                            </Box>
                                            <Box className="contentModal-item">
                                                <Box className="contentModal-item-text">
                                                    <Typography>Max Total Container:</Typography>
                                                </Box>
                                                <Box className="contentModal-item-input">
                                                    <TextField
                                                        id="outlined-textarea"
                                                        label="Max total container"
                                                        placeholder="max total container"
                                                        value={maxContainer}
                                                        size="small"
                                                        onChange={(e) => setMaxContainer(e.target.value)}
                                                    />
                                                </Box>
                                            </Box>
                                        </>
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
                                            onClick={handleClose}
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