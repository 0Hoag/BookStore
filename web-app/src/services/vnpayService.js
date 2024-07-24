import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const vnpayService = {
    createPaymentUrl: async (orderId) => {
        try {
            const token = getToken();
            const url = `${API.CREATE_URL}?orderId=${orderId}`;
            const response = await httpClient.post(url, null, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data.result;
        } catch (error) {
            console.error('Error creating VNPay payment URL:', error);
            throw error;
        }
    },

    processPaymentReturn: async (queryString) => {
        try {
            const token = getToken();
            // Đảm bảo rằng queryString bắt đầu bằng dấu '?'
            const formattedQueryString = queryString.startsWith('?') ? queryString : `?${queryString}`;
            const url = `${API.UPDATE_PAYMENT}${formattedQueryString}`;
            const response = await httpClient.get(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data.result;
        } catch (error) {
            console.error('Error processing VNPay payment return:', error);
            throw error;
        }
    },
}

export default vnpayService;