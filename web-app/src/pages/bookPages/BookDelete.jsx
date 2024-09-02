import React, { useEffect, useState } from "react";
import { Box, Button, Grid, Typography } from "@mui/material";
import bookService from "../../services/bookService";
import BookCard from "./BookCard";
import Scene from "../Scene";

const DeleteBook = () => {
  const [books, setBooks] = useState([]);

  useEffect(() => {
    const fetchBooks = async () => {
      try {
        const response = await bookService.getAllBooks();
        setBooks(response.result.data);
      } catch (error) {
        console.error('Error fetching books:', error);
      }
    };

    fetchBooks();
  }, []);

  const handleDeleteBook = async (bookId) => {
    try {
      await bookService.deleteBook(bookId);
      setBooks(prevBooks => prevBooks.filter(book => book.bookId !== bookId));
    } catch (error) {
      console.error('Error deleting book:', error);
    }
  };

  return (
    <Scene>
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          height: '100vh',
          bgcolor: '#1e1e1e', // Dark background color
          color: '#e0e0e0', // Light text color
          p: 3,
        }}
      >
        <Typography variant="h4" component="div" sx={{ mb: 3, color: '#e0e0e0' }}>
          Xóa Sách
        </Typography>
        <Grid container spacing={2} justifyContent="center">
          {books.map(book => (
            <Grid item xs={12} sm={6} md={4} lg={3} key={book.bookId}>
              <BookCard book={book} />
              <Button
                variant="outlined"
                color="secondary"
                onClick={() => handleDeleteBook(book.bookId)}
                sx={{ mt: 1, borderColor: '#e0e0e0', color: '#e0e0e0', '&:hover': { borderColor: '#f50057', color: '#f50057' } }}
              >
                Xóa
              </Button>
            </Grid>
          ))}
        </Grid>
      </Box>
    </Scene>
  );
};

export default DeleteBook;