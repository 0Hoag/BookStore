import React, { useEffect, useState, useCallback } from "react";
import { Box, Button, Grid, Typography, CircularProgress } from "@mui/material";
import bookService from "../../services/bookService";
import BookCard from "./BookCard";
import Scene from "../Scene";

const BookDelete = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 10;

  const fetchBooks = useCallback(async (pageNum) => {
    try {
      setLoading(pageNum === 1);
      setLoadingMore(pageNum > 1);
      const response = await bookService.getAllBooks(pageNum, pageSize);
      if (response.data.result) {
        setBooks(prevBooks => 
          pageNum === 1 
            ? response.data.result.data 
            : [...prevBooks, ...response.data.result.data]
        );
        setTotalPages(response.data.result.totalPages);
      }
    } catch (error) {
      console.error('Error fetching books:', error);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  }, []);

  const loadMoreBooks = () => {
    if (pageNum < totalPages && !loadingMore) {
      setPageNum(prev => prev + 1);
    }
  };

  useEffect(() => {
    fetchBooks(pageNum);
  }, [pageNum]);

  useEffect(() => {
    const handleScroll = () => {
      const scrollTop = window.scrollY;
      const windowHeight = window.innerHeight;
      const documentHeight = document.documentElement.scrollHeight;

      if (scrollTop + windowHeight >= documentHeight - 100) {
        loadMoreBooks();
      }
    };

    window.addEventListener("scroll", handleScroll);
    return () => {
      window.removeEventListener("scroll", handleScroll);
    };
  }, [loadingMore, pageNum, totalPages]);

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
          justifyContent: 'flex-start',
          minHeight: '100vh',
          bgcolor: '#1e1e1e',
          color: '#e0e0e0',
          p: 3,
        }}
      >
        <Typography variant="h4" component="div" sx={{ mb: 3, color: '#e0e0e0' }}>
          Xóa Sách
        </Typography>
        <Grid container spacing={2} justifyContent="center">
          {books.map(book => (
            <Grid item xs={12} sm={6} md={4} lg={3} key={book.bookId}>
              <BookCard book={book}/>
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
        {loadingMore && <CircularProgress />}
      </Box>
    </Scene>
  );
};

export default BookDelete;