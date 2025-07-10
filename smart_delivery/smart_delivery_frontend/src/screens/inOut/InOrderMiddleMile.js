import React, {useEffect, useState} from 'react';
import {request} from "../../api";
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import StandardTable from "../../components/StandardTable";
import {Box} from "@mui/material";
import {useSelector} from "react-redux";
import {errorNoti, successNoti} from "../../utils/notification";


const InOrderMiddleMile = () => {
    const hubId = useSelector((state) => state.auth.hubId);

    // Trip related states
    const [trips, setTrips] = useState([]);
    const [tripsWithVehicles, setTripsWithVehicles] = useState([]);
    const [selectedTrip, setSelectedTrip] = useState(null);
    const [tripOrderItems, setTripOrderItems] = useState([]);
    const [selectedOrderItems, setSelectedOrderItems] = useState([]);

    // Fetch trips for today at this hub
    useEffect(() => {
        if (hubId) {
            fetchTripsForToday();
        }
    }, [hubId]);

    // After fetching trips, get vehicles for each trip
    useEffect(() => {
        if (trips.length > 0) {
            const tripIds = trips.map(trip => trip.id);
            fetchVehiclesForTrips(tripIds);
        }
    }, [trips]);

    const fetchTripsForToday = () => {
        request(
            "get",
            `smdeli/trip-assignments/hub/${hubId}/today/through`,
            (res) => {
                setTrips(res.data);
            },
            {
                400: () => errorNoti("Invalid data"),
                500: () => errorNoti("Server error, please try again later")
            }
        );
    };

    const fetchVehiclesForTrips = (tripIds) => {
        // If there are no trips, don't make the API call
        if (!tripIds || tripIds.length === 0) return;

        // For each trip, fetch its vehicle
        const promises = tripIds.map(tripId =>
            new Promise((resolve) => {
                request(
                    "get",
                    `smdeli/vehicle/trip/${tripId}`,
                    (res) => {
                        resolve({ tripId, vehicle: res.data });
                    },
                    {
                        400: () => {
                            console.error(`Error fetching vehicle for trip ${tripId}`);
                            resolve({ tripId, vehicle: null });
                        },
                        500: () => {
                            console.error(`Server error fetching vehicle for trip ${tripId}`);
                            resolve({ tripId, vehicle: null });
                        }
                    }
                );
            })
        );

        // After all promises resolve, update the trips with vehicle information
        Promise.all(promises).then(results => {
            const vehicleMap = results.reduce((map, result) => {
                map[result.tripId] = result.vehicle;
                return map;
            }, {});

            // Create a new array of trips with vehicle information
            const updatedTrips = trips.map(trip => ({
                ...trip,
                vehicle: vehicleMap[trip.id] || null,
                plateNumber: vehicleMap[trip.id]?.plateNumber || 'Not assigned'
            }));

            setTripsWithVehicles(updatedTrips);
        });
    };

    const handleTripSelection = (trip) => {
        // Navigate to the order items view screen for this trip
        window.location.href = `/order/trip/items/${trip.id}/out`;
    };

    const fetchOrderItemsForTrip = (tripId) => {
        request(
            "get",
            `smdeli/driver/current-orders/${tripId}`,
            (res) => {
                setTripOrderItems(res.data);
            },
            {
                400: () => errorNoti("Invalid data"),
                500: () => errorNoti("Server error, please try again later")
            }
        );
    };

    const confirmOrderItemsForTrip = () => {
        if (!selectedTrip) {
            errorNoti("Please select a trip");
            return;
        }

        if (selectedOrderItems.length === 0) {
            errorNoti("Please select order items");
            return;
        }

        const orderItemIds = selectedOrderItems.map(item => item.id);

        request(
            "put",
            `smdeli/collected-hub/complete/${orderItemIds.join(',')}`,
            (res) => {
                successNoti("Orders confirmed for trip successfully");
                fetchOrderItemsForTrip(selectedTrip.id);
                setSelectedOrderItems([]);
            },
            {
                400: () => errorNoti("Invalid data"),
                500: () => errorNoti("Server error, please try again later")
            }
        );
    };

    // Trip table columns with vehicle information
    const tripColumns = [
        {
            title: "Trip ID",
            field: "id",
        },
        {
            title: "Route",
            field: "routeName",
        },
        {
            title: "Vehicle Plate Number",
            field: "plateNumber",
        },
        {
            title: "Status",
            field: "status",
        },
        {
            title: "Date",
            field: "date",
        },
        {
            title: "Action",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <IconButton
                    onClick={() => handleTripSelection(rowData)}
                    color="primary"
                >
                    <VisibilityIcon />
                </IconButton>
            ),
        },
    ];

    // Order items columns
    const orderItemColumns = [
        {
            title: "Order Item ID",
            field: "id",
        },
        {
            title: "Order ID",
            field: "orderId",
        },
        {
            title: "Sender",
            field: "senderName",
        },
        {
            title: "Recipient",
            field: "recipientName",
        },
        {
            title: "Status",
            field: "status"
        },
        {
            title: "Weight (kg)",
            field: "weight"
        },
        {
            title: "Volume (m³)",
            field: "volume"
        }
    ];

    return (
        <div>
            <StandardTable
                title="Today's Trips"
                columns={tripColumns}
                data={tripsWithVehicles}
                rowKey="id"
                options={{
                    pageSize: 5,
                    search: true,
                    sorting: true,
                    headerStyle: {
                        backgroundColor: '#f5f5f5',
                        fontWeight: 'bold'
                    }
                }}
            />

            {/* Order items view has been moved to a separate page */}
        </div>
    );
}

export default InOrderMiddleMile;