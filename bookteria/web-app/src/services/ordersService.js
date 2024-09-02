import { getToken } from "./localStorageService";
import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";

const ordersService = {
    createOrders: async (formData) => {
        try {
            const token = getToken();
            const url = API.CREATE_ORDERS;
            const response = await httpClient.post(url, formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data;
        }catch (error) {
            throw error;
        }
    },

    addSelectedProductWithOrdersWithUser: async (userId, orderId) => {
        try {
            const token = getToken();
            const url = `${API.ADD_SELECTED_PRODUCT_WITH_ORDERS_WITH_USER}/${userId}`;
            const response = await httpClient.post(url, { orderId }, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data.result;
        } catch (error) {
            throw error;
        }
    },

    removeSelectedProductWithUSer: async (userId, orderId) => {
        try {
            const token = getToken();
            const url = `${API.REMOVE_SELECTED_PRODUCT_WITH_ORDERS_WITH_USER}/${userId}`;
            const response = await httpClient.delete(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                data: { orderId },
            });
            return response.data.result;
        } catch (error) {
            throw error;
        }
    },

    updateOrder: async (orderId, newOrder) => {
        try {
            const token = getToken();
            const url = `${API.UPDATE_ORDER}/${orderId}`;
            const response = await httpClient.put(url, newOrder, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data.result;
        }catch (error) {
            throw error;
        }
    },

    getOrders: async (orderId) => {
        try {
            const token = getToken();
            const url = `${API.GET_ORDERS}/${orderId}`;
            const response = await httpClient.get(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data.result;
        } catch (error) {
            throw error;
        }
    },

    getAllOrders: async () => {
        try {
            const token = getToken();
            const url = `${API.GET_ALL_ORDERS}`;
            const response = await httpClient.get(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data.result;
        } catch (error) {
            throw error;
        }
    },


    deleteOrders: async (orderId) => {
        try {
            const token = getToken();
            const url = `${API.DELETE_ORDERS}/${orderId}`;
            const response = await httpClient.delete(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data.result;
        } catch (error) {
            throw error;
        }
    }
};

export default ordersService;
