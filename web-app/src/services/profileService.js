import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const profileService = {
    createProfile: async (data) => httpClient.post(API.CREATE_PROFILE, data, getAuthHeader()),

    getUserByProfile: async (userId) => httpClient.get(`${API.GET_USER_BY_PROFILE}/${userId}`, getAuthHeader()),

    updateProfile: async (profileId, data) => httpClient.put(`${API.UPDATE_PROFILE}/${profileId}`, data, getAuthHeader()),
}

function getAuthHeader() {
    return {
      headers: {
        Authorization: `Bearer ${getToken()}`,
        'Content-Type': 'application/json',
      },
    };
}
  

export default profileService;