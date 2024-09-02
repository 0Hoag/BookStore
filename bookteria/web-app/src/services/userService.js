import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";
import keycloak from "./keycloak";

const userService = {
  getMyInfo: () => httpClient.get(API.MY_INFO, getAuthHeader()),

  registerUser: (userDetails) => httpClient.post(API.REGISTRATION, userDetails, getContentTypeHeader()),

  getUser: (userId) => httpClient.get(`${API.GET_USER}/${userId}`, getAuthHeader()),

  getAllUser: () => httpClient.get(API.GET_ALL_USER, getAuthHeader()),

  createPassword: (password) => httpClient.post(API.CREATE_PASSWORD, { password }, getAuthHeader()),

  changePassword: (oldPassword, newPassword) => 
    httpClient.put(API.CHANGE_PASSWORD, { oldPassword, newPassword }, getAuthHeader()),

  updateUserInfo: (userInfo) => httpClient.put(API.UPDATE_USER_INFO, userInfo, getAuthHeader()),

  verifyPassword: (password) => httpClient.put(API.VERIFY_PASSWORD, { password }, getAuthHeader()),

  postReview: (content) => httpClient.post(API.POST_REVIEW, { content }, getAuthHeader()),

  getFriends: () => httpClient.get(API.FRIENDS, getAuthHeader()),

  getMyProfile: () => httpClient.get(API.MY_PROFILE, getAuthHeaderProfile()),
};

function getAuthHeader() {
  return {
    headers: {
      Authorization: `Bearer ${getToken()}`,
      'Content-Type': 'application/json',
    },
  };
}

function getContentTypeHeader() {
  return {
    headers: {
      'Content-Type': 'application/json',
    },
  };
}

function getAuthHeaderProfile() {
  return {
    headers : {
      Authorization: "Bearer " + keycloak.token
    }
  }
}
export default userService;