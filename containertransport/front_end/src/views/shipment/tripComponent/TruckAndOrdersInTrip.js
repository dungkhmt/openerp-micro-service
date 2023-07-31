import { Autocomplete, Box, Button, ClickAwayListener, Container, Icon, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Tooltip, Typography } from "@mui/material";
import React, { useContext, useEffect, useState } from "react";
import SettingsIcon from '@mui/icons-material/Settings';
import CloseIcon from '@mui/icons-material/Close';
import DoneIcon from '@mui/icons-material/Done';
import '../styles.scss';
import { menuIconMap, typeOrderMap } from "config/menuconfig";

const headCells = [
    {
        id: 'code',
        numeric: false,
        disablePadding: true,
        label: 'Order Code',
        with: '12%'
    },
    {
        id: 'customer',
        numeric: false,
        disablePadding: true,
        label: 'Customer',
        with: '10%'
    },
    {
        id: 'fromFacility',
        numeric: false,
        disablePadding: true,
        label: 'From Facility',
        with: '12%'
    },
    {
        id: 'toFacility',
        numeric: false,
        disablePadding: true,
        label: 'To Facility',
        with: '12%'
    },
    {
        id: 'type',
        numeric: false,
        disablePadding: false,
        label: 'Type',
        with: '13%'
    },
    {
        id: 'size',
        numeric: false,
        disablePadding: false,
        label: 'Container Size',
        with: '10%'
    },
    {
        id: 'status',
        numeric: false,
        disablePadding: false,
        label: 'Status',
        with: '18%'
    },
    {
        id: 'createdAt',
        numeric: false,
        disablePadding: false,
        label: 'Created At',
        with: '13%'
    },
    {
        id: 'view',
        numeric: false,
        disablePadding: true,
        label: '',
    },
];

function EnhancedTableHead(props) {
    const { onSelectAllClick, order, orderBy, numSelected, rowCount } = props;

    return (
        <TableHead>
            <TableRow>
                {headCells.map((headCell) => (
                    <TableCell
                        key={headCell.id}
                        align={headCell.numeric ? 'right' : 'left'}
                        padding={headCell.disablePadding ? 'none' : 'normal'}
                        sortDirection={orderBy === headCell.id ? order : false}
                        width={headCell.with}
                    >
                        {headCell.label}
                    </TableCell>
                ))}
            </TableRow>
        </TableHead>
    );
}

const TruckAndOrdersInTrip = ({ trucks, setTruckSelect, truckSelect, orders, ordersSelect, setOrdersSelect, setFlag }) => {
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
    console.log("value", value)
    return (
        <Box className="truck-order">
            <Box className="chose-truck-v2">
                <Box className="header-info">
                    <Typography>Truck:</Typography>
                </Box>
                <Box className="truck-select">
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
                {truckSelect ? (
                    <Box className="truck-select-info">
                        <Box className="truck-info-item">
                            <Box className="truck-info-item-title">
                                <Typography>Truck Code:</Typography>
                            </Box>
                            <Box>{truckSelect?.truckCode}</Box>
                        </Box>
                        <Box className="truck-info-item">
                            <Box className="truck-info-item-title">
                                <Typography>Driver:</Typography>
                            </Box>
                            <Box>{truckSelect?.driverName}</Box>
                        </Box>
                        <Box className="truck-info-item">
                            <Box className="truck-info-item-title">
                                <Typography>Current Facility:</Typography>
                            </Box>
                            <Box>{truckSelect?.facilityResponsiveDTO?.facilityCode}</Box>
                        </Box>
                        <Box className="truck-info-item">
                            <Box className="truck-info-item-title">
                                <Typography>Address:</Typography>
                            </Box>
                            <Box>{truckSelect?.facilityResponsiveDTO?.address}</Box>
                        </Box>
                    </Box>
                ) : null}

            </Box>
            <Box className="chose-orders-v2">
                <Box className="chose-orders-header">
                    <Box className="header-info">
                        <Typography>Orders:</Typography>
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
                    <Box id={id} anchorEl={anchorEl} placement="bottom-start" sx={{width: '50%', float: 'right', zIndex: 10000 }}>
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
                    </Box> : null
                }

                {value.length > 0 ? (
                    <TableContainer className="table-order">
                        <Table
                            // sx={{ minWidth: 750 }}
                            aria-labelledby="tableTitle"
                        >
                            <EnhancedTableHead
                                rowCount={value?.length}
                                headCells={headCells}
                            />
                            <TableBody>
                                {value
                                    ? value.map((row, index) => {
                                        // const isItemSelected = isSelected(row.id);
                                        const labelId = `enhanced-table-checkbox-${index}`;

                                        return (
                                            <TableRow
                                                hover
                                                // onClick={(event) => handleClick(event, row.name)}
                                                role="checkbox"
                                                // aria-checked={isItemSelected}
                                                tabIndex={-1}
                                                key={row.id}
                                                // selected={isItemSelected}
                                                sx={{ cursor: 'pointer' }}
                                            >

                                                <TableCell
                                                    component="th"
                                                    id={labelId}
                                                    scope="row"
                                                    align="left"
                                                >
                                                    {row.orderCode}
                                                </TableCell>
                                                <TableCell align="left" sx={{ paddingLeft: '0px !important' }}>{row?.customerId}</TableCell>
                                                <TableCell align="left">{row?.fromFacility.facilityName}</TableCell>
                                                <TableCell align="left">{row?.toFacility.facilityName}</TableCell>
                                                <TableCell align="left">{typeOrderMap.get(row.type)}</TableCell>
                                                <TableCell align="left">{row?.container?.size}</TableCell>
                                                <TableCell align="left">{row?.status}</TableCell>
                                                <TableCell align="left">{new Date(row?.createdAt).toLocaleDateString()}</TableCell>
                                                <TableCell >
                                                    <Box sx={{ display: 'flex' }}>
                                                        {/* <Tooltip title="View">
                                                        <Box
                                                            onClick={() => { handleDetail(row?.id) }}
                                                        >
                                                            <Icon className='icon-view-screen'>{menuIconMap.get("RemoveRedEyeIcon")}</Icon>
                                                        </Box>
                                                    </Tooltip> */}
                                                        <Tooltip title="Delete">
                                                            <Box onClick={() => handleRemoveOrder(row.id)}>
                                                                <Icon className='icon-view-screen' sx={{ marginLeft: '8px' }}>{menuIconMap.get("DeleteForeverIcon")}</Icon>
                                                            </Box>
                                                        </Tooltip>
                                                    </Box>
                                                </TableCell>
                                            </TableRow>
                                        );
                                    })
                                    : null}
                            </TableBody>
                        </Table>
                    </TableContainer>
                ) : null}
            </Box>
        </Box>
    )
}
export default TruckAndOrdersInTrip;