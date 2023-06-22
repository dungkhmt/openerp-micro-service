import { Box, FormControl, InputLabel, MenuItem, Select, TextField, Typography, Autocomplete, Divider, Switch, Alert, AlertTitle, List, ListItem, ListItemIcon, ListItemText} from "@mui/material";
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


const styles = {
    btn: { margin: "4px 8px" },
};
const NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/search?";

const FacilityModal = ({ open, setOpen }) => {
    const [type, setType] = useState();
    const [name, setName] = useState('');
    const [address, setAddress] = useState('');
    const [maxTruck, setMaxTruck] = useState();
    const [maxTrailer, setMaxTrailer] = useState();
    const [maxContainer, setMaxContainer] = useState();

    const [listPlace, setListPlace] = useState([]);

    const { role, preferred_username } = useContext(MyContext);

    useEffect(() => {
        if (role === roles.get("Customer")) {
            setType(facilityType.get("Depot"));
        }
    }, [])
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
    
    const onChangeAddress = (event) => {
        console.log("start", event.target.value)
        setAddress(event.target.value);
        getListPlace(event.target.value)
    }
    const debouncedOnChangeAddress = useCallback(
        debounce(() => onChangeAddress, 500)
    ,[]);
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
                                <Box>
                                    <List component="nav" aria-label="main mailbox folders">
                                        {listPlace.map((item) => {
                                            return (
                                                <div key={item?.place_id}>
                                                    <ListItem
                                                        button
                                                    // onClick={() => {
                                                    //     setSelectPosition({
                                                    //         lat: item?.lat,
                                                    //         lng: item?.lon,
                                                    //     });
                                                    // }}
                                                    >
                                                        <ListItemIcon>
                                                            {/* <Box
                                                                    component="img"
                                                                    sx={{
                                                                        height: 40,
                                                                        width: 40,
                                                                        maxHeight: { xs: 233, md: 167 },
                                                                        maxWidth: { xs: 350, md: 250 },
                                                                    }}
                                                                    alt="Location"
                                                                    src={AppImages.blue_location}
                                                                /> */}
                                                        </ListItemIcon>
                                                        <ListItemText primary={item?.display_name} />
                                                    </ListItem>
                                                    <Divider />
                                                </div>
                                            );
                                        })}
                                    </List>
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
                                                label="Max total container"
                                                placeholder="max total container"
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