import React, { useEffect, useState } from "react";
import { Box, Button, Grid, Typography } from "@mui/material";
import { Link } from "react-router-dom";
import Scene from "../Scene";
import bookService from "../../services/bookService";
import BookCard from "./BookCard";
import { getMyInfo } from "../../services/userService";

const Books = () => {
  const [books, setBooks] = useState([]);
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    const fetchBooks = async () => {
      try {
        const response = await bookService.getAllBooks();
        setBooks(response.result);
      } catch (error) {
        console.error('Error fetching books:', error);
      }
    };

    const checkAdminRole = async () => {
      try {
        const userInfo = await getMyInfo();
        const roles = userInfo.data?.result?.roles || [];
        const isAdminUser = roles.some(role => role.name === 'ADMIN');
        setIsAdmin(isAdminUser);
      } catch (error) {
        console.error('Error fetching user info:', error);
      }
    };

    fetchBooks();
    checkAdminRole();
  }, []);

  return (
    <Scene>
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'flex-start',
          minHeight: '100vh',
          bgcolor: '#f0f2f5',
          p: 3,
        }}
      >
        <Typography variant="h4" component="div" sx={{ mb: 3, textAlign: 'center' }}>
          Danh sách sách
        </Typography>
        <Grid container spacing={2} justifyContent="center">
          {books.map((book, index) => (
            <Grid item xs={12} sm={6} md={4} lg={2.4} key={book.bookId}>
              <Link to={`/books/${book.bookId}`} style={{ textDecoration: 'none' }}>
                <BookCard book={book} />
              </Link>
            </Grid>
          ))}
        </Grid>
        {isAdmin && (
          <Box sx={{ display: 'flex', gap: 2, mt: 3 }}>
            <Button
              component={Link}
              to="/create-book"
              variant="contained"
              color="primary"
            >
              Tạo Sách Mới
            </Button>
            <Button
              component={Link}
              to="/delete-book"
              variant="contained"
              color="secondary"
            >
              Xóa Sách
            </Button>
          </Box>
        )}
      </Box>
    </Scene>
  );
};

export default Books;
