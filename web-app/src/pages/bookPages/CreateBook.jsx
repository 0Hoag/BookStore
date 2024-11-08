import React, { useState } from "react";
import { Box, Button, TextField, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import Scene from "../Scene";
import bookService from "../../services/bookService";

const CreateBook = () => {
  const [image, setImage] = useState("");
  const [bookForm, setBookForm] = useState({
    bookTitle: "",
    author: "",
    listedPrice: 0,
    price: 0,
    quantity: 1,
    description: "",
    image: image,
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

  const handleImageChange = (event) => {
    const files = event.target.files[0];
    setImage(files);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const newBook = {
        bookTitle: bookForm.bookTitle,
        author: bookForm.author,
        listedPrice: parseFloat(bookForm.listedPrice),
        price: parseFloat(bookForm.price),
        quantity: parseInt(bookForm.quantity),
        description: bookForm.description,
        image: "",
        chapters: [],
      };

      console.log("image: {}", image);
      console.log("newBook: {}", newBook);

      const response = await bookService.createBook(newBook);
      if (image && response.result.bookId) {
        try {
            await bookService.updateImageBook(response.result.bookId, image);
        } catch (imageError) {
            console.error("Error updating image:", imageError);
        }
    }
      // navigate(`/books`);
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
          bgcolor: "#1e1e1e", // Dark background similar to BookDetail
          color: "#e0e0e0", // Light text color for contrast
          p: 3,
        }}
      >
        <Typography variant="h4" component="div" sx={{ mb: 3, color: "#e0e0e0" }}>
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
            sx={{ mb: 2, input: { color: '#e0e0e0' }, label: { color: '#e0e0e0' } }}
          />
          <TextField
            required
            fullWidth
            name="author"
            label="Tác giả"
            value={bookForm.author}
            onChange={handleChange}
            sx={{ mb: 2, input: { color: '#e0e0e0' }, label: { color: '#e0e0e0' } }}
          />
          <TextField
            required
            fullWidth
            type="number"
            name="listedPrice"
            label="Giá niêm yết"
            value={bookForm.listedPrice}
            onChange={handleChange}
            sx={{ mb: 2, input: { color: '#e0e0e0' }, label: { color: '#e0e0e0' } }}
          />
          <TextField
            required
            fullWidth
            type="number"
            name="price"
            label="Giá bán"
            value={bookForm.price}
            onChange={handleChange}
            sx={{ mb: 2, input: { color: '#e0e0e0' }, label: { color: '#e0e0e0' } }}
          />
          <TextField
            required
            fullWidth
            type="number"
            name="quantity"
            label="Số lượng"
            value={bookForm.quantity}
            onChange={handleChange}
            sx={{ mb: 2, input: { color: '#e0e0e0' }, label: { color: '#e0e0e0' } }}
          />
          <TextField
            fullWidth
            multiline
            rows={4}
            name="description"
            label="Mô tả"
            value={bookForm.description}
            onChange={handleChange}
            sx={{ mb: 2, input: { color: '#e0e0e0' }, label: { color: '#e0e0e0' } }}
          />
          <TextField
            required
            fullWidth
            type="file"
            name="imageFile"
            label="Ảnh bìa"
            onChange={handleImageChange}
            sx={{ mb: 2 }}
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
            sx={{ mt: 3, backgroundColor: '#3f51b5', '&:hover': { backgroundColor: '#303f9f' }, alignSelf: "flex-start" }}
          >
            Tạo Sách
          </Button>
        </Box>
      </Box>
    </Scene>
  );
};

export default CreateBook;