import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const messengerService = {
    getUserConversations: (userId) => httpClient.get(API.GET_USER_CONVERSATIONS + "/" + userId, getAuthHeader()),

    createConversation: (conversation) => httpClient.post(API.CREATE_CONVERSATION, conversation, getAuthHeader()),
    
    sendMessage: (message) => httpClient.post(API.SEND_MESSAGE, message, getAuthHeader()),

    getMessageForConversation: (conversationId, page) => httpClient.get(API.GET_MESSAGE_FOR_CONVERSATION, getContentConversationWithParams(conversationId, page)),

    getUserConversationsList: (userId, limit) => httpClient.get(API.GET_USER_CONVERSATIONS_LIST, getContentConversationListWithParams(userId, limit)),

    getParticipantIds: (conversationId) => httpClient.get(API.GET_PARTICIPANTS_CONVERSATION + "/" + conversationId, getAuthHeader()),
}

function getAuthHeader() {
    return {
      headers: {
        Authorization: `Bearer ${getToken()}`,
        'Content-Type': 'application/json',
      },
    };
  }

  function getContentConversationWithParams(conversationId, page) {
    return {
        headers: {
            Authorization: `Bearer ${getToken()}`,
            'Content-Type': 'application/json',
          },

        params: {
            conversationId: conversationId,
            page: page,
            size: 10,
        },
    };
  }

  function getContentConversationListWithParams(userId, limit) {
    return {
        headers: {
            Authorization: `Bearer ${getToken()}`,
            'Content-Type': 'application/json',
          },

        params: {
            userId: userId,
            limit: limit,
        },
    };
  }

export default messengerService;
