import React from "react";
import { Card, CardContent, Typography, CardMedia } from "@mui/material";

const BookCard = ({ book }) => {
  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardMedia
        component="img"
        height="200"
        image={book.image.replace("?raw=1", "?dl=0")}
        alt={book.bookTitle}
      />
      <CardContent sx={{ flexGrow: 1 }}>
        <Typography gutterBottom variant="h6">
          {book.bookTitle}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Tác giả: {book.author}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Giá: {book.price} VND
        </Typography>
      </CardContent>
    </Card>
  );
};

export default BookCard;
