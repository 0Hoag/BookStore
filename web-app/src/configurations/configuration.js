export const OAuthConfig = {
  clientId: "YOUR_CLIENT_ID_HERE", 
  redirectUri: "YOUR_REDIRECT_URI_HERE",
  authUri: "https://accounts.google.com/o/oauth2/auth",
};

export const CONFIG = {
  API_GATEWAY: "http://localhost:8888/api/v1",
};

export const API = {
  //user-service
  LOGIN: "/identity/auth/token",
  MY_INFO: "/identity/users/my-info",
  UPDATE_USER_INFORMATION: "/identity/users/updateInformationUser",
  
  // MY_PROFILE: "/profile/users",
  GET_USER: "/identity/users",
  CREATE_PASSWORD: "/identity/users/create-password",
  CHANGE_PASSWORD: "/identity/users/change-password",
  VERIFY_PASSWORD: "/identity/users/verify-password", 
  UPDATE_USER_INFO: "/identity/users/update-info",
  GET_ALL_USER: "/identity/users/getAllUserInformationBasic",
  FRIENDS: "/",
  REGISTRATION: "/identity/users/registration",

  //User-Image(user-service)
  VIEW_IMAGE: "/identity/userImage/getUserImage",
  POST_PROFILE_IMAGE: "/identity/users/uploadImageUserProfile",
  POST_COVER_IMAGE: "/identity/users/uploadImageUserCover",
  DELETE_IMAGE: "/identity/users/deleteUserImage",

  //profile-service
  CREATE_PROFILE: "/profile/users/register",
  GET_USER_BY_PROFILE: "/profile/users/getUserId",
  MY_PROFILE: "/my-profile",
  UPDATE_PROFILE: "/profile/users",

  //user-service(profile-service)
  CREATE_PROFILE: "/profile/register",
  GET_ALL_PROFILE: "/profile/profiles",
  DELETE_PROFILE: "/profile",

  //user-service(CartItem-service)
  CREATE_CART_ITEM: "/identity/cartItem/registration",
  GET_CART_ITEM: "/identity/cartItem",
  GETALL_CART_ITEM: "/identity/cartItem/getAllCartItem",
  UPDATE_CART_ITEM: "/identity/cartItem/updateCartItem",
  DELETE_CART_ITEM: "/identity/cartItem",
  ADD_CART_ITEM: "/identity/cartItem/addCart",
  REMOVE_CART_ITEM: "/identity/cartItem/removeCart",

  //user-service(SelectedProduct-service)
  CREATE_SELECTED_PRODUCT: "/identity/selectProduct/registration",
  ADD_SELECTED_PRODUCT_WITH_USER: "/identity/selectProduct/addSelectedProductWithUser",
  GET_SELECTED_PRODUCT: "/identity/selectProduct",
  GET_ALL_SELECTED_PRODUCT: "/identity/selectProduct/GetAllSelectedProduct",
  UPDATE_SELECTED_PRODUCT: "/identity/selectProduct/updateSelectedProduct",
  REMOVE_SELECTED_PRODUCT_WITH_USER: "/identity/selectProduct/removeSelectedProductWithUser",
  DELETE_SELECTED_PRODUCT_WITH_USER: "/identity/selectProduct",

  //user-service(Orders-service)
  CREATE_ORDERS: "/identity/order/registration",
  GET_ORDERS: "/identity/order",
  GET_ALL_ORDERS: "/identity/order/getAllOrders",
  ADD_SELECTED_PRODUCT_WITH_ORDERS_WITH_USER: "/identity/order/addSelectedProductWithOrdersWithUser",
  REMOVE_SELECTED_PRODUCT_WITH_ORDERS_WITH_USER: "/identity/order/removeSelectedProductWithOrdersWithUser",
  DELETE_ORDERS: "/identity/order",
  UPDATE_ORDER: "/identity/order/updateOrder",

  //VNPay (VNPay-service)
  CREATE_URL: "/identity/api/vnpay/create-payment",
  UPDATE_PAYMENT: "/identity/api/vnpay/payment-return",

  //book-service
  CREATE_BOOKS: "/book/registration/ManyChapter",
  DELETE_BOOK_WITH_CHAPTER: "/book/books",
  UPDATE_BOOK_CHAPTER: "/book/update/ManyChapter",
  GET_ALL_BOOKS: "/book/getAllBooks",
  GET_BOOKS: "/book/books",
  CREATE_CHAPTER: "/book/truyen/registration",
  DELETE_CHAPTER: "/book/truyen/chapter",
  ADD_CHAPTER: "/book/addChapter",
  REMOVE_CHAPTER: "/book/removeChapter",
  GET_CHAPTER: "/book/truyen/chapter",
  GET_ALL_CHAPTER: "/book/truyen/chapter/getAll",

  //friend-service
  GET_USER_RELATION_SHIP: "/friend/relationship",
  GET_REQUEST: "/friend/getFriend",
  GET_ALL_REQUEST: "/friend/Request/getAllFriend",
  CREATE_FRIEND_REQUEST: "/friend/Request/Registration",
  CREATE_FRIEND_SHIP: "/friend/ship/Registration",
  CREATE_BLOCK_LIST: "/friend/blockList/registration",

  REMOVE_REQUEST_FRIEND: "/friend/Request/deleteFriendRequest",
  UPDATE_REQUEST_STATUS: "/friend/Request/updateFriendRequest",

  UNFRIEND: "/friend/ship/removeFriendShip",
  
  //post-service
  CREATE_POST: "/post/registration",
  MY_POST: "/post/my-posts",
  GET_POST: "/post/activity",
  GET_POST_WITH_USERID: "/post/activity/getPostWithUser",
  GET_ALL_POST: "/post/activity",
  UPDATE_POST: "/post/activity/update",
  DELETE_POST: "/post/activity",
  GET_TOKEN_BLACK_BLAZE: "/post/getToken",
  CREATE_IMAGE_POST: "/post/image/registration",
  GET_IMAGE_POST: "/post/image/viewImage",
  UPDATE_COMMENT: "/post/updateCommentToPost",

  ADD_LIKE_TO_POST: "/post/addLikeToPost",
  REMOVE_LIKE_TO_POST: "/post/removeLikeToPost",

  ADD_COMMENT_TO_POST: "/post/addCommentToPost",
  REMOVE_COMMENT_TO_POST: "/post/removeCommentToPost",

  UPLOAD_MEDIA_TO_POST: "/post/updateMediaToPost",

  //Comment-service
  CREATE_COMMENT: "/comment/comments/registration",
  GET_COMMENT: "/comment/comments/activity",

  //Interaction-service
  CREATE_LIKE: "/interaction/like/registration",
  GET_LIKE: "/interaction/like/activity",

  //Messenger-service
  GET_USER_CONVERSATIONS: "/messaging/mess/getUserConversations",
  CREATE_CONVERSATION: "/messaging/mess/registration",
  SEND_MESSAGE: "/messaging/messenger/registration",
  GET_MESSAGE_FOR_CONVERSATION: "/messaging/messenger/getMessageForConversation",
  GET_USER_CONVERSATIONS_LIST: "/messaging/messenger/getUserConversationList",
  GET_PARTICIPANTS_CONVERSATION: "/messaging/participant/getParticipantIds",
};
