import React, { useState } from "react";
import { Box, Button, TextField, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import Scene from "../Scene";
import bookService from "../../services/bookService";
import uploadImageToDropbox from "./uploadImageToDropbox";

const CreateBook = () => {
  const [bookForm, setBookForm] = useState({
    bookTitle: "",
    author: "",
    listedPrice: 0,
    price: 0,
    quantity: 1,
    description: "",
    imageFile: null,
  });
  const navigate = useNavigate();

  const handleChange = (event) => {
    const { name, value, files } = event.target;
    if (name === "imageFile") {
      setBookForm({ ...bookForm, imageFile: files[0] });
    } else {
      setBookForm({ ...bookForm, [name]: value });
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const { publicURL } = await uploadImageToDropbox(bookForm.imageFile);

      const newBook = {
        bookTitle: bookForm.bookTitle,
        author: bookForm.author,
        listedPrice: parseFloat(bookForm.listedPrice),
        price: parseFloat(bookForm.price),
        quantity: parseInt(bookForm.quantity),
        description: bookForm.description,
        image: publicURL,
        chapters: [],
      };

      const createdBook = await bookService.createBook(newBook);
      navigate(`/books`);
    } catch (error) {
      console.error("Error creating book:", error);
    }
  };

  return (
    <Scene>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          height: "100vh",
          bgcolor: "#f0f2f5",
          p: 3,
        }}
      >
        <Typography variant="h4" component="div" sx={{ mb: 3 }}>
          Tạo Sách Mới
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ width: "100%", maxWidth: 600 }}>
          <TextField
            required
            fullWidth
            name="bookTitle"
            label="Tiêu đề sách"
            value={bookForm.bookTitle}
            onChange={handleChange}
            sx={{ mb: 2 }}
          />
          <TextField
            required
            fullWidth
            name="author"
            label="Tác giả"
            value={bookForm.author}
            onChange={handleChange}
            sx={{ mb: 2 }}
          />
          <TextField
            required
            fullWidth
            type="number"
            name="listedPrice"
            label="Giá niêm yết"
            value={bookForm.listedPrice}
            onChange={handleChange}
            sx={{ mb: 2 }}
          />
          <TextField
            required
            fullWidth
            type="number"
            name="price"
            label="Giá bán"
            value={bookForm.price}
            onChange={handleChange}
            sx={{ mb: 2 }}
          />
          <TextField
            required
            fullWidth
            type="number"
            name="quantity"
            label="Số lượng"
            value={bookForm.quantity}
            onChange={handleChange}
            sx={{ mb: 2 }}
          />
          <TextField
            fullWidth
            multiline
            rows={4}
            name="description"
            label="Mô tả"
            value={bookForm.description}
            onChange={handleChange}
            sx={{ mb: 2 }}
          />
          <TextField
            required
            fullWidth
            type="file"
            name="imageFile"
            label="Ảnh bìa"
            onChange={handleChange}
            sx={{ mb: 2 }}
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
            sx={{ mt: 3, alignSelf: "flex-start" }}
          >
            Tạo Sách
          </Button>
        </Box>
      </Box>
    </Scene>
  );
};

export default CreateBook;
