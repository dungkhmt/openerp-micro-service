import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TablePagination,
  TableHead,
  TableRow,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../api";
import Skeleton from "@mui/material/Skeleton";


const DeliveryTripItem = () => {
  const navigate = useNavigate();
  const { id1, id2 } = useParams();
  const [details, setDetails] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(3);
  const [loading, setLoading] = useState(false);
  const [totalItems, setTotalItems] = useState(0);

  useEffect(() => {
    if (id1 && id2) {
      setLoading(true);
      request("get", `/delivery-trip-items?deliveryTripId=${id1}&orderId=${id2}&page=${page}&size=${rowsPerPage}`, (res) => {
        setDetails(res.data.content);
        setTotalItems(res.data.totalElements);
        setLoading(false);
      }).catch(() => setLoading(false));
    }
  }, [id1, id2]);


  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton color="primary" onClick={() => navigate(`/delivery-staff/delivery-trip/${id1}`)} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 2 }}>
          Delivery Items
        </Typography>
      </Box>

      <Paper elevation={3} sx={{ p: 3, mt: 4 }}>
        <Typography variant="h6" gutterBottom sx={{ mb: 2 }}>
          Delivery items
        </Typography>
        <div className='mb-4'>
          <Typography variant="h7" gutterBottom className="text-green-500">
            Total items : {details.length}
          </Typography>
        </div>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Product</TableCell>
                <TableCell>Weight (kg)</TableCell>
                <TableCell>Quantity</TableCell>
                <TableCell>Unit</TableCell>
                <TableCell>Bay Code</TableCell>
                <TableCell>Lot ID</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading
                ? Array.from({ length: rowsPerPage }).map((_, index) => (
                  <TableRow key={`skeleton-${index}`}>
                    <TableCell width={200}>
                      <Skeleton variant="text" />
                    </TableCell>
                    <TableCell width={100}>
                      <Skeleton variant="text" />
                    </TableCell>
                    <TableCell width={100}>
                      <Skeleton variant="text" />
                    </TableCell>
                    <TableCell width={100}>
                      <Skeleton variant="text" />
                    </TableCell>
                    <TableCell width={120}>
                      <Skeleton variant="text" />
                    </TableCell>
                    <TableCell width={120}>
                      <Skeleton variant="text" />
                    </TableCell>
                  </TableRow>
                ))
                : details.map((detail) => (
                  <TableRow key={detail.id}>
                    <TableCell width={200}>{detail.productName}</TableCell>
                    <TableCell width={100}>{detail.weight}</TableCell>
                    <TableCell width={100}>{detail.quantity}</TableCell>
                    <TableCell width={100}>{detail.uom}</TableCell>
                    <TableCell width={120}>{detail.bayCode}</TableCell>
                    <TableCell width={120}>{detail.lotId}</TableCell>
                  </TableRow>
                ))}
            </TableBody>

          </Table>
          {/* Thêm phân trang */}
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

    </Box>
  );
};

export default DeliveryTripItem;
