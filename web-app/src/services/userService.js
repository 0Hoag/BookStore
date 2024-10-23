import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const userService = {
  getMyInfo: () => httpClient.get(API.MY_INFO, getAuthHeader()),

  registerUser: (userDetails) => httpClient.post(API.REGISTRATION, userDetails, getContentTypeHeader()),

  getUser: (userId) => httpClient.get(`${API.GET_USER}/${userId}`, getAuthHeader()),
  //post image profile
  updateImageProfile: async (userId, file) => {
      const formData = new FormData();
      formData.append('image', file); // Append the file to FormData
      return await httpClient.post(`${API.POST_PROFILE_IMAGE}/${userId}`, formData, getAuthHeaderImage(file)); // Use the updated getAuthHeaderImage function
  },
  //post image cover
  updateImageCover: async (userId, file) => {
    const formData = new FormData();
    formData.append('image', file);
    return await httpClient.post(`${API.POST_COVER_IMAGE}/${userId}`, formData, getAuthHeaderImage(file))
  },
  //get image
  viewImage: (userImageId) => httpClient.get(`${API.VIEW_IMAGE}/${userImageId}`, getAuthHeader()),
  // delete image
  deleteImage: (userId, images) => {
    try {
      const url = `${API.DELETE_IMAGE}/${userId}`;
      const token = getToken(); // Ensure this returns a valid token
      console.log("Token: {}", token); // Check if the token is valid
      const response = httpClient.delete(url, {
        headers: {
          Authorization: `Bearer ${token}`, // Ensure this is correctly formatted
        },
        data: { images }
      });
      return response;
    } catch (error) {
      throw error; // Log the error for debugging
    }
  },


  getAllUser: () => httpClient.get(API.GET_ALL_USER, getAuthHeader()),

  createPassword: (userId, password) => httpClient.post(`${API.CREATE_PASSWORD}/${userId}`, { password }, getAuthHeader()),

  changePassword: (oldPassword, newPassword) => httpClient.put(API.CHANGE_PASSWORD, { oldPassword, newPassword }, getAuthHeader()),

  updateInformation: (newData) => httpClient.put(API.UPDATE_USER_INFORMATION, newData, getAuthHeader()),

  updateUserInfo: (userInfo) => httpClient.put(API.UPDATE_USER_INFO, userInfo, getAuthHeader()),

  verifyPassword: (password) => httpClient.put(API.VERIFY_PASSWORD, { password }, getAuthHeader()),

  postReview: (content) => httpClient.post(API.POST_REVIEW, { content }, getAuthHeader()),

  getFriends: () => httpClient.get(API.FRIENDS, getAuthHeader()),
};

function getAuthHeader() {
  return {
    headers: {
      Authorization: `Bearer ${getToken()}`
    },
  };
}

function getAuthHeaderImage(file) {
  const formData = new FormData();
  formData.append('image', file); // Append the file to FormData
  return {
      headers: {
        'Authorization': `Bearer ${getToken()}`, // Add the token to the headers
        'Content-Type': 'multipart/form-data', // Set the content type
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
export default userService;