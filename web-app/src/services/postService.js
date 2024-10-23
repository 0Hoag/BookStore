import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const postService = {
    createPostFile: async (formData) => {
        try {
            const token = getToken();
            const url = API.CREATE_POST;
            const response = httpClient.post(url, formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response;
        }catch (error) {
            throw error;
        }
    },

    uploadImageToPost: async (postId, file) => {
        try {
          const token = getToken();
          const url = `${API.UPLOAD_MEDIA_TO_POST}/${postId}`;
          const formData = new FormData();
          
          formData.append('file', file);
          
          const response = await httpClient.post(url, formData, {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'multipart/form-data'
            },
          });
          return response;
        } catch(error) {
          throw error;
        }
    },

    createImage: async (file) => {
        try {
            const token = getToken();
            const url = API.CREATE_IMAGE_POST;
            const formData = new FormData();
            formData.append('image', file);
            const response = httpClient.post(url, formData, {
                headers: {
                    'Authorization': `Bearer ${token}`, // Add the token to the headers
                    'Content-Type': 'multipart/form-data', // Set the content type
            }},
        );
        return response;
        }catch (error) {
            throw error;
        }
    },

    updateImageToPost: async (postId, images) => {
        try {
            const token = getToken();
            const url = `${API.UPDATE_IMAGE_TO_POST}/${postId}`;
            const response = httpClient.put(url, { images }, {
                headers: {
                    'Authorization': `Bearer ${token}`, // Add the token to the headers
                    'Content-Type': 'multipart/form-data', // Set the content type
            }});
            return response;
        }catch (error) {
            throw error;
        }
    },

    viewImage: async (imageId) => {
        try {
            const token = getToken();
            const url = `${API.GET_IMAGE_POST}/${imageId}`;
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
            return response;
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

    updatePost: async (postId, content, images, videos) => {
        try {
            const token = getToken();
            const url = `${API.UPDATE_POST}`;
            const response = httpClient.put(url, null, { // Set body to null
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                params: { // Use params to send the parameters
                    postId,
                    content,
                    images: Array.from(images), // Convert Set to Array
                    videos: Array.from(videos), // Convert Set to Array
                },
            });
            return response.data.result;
        } catch (error) {
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

    updateCommentToPost: async (postId, commentId, newComment) => {
        try {
            const token = getToken();
            const url = `${API.UPDATE_COMMENT}/${postId}/${commentId}`;
            const response = httpClient.put(url, newComment, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
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