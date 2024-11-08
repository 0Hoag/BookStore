import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const bookService = {
  createBook: async (newBook) => {
    try {
      const response = await httpClient.post(API.CREATE_BOOKS, newBook);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  deleteBook: async (bookId) => {
    try {
      const token = getToken();
      const url = `${API.DELETE_BOOK_WITH_CHAPTER}/${bookId}`;
      const response = await httpClient.delete(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data.result;
    }catch (error) {
      throw error;
    }
  },

  getAllBooks: async (page) => {
    try {
      const token = getToken();
      const response = await httpClient.get(API.GET_ALL_BOOKS, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        params: {
          page: page,
          size: 10,
        }
      });
      return response;
    } catch (error) {
      throw error;
    }
  },

  getBookById: async (bookId) => {
    try {
      const token = getToken();
      const url = `${API.GET_BOOKS}/${bookId}`;
      const response = await httpClient.get(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data.result; // Đảm bảo rằng `result` chứa thông tin sách đầy đủ
    } catch (error) {
      throw error;
    }
  },

  getChapterById: async (chapterId) => {
    try {
      const token = getToken();
      const url = `${API.GET_CHAPTER}/${chapterId}`;
      const response = await httpClient.get(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  getAllChapters: async (bookId) => {
    try {
      const token = getToken();
      const url = `${API.GET_ALL_CHAPTER}/${bookId}`;
      const response = await httpClient.get(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  createChapter: async (newChapter) => {
    try {
      const url = `${API.CREATE_CHAPTER}`;
      const response = await httpClient.post(url, newChapter,);
      return response.data.result;
    } catch (error) {
      throw error;
    }
  },

  deleteChapter: async (chapterId) => {
    try {
      const token = getToken();
      const url = `${API.DELETE_CHAPTER}/${chapterId}`;
      const response = await httpClient.delete(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data.result;
    }catch (error) {
      throw error;
    }
  },

  addChapterToBook: async (bookId, chapterIds) => {
    try {
      const token = getToken();
      const url = `${API.ADD_CHAPTER}/${bookId}`;
      const response = await httpClient.post(url, { chapterIds }, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data.result;
    } catch (error) {
      throw error;
    }
  },

  removeChapterToBook: async (bookId, chapterIds) => {
    try {
      const token = getToken();
      const url = `${API.REMOVE_CHAPTER}/${bookId}`;
      const response = await httpClient.post(url, { chapterIds }, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data.result;
    } catch (error) {
      throw error;
    }
  },
  
  updateBookChapter: async (bookId, updatedBookChapter) => {
    try {
      const token = getToken();
      const url = `${API.UPDATE_BOOK_CHAPTER}/${bookId}`;
      const response = await httpClient.put(url, updatedBookChapter, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data.result;
    } catch (error) {
      throw error;
    }
  },

  updateImageBook: async (bookId, file) => {
    try {
        const token = getToken();
        const url = `${API.UPDATE_IMAGE_BOOK}/${bookId}`;
        const formData = new FormData();
          formData.append('file', file);
          const response = httpClient.put(url, formData, {
              headers: {
                  'Authorization': `Bearer ${token}`, // Add the token to the headers
                  'Content-Type': 'multipart/form-data', // Set the content type
          }},
        );
        return response;
    } catch (error) {
        console.error('Error updating image:', error);
        throw error;
    }
  },
};

export default bookService;
