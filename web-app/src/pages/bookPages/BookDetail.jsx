import React, { useEffect, useState } from "react";
import { useParams, Link } from 'react-router-dom';
import { Box, Typography, Grid, Card, CardContent, Button, IconButton, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Checkbox, FormControlLabel } from "@mui/material";
import { FavoriteBorderOutlined, FavoriteOutlined, BookmarkBorderOutlined, BookmarkOutlined, LockOpenOutlined, ShoppingBasketOutlined } from "@mui/icons-material";
import bookService from "../../services/bookService";
import Scene from "../Scene";
import { useNavigate } from "react-router-dom";
import { getMyInfo } from "../../services/userService";
import cartItemService from "../../services/cartItemService";

const BookDetail = () => {
  const { bookId } = useParams();
  const [book, setBook] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false); // Default role, it can be "ADMIN" or "USER"
  const [isFavorite, setIsFavorite] = useState(false);
  const [isBookmarked, setIsBookmarked] = useState(false);
  const [isFollowing, setIsFollowing] = useState(false);
  const [isUnlocked, setIsUnlocked] = useState(false);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [selectedChapters, setSelectedChapters] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchBookDetail = async () => {
      try {
        const response = await bookService.getBookById(bookId);
        if (response) {
          setBook(response);
          const userInfo = await getMyInfo();
          const roles = userInfo.data?.result?.roles || [];
          const isAdminUser = roles.some(role => role.name === 'ADMIN');
          setIsAdmin(isAdminUser);
          setIsFavorite(response.isFavorite || false);
          setIsBookmarked(response.isBookmarked || false);
          setIsFollowing(response.isFollowing || false);
          setIsUnlocked(response.isUnlocked || false);
        } else {
          console.error('No book found with the given ID');
        }
      } catch (error) {
        console.error('Error fetching book details:', error);
      }
    };

    fetchBookDetail();
  }, [bookId]);

  const handleChapterClick = (chapterId) => {
    navigate(`/books/${bookId}/${chapterId}`);
  };

  const handleFavoriteToggle = () => {
    setIsFavorite(!isFavorite);
  };

  const handleBookmarkToggle = () => {
    setIsBookmarked(!isBookmarked);
  };

  const handleFollowToggle = () => {
    setIsFollowing(!isFollowing);
  };

  const handleUnlock = () => {
    setIsUnlocked(true);
  };

  const handleReadFirstChapter = () => {
    if (sortedChapters.length > 0) {
      const firstChapterId = sortedChapters[0].chapterId;
      navigate(`/books/${bookId}/${firstChapterId}`);
    }
  };

  const handleBuyNow = async (event) => {
    event.preventDefault();
    try {
      const response = await getMyInfo();
      const data = response.data;

      const existingCartItem = data.result.cartItem.find(cartItem => cartItem.bookId.bookId === bookId);
  
      if (existingCartItem) {
        console.log("Book is already in the cart.");
        return;
      }

      const newCart = {
        bookId: bookId,
        quantity: 1,
        userId: data.result.userId,
      };
      
      await cartItemService.createCartItem(newCart);
    } catch (error) {
      console.error("Error adding cartItem:", error);
    }
  };
  
  
  const handleOpenDeleteDialog = () => {
    setOpenDeleteDialog(true);
  };

  const handleCloseDeleteDialog = () => {
    setOpenDeleteDialog(false);
  };

  const handleChapterSelect = (event, chapterId) => {
    if (event.target.checked) {
      setSelectedChapters([...selectedChapters, chapterId]);
    } else {
      setSelectedChapters(selectedChapters.filter(id => id !== chapterId));
    }
  };

  const handleDeleteChapters = async () => {
    try {
      const deletePromises = selectedChapters.map(chapterId => bookService.deleteChapter(chapterId));
      await Promise.all(deletePromises);
      
      // Update sortedChapters after deletion
      const updatedChapters = book.chapters.filter(chapter => !selectedChapters.includes(chapter.chapterId));
      setBook(prevBook => ({
        ...prevBook,
        chapters: updatedChapters,
      }));
  
      setSelectedChapters([]);
      setOpenDeleteDialog(false);
    } catch (error) {
      console.error('Error deleting chapters:', error);
    }
  };

  // Sắp xếp các chương khi book và chapters đã có giá trị
  const sortedChapters = book?.chapters?.sort((a, b) => a.sequenceNumber - b.sequenceNumber) || [];

  return (
    <Scene>
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '100vh', // Set minHeight to ensure the box fills the viewport height
          bgcolor: '#f0f2f5',
          p: 3,
        }}
      >
        {book ? (
          <Box sx={{ maxWidth: 1200, width: '100%' }}>
            {book.image && (
              <Box sx={{ maxWidth: "100%", mb: 3 }}>
                <img
                  src={book.image.replace("?dl=0", "?raw=1")}
                  alt={book.bookTitle}
                  style={{ maxWidth: "100%" }}
                />
              </Box>
            )}
            <Typography variant="h4" component="div" sx={{ mb: 3 }}>
              Book Details - {book.bookTitle}
            </Typography>
            <Box sx={{ textAlign: 'left', maxWidth: '100%' }}>
              <Typography variant="h6">Title: {book.bookTitle}</Typography>
              <Typography variant="body1" sx={{ mt: 2, width: '100%', wordWrap: 'break-word' }}>
                Author: {book.author}
              </Typography>
              <Typography variant="body1" sx={{ mt: 2, width: '100%', wordWrap: 'break-word' }}>
                Description: {book.description}
              </Typography>
              <Typography variant="body1" sx={{ mt: 2 }}>
                Listed Price: {book.listedPrice} VND
              </Typography>
              <Typography variant="body1" sx={{ mt: 2 }}>
                Price: {book.price} VND
              </Typography>
              <Typography variant="body1" sx={{ mt: 2 }}>
                Quantity: {book.quantity}
              </Typography>
              {/* Action Buttons */}
              <Box sx={{ mt: 2 }}>
                <Button onClick={handleReadFirstChapter} variant="contained" color="primary" sx={{ ml: 2 }}>
                  Đọc ngay
                </Button>
                <IconButton onClick={handleFavoriteToggle} sx={{ mr: 2 }}>
                  {isFavorite ? <FavoriteOutlined /> : <FavoriteBorderOutlined />}
                </IconButton>
                <IconButton onClick={handleBookmarkToggle} sx={{ mr: 2 }}>
                  {isBookmarked ? <BookmarkOutlined /> : <BookmarkBorderOutlined />}
                </IconButton>
                <IconButton onClick={handleFollowToggle} sx={{ mr: 2 }}>
                  {isFollowing ? "Following" : "Follow"}
                </IconButton>
                {!isUnlocked && (
                  <Button onClick={handleUnlock} variant="contained" color="secondary" sx={{ ml: 2 }}>
                    <LockOpenOutlined /> Unlock
                  </Button>
                )}
                <Button onClick={handleBuyNow} variant="contained" color="primary" startIcon={<ShoppingBasketOutlined />} sx={{ ml: 2 }}>
                  Mua ngay
                </Button>
              </Box>
              {sortedChapters && sortedChapters.length > 0 && (
                <Box sx={{ mt: 2, width: '100%' }}>
                  <Typography variant="h6" sx={{ mb: 1, color: 'green' }}>Danh sách chương:</Typography>
                  <Box sx={{ border: '1px solid #ccc', borderRadius: '5px', padding: '10px' }}>
                    <Grid container spacing={2} sx={{ direction: 'ltr' }}>
                      {sortedChapters.map((chapter, index) => (
                        <Grid item xs={12} sm={4} key={chapter.chapterId}>
                          <Card
                            variant="outlined"
                            sx={{ cursor: "pointer", minHeight: 120 }}
                            onClick={() => handleChapterClick(chapter.chapterId)}
                          >
                            <CardContent>
                              <Typography variant="subtitle1" gutterBottom>
                                Chương {chapter.sequenceNumber}: {chapter.chapterTitle}
                              </Typography>
                            </CardContent>
                          </Card>
                        </Grid>
                      ))}
                    </Grid>
                  </Box>
                </Box>
              )}
            </Box>
            {/* Add Chapter Link */}
            {isAdmin && (
              <Box sx={{ mt: 2 }}>
                <Button
                  component={Link}
                  to={`/books/${book.bookId}/add-chapter`}
                  variant="contained"
                  color="primary"
                  sx={{ mr: 2 }}
                >
                  Thêm chương
                </Button>
                <Button
                  onClick={handleOpenDeleteDialog}
                  variant="contained"
                  color="secondary"
                >
                  Xóa chương
                </Button>
              </Box>
            )}
            {/* Delete Dialog */}
            <Dialog
              open={openDeleteDialog}
              onClose={handleCloseDeleteDialog}
              aria-labelledby="alert-dialog-title"
              aria-describedby="alert-dialog-description"
            >
              <DialogTitle id="alert-dialog-title">{"Chọn các chương muốn xóa"}</DialogTitle>
              <DialogContent>
                <DialogContentText id="alert-dialog-description">
                  Chọn các chương bạn muốn xóa khỏi sách này.
                </DialogContentText>
                <Box>
                  {sortedChapters.map((chapter) => (
                    <FormControlLabel
                      key={chapter.chapterId}
                      control={
                        <Checkbox
                          onChange={(event) => handleChapterSelect(event, chapter.chapterId)}
                          checked={selectedChapters.includes(chapter.chapterId)}
                        />
                      }
                      label={`Chương ${chapter.sequenceNumber}: ${chapter.chapterTitle}`}
                    />
                  ))}
                </Box>
              </DialogContent>
              <DialogActions>
                <Button onClick={handleCloseDeleteDialog} color="primary">
                  Hủy
                </Button>
                <Button onClick={handleDeleteChapters} color="secondary" autoFocus>
                  Xóa
                </Button>
              </DialogActions>
            </Dialog>
          </Box>
        ) : (
          <Typography variant="body1">Loading book details...</Typography>
        )}
      </Box>
    </Scene>
  );
};

export default BookDetail;
