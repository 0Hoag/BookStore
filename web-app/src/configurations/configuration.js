export const OAuthConfig = {
  clientId: "280704717040-7k4j2dd5boefu14h1opsr74i92bdjrn6.apps.googleusercontent.com",
  redirectUri: "http://localhost:3000/authenticate",
  authUri: "https://accounts.google.com/o/oauth2/auth",
};

export const CONFIG = {
  API_GATEWAY: "http://localhost:8888/api/v1",
};

export const API = {
  //user-service
  LOGIN: "/identity/auth/token",
  MY_INFO: "/identity/users/my-info",
  MY_PROFILE: "/profile/users",
  CREATE_PASSWORD: "/identity/users/create-password",
  CHANGE_PASSWORD: "/identity/users/change-password",
  VERIFY_PASSWORD: "/identity/users/verify-password", 
  UPDATE_USER_INFO: "/identity/users/update-info", 
  FRIENDS: "/",
  REGISTRATION: "/identity/users/registration",

  //user-service(profile-service)
  UPDATE_PROFILE: "/profile/users",

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
  GET_ALL_CHAPTER: "/book/truyen/chapter/getAll"
};
