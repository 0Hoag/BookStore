import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Grid,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { getMyInfo } from '../../services/userService';
import ordersService from '../../services/ordersService';
import Scene from '../Scene';
import vnpayService from '../../services/vnpayService';

const CheckOut = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    fullName: '',
    phoneNumber: '',
    country: '',
    city: '',
    district: '',
    ward: '',
    address: '',
    paymentMethod: '',
    vnpTxnRef: '',
    vnpOrderInfo: '',
    vnpAmount: 0,
    vnpResponseCode: '',
    vnpTransactionNo: '',
    vnpPayDate: '',
    vnpTransactionStatus: '',
    selectedProducts: [],
    userId: '',
  });

  const [selectedProducts, setSelectedProducts] = useState([]);
  const [orderId, setOrderId] = useState(null); // State để lưu orderId

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await getMyInfo();
        if (response.data.code === 1000) {
          const userInfo = response.data.result;
          setFormData({
            fullName: userInfo.fullName,
            phoneNumber: userInfo.phoneNumber,
            country: userInfo.country,
            city: userInfo.city,
            district: userInfo.district,
            ward: userInfo.ward,
            address: userInfo.address,
            paymentMethod: userInfo.paymentMethod,
            vnpTxnRef: userInfo.vnpTxnRef,
            vnpOrderInfo: userInfo.vnpOrderInfo,
            vnpAmount: userInfo.vnpAmount,
            vnpResponseCode: userInfo.vnpResponseCode,
            vnpTransactionNo: userInfo.vnpTransactionNo,
            vnpPayDate: userInfo.vnpPayDate,
            vnpTransactionStatus: userInfo.vnpTransactionStatus,
            selectedProducts: userInfo.cartItem,
            userId: userInfo.userId,
          });
          setSelectedProducts(userInfo.cartItem);
          
          // Lấy orderId từ đơn hàng đầu tiên trong mảng
          const firstOrder = userInfo.orders[0];
          if (firstOrder) {
            setOrderId(firstOrder.orderId);
          } else {
            console.error('Không tìm thấy đơn hàng nào.');
          }
        } else {
          console.error('Failed to fetch user info');
        }
      } catch (error) {
        console.error('Error fetching user info:', error);
      }
    };

    fetchData();
  }, []);

  const handleChange = async (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  
    // if (name === 'paymentMethod' && value === 'Thanh toán VNPAY') {
    //   try {
        
    //     const paymentUrl = await vnpayService.createPaymentUrl(orderId);
    //     // Chuyển hướng người dùng đến URL thanh toán VNPay
    //     window.location.href = paymentUrl.paymentUrl;
    //   } catch (error) {
    //     console.error('Error creating VNPay payment URL:', error);
    //     // Xử lý lỗi ở đây, ví dụ: hiển thị thông báo lỗi cho người dùng
    //     alert('Có lỗi xảy ra khi tạo liên kết thanh toán. Vui lòng thử lại.');
    //   }
    // }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      console.log('Form Data:', formData);
  
      // Cập nhật đơn hàng
      const updateOrderResponse = await ordersService.updateOrder(orderId, formData);
      console.log('Update Order Response:', updateOrderResponse);
  
      if (updateOrderResponse.error) {
        throw new Error(`Error updating order: ${updateOrderResponse.error}`);
      }
  
      console.log('Order updated successfully:', updateOrderResponse);

      if (formData.paymentMethod === 'Thanh toán VNPAY') {
        try {
          
          const paymentUrl = await vnpayService.createPaymentUrl(orderId);
          // Chuyển hướng người dùng đến URL thanh toán VNPay
          window.location.href = paymentUrl.paymentUrl;
        } catch (error) {
          console.error('Error creating VNPay payment URL:', error);

          alert('Có lỗi xảy ra khi tạo liên kết thanh toán. Vui lòng thử lại.');
        }
      }else if (formData.paymentMethod === 'Thanh toán khi nhận hàng'){
        navigate("/myOrders");
      }
    } catch (error) {
      console.error('Error:', error.message);
    }
  };

  const calculateTotalPrice = () => {
    return selectedProducts.reduce((total, product) => {
      return total + product.bookId.price * product.quantity;
    }, 0);
  };

  return (
    <Scene>
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" gutterBottom>
          Địa chỉ giao hàng
        </Typography>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                label="Họ và tên người nhận"
                name="fullName"
                value={formData.fullName}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                label="Số điện thoại"
                name="phoneNumber"
                value={formData.phoneNumber}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Quốc gia"
                name="country"
                value={formData.country}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Tỉnh/Thành phố"
                name="city"
                value={formData.city}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Quận/Huyện"
                name="district"
                value={formData.district}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Phường/Xã"
                name="ward"
                value={formData.ward}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                multiline
                minRows={3}
                label="Địa chỉ nhận hàng"
                name="address"
                value={formData.address}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth required>
                <InputLabel>Phương thức thanh toán</InputLabel>
                <Select
                  name="paymentMethod"
                  value={formData.paymentMethod}
                  onChange={handleChange}
                >
                  <MenuItem value="Thanh toán khi nhận hàng">Thanh toán khi nhận hàng</MenuItem>
                  <MenuItem value="Thanh toán ví điện tử">Ví điện tử</MenuItem>
                  <MenuItem value="Thanh toán VNPAY">VNPAY</MenuItem>
                </Select>
              </FormControl>
            </Grid>
          </Grid>
          <Typography variant="h5" gutterBottom sx={{ mt: 3 }}>
            Thông tin đơn hàng
          </Typography>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Sản phẩm</TableCell>
                  <TableCell align="right">Đơn giá</TableCell>
                  <TableCell align="right">Số lượng</TableCell>
                  <TableCell align="right">Thành tiền</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {selectedProducts.map((product) => (
                  <TableRow key={product.cartItemId}>
                    <TableCell>{product.bookId.bookTitle}</TableCell>
                    <TableCell align="right">{product.bookId.price} VND</TableCell> 
                    <TableCell align="right">{product.quantity}</TableCell>
                    <TableCell align="right">{product.bookId.price * product.quantity} VND</TableCell> 
                  </TableRow>
                ))}
                <TableRow>
                  <TableCell colSpan={3} align="right"> 
                    Tổng cộng:
                  </TableCell>
                  <TableCell align="right">{calculateTotalPrice()} VND</TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </TableContainer>
          <Box sx={{ mt: 3 }}>
            <Button type="submit" variant="contained" color="primary">
              Đặt hàng
            </Button>
          </Box>
        </form>
      </Box>
    </Scene>
  );
};

export default CheckOut;
