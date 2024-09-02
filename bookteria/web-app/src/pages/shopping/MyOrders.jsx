import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  CircularProgress,
  IconButton,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import ordersService from '../../services/ordersService';
import { Link } from 'react-router-dom';
import Scene from '../Scene';
import userService from '../../services/userService';
import selectedProductService from '../../services/selectedProductService';

const MyOrders = () => {
  const [loading, setLoading] = useState(true);
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await userService.getMyInfo();
        if (response.data.code === 1000) {
          const ordersWithTotal = response.data.result.orders.map(order => {
            const totalAmount = order.selectedProducts.reduce((total, product) => {
              return total + (product.quantity * product.bookId.price);
            }, 0);
            return { ...order, totalAmount };
          });
          setOrders(ordersWithTotal);
        } else {
          console.error('Failed to fetch user orders');
        }
      } catch (error) {
        console.error('Error fetching user orders:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, []);

  const handleDelete = async (orderId) => {
    try {
      setLoading(true);
      const userInfo = await userService.getMyInfo();
      const userId = userInfo.data.result.userId;

      const order = orders.find(order => order.orderId === orderId);
      const selectedProductIds = order.selectedProducts.map(product => product.selectedId);

      await selectedProductService.removeSelectedProduct(orderId, selectedProductIds);
      await ordersService.deleteOrders(orderId);

      setOrders(prevOrders => prevOrders.filter(order => order.orderId !== orderId));
    } catch (error) {
      console.error('Error deleting order:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Scene>
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
          <CircularProgress />
        </Box>
      </Scene>
    );
  }

  return (
    <Scene>
      <Box sx={{ p: 3, bgcolor: '#303030', color: '#e0e0e0' }}>
        <Typography variant="h4" gutterBottom>
          Đơn hàng của tôi
        </Typography>
        {orders.length === 0 ? (
          <Typography variant="body1">Bạn chưa có đơn hàng nào.</Typography>
        ) : (
          <TableContainer component={Paper} sx={{ bgcolor: '#424242' }}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ color: '#e0e0e0' }}>ID Đơn hàng</TableCell>
                  <TableCell align="right" sx={{ color: '#e0e0e0' }}>Ngày đặt hàng</TableCell>
                  <TableCell align="right" sx={{ color: '#e0e0e0' }}>Tổng tiền</TableCell>
                  <TableCell align="right" sx={{ color: '#e0e0e0' }}>Số lượng</TableCell>
                  <TableCell align="center" sx={{ color: '#e0e0e0' }}>Chi tiết</TableCell>
                  <TableCell align="center" sx={{ color: '#e0e0e0' }}>Xóa</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {orders.map(order => (
                  <TableRow key={order.orderId}>
                    <TableCell component="th" scope="row" sx={{ color: '#e0e0e0' }}>
                      {order.orderId}
                    </TableCell>
                    <TableCell align="right" sx={{ color: '#e0e0e0' }}>{order.orderDate}</TableCell>
                    <TableCell align="right" sx={{ color: '#e0e0e0' }}>{order.totalAmount.toFixed(2)} VND</TableCell>
                    <TableCell align="right" sx={{ color: '#e0e0e0' }}>
                      {order.selectedProducts.reduce((total, product) => total + product.quantity, 0)}
                    </TableCell>
                    <TableCell align="center">
                      <Button
                        component={Link}
                        to={`/orders/${order.orderId}`}
                        variant="outlined"
                        color="primary"
                      >
                        Xem chi tiết
                      </Button>
                    </TableCell>
                    <TableCell align="center">
                      <IconButton
                        onClick={() => handleDelete(order.orderId)}
                        color="secondary"
                      >
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Box>
    </Scene>
  );
};

export default MyOrders;
