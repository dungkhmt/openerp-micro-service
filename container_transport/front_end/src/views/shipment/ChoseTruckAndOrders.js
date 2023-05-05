import { Autocomplete, Box, Button, ClickAwayListener, TextField, Typography } from "@mui/material";
import { request } from "api";
import React, { useEffect, useState } from "react";
import SettingsIcon from '@mui/icons-material/Settings';
import CloseIcon from '@mui/icons-material/Close';
import DoneIcon from '@mui/icons-material/Done';
import './styles.scss';

const labels = [
    {
        name: "anhvu",
        id: 1
    },
    {
        name: "vudinh",
        id: 2
    }
]
// function PopperComponent(props) {
//     const { disablePortal, anchorEl, open, ...other } = props;
//     return <StyledAutocompletePopper {...other} />;
//   }
const ChoseTruckAndOrders = ({ trucks, setTruckId }) => {
    const [anchorEl, setAnchorEl] = useState(null);
    const [value, setValue] = useState([]);
    const [pendingValue, setPendingValue] = useState([]);
    const open = Boolean(anchorEl);
    const id = open ? 'github-label' : undefined;
    const handleClick = (event) => {
        setPendingValue(value);
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setValue(pendingValue);
        if (anchorEl) {
            anchorEl.focus();
        }
        setAnchorEl(null);
    };
    return (
        <>
            <Box className="chose-truck">
                <Typography className="chose-truck-text">Truck:</Typography>
                <Autocomplete
                    disablePortal
                    id="combo-box-demo"
                    options={trucks}
                    getOptionLabel={(option) => option.truckCode}
                    // sx={{ width: 300 }}
                    renderInput={(params) => <TextField {...params} label="chose truck" />}
                />
            </Box>
            <Box className="chose-orders">
                <Box className="chose-orders-header">
                    <Box>
                        <Typography className="chose-order-text">Orders:</Typography>
                    </Box>
                    <Box>
                        <Button disableRipple
                            aria-describedby={id}
                            onClick={handleClick}
                        >
                            <span>Add Orders</span>
                        </Button></Box>
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
                                    // PopperComponent={PopperComponent}
                                    renderTags={() => null}
                                    noOptionsText="No labels"
                                    renderOption={(props, option, { selected }) => (
                                        <li {...props}>
                                            <Box
                                                component={DoneIcon}
                                                sx={{ width: 17, height: 17, mr: '5px', ml: '-2px' }}
                                                style={{
                                                    visibility: selected ? 'visible' : 'hidden',
                                                }}
                                            />
                                            <Box
                                                component="span"
                                                sx={{
                                                    width: 14,
                                                    height: 14,
                                                    flexShrink: 0,
                                                    borderRadius: '3px',
                                                    mr: 1,
                                                    mt: '2px',
                                                }}
                                                style={{ backgroundColor: option.color }}
                                            />
                                            <Box
                                            // sx={{
                                            //     flexGrow: 1,
                                            //     '& span': {
                                            //         color:
                                            //             theme.palette.mode === 'light' ? '#586069' : '#8b949e',
                                            //     },
                                            // }}
                                            >
                                                {option.name}
                                                <br />
                                                <span>{option.description}</span>
                                            </Box>
                                            <Box
                                                component={CloseIcon}
                                                sx={{ opacity: 0.6, width: 18, height: 18 }}
                                                style={{
                                                    visibility: selected ? 'visible' : 'hidden',
                                                }}
                                            />
                                        </li>
                                    )}
                                    options={[...labels].sort((a, b) => {
                                        // Display the selected labels first.
                                        let ai = value.indexOf(a);
                                        ai = ai === -1 ? value.length + labels.indexOf(a) : ai;
                                        let bi = value.indexOf(b);
                                        bi = bi === -1 ? value.length + labels.indexOf(b) : bi;
                                        return ai - bi;
                                    })}
                                    getOptionLabel={(option) => option.name}
                                    renderInput={(params) => <TextField {...params} label="chose order" />}
                                // renderInput={(params) => (
                                //     <StyledInput
                                //         ref={params.InputProps.ref}
                                //         inputProps={params.inputProps}
                                //         autoFocus
                                //         placeholder="Filter labels"
                                //     />
                                // )}
                                />
                            </div>
                        </ClickAwayListener>
                    </Box> : null}
                {value.map((label) => (
                    <Box
                        key={label.id}
                        sx={{
                            mt: '3px',
                            height: 20,
                            padding: '.15em 4px',
                            fontWeight: 600,
                            lineHeight: '15px',
                            borderRadius: '2px',
                        }}
                    >
                        {label.name}
                    </Box>
                ))}
            </Box>
        </>
    )
}
export default ChoseTruckAndOrders;