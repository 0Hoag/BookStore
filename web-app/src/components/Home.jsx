import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { getToken } from "../services/localStorageService";
import userService from "../services/userService";
import postService from "../services/postService";
import {
  Alert,
  Box,
  Button,
  Card,
  CircularProgress,
  Snackbar,
  TextField,
  Typography,
  Menu,
  MenuItem,
} from "@mui/material";
import Scene from "../pages/Scene";
import Post from "../components/Post";
import 'normalize.css';

export default function Home() {
  const navigate = useNavigate();
  const [userDetails, setUserDetails] = useState(null);
  const [posts, setPosts] = useState([]);
  const [newPost, setNewPost] = useState("");
  const [newComment, setNewComment] = useState("");
  const [commentingPostId, setCommentingPostId] = useState(null);
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  const [snackType, setSnackType] = useState("error");
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false); // New state for loading more posts
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedPost, setSelectedPost] = useState(null);
  const [commentMenuAnchorEl, setCommentMenuAnchorEl] = useState(null);
  const [selectedComment, setSelectedComment] = useState(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);

  const handleCloseSnackBar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setSnackBarOpen(false);
  };

  const showMessage = (message, type = "error") => {
    setSnackType(type);
    setSnackBarMessage(message);
    setSnackBarOpen(true);
  };

  const getUserDetails = async () => {
    try {
      const response = await userService.getMyInfo();
      setUserDetails(response.data.result);
    } catch (error) {
      showMessage(error.message);
    }
  };

  const getAllPosts = useCallback(async (pageNum) => {
    try {
      setLoading(pageNum === 1); // Đặt trạng thái loading cho trang đầu tiên
      setLoadingMore(pageNum > 1); // Đặt trạng thái loadingMore cho các trang sau
      const response = await postService.getAllPost(pageNum);
      if (response.data.code === 1000 && Array.isArray(response.data.result.data)) {
        setPosts(prevPosts => pageNum === 1 ? response.data.result.data : [...prevPosts, ...response.data.result.data]);
        setTotalPages(response.data.result.totalPages); // Tổng số trang
      } else {
        showMessage("Không thể tải bài viết. Dữ liệu không hợp lệ.");
      }
    } catch (error) {
      console.error("Error fetching posts:", error);
      showMessage("Không thể tải bài viết. Vui lòng thử lại sau.");
    } finally {
      setLoading(false);
      setLoadingMore(false); // Đảm bảo trạng thái loadingMore được tắt
    }
  }, []);
  

  const loadMorePosts = () => {
    if (page < totalPages) {
      setPage(prevPage => prevPage + 1);
    }
  };

  const createPost = async () => {
    try {
      const postData = {
        content: newPost,
        imageUrls: [],
        videoUrls: [],
        likes: [],
        comments: []
      };

      const result = await postService.createPost(postData);

      if (result.data.code === 1000) {
        setPosts([result.data.result, ...posts]);
        setNewPost("");
        showMessage("Đã đăng bài viết thành công", "success");
      } else {
        showMessage("Không thể đăng bài viết", "error");
      }
    } catch (error) {
      console.error("Error creating post:", error);
      showMessage(error.message);
    }
  };

  const likePost = async (postId) => {
    try {
      const likeData = {
        userId: userDetails.userId,
        postId: postId
      };
      const like = await postService.createLike(likeData);
      await postService.addLikeToPost(postId, [like.data.result.likeId]);
      
      setPosts(posts.map(post => 
        post.postId === postId 
          ? { ...post, likes: [...post.likes, like.data.result] } 
          : post
      ));
      
      showMessage("Đã thích bài viết", "success");
    } catch (error) {
      showMessage(error.message);
    }
  };

  const unlikePost = async (postId) => {
    try {
      const likeToRemove = posts.find(post => post.postId === postId).likes
        .find(like => like.userId === userDetails.userId);
      
      if (likeToRemove) { 
        await postService.removeLikeToPost(postId, [likeToRemove.likeId]);
        console.log("likeId: ", likeToRemove.likeId);
        
        setPosts(posts.map(post => 
          post.postId === postId 
            ? { ...post, likes: post.likes.filter(like => like.likeId !== likeToRemove.likeId) } 
            : post
        ));
        
        showMessage("Đã bỏ thích bài viết", "success");
      }
    } catch (error) {
      showMessage(error.message);
    }
  };

  const deleteComment = async (commentId) => {
    try {
      const postId = posts.find(post => post.comments.some(comment => comment.commentId === commentId)).postId;
      await postService.removeCommentToPost(postId, [commentId]);
      setPosts(posts.map(post => ({
        ...post,
        comments: post.comments.filter(comment => comment.commentId !== commentId)
      })));
      showMessage("Đã xóa bình luận", "success");
    } catch (error) {
      showMessage(error.message);
    }
  };

  const commentOnPost = async (postId) => {
    try {
      const commentData = {
        userId: userDetails.userId,
        content: newComment,
        postId: postId
      };

      const comment = await postService.createComment(commentData);
      await postService.addCommentToPost(postId, { commentId: [comment.data.result.commentId]});
      
      setPosts(posts.map(post => 
        post.postId === postId 
          ? { ...post, comments: [...post.comments, comment.data.result] } 
          : post
      ));
      
      setNewComment("");
      setCommentingPostId(null);
      showMessage("Đã thêm bình luận", "success");
    } catch (error) {
      showMessage(error.message);
    }
  };

  const updatePost = async (postId, newContent) => {
    try {
      const updatedPost = await postService.updatePost(postId, { content: newContent });
      setPosts(posts.map(post => 
        post.postId === postId ? { ...post, content: updatedPost.content } : post
      ));
      showMessage("Đã cập nhật bài viết", "success");
    } catch (error) {
      showMessage(error.message);
    }
  };

  const deletePost = async (postId) => {
    try {
      await postService.removePost(postId);
      setPosts(posts.filter(post => post.postId !== postId));
      showMessage("Đã xóa bài viết", "success");
    } catch (error) {
      showMessage(error.message);
    }
  };

  const handleMenuOpen = (event, post) => {
    setAnchorEl(event.currentTarget);
    setSelectedPost(post);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedPost(null);
  };

  const handleCommentMenuOpen = (event, comment) => {
    setCommentMenuAnchorEl(event.currentTarget);
    setSelectedComment(comment);
  };

  const handleCommentMenuClose = () => {
    setCommentMenuAnchorEl(null);
    setSelectedComment(null);
  };

  useEffect(() => {
    const accessToken = getToken();
    if (!accessToken) {
      navigate("/login");
    } else {
      getAllPosts(page);
      getUserDetails();
    }
  }, [navigate, page, getAllPosts]);

  useEffect(() => {
    const handleScroll = () => {
      const scrollTop = window.scrollY;
      const windowHeight = window.innerHeight;
      const documentHeight = document.documentElement.scrollHeight;

      if (scrollTop + windowHeight >= documentHeight - 100 && !loadingMore && page < totalPages) {
        loadMorePosts();
      }
    };

    window.addEventListener("scroll", handleScroll);

    return () => {
      window.removeEventListener("scroll", handleScroll);
    };
  }, [loadingMore, page, totalPages]);

  return (
    <Scene>
      <Snackbar
        open={snackBarOpen}
        onClose={handleCloseSnackBar}
        autoHideDuration={6000}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          onClose={handleCloseSnackBar}
          severity={snackType}
          variant="filled"
          sx={{ width: "100%" }}
        >
          {snackBarMessage}
        </Alert>
      </Snackbar>
      {loading ? (
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
          <Typography>Đang tải bài viết...</Typography>
        </Box>
      ) : userDetails ? (
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            padding: "20px",
            bgcolor: "#1e1e1e",
            color: "#e0e0e0",
            minHeight: "100vh",
          }}
        >
          <Card
            sx={{
              width: "100%",
              maxWidth: 600,
              bgcolor: "#333",
              color: "#e0e0e0",
              padding: "20px",
              marginBottom: "20px",
            }}
          >
            <TextField
              fullWidth
              multiline
              rows={3}
              variant="outlined"
              placeholder="Bạn đang nghĩ gì?"
              value={newPost}
              onChange={(e) => setNewPost(e.target.value)}
              sx={{ marginBottom: "10px" }}
            />
            <Button
              variant="contained"
              onClick={createPost}
              disabled={!newPost.trim()}
            >
              Đăng bài
            </Button>
          </Card>

          {posts.map((post) => (
            <Post
              key={post.postId}
              post={post}
              userDetails={userDetails}
              handleMenuOpen={handleMenuOpen}
              likePost={likePost}
              unlikePost={unlikePost}
              commentOnPost={commentOnPost}
            />
          ))}
        </Box>
      ) : null}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
      >
        <MenuItem onClick={() => {
          const newContent = prompt("Nhập nội dung mới cho bài viết:", selectedPost.content);
          if (newContent) {
            updatePost(selectedPost.postId, newContent);
          }
          handleMenuClose();
        }}>
          Chỉnh sửa
        </MenuItem>
        <MenuItem onClick={() => {
          if (window.confirm("Bạn có chắc chắn muốn xóa bài viết này?")) {
            deletePost(selectedPost.postId);
          }
          handleMenuClose();
        }}>
          Xóa
        </MenuItem>
      </Menu>
      <Menu
        anchorEl={commentMenuAnchorEl}
        open={Boolean(commentMenuAnchorEl)}
        onClose={handleCommentMenuClose}
      >
        {selectedComment && selectedComment.userId === userDetails.userId && (
          <MenuItem onClick={() => {
            deleteComment(selectedComment.commentId);
            handleCommentMenuClose();
          }}>
            Xóa bình luận
          </MenuItem>
        )}
      </Menu>
    </Scene>
  );
}