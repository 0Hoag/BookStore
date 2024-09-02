import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const friendService = {
  createFriendRequest: async (newRequest) => {
    try {
      const response = await httpClient.post(API.CREATE_FRIEND_REQUEST, newRequest);
      return response.data.result;
    }catch (error) {
      throw error;
    }
  },

  createFriendShip: async (newFrShip) => {
    try {
      const response = await httpClient.post(API.CREATE_FRIEND_SHIP, newFrShip);
      return response.data.result;
    }catch (error) {
      throw error;
    }
  },

  createBlockList: async (newBlockList) => {
    try {
      const response = await httpClient.post(API.CREATE_BLOCK_LIST, newBlockList);
      return response.data.result;
    }catch (error) {
      throw error;
    }
  },

  getUserRelationShip: async (userId) => {
    try {
      const token = getToken();
      const url = `${API.GET_USER_RELATION_SHIP}/${userId}`;
      const response = await httpClient.get(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        }
      });
      return response.data.result;
    }catch (error) {
      throw error;
    }
  },

  removeFriendRequest: async (requestId) => {
    try {
      const token = getToken();
      const url = `${API.REMOVE_REQUEST_FRIEND}/${requestId}`;
      const response = await httpClient.delete(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        }
      });
      return response.data.result;
    }catch (error) {
      throw error;
    }
  },

  UnFriendShip: async (friendshipId) => { //I add new
    try {
      const token = getToken();
      const url = `${API.UNFRIEND}/${friendshipId}`;
      const response = httpClient.delete(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data.result;
    }catch (error) {
      throw error;
    }
  },

  updateFriendStatus: async (requestId, status) => {
    try {
      const url = `${API.UPDATE_REQUEST_STATUS}/${requestId}`;
      const response = await httpClient.put(url, status, {
        headers: {
          Authorization: `Bearer ${getToken()}`,
        }
      });
      return response.data.result;
    }catch (error) {
      throw error;
    }
  },

  async getFriendRequestStatus(currentUserId, otherUserId) {
    const allRequests = await this.getAllRequests();
    const request = allRequests.find(req => 
      (req.senderId === currentUserId && req.receiverId === otherUserId) ||
      (req.senderId === otherUserId && req.receiverId === currentUserId)
    );
    
    if (!request) return { status: 'none', requestId: null };
    if (request.condition === 'PENDING') {
      return {
        status: request.senderId === currentUserId ? 'sent' : 'received',
        requestId: request.requestId
      };
    }
    return { status: request.condition.toLowerCase(), requestId: request.requestId };
  },

  saveSentFriendRequests: (requests) => {
    localStorage.setItem('sentFriendRequests', JSON.stringify(requests));
  },

  getSentFriendRequestsFromLocal: () => {
    const data = localStorage.getItem('sentFriendRequests');
    return data ? JSON.parse(data) : [];
  },

  async getAllRequests() {
    // Implement API call to get all friend requests
    const response = await httpClient.get(API.GET_ALL_REQUEST, {
      headers: {
        Authorization: `Bearer ${getToken()}`,
      },
    })
    return response.data.result;
  },
}

export default friendService;
