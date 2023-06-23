import React, { useContext, useEffect, useState } from "react";
import '../styles.scss';
import { Alert, Box, Button, Container, Divider, Icon, Typography } from "@mui/material";
import { useHistory, useParams } from "react-router-dom";
import { menuIconMap } from "config/menuconfig";
import { getFacilityById } from "api/FacilityAPI";
import ContentsTruckManagement from "views/truck/ContentTruckManagement";
import { getTrucks } from "api/TruckAPI";
import TruckInFacility from "./TrucksInFacility";
import { truck } from "config/menuconfig/truck";
import { getContainers } from "api/ContainerAPI";
import ContainerInFacility from "./ContainerInFacility";
import TrailerInFacility from "./TrailerInFacility";
import { getTraler } from "api/TrailerAPI";
import FacilityModal from "../create/NewFacilityModal";

const FacilityDetail = () => {
    const history = useHistory();
    const { facilityId } = useParams();
    const [facility, setFacility] = useState();

    const [type, setType] = useState('');

    const [trucks, setTrucks] = useState([]);
    const [trailers, setTrailers] = useState([]);
    const [containers, setContainers] = useState([]);

    const [openModify, setOpenModify] = useState(false);
    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    useEffect(() => {
        getFacilityById(facilityId).then((res) => {
            setFacility(res?.data);
            setType(res?.data.facilityType)
        })
        getTrucks({ facilityId: facilityId }).then((res) => {
            setTrucks(res?.data.truckModels);
        });
        getContainers({ facilityId: facilityId }).then((res) => {
            setContainers(res?.data.data.containerModels);
        });
        getTraler({ facilityId: facilityId }).then((res) => {
            setTrailers(res?.data.data.trailerModels);
        });
    }, [openModify])

    const handleClose = () => {
        setOpenModify(!openModify);
    }

    console.log("facility", facility);
    return (
        <Box className="fullScreen">
            <Container maxWidth="lg" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >) : null}
                </Box>
                <Box className="header-detail">
                    <Box className="headerScreen-go-back"
                        onClick={() => history.push('/facility')}
                        sx={{ cursor: "pointer" }}
                    >
                        <Icon>
                            {menuIconMap.get("ArrowBackIosIcon")}
                        </Icon>
                        <Typography>Go back Facility screen</Typography>
                    </Box>
                    <Box className="headerScreen-detail-info">
                        <Box className="title-header">
                            <Typography >Facility {facility?.facilityCode}</Typography>
                        </Box>
                        <Box className="btn-header">
                            <Button variant="outlined" color="error" className="header-create-shipment-btn-cancel"
                            // onClick={handleCancelCreateShipment}
                            >Delete</Button>
                            <Button variant="contained" className="header-submit-shipment-btn-save"
                            onClick={handleClose}
                            >Modify</Button>
                        </Box>
                    </Box>
                </Box>
                <Box className="divider">
                    <Divider />
                </Box>

                <Box className="title">
                    <Typography>Facility Info</Typography>
                </Box>
                <Box className="facility-info">
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Facility Code:</Typography>
                        </Box>
                        <Typography>{facility?.facilityCode}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Facility Name:</Typography>
                        </Box>
                        <Typography>{facility?.facilityName}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Acreage:</Typography>
                        </Box>
                        <Typography>{facility?.acreage} m2</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Address</Typography>
                        </Box>
                        <Typography>{facility?.address}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Facility Type:</Typography>
                        </Box>
                        <Typography>{facility?.facilityType}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Processing Time Pick Up:</Typography>
                        </Box>
                        <Typography>{facility?.processingTimePickUp} s</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Processing Time Drop:</Typography>
                        </Box>
                        <Typography>{facility?.processingTimeDrop} s</Typography>
                    </Box>

                    {facility?.facilityType === "Trailer" ? (<Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Max number trailer:</Typography>
                        </Box>
                        <Typography>{facility?.maxNumberTrailer}</Typography>
                    </Box>) : null}
                    {facility?.facilityType === "Truck" ? (<Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Max number trucks:</Typography>
                        </Box>
                        <Typography>{facility?.maxNumberTruck}</Typography>
                    </Box>) : null}
                    {facility?.facilityType === "Container" ? (<Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Max number containers:</Typography>
                        </Box>
                        <Typography>{facility?.maxNumberContainer}</Typography>
                    </Box>) : null}

                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Owner:</Typography>
                        </Box>
                        <Typography>{facility?.owner}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Create At:</Typography>
                        </Box>
                        <Typography>{new Date(facility?.createdAt).toLocaleString()}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Update At:</Typography>
                        </Box>
                        <Typography>{new Date(facility?.updatedAt).toLocaleString()}</Typography>
                    </Box>
                </Box>

                {trucks?.length > 0 ? (
                    <Box sx={{ marginBottom: '16px' }}>
                        <Box className="title">
                            <Typography>Trucks In Facility</Typography>
                        </Box>
                        <TruckInFacility facilityId={facilityId} />
                    </Box>
                ) : null}

                {containers?.length > 0 ? (
                    <Box sx={{ marginBottom: '16px' }}>
                        <Box className="title">
                            <Typography>Containers In Facility</Typography>
                        </Box>
                        <ContainerInFacility facilityId={facilityId} />
                    </Box>
                ) : null}

                {trailers?.length > 0 ? (
                    <Box sx={{ marginBottom: '16px' }}>
                        <Box className="title">
                            <Typography>Trailers In Facility</Typography>
                        </Box>
                        <TrailerInFacility facilityId={facilityId} />
                    </Box>
                ) : null}
                {openModify ? <FacilityModal open={openModify} handleClose={handleClose} facility={facility}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} /> : null}
            </Container>
        </Box>
    )
}
export default FacilityDetail;