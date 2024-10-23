import React from "react";
import { Card, CardContent, Typography, CardMedia } from "@mui/material";

const BookCard = ({ book }) => {
  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', bgcolor: '#2e2e2e', color: '#e0e0e0' }}>
      <CardMedia
        component="img"
        height="400"
        image={book.image}
        alt={book.bookTitle}
        sx={{ objectFit: 'cover' }}
      />
      <CardContent sx={{ flexGrow: 1, color: '#f5f5f5' }}>
        <Typography gutterBottom variant="h6" sx={{ color: '#f5f5f5' }}>
          {book.bookTitle}
        </Typography>
        <Typography variant="body2" sx={{ color: '#f5f5f5' }}>
          Tác giả: {book.author}
        </Typography>
        <Typography variant="body2" sx={{ color: '#f5f5f5' }}>
          Giá: {book.price} VND
        </Typography>
      </CardContent>
    </Card>
  );
};

export default BookCard;