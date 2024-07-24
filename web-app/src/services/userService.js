import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

export const getMyInfo = async () => {
  return await httpClient.get(API.MY_INFO, {
    headers: {
      Authorization: `Bearer ${getToken()}`,
    },
  });
};

export const registerUser = async (userDetails) => {
  try {
    const response = await httpClient.post(API.REGISTRATION, userDetails, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};


export const createPassword = async (password) => {
  return await httpClient.post(API.CREATE_PASSWORD, { password }, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${getToken()}`,
    },
  });
};

export const changePassword = async (oldPassword, newPassword) => {
  try {
    const response = await httpClient.put(
      API.CHANGE_PASSWORD,
      { oldPassword, newPassword },
      {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${getToken()}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Cập nhật thông tin người dùng
export const updateUserInfo = async (userInfo) => {
  try {
    const response = await httpClient.put(
      API.UPDATE_USER_INFO,
      userInfo,
      {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${getToken()}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const verifyPassword = async (password) => {
  try {
    const response = await httpClient.put(
      API.VERIFY_PASSWORD,
      { password },  // Send the password as the payload
      {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${getToken()}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const postReview = async (content) => {
  try {
    const response = await httpClient.post(
      API.POST_REVIEW,
      { content },
      {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${getToken()}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const getFriends = async () => {
  return await httpClient.get(API.FRIENDS, {
    headers: {
      Authorization: `Bearer ${getToken()}`,
    },
  });
};