import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Box, Button, TextField, Typography } from "@mui/material";
import Scene from "../../Scene";
import bookService from "../../../services/bookService";

const AddChapter = () => {
  const { bookId } = useParams();
  const [chapterForm, setChapterForm] = useState({
    chapterTitle: "",
    sequenceNumber: 0,
    content: "",
  });
  const navigate = useNavigate();

  const handleChange = (event) => {
    const { name, value } = event.target;
    setChapterForm({ ...chapterForm, [name]: value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const newChapter = {
        chapterTitle: chapterForm.chapterTitle,
        sequenceNumber: parseInt(chapterForm.sequenceNumber, 10),
        content: chapterForm.content,
      };

      const createdChapter = await bookService.createChapter(newChapter);

      await bookService.addChapterToBook(bookId,  [createdChapter.chapterId]);

      navigate(`/books/${bookId}`);
    } catch (error) {
      console.error("Error adding chapter:", error);
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
          bgcolor: "#2e2e2e", // Dark background color
          p: 3,
          color: '#f5f5f5' // White smoke text color
        }}
      >
        <Typography variant="h4" component="div" sx={{ mb: 3, color: '#f5f5f5' }}>
          Thêm Chương Mới
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ width: "100%", maxWidth: 600 }}>
          <TextField
            required
            fullWidth
            name="chapterTitle"
            label="Tiêu đề chương"
            value={chapterForm.chapterTitle}
            onChange={handleChange}
            sx={{ mb: 2, bgcolor: '#424242', color: '#f5f5f5', '& .MuiInputLabel-root': { color: '#f5f5f5' }, '& .MuiInputBase-input': { color: '#f5f5f5' } }}
          />
          <TextField
            required
            fullWidth
            type="number"
            name="sequenceNumber"
            label="Số thứ tự chương"
            value={chapterForm.sequenceNumber}
            onChange={handleChange}
            sx={{ mb: 2, bgcolor: '#424242', color: '#f5f5f5', '& .MuiInputLabel-root': { color: '#f5f5f5' }, '& .MuiInputBase-input': { color: '#f5f5f5' } }}
          />
          <TextField
            required
            fullWidth
            multiline
            rows={4}
            name="content"
            label="Nội dung"
            value={chapterForm.content}
            onChange={handleChange}
            sx={{ mb: 2, bgcolor: '#424242', color: '#f5f5f5', '& .MuiInputLabel-root': { color: '#f5f5f5' }, '& .MuiInputBase-input': { color: '#f5f5f5' } }}
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
            sx={{ mt: 3, alignSelf: "flex-start", bgcolor: '#f5f5f5', color: '#2e2e2e', '&:hover': { bgcolor: '#e0e0e0' } }}
          >
            Thêm Chương
          </Button>
        </Box>
      </Box>
    </Scene>
  );
};

export default AddChapter;
