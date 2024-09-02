import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { Box, Typography, Button } from "@mui/material";
import bookService from "../../../services/bookService";
import Scene from "../../Scene";
import { useNavigate } from "react-router-dom";

const ChapterDetail = () => {
  const { bookId, chapterId } = useParams();
  const [book, setBook] = useState(null);
  const [currentChapter, setCurrentChapter] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchBookDetail = async () => {
      try {
        const response = await bookService.getBookById(bookId);
        if (response && response.chapters) {
          const sortedChapters = Array.from(response.chapters).sort(
            (a, b) => a.sequenceNumber - b.sequenceNumber
          );
          setBook({ ...response, chapters: sortedChapters });

          const foundChapter = sortedChapters.find(
            (chapter) => chapter.chapterId === chapterId
          );
          if (foundChapter) {
            setCurrentChapter(foundChapter);
          }
        }
      } catch (error) {
        console.error("Error loading book detail:", error);
      }
    };

    fetchBookDetail();
  }, [bookId, chapterId]);

  const handleNavigation = (direction) => {
    if (book && book.chapters && book.chapters.length > 0) {
      let newIndex;
      if (direction === "prev") {
        newIndex = book.chapters.findIndex(
          (chapter) => chapter.chapterId === chapterId
        ) - 1;
      } else if (direction === "next") {
        newIndex = book.chapters.findIndex(
          (chapter) => chapter.chapterId === chapterId
        ) + 1;
      }

      if (newIndex >= 0 && newIndex < book.chapters.length) {
        const nextChapter = book.chapters[newIndex];
        navigate(`/books/${bookId}/${nextChapter.chapterId}`);
        window.scrollTo(0, 0);
      } else {
        console.log(`No ${direction} chapter available`);
      }
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
          minHeight: "100vh",
          bgcolor: "#2e2e2e", // Dark background color
          color: '#f5f5f5', // White smoke text color
          p: 3,
        }}
      >
        {/* Navigation buttons */}
        <Box sx={{ width: "100%", maxWidth: 1000, marginBottom: 2 }}>
          <Box sx={{ display: "flex", justifyContent: "space-between", marginBottom: 2 }}>
            <Button
              onClick={() => handleNavigation("prev")}
              variant="outlined"
              color="secondary"
              disabled={!currentChapter || currentChapter.sequenceNumber === 1}
              sx={{ color: '#f5f5f5', borderColor: '#f5f5f5', '&:hover': { borderColor: '#e0e0e0' } }}
            >
              <i className="fas fa-chevron-left" /> Chương trước
            </Button>
            <Link to={`/books/${bookId}`} style={{ textDecoration: "none" }}>
              <Button
                variant="outlined"
                color="secondary"
                sx={{ color: '#f5f5f5', borderColor: '#f5f5f5', '&:hover': { borderColor: '#e0e0e0' } }}
              >
                Mục lục
              </Button>
            </Link>
            <Button
              onClick={() => handleNavigation("next")}
              variant="outlined"
              color="secondary"
              disabled={!currentChapter || currentChapter.sequenceNumber === book?.chapters.length}
              sx={{ color: '#f5f5f5', borderColor: '#f5f5f5', '&:hover': { borderColor: '#e0e0e0' } }}
            >
              Chương sau <i className="fas fa-chevron-right" />
            </Button>
          </Box>
        </Box>

        {/* Chapter content */}
        <Box sx={{ display: "flex", justifyContent: "center", maxWidth: 1000, overflowY: "auto" }}>
          {!currentChapter ? (
            <Box sx={{ textAlign: "center" }}>
              <Typography variant="h4" sx={{ color: '#f5f5f5' }}>Loading...</Typography>
            </Box>
          ) : (
            <Box sx={{ width: "100%" }}>
              <Typography variant="h4" sx={{ fontWeight: "bold", mb: 3, textAlign: "center", color: '#f5f5f5' }}>
                {book.bookTitle}
              </Typography>
              <Typography variant="h6" sx={{ color: "#b0b0b0", mb: 3, textAlign: "center" }}>
                Thứ {currentChapter.sequenceNumber} - {currentChapter.chapterTitle}
              </Typography>
              <Typography variant="body1" sx={{ fontSize: "1.4rem", textAlign: "left", color: '#e0e0e0' }}>
                {currentChapter.content}
              </Typography>
            </Box>
          )}
        </Box>

        {/* Navigation buttons */}
        <Box sx={{ width: "100%", maxWidth: 1000, marginTop: 2 }}>
          <Box sx={{ display: "flex", justifyContent: "space-between", marginTop: 2 }}>
            <Button
              onClick={() => handleNavigation("prev")}
              variant="outlined"
              color="secondary"
              disabled={!currentChapter || currentChapter.sequenceNumber === 1}
              sx={{ color: '#f5f5f5', borderColor: '#f5f5f5', '&:hover': { borderColor: '#e0e0e0' } }}
            >
              <i className="fas fa-chevron-left" /> Chương trước
            </Button>
            <Link to={`/books/${bookId}`} style={{ textDecoration: "none" }}>
              <Button
                variant="outlined"
                color="secondary"
                sx={{ color: '#f5f5f5', borderColor: '#f5f5f5', '&:hover': { borderColor: '#e0e0e0' } }}
              >
                Mục lục
              </Button>
            </Link>
            <Button
              onClick={() => handleNavigation("next")}
              variant="outlined"
              color="secondary"
              disabled={!currentChapter || currentChapter.sequenceNumber === book?.chapters.length}
              sx={{ color: '#f5f5f5', borderColor: '#f5f5f5', '&:hover': { borderColor: '#e0e0e0' } }}
            >
              Chương sau <i className="fas fa-chevron-right" />
            </Button>
          </Box>
        </Box>
      </Box>
    </Scene>
  );
};

export default ChapterDetail;
