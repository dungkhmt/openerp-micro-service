import React, { useEffect, useState } from "react";
import { Alert, Box, Button, Container, Divider, FormControl, Icon, IconButton, InputAdornment, InputBase, InputLabel, MenuItem, OutlinedInput, Select, TextField, Typography } from "@mui/material";
import { menuIconMap } from "config/menuconfig";
import SearchIcon from '@mui/icons-material/Search';
import CloseIcon from '@mui/icons-material/Close';
import { theme } from "App";

const styles = {
    root: (theme) => ({
        display: 'flex',
        width: '80%',
        backgroundColor: 'white',
        marginBottom: '32px',
        maxHeight: '40px',
        '& .filter-by-box': {
            maxWidth: '20%',
            minWidth: '20%',
            '& .filter-by': {
                border: '1px solid #c5c3c3',
                borderRadius: '3px',
                width: '100%',
                height: '40px',
                display: 'flex',
                alignItems: 'baseline',
                justifyContent: 'space-evenly',
                '& .MuiTypography-root': {
                    fontWeight: 600
                }
            },
            '& .filter-by-item': {
                zIndex: 100,
                position: 'relative',
                overflow: 'visible',
                backgroundColor: 'white',
                // width: '200px',
                padding: '16px 4px 8px 4px',
                border: '1px solid #c5c3c3',
                '& .filter-by-item-title': {
                    marginBottom: '16px',
                    '& .MuiTypography-root': {
                        fontWeight: 600
                    }
                },
                '& .btn-filter': {
                    marginTop: '32px'
                },
                '& .MuiInputBase-root': {
                    height: '40px'
                }
            },
        },
        '& .input-search': {
            width: '100%',
            '& .MuiInputBase-root': {
                height: '40px !important',
                width: '100%'
            }
        }
    })
}
const SearchBar = ({ filters, setFilters }) => {
    const [open, setOpen] = useState(false);

    const [code, setCode] = useState('');
    const [statusTruck, setStatusTruck] = useState('');

    const status = [
        { name: "AVAILABLE" },
        { name: "EXECUTING" }
    ]
    const handleSwitch = () => {
        setOpen(!open)
    }
    const handleFilterStatus = () => {
        let data = { type: "status", value: statusTruck }
        setFilters(prevState => [...prevState, data]);
        handleSwitch();
    }
    const searchCode = () => {
        let data = { type: "code", value: code }
        setFilters(prevState => [...prevState, data])
    }
    const handleRemoveFilter = (type) => {
        let data = filters.filter((item) => item.type !== type);
        if(type === "code") {
            setCode('');
        }
        if(type === "status") {
            setStatusTruck('');
        }
        setFilters(data);
    }
    console.log("filters", filters);
    return (
        <Box>
            <Box sx={(them) => ({
                ...styles.root(theme), ...{},
            })}>
                <Box className="filter-by-box">
                    <Box className="filter-by"
                        onClick={handleSwitch}>
                        <Box>
                            <Typography>Filter By</Typography>
                        </Box>
                        <Box>
                            <Icon>{menuIconMap.get("ArrowDropDownIcon")}</Icon>
                        </Box>
                    </Box>
                    {open ? (
                        <Box className="filter-by-item">
                            <Box className="filter-by-item-title">
                                <Typography>Status:</Typography>
                            </Box>
                            <FormControl fullWidth>
                                <InputLabel id="demo-simple-select-label">Status</InputLabel>
                                <Select
                                    labelId="demo-simple-select-label"
                                    id="demo-simple-select"
                                    label="Status"
                                    onChange={(e) => setStatusTruck(e.target.value)}
                                >
                                    {status.map((item) => {
                                        return (
                                            <MenuItem value={item?.name}>{item?.name}</MenuItem>
                                        )
                                    })}
                                </Select>
                            </FormControl>
                            <Box className="btn-filter"
                                onClick={handleFilterStatus}
                            >
                                <Button variant="contained">Search</Button>
                            </Box>
                        </Box>
                    ) : null}

                </Box>
                <Box className="input-search">
                    <OutlinedInput

                        id="outlined-adornment-password"
                        type='text'
                        InputLabelProps={{ shrink: false }}
                        variant="outlined"
                        placeholder="input search code"
                        value={code}
                        onChange={(e) => { setCode(e.target.value) }}
                        endAdornment={
                            <InputAdornment position="end">
                                <IconButton
                                    aria-label="toggle password visibility"
                                    onClick={searchCode}
                                    // onMouseDown={handleMouseDownPassword}
                                    edge="end"
                                >
                                    <SearchIcon />
                                </IconButton>
                            </InputAdornment>
                        }
                    />
                </Box>
            </Box>
            <Box className="item-filter">
                {filters.length > 0 ? (
                    filters.map((item) => {
                        return (
                            <Box sx={{ display: 'flex' }}>
                                <Typography>{item.value}</Typography>
                                <Box onClick={() => handleRemoveFilter(item.type)}
                                sx={{cursor: 'pointer'}}>
                                    <CloseIcon />
                                </Box>
                            </Box>
                        )
                    })

                ) : null}
            </Box>
        </Box>
    )
}
export default SearchBar;