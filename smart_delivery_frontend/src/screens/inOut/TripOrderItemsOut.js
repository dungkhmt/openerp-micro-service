import React, {useEffect, useState} from 'react';
import {request} from "../../api";
import {useParams} from "react-router-dom";
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import StandardTable from "../../components/StandardTable";
import {Box, Button, Typography, IconButton, Tabs, Tab} from "@mui/material";
import {errorNoti, successNoti} from "../../utils/notification";

const TripOrderItemsOut = () => {
    const {tripId} = useParams();
    const [tripDetails, setTripDetails] = useState(null);
    const [tripItems, setTripItems] = useState([]);
    const [selectedTripItems, setSelectedTripItems] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [activeTab, setActiveTab] = useState(0);

    useEffect(() => {
        if (tripId) {
            fetchTripDetails();
            fetchTripItems();
        }
    }, [tripId]);

    const fetchTripDetails = () => {
        request(
            "get",
            `smdeli/trip-assignments/trips/${tripId}/orders`,
            (res) => {
                setTripDetails(res.data);
            },
            {
                400: () => errorNoti("Invalid data"),
                500: () => errorNoti("Server error, please try again later")
            }
        );
    };

    const fetchTripItems = () => {
        setIsLoading(true);
        request(
            "get",
            `smdeli/trip-items/trip/${tripId}`,
            (res) => {
                setTripItems(res.data);
                setIsLoading(false);
            },
            {
                400: () => {
                    errorNoti("Invalid data");
                    setIsLoading(false);
                },
                500: () => {
                    errorNoti("Server error, please try again later");
                    setIsLoading(false);
                }
            }
        );
    };

    const confirmHandle = (selectedIds) => {
        if(selectedIds.length === 0) {
            errorNoti("Please select order items");
            return;
        }
        const ids = Array.isArray(selectedIds) && selectedIds.length > 1
            ? selectedIds.join(',')
            : selectedIds;

        request(
            "put",
            `smdeli/trip-items/confirm-out`,
            (res) => {
                successNoti(`Successfully confirmed ${res.data.itemCount} items for outbound`);
                fetchTripItems();
                setSelectedTripItems([]);
            },
            {
                500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
            },
            selectedIds
        )

    }
    const confirmOutTripItems = () => {
        if (selectedTripItems.length === 0) {
            errorNoti("Please select order items");
            return;
        }

        const tripItemIds = selectedTripItems.map(item => item.id);

        request(
            "put",
            'smdeli/trip-items/confirm-out',
            (res) => {
                successNoti(`Successfully confirmed ${res.data.itemCount} items for outbound`);
                fetchTripItems();
                setSelectedTripItems([]);
            },
            {
                400: () => errorNoti("Invalid data"),
                500: () => errorNoti("Server error, please try again later")
            },
            tripItemIds
        );
    };

    const confirmInTripItems = () => {
        if (selectedTripItems.length === 0) {
            errorNoti("Please select order items");
            return;
        }

        const tripItemIds = selectedTripItems.map(item => item.id);

        request(
            "put",
            'smdeli/trip-items/confirm-in',
            (res) => {
                successNoti(`Successfully confirmed ${res.data.itemCount} items for inbound`);
                fetchTripItems();
                setSelectedTripItems([]);
            },
            {
                400: () => errorNoti("Invalid data"),
                500: () => errorNoti("Server error, please try again later")
            },
            tripItemIds
        );
    };

    const handleGoBack = () => {
        window.history.back();
    };

    const handleTabChange = (event, newValue) => {
        setActiveTab(newValue);
        setSelectedTripItems([]);
    };

    // Filter trip items based on active tab
    const getFilteredTripItems = () => {
        if (activeTab === 0) {
            // Out tab - show items pending outbound confirmation
            return tripItems.filter(item => item.status === "PENDING" || !item.confirmedOutBy);
        } else {
            // In tab - show items that are picked up but not yet delivered
            return tripItems.filter(item => item.status === "PICKED_UP" && !item.confirmedInBy);
        }
    };

    // Trip items columns
    const tripItemColumns = [
        {
            title: "Trip Item ID",
            field: "id",
        },
        {
            title: "Order Item ID",
            field: "orderItemId",
        },
        {
            title: "Status",
            field: "status"
        },
        {
            title: "Sequence",
            field: "sequenceNumber"
        },
        {
            title: "Out Confirmed By",
            field: "confirmedOutBy",
            renderCell: (rowData) => rowData.confirmedOutBy || "-"
        },
        {
            title: "In Confirmed By",
            field: "confirmedInBy",
            renderCell: (rowData) => rowData.confirmedInBy || "-"
        },
        {
            title: "Created At",
            field: "createdAt",
            renderCell: (rowData) => {
                const date = new Date(rowData.createdAt);
                return date.toLocaleString();
            }
        }
    ];

    return (
        <div>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <IconButton onClick={handleGoBack} color="primary" sx={{ mr: 2 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h5">
                    Order Items for Trip: {tripDetails?.routeName || tripId}
                </Typography>
            </Box>

            {tripDetails && (
                <Box sx={{ mb: 3 }}>
                    <Typography variant="subtitle1">
                        <strong>Route:</strong> {tripDetails.routeName}
                    </Typography>
                    <Typography variant="subtitle1">
                        <strong>Status:</strong> {tripDetails.status}
                    </Typography>
                    <Typography variant="subtitle1">
                        <strong>Date:</strong> {tripDetails.date}
                    </Typography>
                    {tripDetails.vehiclePlateNumber && (
                        <Typography variant="subtitle1">
                            <strong>Vehicle:</strong> {tripDetails.vehiclePlateNumber}
                        </Typography>
                    )}
                </Box>
            )}

            <Tabs value={activeTab} onChange={handleTabChange} sx={{ mb: 2 }}>
                <Tab label="Outbound Confirmation" />
            </Tabs>

            {isLoading ? (
                <Typography>Loading trip items...</Typography>
            ) : (
                <>
                    <StandardTable
                        title={"Pending Outbound Items" }
                        columns={tripItemColumns}
                        data={getFilteredTripItems()}
                        rowKey="id"
                        editable={true}
                        onRowClick={(evt, selectedRow) => {
                            const isSelected = selectedTripItems.some(item => item.id === selectedRow.id);
                            if (isSelected) {
                                setSelectedTripItems(selectedTripItems.filter(item => item.id !== selectedRow.id));
                            } else {
                                setSelectedTripItems([...selectedTripItems, selectedRow]);
                            }
                        }}
                        actions={[
                            {
                                iconOnClickHandle: confirmHandle,
                                tooltip: "Duyệt ra",
                            }
                        ]}
                        selectedData={selectedTripItems}
                        options={{
                            selection: true,
                            pageSize: 10,
                            search: true,
                            sorting: true,
                            headerStyle: {
                                backgroundColor: '#f5f5f5',
                                fontWeight: 'bold'
                            }
                        }}
                    />

                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 3 }}>
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={ confirmOutTripItems}
                            disabled={selectedTripItems.length === 0}
                        >
                            'Confirm Outbound'
                        </Button>
                    </Box>
                </>
            )}
        </div>
    );
};

export default TripOrderItemsOut;