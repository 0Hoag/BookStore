import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
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
  CircularProgress,
  Grid,
} from '@mui/material';
import Scene from '../Scene';
import { Link } from 'react-router-dom';
import userService from '../../services/userService';

const OrderDetails = () => {
  const [loading, setLoading] = useState(true);
  const [order, setOrder] = useState(null);
  const { orderId } = useParams();

  useEffect(() => {
    const fetchOrderDetails = async () => {
      try {
        const response = await userService.getMyInfo();
        if (response.data.code === 1000) {
          const foundOrder = response.data.result.orders.find(
            (o) => o.orderId === orderId
          );
          if (foundOrder) {
            setOrder(foundOrder);
          } else {
            console.error('Order not found');
          }
        } else {
          console.error('Failed to fetch order details');
        }
      } catch (error) {
        console.error('Error fetching order details:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchOrderDetails();
  }, [orderId]);

  if (loading) {
    return (
      <Scene>
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
          <CircularProgress />
        </Box>
      </Scene>
    );
  }

  if (!order) {
    return (
      <Scene>
        <Box sx={{ p: 3, bgcolor: '#303030', color: '#e0e0e0' }}>
          <Typography variant="h4">Đơn hàng không tồn tại</Typography>
        </Box>
      </Scene>
    );
  }

  return (
    <Scene>
      <Box sx={{ p: 3, bgcolor: '#303030', color: '#e0e0e0' }}>
        <Typography variant="h4" gutterBottom>
          Chi tiết đơn hàng: {order.orderId}
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <Typography variant="h6" gutterBottom>Thông tin đơn hàng</Typography>
            <Typography>Mã đơn hàng: {order.orderId}</Typography>
            <Typography>Ngày thanh toán: {order.vnpPayDate}</Typography>
            <Typography>Tổng tiền: {order.vnpAmount.toLocaleString()} VND</Typography>
            <Typography>Phương thức thanh toán: {order.paymentMethod || 'Chưa có thông tin'}</Typography>
            <Typography>Trạng thái thanh toán: {order.vnpTransactionStatus}</Typography>
          </Grid>
          <Grid item xs={12} md={6}>
            <Typography variant="h6" gutterBottom>Thông tin giao hàng</Typography>
            <Typography>Họ tên: {order.fullName || 'Chưa có thông tin'}</Typography>
            <Typography>Số điện thoại: {order.phoneNumber || 'Chưa có thông tin'}</Typography>
            <Typography>Địa chỉ: {`${order.address || ''}, ${order.ward || ''}, ${order.district || ''}, ${order.city || ''}, ${order.country || ''}`}</Typography>
          </Grid>
        </Grid>
        <Typography variant="h6" sx={{ mt: 3, mb: 2 }}>Chi tiết sản phẩm</Typography>
        <TableContainer component={Paper} sx={{ bgcolor: '#424242' }}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell sx={{ color: '#e0e0e0' }}>Hình ảnh</TableCell>
                <TableCell sx={{ color: '#e0e0e0' }}>Sản phẩm</TableCell>
                <TableCell align="right" sx={{ color: '#e0e0e0' }}>Số lượng</TableCell>
                <TableCell align="right" sx={{ color: '#e0e0e0' }}>Giá</TableCell>
                <TableCell align="right" sx={{ color: '#e0e0e0' }}>Tổng</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {order.selectedProducts.map((product) => (
                <TableRow key={product.selectedId}>
                  <TableCell>
                    <Link to={`/books/${product.bookId.bookId}`}>
                      <img
                        src={product.bookId.image.replace("?raw=1", "?dl=0")}
                        height="200"
                        style={{ cursor: 'pointer' }}
                      />
                    </Link>
                  </TableCell>
                  <TableCell component="th" scope="row" sx={{ color: '#e0e0e0' }}>
                    {product.bookId.bookTitle}
                  </TableCell>
                  <TableCell align="right" sx={{ color: '#e0e0e0' }}>{product.quantity}</TableCell>
                  <TableCell align="right" sx={{ color: '#e0e0e0' }}>{product.bookId.price.toLocaleString()} VND</TableCell>
                  <TableCell align="right" sx={{ color: '#e0e0e0' }}>
                    {(product.quantity * product.bookId.price).toLocaleString()} VND
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
    </Scene>
  );
};

export default OrderDetails;
