import React, { useEffect, useState, useCallback } from "react";
import { Box, Button, Grid, Typography, CircularProgress } from "@mui/material";
import { Link } from "react-router-dom";
import Scene from "../Scene";
import bookService from "../../services/bookService";
import BookCard from "./BookCard";
import userService from "../../services/userService";
import { getToken } from "../../services/localStorageService";
import { debounce } from 'lodash';

const Books = () => {
  const [books, setBooks] = useState([]);
  const [isAdmin, setIsAdmin] = useState(false);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  const [totalPages, setTotalPages] = useState(0);

  const fetchBooks = useCallback(async (pageNum) => {
    try {
      setLoading(pageNum === 1);
      setLoadingMore(pageNum > 1);

      const response = await bookService.getAllBooks(pageNum);
      
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

  const checkAdminRole = async () => {
    try {
      const userInfo = await userService.getMyInfo();
      const roles = userInfo.data?.result?.roles || [];
      const isAdminUser = roles.some(role => role.name === 'ADMIN');
      setIsAdmin(isAdminUser);
    } catch (error) {
      console.error('Error fetching user info:', error);
    }
  };

  useEffect(() => {
    const accessToken = getToken();
    if (accessToken) {
      fetchBooks(pageNum);
      if (pageNum === 1) {
        checkAdminRole();
      }
    }
  }, [fetchBooks, pageNum]);

  useEffect(() => {
    const debouncedLoadMore = debounce(() => {
      loadMoreBooks();
    }, 300);

    const handleScroll = () => {
      const scrollTop = window.scrollY;
      const windowHeight = window.innerHeight;
      const documentHeight = document.documentElement.scrollHeight;

      if (scrollTop + windowHeight >= documentHeight - 100) {
        debouncedLoadMore();
      }
    };

    window.addEventListener("scroll", handleScroll);

    return () => {
      window.removeEventListener("scroll", handleScroll);
      debouncedLoadMore.cancel();
    };
  }, [loadingMore, pageNum, totalPages]);

  if (loading) {
    return (
      <Scene>
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            gap: "30px",
            justifyContent: "center",
            alignItems: "center",
            height: "100vh",
            bgcolor: "#1e1e1e",
            color: "#e0e0e0",
          }}
        >
          <CircularProgress color="inherit" />
          <Typography>Đang tải danh sách sách...</Typography>
        </Box>
      </Scene>
    );
  }

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
        <Typography variant="h4" component="div" sx={{ mb: 3, textAlign: 'center', color: '#e0e0e0' }}>
          Danh sách sách
        </Typography>

        <Grid container spacing={2} justifyContent="center">
          {books.map((book) => (
            <Grid item xs={12} sm={6} md={4} lg={2.4} key={book.bookId}>
              <Link to={`/books/${book.bookId}`} style={{ textDecoration: 'none' }}>
                <BookCard book={book} />
              </Link>
            </Grid>
          ))}
        </Grid>

        {loadingMore && (
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
            <CircularProgress color="inherit" />
          </Box>
        )}

        {isAdmin && (
          <Box sx={{ display: 'flex', gap: 2, mt: 3 }}>
            <Button
              component={Link}
              to="/create-book"
              variant="contained"
              color="primary"
              sx={{ bgcolor: '#444', '&:hover': { bgcolor: '#555' } }}
            >
              Tạo Sách Mới
            </Button>
            <Button
              component={Link}
              to="/delete-book"
              variant="contained"
              color="secondary"
              sx={{ bgcolor: '#444', '&:hover': { bgcolor: '#555' } }}
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