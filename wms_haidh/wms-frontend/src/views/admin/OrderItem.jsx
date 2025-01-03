import React, { useState, useEffect } from 'react';
import {
  Box,
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
  TableHead,
  TableRow,
  TextField,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { CircularProgress } from "@nextui-org/react";
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../api";

const OrderItem = () => {
  const navigate = useNavigate();
  const { id1, id2 } = useParams();
  const [generalInfo, setGeneralInfo] = useState(null);
  const [warehouseOptions, setWarehouseOptions] = useState([]);
  const [bayOptions, setBayOptions] = useState([]);
  const [lotIds, setLotIds] = useState([]);
  const [quantity, setQuantity] = useState('');
  const [warehouseId, setWarehouseId] = useState('');
  const [bayId, setBayId] = useState('');
  const [lotId, setLotId] = useState('');
  const [quantityOnHand, setQuantityOnHand] = useState('');
  const [details, setDetails] = useState([]);

  useEffect(() => {
    request("get", `/admin/orders/general-info/${id2}`, (res) => {
      setGeneralInfo(res.data);
    });

    request("get", `/admin/orders/warehouses/${id2}`, (res) => {
      setWarehouseOptions(res.data);
    });

    request("get", `/admin/orders/detail-info/${id2}`, (res) => {
      setDetails(res.data);
    });

  }, [id2]);

  useEffect(() => {
    if (warehouseId) {
      request("get", `/admin/orders/bays?saleOrderItemId=${id2}&warehouseId=${warehouseId}`, (res) => {
        setBayOptions(res.data);
      }).then();
    }
  }, [warehouseId]);

  useEffect(() => {
    if (bayId) {
      request("get", `/admin/orders/lots?saleOrderItemId=${id2}&bayId=${bayId}`, (res) => {
        setLotIds(res.data);
      }).then();
    }
  }, [bayId]);

  useEffect(() => {
    if (lotId) {
      request("get", `/admin/orders/quantity?saleOrderItemId=${id2}&bayId=${bayId}&lotId=${lotId}`, (res) => {
        setQuantityOnHand(res.data);
      }).then();
    }
  }, [lotId]);

  const handleSubmit = () => {
    const payload = {
      warehouseId,
      bayId,
      lotId,
      quantity,
      saleOrderItemId: id2,
      assignedBy: "admin"
    };
    console.log(payload);
    request("post", `/admin/orders/assign`, (res) => {
      if (res.status === 200) {
        request("get", `/admin/orders/general-info/${id2}`, (res) => {
          setGeneralInfo(res.data);
        });
        request("get", `/admin/orders/detail-info/${id2}`, (res) => {
          setDetails(res.data);
        });
        alert("Assign successfully!")
      } else {
        alert("Error occcured while assigning order item!");
      }
    }, {}, payload);
  };

  const formatPrice = (price) => {
    return price.toLocaleString("vi-VN", {
      style: "currency",
      currency: "VND",
    });
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton color="primary" onClick={() => navigate(`/admin/orders/${id1}`)} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 2 }}>
          Assign order
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Box chứa vòng tròn */}
        <Grid item xs={12} md={4} sx={{ maxWidth: '30%' }}>
          <Paper
            elevation={3}
            sx={{
              p: 3,
              textAlign: 'center',
              height: 300,
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              position: 'relative', // Quan trọng để dùng positioning bên trong
            }}
          >
            <CircularProgress
              aria-label="Loading..."
              classNames={{
                svg: "w-48 h-48 drop-shadow-lg", // Kích thước vòng tròn
                indicator: (generalInfo && generalInfo.completed === 100) ? "stroke-green-400" : "stroke-orange-400",
                track: "stroke-gray-300",        // Màu nền vòng
              }}
              strokeWidth={2}
              value={generalInfo ? generalInfo.completed : 0} // Giá trị 100%
            />
            {/* Nội dung trong tâm */}
            <Box
              sx={{
                position: 'absolute', // Canh giữa
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
              }}
            >
              <Typography variant="h4" >
                {generalInfo ? generalInfo.completed : 0}%
              </Typography>
              <Typography variant="subtitle1" >
                {generalInfo && generalInfo.completed === 100 ? "Completed" : "In progress"}
              </Typography>
            </Box>
          </Paper>
        </Grid>

        <Grid item xs={12} md={5}>
          <Paper
            elevation={3}
            sx={{
              p: 3,
              height: 300, // Chiều cao bằng với box bên trái
            }}
          >
            <Typography
              variant="h6"
              gutterBottom
              sx={{
                textAlign: 'center', // Căn giữa
                marginBottom: 2, // Khoảng cách bên dưới
              }}
            >
              Order item
            </Typography>
            <Typography>
              <b>Product:</b>
              <br />
              {generalInfo?.productName}
            </Typography>
            <Typography>
              <b>Quantity:</b>
              <br />
              {generalInfo?.quantity}
            </Typography>
            <Typography>
              <b>Price Unit:</b>
              <br />
              {generalInfo && formatPrice(generalInfo.priceUnit)}
            </Typography>
          </Paper>
        </Grid>


      </Grid>

      {generalInfo && generalInfo.completed < 100 &&
        <Box sx={{ mt: 4 }}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Assign order
            </Typography>
            <div className='mb-4'>
              <Typography variant="h7" gutterBottom className="text-green-500">
                Quantity on hand : {quantityOnHand}
              </Typography>
            </div>

            <Grid container spacing={3}>
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth>
                  <InputLabel>Warehouse</InputLabel>
                  <Select
                    value={warehouseId}
                    onChange={(e) => setWarehouseId(e.target.value)}
                    label="Warehouse"
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
                  <InputLabel>Bay Code</InputLabel>
                  <Select
                    value={bayId}
                    onChange={(e) => setBayId(e.target.value)}
                    label="Bay Code"
                  >
                    {bayOptions.map((bay) => (
                      <MenuItem key={bay.bayId} value={bay.bayId}>
                        {bay.code}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth>
                  <InputLabel>Lot ID</InputLabel>
                  <Select
                    value={lotId}
                    onChange={(e) => setLotId(e.target.value)}
                    label="Lot ID"
                  >
                    {lotIds.map((lot) => (
                      <MenuItem key={lot.lotId} value={lot.lotId}>
                        {lot.lotId}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Quantity"
                  type="number"
                  value={quantity}
                  onChange={(e) => setQuantity(e.target.value)}
                />
              </Grid>
            </Grid>
            <Box sx={{ mt: 3, textAlign: 'right' }}>
              <Button variant="contained" color="primary" onClick={handleSubmit}>
                Submit
              </Button>
            </Box>
          </Paper>
        </Box>
      }
      <Box sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            Detail Information
          </Typography>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Warehouse</TableCell>
                  <TableCell sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Bay Code</TableCell>
                  <TableCell sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Lot ID</TableCell>
                  <TableCell sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Quantity</TableCell>
                  <TableCell sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Status</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {details.map((detail, index) => (
                  <TableRow key={index}>
                    <TableCell>{detail.warehouseName}</TableCell>
                    <TableCell>{detail.bayCode}</TableCell>
                    <TableCell>{detail.lotId}</TableCell>
                    <TableCell>{detail.quantity}</TableCell>
                    <TableCell>{detail.status}</TableCell>

                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>
      </Box>
    </Box>
  );
};

export default OrderItem;
