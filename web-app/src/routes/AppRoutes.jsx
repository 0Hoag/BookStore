import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Login from "../components/Login";
import Home from "../components/Home";
import Authenticate from "../components/Authenticate";
import Profile from "../pages/Profile";
import Settings from "../pages/Settings";
import Friend from "../pages/Friends";
import Register from "../pages/Register";
import Books from "../pages/bookPages/Books";
import BookDetail from "../pages/bookPages/BookDetail";
import CreateBook from "../pages/bookPages/CreateBook";
import ChapterDetail from "../pages/bookPages/chapters/ChapterDetail";
import AddChapter from "../pages/bookPages/chapters/AddChapter";
import ShoppingCart from "../pages/shopping/ShoppingCart";
import DeleteBook from "../pages/bookPages/BookDelete";
import CheckOut from "../pages/shopping/CheckOut";
import MyOrders from "../pages/shopping/MyOrders";
import OrderDetails from "../pages/shopping/OrderDetails";
import PaymentResult from "../pages/shopping/PaymentResult";

const AppRoutes = () => {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/books" element={<Books />} />
        <Route path="/create-book" element={<CreateBook />} />
        <Route path="/delete-book" element={<DeleteBook />} />
        <Route path="/books/:bookId/add-chapter" element={<AddChapter />} />
        <Route path="/books/:bookId/:chapterId" element={<ChapterDetail />} />
        <Route path="/books/:bookId/*" element={<BookDetail />} />
        <Route path="/orders" element={<MyOrders />} />
        <Route path="/payment-result" element={<PaymentResult />} />
        <Route path="/orders/:orderId" element={<OrderDetails />} />
        <Route path="/shoppingcart" element={<ShoppingCart />} />
        <Route path="/myOrders" element={<MyOrders />} />
        <Route path="/checkout" element={<CheckOut />} />
        <Route path="/authenticate" element={<Authenticate />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/friends" element={<Friend />} />
        <Route path="/settings" element={<Settings />} />
        <Route path="/" element={<Home />} />
      </Routes>
    </Router>
  );
};

export default AppRoutes;
