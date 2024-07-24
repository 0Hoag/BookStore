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
import { getMyInfo } from '../../services/userService';
import ordersService from '../../services/ordersService';
import { Link } from 'react-router-dom'; // Make sure to import Link from react-router-dom for navigation
import Scene from '../Scene';
import selectedProductService from '../../services/selectedProductService';

const MyOrders = () => {
  const [loading, setLoading] = useState(true);
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await getMyInfo();
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
      const userInfo = await getMyInfo();
      const userId = userInfo.data.result.userId;

      // Lấy danh sách các sản phẩm được chọn từ đơn hàng
      const order = orders.find(order => order.orderId === orderId);
      const selectedProductIds = order.selectedProducts.map(product => product.selectedId);

      // Xóa các sản phẩm được chọn liên quan đến đơn hàng
      await selectedProductService.removeSelectedProduct(orderId, selectedProductIds);

      // Xóa đơn hàng
      await ordersService.deleteOrders(orderId);

      // Cập nhật state để loại bỏ đơn hàng đã xóa
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
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" gutterBottom>
          Đơn hàng của tôi
        </Typography>
        {orders.length === 0 ? (
          <Typography variant="body1">Bạn chưa có đơn hàng nào.</Typography>
        ) : (
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID Đơn hàng</TableCell>
                  <TableCell align="right">Ngày đặt hàng</TableCell>
                  <TableCell align="right">Tổng tiền</TableCell>
                  <TableCell align="right">Số lượng</TableCell>
                  <TableCell align="center">Chi tiết</TableCell>
                  <TableCell align="center">Xóa</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {orders.map(order => (
                  <TableRow key={order.orderId}>
                    <TableCell component="th" scope="row">
                      {order.orderId}
                    </TableCell>
                    <TableCell align="right">{order.orderDate}</TableCell>
                    <TableCell align="right">{order.totalAmount.toFixed(2)} VND</TableCell>
                    <TableCell align="right">
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
