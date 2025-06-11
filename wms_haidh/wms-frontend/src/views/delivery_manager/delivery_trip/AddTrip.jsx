import React, { useState, useEffect, useRef, useMemo } from 'react';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate } from 'react-router-dom';
import Skeleton from "@mui/material/Skeleton";
import { request } from "../../../api";
import {
  Box,
  TextField,
  Typography,
  Grid,
  Button,
  Paper,
  IconButton,
  Select,
  MenuItem,
  InputLabel,
  FormControl,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TablePagination,
  TableHead,
  TableRow,
  Checkbox
} from '@mui/material';
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import MyMap from '../../../components/Map';
import fetchRoute from '../../../utils/fetchRoute';
import { formatDate } from '../../../utils/utils';
import SaveIcon from '@mui/icons-material/Save';
import { toast, Toaster } from "react-hot-toast";

const AddTrip = () => {
  const navigate = useNavigate();
  const [description, setDescription] = useState('');
  const [warehouseId, setWarehouseId] = useState('');
  const [warehouseOptions, setWarehouseOptions] = useState([]);
  const [deliveryPersonOptions, setDeliveryPersonOptions] = useState([]);
  const [deliveryPersonId, setDeliveryPersonId] = useState('');
  const [shipmentOptions, setShipmentOptions] = useState([]);
  const [shipmentId, setShipmentId] = useState('');
  const [vehicleOptions, setVehicleOptions] = useState([]);
  const [vehicleId, setVehicleId] = useState('');
  const [assignedItems, setAssignedItems] = useState([]);
  const [deliverySequence, setDeliverySequence] = useState([]);
  const [customerInfo, setCustomerInfo] = useState({});
  const [totalWeight, setTotalWeight] = useState(0);
  const [details, setDetails] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(3);
  const [totalItems, setTotalItems] = useState(0);
  const [loading, setLoading] = useState(false);
  const [selectedItems, setSelectedItems] = useState(new Set());
  const [route, setRoute] = useState([]);
  const [loadingMap, setLoadingMap] = useState(true);
  const [distance, setDistance] = useState(0);
  const [maxWeight, setMaxWeight] = useState(0);
  const prevWarehouseId = useRef(warehouseId);

  const locationSequenceMap = useMemo(() => {
    const seen = new Set();
    let sequence = 1;
    const map = {};
    for (const item of deliverySequence) {
      const addressId = customerInfo[item.orderId]?.customerAddressId;
      if (addressId && !seen.has(addressId)) {
        seen.add(addressId);
        map[addressId] = sequence++;
      }
    }
    return map;
  }, [deliverySequence, customerInfo]);

  const groupedSequence = useMemo(() => {
    const groups = new Map();
    for (const item of deliverySequence) {
      const addressId = customerInfo[item.orderId]?.customerAddressId;
      if (!addressId) continue;
      if (!groups.has(addressId)) {
        groups.set(addressId, []);
      }
      groups.get(addressId).push(item);
    }
    return Array.from(groups.entries()); // [ [addressId, [item, item]], ... ]
  }, [deliverySequence, customerInfo]);

  const totalLocations = groupedSequence.length;

  const selectedWarehouse = useMemo(() =>
    warehouseOptions.find(wh => wh.warehouseId === warehouseId),
    [warehouseId, warehouseOptions]
  );

  const warehouseCoordinates = useMemo(() =>
    selectedWarehouse ? { lat: selectedWarehouse.latitude, lng: selectedWarehouse.longitude } : null,
    [selectedWarehouse]
  );

  const customerCoordinates = useMemo(() =>
    groupedSequence.map(([addressId, orders]) => {
      const firstOrderId = orders[0]?.orderId;
      const info = customerInfo[firstOrderId];
      return info ? { lat: info.latitude, lng: info.longitude } : null;
    }).filter(Boolean),
    [groupedSequence, customerInfo]
  );

  const coordinates = useMemo(() =>
    warehouseCoordinates
      ? [warehouseCoordinates, ...customerCoordinates, warehouseCoordinates]
      : [],
    [warehouseCoordinates, customerCoordinates]
  );


  const prevCoords = useRef([]);

  useEffect(() => {
    if (coordinates.length < 3 || warehouseId !== prevWarehouseId.current) {
      setLoadingMap(true);
      setDistance(0);  // Reset distance khi không có đủ điểm
      return;
    }
    prevCoords.current = coordinates;
    fetchRoute(coordinates, setRoute, setDistance, setLoadingMap);
  }, [coordinates, warehouseId]);

  useEffect(() => {
    request("get", "/warehouses", (res) => {
      setWarehouseOptions(res.data);
    }).then();
  }, []);

  useEffect(() => {
    request("get", "/delivery-persons/available", (res) => {
      setDeliveryPersonOptions(res.data);
    }).then();
  }, []);
  useEffect(() => {
    request("get", "/shipments", (res) => {
      setShipmentOptions(res.data);
    }).then();
  }, []);
  useEffect(() => {
    request("get", "/vehicles", (res) => {
      setVehicleOptions(res.data);
    }).then();
  }, []);

  useEffect(() => {
    if (warehouseId !== prevWarehouseId.current) {
      setTotalWeight(0);
      setLoadingMap(true);
      setDistance(0);
      setRoute([]);
      setCustomerInfo({});
      setDeliverySequence([]);
      setSelectedItems(new Set());
      prevWarehouseId.current = warehouseId;
    }
  }, [warehouseId]);

  useEffect(() => {
    if (warehouseId) {
      setLoading(true);
      request(
        "get",
        `/assigned-order-items/picked?warehouseId=${warehouseId}&page=${page}&size=${rowsPerPage}`,
        (res) => {
          const newDetails = res.data.content;
          setTimeout(() => {
            setTotalItems(res.data.totalElements);
            setDetails(newDetails);
            setLoading(false);
          }, 200);
        }
      ).catch(() => setLoading(false));
    }
  }, [warehouseId, page, rowsPerPage]);


  const handleSubmit = async () => {

    if (totalWeight > maxWeight) {
      toast.error("Total weight exceeds vehicle load capacity ! ");
      return;
    }

    const orderSequenceMap = groupedSequence.reduce((acc, [customerAddressId, orders], index) => {
      orders.forEach(order => {
        acc[order.orderId] = index + 1; // sequence của nhóm
      });
      return acc;
    }, {});


    const submittedData = assignedItems.map(item => ({
      assignedOrderItemId: item.assignedOrderItemId,
      quantity: item.originalQuantity,
      orderId: item.orderId,
      sequence: orderSequenceMap[item.orderId] || null,
    }));

    const payload = {
      warehouseId,
      vehicleId,
      deliveryPersonId,
      description,
      shipmentId,
      totalWeight,
      totalLocations,
      distance,
      items: submittedData,
      coordinates
    };
    // console.log(payload);

    const requestUrl = "/delivery-trips";

    request("post", requestUrl, (res) => {
      if (res.status === 200) {
        alert("Trip created successfully!");
        navigate(`/delivery-manager/delivery-trip`);
      } else {
        alert("Failed to create trip!");
      }
    }, {
      onError: (e) => {
        toast.error(e?.response?.data || "Error occured!");
      }
    }, payload);
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleVehicleChange = (e) => {
    const selectedId = e.target.value;
    setVehicleId(selectedId);

    const selectedVehicle = vehicleOptions.find(v => v.vehicleId === selectedId);
    setMaxWeight(selectedVehicle.maxWeight);

  };

  const fetchCustomerInfo = async (orderId) => {
    if (customerInfo[orderId]) return;
    request("get", `/orders/${orderId}/customer-address`, (res) => {
      setCustomerInfo((prev) => ({ ...prev, [orderId]: res.data }));
    }).then();
  };

  const handleSelect = (item, checked) => {
    const itemWeight = item.weight * item.originalQuantity;
    setAssignedItems((prev) => {
      let updatedItems = checked
        ? [...prev, item]
        : prev.filter((i) => i.assignedOrderItemId !== item.assignedOrderItemId);

      let orderExists = deliverySequence.some(d => d.orderId === item.orderId);
      if (checked && !orderExists) {
        setDeliverySequence((prevSeq) => [...prevSeq, { orderId: item.orderId }]);
        fetchCustomerInfo(item.orderId);
      }

      if (!checked && !updatedItems.some(i => i.orderId === item.orderId)) {
        setDeliverySequence((prevSeq) => prevSeq.filter(d => d.orderId !== item.orderId));
      }

      return updatedItems;
    });

    setTotalWeight((prevWeight) => checked ? prevWeight + itemWeight : prevWeight - itemWeight);

    // Cập nhật trạng thái đã chọn
    setSelectedItems(prev => {
      const updated = new Set(prev);
      if (checked) {
        updated.add(item.assignedOrderItemId);
      } else {
        updated.delete(item.assignedOrderItemId);
      }
      return updated;
    });
  };


  const handleDragEnd = (result) => {
    if (!result.destination) return;

    // nhóm lại như groupedSequence
    const groupMap = new Map();
    for (const item of deliverySequence) {
      const addressId = customerInfo[item.orderId]?.customerAddressId;
      if (!groupMap.has(addressId)) {
        groupMap.set(addressId, []);
      }
      groupMap.get(addressId).push(item);
    }
    const grouped = Array.from(groupMap.values()); // list of groups

    // Di chuyển nhóm
    const [movedGroup] = grouped.splice(result.source.index, 1);
    grouped.splice(result.destination.index, 0, movedGroup);

    // Flatten lại
    const newSequence = grouped.flat();
    setDeliverySequence(newSequence);
  };

  return (
    <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
      <Toaster />
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton color="primary" onClick={() => navigate('/delivery-manager/delivery-trip')} sx={{ color: 'grey.700', mr: 1 }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
          Create New Delivery Trip
        </Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<SaveIcon />}
          sx={{
            marginLeft: 'auto',
            backgroundColor: '#019160',
            color: '#fff',
            '&:hover': {
              backgroundColor: '#2fbe8e',
            },
            '&:active': {
              backgroundColor: '#01b075',
            },
          }}
          onClick={handleSubmit}
        >
          Save
        </Button>
      </Box>
      <Box sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom sx={{ mb: 2 }}>
            General information
          </Typography>

          <Grid container spacing={3}>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Departure Warehouse</InputLabel>
                <Select
                  value={warehouseId}
                  onChange={(e) => setWarehouseId(e.target.value)}
                  label="Departure Warehouse"
                >
                  {warehouseOptions.map((wh) => (
                    <MenuItem key={wh.warehouseId} value={wh.warehouseId}>
                      {wh.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Shipment</InputLabel>
                <Select
                  value={shipmentId}
                  onChange={(e) => setShipmentId(e.target.value)}
                  label="Shipment"
                >
                  {shipmentOptions.map((item) => (
                    <MenuItem key={item.shipmentId} value={item.shipmentId}>
                      {item.shipmentId} - {formatDate(item.expectedDeliveryStamp)}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Delivery Person</InputLabel>
                <Select
                  value={deliveryPersonId}
                  onChange={(e) => setDeliveryPersonId(e.target.value)}
                  label="Delivery Person"
                >
                  {deliveryPersonOptions.map((item) => (
                    <MenuItem key={item.userLoginId} value={item.userLoginId}>
                      {item.fullName}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Vehicle</InputLabel>
                <Select
                  value={vehicleId}
                  onChange={handleVehicleChange}
                  label="Vehicle"
                >
                  {vehicleOptions.map((item) => (
                    <MenuItem key={item.vehicleId} value={item.vehicleId}>
                      {item.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <TextField
                  fullWidth
                  label="Description"
                  placeholder="Enter trip description"
                  multiline
                  rows={2}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
              </FormControl>
            </Grid>

          </Grid>
        </Paper>
        <Paper elevation={3} sx={{ p: 3, mt: 4 }}>
          <Typography variant="h6" gutterBottom sx={{ mb: 2 }}>
            Assigned order items
          </Typography>
          <div className='mb-4'>
            <Typography
              variant="h7"
              gutterBottom
              className={totalWeight > maxWeight ? "text-red-500" : "text-green-500"}
            >
              Total weight: {totalWeight} / {maxWeight} kg
            </Typography>

          </div>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Select</TableCell>
                  <TableCell>Product</TableCell>
                  <TableCell align="center">Weight (kg)</TableCell>
                  <TableCell align="center">Quantity</TableCell>
                  <TableCell align="center">Bay Code</TableCell>
                  <TableCell align="center">Lot ID</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {loading
                  ? Array.from({ length: rowsPerPage }).map((_, index) => (
                    <TableRow key={`skeleton-${index}`}>
                      <TableCell width={50}>
                        <Checkbox
                          checked={false}
                          inputProps={{ "aria-label": "Select product" }}
                        />
                      </TableCell>
                      <TableCell width={200}>
                        <Skeleton variant="text" />
                      </TableCell>
                      <TableCell width={100} align="center">
                        <Skeleton variant="text" />
                      </TableCell>
                      <TableCell width={100} align="center">
                        <Skeleton variant="text" />
                      </TableCell>
                      <TableCell width={120} align="center">
                        <Skeleton variant="text" />
                      </TableCell>
                      <TableCell width={120} align="center">
                        <Skeleton variant="text" />
                      </TableCell>
                    </TableRow>
                  ))
                  : details.map((detail) => (
                    <TableRow key={detail.assignedOrderItemId}>
                      <TableCell width={50}>
                        <Checkbox
                          checked={selectedItems.has(detail.assignedOrderItemId)}
                          onChange={(e) => handleSelect(detail, e.target.checked)}
                          inputProps={{ "aria-label": "Select product" }}
                        />
                      </TableCell>
                      <TableCell width={200}>{detail.productName}</TableCell>
                      <TableCell width={100} align="center">{detail.weight}</TableCell>
                      <TableCell width={100} align="center">{detail.originalQuantity}</TableCell>
                      <TableCell width={120} align="center">{detail.bayCode}</TableCell>
                      <TableCell width={120} align="center">{detail.lotId}</TableCell>
                    </TableRow>
                  ))}
              </TableBody>

            </Table>
            <TablePagination
              rowsPerPageOptions={[3, 5, 10,]}
              component="div"
              count={totalItems}
              rowsPerPage={rowsPerPage}
              page={page}
              onPageChange={handleChangePage}
              onRowsPerPageChange={handleChangeRowsPerPage}
            />
          </TableContainer>
        </Paper>
        <Paper elevation={3} sx={{ p: 3, mt: 4 }}>
          <Typography variant="h6" gutterBottom>
            Delivery sequence (Drag & Drop)
          </Typography>
          <Typography variant="h7" gutterBottom className="text-green-500">
            Total orders : {deliverySequence.length}
          </Typography>
          <div className='mb-4'>
            <Typography variant="h7" gutterBottom className="text-green-500">
              Total locations : {totalLocations}
            </Typography>
          </div>
          <DragDropContext onDragEnd={handleDragEnd}>
            <Droppable droppableId="deliverySequence">
              {(provided) => (
                <TableContainer ref={provided.innerRef} {...provided.droppableProps}>
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell>#</TableCell>
                        <TableCell align="center">Customer Name</TableCell>
                        <TableCell align="center">Phone Number</TableCell>
                        <TableCell>Customer Address</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {groupedSequence.map(([addressId, orders], index) => (
                        <Draggable key={addressId} draggableId={addressId} index={index}>
                          {(provided) => (
                            <React.Fragment>
                              {orders.map((item, i) => (
                                <TableRow
                                  key={item.orderId}
                                  ref={i === 0 ? provided.innerRef : null}
                                  {...(i === 0 ? provided.draggableProps : {})}
                                  {...(i === 0 ? provided.dragHandleProps : {})}
                                >
                                  <TableCell>
                                    {locationSequenceMap[customerInfo[item.orderId]?.customerAddressId] || '-'}
                                  </TableCell>
                                  <TableCell sx={{ textAlign: 'center' }}>{customerInfo[item.orderId]?.customerName || 'Loading...'}</TableCell>
                                  <TableCell sx={{ textAlign: 'center' }}>{customerInfo[item.orderId]?.customerPhoneNumber || 'Loading...'}</TableCell>
                                  <TableCell>{customerInfo[item.orderId]?.addressName || 'Loading...'}</TableCell>
                                </TableRow>
                              ))}
                            </React.Fragment>
                          )}
                        </Draggable>
                      ))}

                      {provided.placeholder}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
            </Droppable>
          </DragDropContext>
        </Paper>
        <Grid container sx={{ mt: 4 }}>
          {/* Map Container */}
          <Grid item xs={12}>
            <Paper elevation={3} sx={{ p: 3, height: 540 }}>
              <Typography
                variant="h6"
                gutterBottom
                sx={{
                  textAlign: 'center'
                }}
              >
                Delivery Route
              </Typography>
              <Typography variant="h6" gutterBottom className="text-green-500" sx={{
                textAlign: 'center',
                marginBottom: 2,    // Spacing below title
              }}>
                Distance : {(distance / 1000).toFixed(2)} km
              </Typography>

              <Box sx={{ height: '100%', borderRadius: 1, overflow: 'hidden' }}>
                {/* Pass the route data to the Map component */}
                {loadingMap ? (
                  <Typography variant="h6" sx={{ textAlign: 'center' }}>
                    Loading route...
                  </Typography>
                ) : (
                  <MyMap route={route} markerCoordinates={coordinates} />
                )}
              </Box>
            </Paper>
          </Grid>

        </Grid>
      </Box>
    </Box>
  );
};

export default AddTrip;