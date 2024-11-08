import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const notificationService = {
    postNotification: async (notification) => httpClient.post(API.POST_NOTIFICATION, notification, getAuthHeader()),

    getUserNotification: async (userId) => httpClient.get(`${API.GET_NOTIFICATION}/${userId}`, getAuthHeader()),

    getUnreadNotification: async (userId) => httpClient.get(`${API.GET_UNREAD_NOTIFICATION}/${userId}`, getAuthHeader()),

    putMarkAsReadNotification: async (notificationId) => httpClient.put(`${API.PUT_MARK_AS_READ_NOTIFICATION}/${notificationId}`, null, getAuthHeader()),

    putMarkAsReadAllNotification: async (userId) => httpClient.put(`${API.PUT_MARK_AS_READ_ALL_NOTIFICATION}/${userId}`, null, getAuthHeader()),

    getAllNotifications: async (userId) => httpClient.get(`${API.GET_NOTIFICATION_BY_USER}/${userId}`, getAuthHeader()),

    deleteNotification: async (notificationId) => httpClient.delete(`${API.DELETE_NOTIFICATION}/${notificationId}`, getAuthHeader()),

    sendNotification: async (notification) => httpClient.post(API.POST_SEND_NOTIFICATION, notification, getAuthHeader()),

    getAllSendFriendByUser: async (senderId) => httpClient.get(`${API.GET_ALL_SEND_NOTIFICATION_BY_USER}/${senderId}`, getAuthHeader()),

    getSendFriendByUser: async (senderId) => httpClient.get(`${API.GET_SEND_NOTIFICATION_BY_USER}/${senderId}`, getAuthHeader())
}

function getAuthHeader() {
  return {
    headers: {
      Authorization: `Bearer ${getToken()}`
    },
  };
}


export default notificationService;
