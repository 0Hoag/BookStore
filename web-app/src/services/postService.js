import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const postService = {
    createPost: async (newPost) => {
        try {
            const token = getToken();
            const url = API.CREATE_POST;
            const response = httpClient.post(url, newPost, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response;
        }catch (error) {
            throw error;
        }
    },

    getTokenBlackBlaze: async () => {
        try {
            const url = API.GET_TOKEN_BLACK_BLAZE;
            const token = getToken();
            const response = await httpClient.get(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data; // Giả sử response.data chứa token từ Backblaze B2
        } catch (error) {
            console.error('Error getting Backblaze B2 token:', error);
            throw error;
        }
    },

    getPostWithUser: async (userId, page) => {
        try {
            const token = getToken();
            const response = await httpClient.get(API.GET_POST_WITH_USERID, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                params: {
                    userId: userId,
                    page: page,
                    size: 10,
                },
            });

            return response;
        }catch (error) {
            throw error;
        }
    },

    getMyPost: async(page) => {
        try {
            const response = httpClient.get(API.MY_POST, {
                headers: {
                    Authorization: `Bearer ${getToken()}`,
                },
                params: {
                    page: page,
                    size: 10,
                },
            });

        return response;
        
        }catch (error) {
            throw error;
        }
    },

    createLike: async (newLike) => {
        try {
            const response = httpClient.post(API.CREATE_LIKE, newLike);
            return response;
        }catch (error) {
            throw error;
        }
    },

    createComment: async (newComment) => {
        try {
            const token = getToken();
            const response = httpClient.post(API.CREATE_COMMENT, newComment, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response;
        }catch (error) {
            throw error;
        }
    },

    getPost: async (postId) => {
        try {
            const token = getToken();
            const url = `${API.GET_POST}/${postId}`
            const response = httpClient.get(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data.result;
        }catch (error) {
            throw error;
        }
    },

    getAllPost: async (page) => {
        try {
            const token = getToken();
            const url = API.GET_ALL_POST;
            const response = httpClient.get(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                    params: {
                    page: page,
                    size: 10,
                },
            });
            return response;
        }catch (error) {
            throw error;
        }
    },

    removePost: async (postId) => {
        try {
            const url = `${API.DELETE_POST}/${postId}`;
            const token = getToken();
            const response = httpClient.delete(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response;
        }catch (error) {
            throw error;
        }
    },

    updatePost: async (postId, newPost) => {
        try {
            const token = getToken();
            const url = `${API.UPDATE_POST}/${postId}`;
            const response = httpClient.put(url, newPost, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data.result;
        }catch (error) {
            throw error;
        }
    },

    updateComment: async (commentId, newComment) => {
        try {
            const token = getToken();
            const url = `${API.UPDATE_COMMENT}/${commentId}`;
            const response = httpClient.put(url, newComment, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            return response.data.result;
        }catch (error) {
            throw error;
        }
    },

    addLikeToPost: async(postId, likeId) => {
        try {
            const token = getToken();
            const url = `${API.ADD_LIKE_TO_POST}/${postId}`;
            const response = httpClient.post(url, {likeId}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            return response;
        }catch (error) {
            throw error;
        }
    },

    removeLikeToPost: async(postId, likeId) => {
        try {
            const url = `${API.REMOVE_LIKE_TO_POST}/${postId}`;
            const response = httpClient.delete(url, {
                headers: {
                    Authorization: `Bearer ${getToken()}`,
                },
                data: { likeId },
            });
            return response;
        }catch (error) {
            throw error;
        }
    },

    addCommentToPost: async(postId, comment) => {
        try {
            const token = getToken();
            const url = `${API.ADD_COMMENT_TO_POST}/${postId}`;
            const response = httpClient.post(url, comment, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            return response;
        }catch (error) {
            throw error;
        }
    },

    removeCommentToPost: async(postId, commentId) => {
        try {
            const token = getToken();
            const url = `${API.REMOVE_COMMENT_TO_POST}/${postId}`;
            const response = httpClient.delete(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                data: { commentId }
            });
            return response;
        }catch (error) {
            throw error;
        }
    },

    getComment: async(commentId) => {
        try {
            const url = `${API.GET_COMMENT}/${commentId}`;
            const token = getToken();
            const response = httpClient.get(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response;
        }catch (error) {
            throw error;
        }
    },

    getLike: async(likeId) => {
        try {
            const url = `${API.GET_LIKE}/${likeId}`;
            const token = getToken();
            const response = httpClient.get(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response;
        }catch (error) {
            throw error;
        }
    },


}

export default postService;