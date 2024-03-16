import React, { useEffect, useState, useRef } from "react";
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import MapComponent from './components/map';
import CardHub from "./components/CardHub";
import { TextField, FormControl } from '@mui/material';
import Grid from '@mui/material/Grid';
import { request } from "api";
import { hub } from "config/menuconfig/hub";

const HubScreen = () => {
    const mapRef = useRef();
    const mapRef2 = useRef();


    const [hubs, setHubs] = useState([]);

    const [hubEdit, setHubEdit] = useState(null);
    const [open, setOpen] = useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => {
        setFormData({
            hubCode: "",
            address: "",
            status: "",
            latitude: "",
            longitude: ""

        })
        setHubEdit(null);
        setOpen(false)
    };
    const [formData, setFormData] = useState({
        hubCode: "",
        address: "",
        status: "",
        latitude: "",
        longitude: ""
    });

    useEffect(() => {
        if (hubEdit) {
            setFormData(hubEdit);
        }
    }, [hubEdit]);

    useEffect(() => {
        request("get", "/hub/get-all", (res) => {
            console.log(res.data);
            setHubs(res.data);
        }).then()
    }, []);

    const handleChange = (event) => {
        setFormData({
            ...formData,
            [event.target.name]: event.target.value
        });
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        // Do something with formData, like submitting it to a server


        if (hubEdit) {
            request("put", "/hub/update", (res) => {
                console.log(res.data);
                setHubs(hubs.map(hub => hub.id === res.data.id ? res.data : hub));
            }, {}, {
                ...hubEdit,
                hubCode: formData.hubCode,
                address: formData.address,
                status: formData.status,
                latitude: formData.latitude,
                longitude: formData.longitude
            }).then()
        } else {
            request("post", "/hub/add", (res) => {
                console.log(res.data);
                setHubs([...hubs, res.data]);
            }, {}, {
                hubCode: formData.hubCode,
                address: formData.address,
                status: formData.status,
                latitude: formData.latitude,
                longitude: formData.longitude
            }).then()
        }

        handleClose();
    };

    function onHubEdit(hub) {
        setHubEdit(hub);
        setFormData(hub);
        handleOpen();
    }
    const onHubDelete = (hub) => {
        request("delete", `/hub/delete/${hub.id}`, (res) => {
            console.log(res.data);
            setHubs(hubs.filter(h => h.id !== hub.id));
        }, {}, {}).then()
    }


    function handleOnFlyTo(latlng) {
        const { current = {} } = mapRef;

        if (current) {
            current.flyTo(latlng, 14, {
                duration: 2,
                animate: true,
            });
        }
    }

    return (
        <>
            <Box
                sx={{
                    position: "relative",
                    height: "100vh",
                    width: "100%",
                }}
            >

                <MapComponent hubs={hubs} mapRef={mapRef} onHubDelete={onHubDelete} onHubEdit={onHubEdit} />
            </Box>
            <Box
                sx={{
                    position: "absolute",
                    zIndex: 10000,
                    right: 0,
                    top: "20vh",
                }}
                color={"grey.800"}
                bgcolor={"common.white"}
                overflow={"auto"}
            >

                <Button onClick={handleOpen}>Thêm hub</Button>
                {hubs.map((hub, index) => (
                    <CardHub key={index} hub={hub} onClick={handleOnFlyTo} />
                ))}

            </Box>
            <Modal
                open={open}
                onClose={handleClose}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
            >
                <Box
                    sx={{
                        position: 'absolute',
                        top: '50%',
                        left: '50%',
                        transform: 'translate(-50%, -50%)',
                        width: 1000,
                        bgcolor: 'background.paper',
                        border: '2px solid #000',
                        boxShadow: 24,
                        p: 4,
                    }}
                >
                    <Grid container spacing={2}>
                        <Grid item xs={4}>
                            <form onSubmit={handleSubmit}>
                                <FormControl sx={{ width: '300px' }}>
                                    <Typography variant="h6" component="h2" gutterBottom>
                                        Enter Hub Information
                                    </Typography>
                                    <TextField
                                        fullWidth
                                        label="Hub Code"
                                        name="hubCode"
                                        value={formData.hubCode}
                                        onChange={handleChange}
                                        margin="normal"
                                        required
                                    />
                                    <TextField
                                        fullWidth
                                        label="Address"
                                        name="address"
                                        value={formData.address}
                                        onChange={handleChange}
                                        margin="normal"
                                        required
                                    />
                                    <TextField
                                        fullWidth
                                        label="Status"
                                        name="status"
                                        value={formData.status}
                                        onChange={handleChange}
                                        margin="normal"
                                        required
                                    />
                                    <TextField
                                        fullWidth
                                        label="Latitude"
                                        name="latitude"
                                        value={formData.latitude}
                                        onChange={handleChange}
                                        margin="normal"
                                        required
                                    />
                                    <TextField
                                        fullWidth
                                        label="Longitude"
                                        name="longitude"
                                        value={formData.longitude}
                                        onChange={handleChange}
                                        margin="normal"
                                        required
                                    />


                                    <Button type="submit" variant="contained" color="primary" sx={{ mt: 2 }}>
                                        Submit
                                    </Button>
                                </FormControl>
                            </form>
                        </Grid>
                        <Grid item xs={8}>
                            <MapComponent hubs={hubs} isEdit mapRef={mapRef2} setCoordinates={(lat, lng) => setFormData({ ...formData, latitude: lat, longitude: lng })} />
                        </Grid>

                    </Grid>

                </Box>
            </Modal>
        </>



    );
};

export default HubScreen;
