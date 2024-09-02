import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import vnpayService from '../../services/vnpayService';
import Scene from '../Scene';

function PaymentResult() {
    const [paymentStatus, setPaymentStatus] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const processPayment = async () => {
            setIsLoading(true);
            try {
                const result = await vnpayService.processPaymentReturn(location.search);
                setPaymentStatus(result);
            } catch (error) {
                console.error('Failed to process payment:', error);
                setError('Có lỗi xảy ra khi xử lý thanh toán');
            } finally {
                setIsLoading(false);
            }
        };

        processPayment();
    }, [location]);

    if (isLoading) {
        return <div>Đang xử lý kết quả thanh toán...</div>;
    }

    if (error) {
        return (
            <div>
                <h1>Lỗi thanh toán</h1>
                <p>{error}</p>
                <button onClick={() => navigate('/')}>Quay lại trang chủ</button>
            </div>
        );
    }

    if (!paymentStatus) {
        return <div>Không có thông tin thanh toán</div>;
    }

    return (
        <Scene>
            <div>
                <h1>Kết quả thanh toán</h1>
                <p>Trạng thái: {paymentStatus.transactionStatus === "00"?"Thành Công" :"Thất Bại"}</p>
                <p>Mã đơn hàng: {paymentStatus.txnRef}</p>
                <p>Số giao dịch: {paymentStatus.transactionNo}</p>
                <p>Ngày thanh toán: {paymentStatus.payDate}</p>
                <button onClick={() => navigate('/')}>Quay lại trang chủ</button>
            </div>
        </Scene>
    );
}

export default PaymentResult;