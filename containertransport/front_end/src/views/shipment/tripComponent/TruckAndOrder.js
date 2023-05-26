import { Autocomplete, Box, Button, ClickAwayListener, TextField, Typography } from "@mui/material";
import { request } from "api";
import React, { useContext, useEffect, useState } from "react";
import SettingsIcon from '@mui/icons-material/Settings';
import CloseIcon from '@mui/icons-material/Close';
import DoneIcon from '@mui/icons-material/Done';
import '../styles.scss';
import { MyContext } from "contextAPI/MyContext";

const TruckAndOrder = ({ trucks, setTruckSelect, truckSelect, orders, ordersSelect, setOrdersSelect, setFlag}) => {
    const [anchorEl, setAnchorEl] = useState(null);
    const [value, setValue] = useState([]);
    const [pendingValue, setPendingValue] = useState([]);
    const open = Boolean(anchorEl);
    const id = open ? 'github-label' : undefined;
    const [tripTmpId, setTripTmpId] = useState('');
    const [tripTmp, setTripTmp] = useState();
    const [initTruckSelect, setInitTruck] = useState();

    useEffect(() => {
        setValue(ordersSelect);
        setPendingValue(ordersSelect);
    }, [ordersSelect])
    const handleClick = (event) => {
        setPendingValue(value);
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setValue(pendingValue);
        setOrdersSelect(pendingValue);
        setFlag(true);
        if (anchorEl) {
            anchorEl.focus();
        }
        setAnchorEl(null);
    };
    const handleRemoveOrder = (id) => {
        let valueTmp = value.filter((item) => item.id !== id);
        setValue(valueTmp);
        setOrdersSelect(valueTmp);
        setFlag(true);
    }
    return (
        <>
            <Box className="chose-truck">
                <Typography className="chose-truck-text">Truck:</Typography>
                <Autocomplete
                    disablePortal
                    id="combo-box-demo"
                    options={trucks}
                    value={truckSelect}
                    renderOption={(props, option) => (<li {...props}>{option.truckCode} - {option?.facilityResponsiveDTO?.facilityName}</li>)}
                    getOptionLabel={(option) => `${option.truckCode} - ${option?.facilityResponsiveDTO?.facilityName}`}
                    onChange={(event, values) => {
                        setTruckSelect(values)
                        setFlag(true);
                    }}
                    renderInput={(params) => {
                        return (
                            <TextField {...params}
                                label="chose truck" />
                        )
                    }}
                />
            </Box>
            <Box className="chose-orders">
                <Box className="chose-orders-header">
                    <Box>
                        <Typography className="chose-order-text">Orders:</Typography>
                    </Box>
                    <Box sx={{ backgroundColor: "#1976d2", borderRadius: '4px' }}>
                        <Button disableRipple
                            aria-describedby={id}
                            onClick={handleClick}
                            sx={{ color: "white" }}
                        >
                            <span>Add Orders</span>
                        </Button>
                    </Box>
                </Box>
                {open ?
                    <Box id={id} anchorEl={anchorEl} placement="bottom-start" sx={{ zIndex: 10000 }}>
                        <ClickAwayListener onClickAway={handleClose}>
                            <div>
                                <Autocomplete
                                    open
                                    multiple
                                    onClose={(event, reason) => {
                                        if (reason === 'escape') {
                                            handleClose();
                                        }
                                    }}
                                    value={pendingValue}
                                    onChange={(event, newValue, reason) => {
                                        console.log("newValue", newValue)
                                        if (
                                            event.type === 'keydown' &&
                                            event.key === 'Backspace' &&
                                            reason === 'removeOption'
                                        ) {
                                            return;
                                        }
                                        setPendingValue(newValue);
                                    }}
                                    disableCloseOnSelect
                                    renderTags={() => null}
                                    noOptionsText="No labels"
                                    renderOption={(props, option, { selected }) => (
                                        <li {...props} style={{ justifyContent: "center" }}>
                                            <Box
                                                component={DoneIcon}
                                                sx={{ width: 17, height: 17, mr: '16px', ml: '-2px' }}
                                                style={{
                                                    visibility: selected ? 'visible' : 'hidden',
                                                }}
                                            />
                                            <Box>
                                                {option.orderCode}: {option.fromFacility.facilityName} - {option.toFacility.facilityName}
                                            </Box>
                                            <Box
                                                component={CloseIcon}
                                                sx={{ opacity: 0.6, width: 18, height: 18, ml: '16px' }}
                                                style={{
                                                    visibility: selected ? 'visible' : 'hidden',
                                                }}
                                            />
                                        </li>
                                    )}
                                    options={[...orders].sort((a, b) => {
                                        // Display the selected labels first.
                                        let ai = value.indexOf(a);
                                        ai = ai === -1 ? value.length + orders.indexOf(a) : ai;
                                        let bi = value.indexOf(b);
                                        bi = bi === -1 ? value.length + orders.indexOf(b) : bi;
                                        return ai - bi;
                                    })}
                                    // getOptionLabel={(option) => option.id}
                                    renderInput={(params) => <TextField {...params} label="chose order" />}
                                />
                            </div>
                        </ClickAwayListener>
                    </Box> : null}
                {value.map((label) => (
                    <Box
                        key={label.id}
                        sx={{
                            mt: '16px',
                            padding: '8px 8px',
                            fontWeight: 600,
                            lineHeight: '15px',
                            borderRadius: '2px',
                            border: '1px solid #e5e5e5',
                            display: 'flex',
                            justifyContent: 'space-evenly',
                            alignItems: 'center'
                        }}
                    >
                        <Typography>{label.orderCode}: {label.fromFacility.facilityName} - {label.toFacility.facilityName}</Typography>
                        <Box
                            component={CloseIcon}
                            sx={{ opacity: 0.6, width: 18, height: 18, cursor: "pointer" }}
                            onClick={() => handleRemoveOrder(label.id)}
                        />
                    </Box>
                ))}
            </Box>
        </>
    )
}
export default TruckAndOrder;