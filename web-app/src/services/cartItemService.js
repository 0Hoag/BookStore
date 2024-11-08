import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const cartItemService = {
  createCartItem: async (newCartItem) => {
    try {
      const token = getToken();
      const response = await httpClient.post(API.CREATE_CART_ITEM, newCartItem, {
        headers: {
          Authorization: `Bearer ${token}`,
        }
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  getAllCartItem: async () => {
    try {
      const token = getToken();
      const response = await httpClient.get(API.GETALL_CART_ITEM, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data.result;
    } catch (error) {
      throw error;
    }
  },

  getCartItem: async (cartItemId) => {
    try {
      const token = getToken();
      const url = `${API.GET_CART_ITEM}/${cartItemId}`;
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

  // deleteCartItem: async (cartItemId) => {
  //   try {
  //     const token = getToken();
  //     const url = `${API.DELETE_CART_ITEM}/${cartItemId}`;
  //     const response = await httpClient.delete(url, {
  //       headers: {
  //         Authorization: `Bearer ${token}`,
  //       },
  //     });
  //     return response.data.result;
  //   } catch (error) {
  //     throw error;
  //   }
  // },

  addCartItem: async (userId, cartItemId) => {
    try {
      const token = getToken();
      const url = `${API.ADD_CART_ITEM}/${userId}`;
      const response = await httpClient.post(url, { cartItemId }, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data.result;
    } catch (error) {
      throw error;
    }
  },

  updateCartItem: async (cartItemId, updateCart) => {
    try {
      const token = getToken();
      const url = `${API.UPDATE_CART_ITEM}/${cartItemId}`;
      const response = await httpClient.put(url, updateCart, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data.result;
    } catch (error) {
      throw error;
    }
  },

  removeCartItem: async (userId, cartItemId) => {
    try {
      const token = getToken();
      const url = `${API.REMOVE_CART_ITEM}/${userId}`;
      const response = await httpClient.delete(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        data: { cartItemId }, // Đưa cartItemId vào phần data của options
      });
      return response.data.result;
    } catch (error) {
      throw error;
    }
  },  
};

export default cartItemService;
