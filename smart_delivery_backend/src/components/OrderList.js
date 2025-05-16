import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  List,
  ListItem,
  ListItemText,
  Typography,
  Paper,
  Divider,
  CircularProgress,
} from '@mui/material';

const OrderList = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    // TODO: Replace with actual API call
    const fetchOrders = async () => {
      try {
        // Simulated API call
        const response = await fetch('/api/orders');
        const data = await response.json();
        setOrders(data);
      } catch (error) {
        console.error('Error fetching orders:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, []);

  const handleOrderClick = (orderId) => {
    navigate(`/orders/${orderId}`);
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ maxWidth: 800, margin: '0 auto', padding: 2 }}>
      <Typography variant="h4" gutterBottom>
        My Orders
      </Typography>
      <Paper elevation={2}>
        <List>
          {orders.map((order, index) => (
            <React.Fragment key={order.id}>
              <ListItem
                button
                onClick={() => handleOrderClick(order.id)}
                sx={{
                  '&:hover': {
                    backgroundColor: 'rgba(0, 0, 0, 0.04)',
                  },
                }}
              >
                <ListItemText
                  primary={`Order #${order.id}`}
                  secondary={
                    <>
                      <Typography component="span" variant="body2" color="text.primary">
                        Status: {order.status}
                      </Typography>
                      <br />
                      <Typography component="span" variant="body2">
                        Date: {new Date(order.createdAt).toLocaleDateString()}
                      </Typography>
                      <br />
                      <Typography component="span" variant="body2">
                        Total: ${order.total.toFixed(2)}
                      </Typography>
                    </>
                  }
                />
              </ListItem>
              {index < orders.length - 1 && <Divider />}
            </React.Fragment>
          ))}
          {orders.length === 0 && (
            <ListItem>
              <ListItemText primary="No orders found" />
            </ListItem>
          )}
        </List>
      </Paper>
    </Box>
  );
};

export default OrderList; 