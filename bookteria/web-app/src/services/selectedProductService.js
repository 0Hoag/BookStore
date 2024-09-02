import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const selectedProductService = {
    createSelectedProduct: async (newSelectedProduct) => {
        try {
            const url = API.CREATE_SELECTED_PRODUCT;
            const response = await httpClient.post(url, newSelectedProduct);
            return response.data;
        }catch (error) {
            throw error;
        }
    },

    getAllSelectedProduct: async () => {
        try {
          const token = getToken();
          const url = API.GET_ALL_SELECTED_PRODUCT;
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

    getSelectedProduct: async (selectedId) => {
        try {
          const token = getToken();
          const url = `${API.GET_SELECTED_PRODUCT}/${selectedId}`;
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

    addSelectedProduct: async (orderId, selectedId) => {
        try {
          const token = getToken();
          const url = `${API.ADD_SELECTED_PRODUCT_WITH_USER}/${orderId}`;
          const response = await httpClient.post(url, { selectedId }, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          return response.data.result;
        } catch (error) {
          throw error;
        }
    },
    
    removeSelectedProduct: async (orderId, selectedId) => {
        try {
          const token = getToken();
          const url = `${API.REMOVE_SELECTED_PRODUCT_WITH_USER}/${orderId}`;
          const response = await httpClient.delete(url, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            data: { selectedId }, // Đưa cartItemId vào phần data của options
          });
          return response.data.result;
        } catch (error) {
          throw error;
        }
    },  

    updateSelectedProduct: async (selectedId, updateSelected) => {
      try {
        const token = getToken();
        const url = `${API.UPDATE_SELECTED_PRODUCT}/${selectedId}`;
        const response = await httpClient.put(url, updateSelected, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        return response.data.result;
      } catch (error) {
        throw error;
      }
    },

    deleteSelectedProduct: async (selectedId) => {
      try {
        const token = getToken();
        const url = `${API.DELETE_SELECTED_PRODUCT_WITH_USER}/${selectedId}`;
        const response = await httpClient.delete(url, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        return response.data.result;
      }catch (error) {
        throw error;
      }
    }


}

export default selectedProductService;
