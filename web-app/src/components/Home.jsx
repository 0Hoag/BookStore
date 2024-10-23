import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { getToken } from "../services/localStorageService";
import userService from "../services/userService";
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
import { v4 as uuidv4 } from 'uuid';
import { debounce } from 'lodash';
import postService from "../services/postService";

export default function Home() {
  const navigate = useNavigate();
  const [userDetails, setUserDetails] = useState(null);
  const [posts, setPosts] = useState([]);
  const [newPost, setNewPost] = useState("");
  const [commentingPostId, setCommentingPostId] = useState(null);
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  const [snackType, setSnackType] = useState("error");
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedPost, setSelectedPost] = useState(null);
  const [commentMenuAnchorEl, setCommentMenuAnchorEl] = useState(null);
  const [selectedComment, setSelectedComment] = useState(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [selectedMedia, setSelectedMedia] = useState([]);
  const [password, setPassword] = useState("");
  const [check, setCheck] = useState(null);
  const [image, setImage] = useState("");

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
      setCheck(response.data.result.noPassword);
    } catch (error) {
      showMessage(error.message);
    }
  };

  const addPassword = (event) => {
    event.preventDefault();

    getUserDetails(getToken());

    showMessage("Your password has been created, you can use your password to login")
  };

  const createPassword = async () => {
    const user = await userService.getMyInfo();
    const userId = user.data.result.userId;
    userService.createPassword(userId, password);
  }

  const getAllPosts = useCallback(async (pageNum) => {
    try {
      setLoading(pageNum === 1);
      setLoadingMore(pageNum > 1);
      const response = await postService.getAllPost(pageNum);
      if (response.data.code === 1000 && Array.isArray(response.data.result.data)) {
        setPosts(prevPosts => pageNum === 1 ? response.data.result.data : [...prevPosts, ...response.data.result.data]);
        setTotalPages(response.data.result.totalPages);
      } else {
        showMessage("Không thể tải bài viết. Dữ liệu không hợp lệ.");
      }
    } catch (error) {
      console.error("Error fetching posts:", error);
      showMessage("Không thể tải bài viết. Vui lòng thử lại sau.");
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  }, []);
  

  const loadMorePosts = () => {
    if (page < totalPages) {
      setPage(prevPage => prevPage + 1);
    }
  };

  const createPost = async () => {
    try {
      const formData = {
        content: newPost,
        medias: [],
        likes: [],
        comments: []
      };
  
      const result = await postService.createPostFile(formData);
      
      for (let file of image) {
        const uploadImage = await postService.uploadImageToPost(result.data.result.postId, file);
        if (uploadImage.data.code !== 1000) {
          showMessage("Không thể tải lên một số hình ảnh", "warning");
        }
      }
  
      setPosts([result.data.result, ...posts]);
      setNewPost("");
      setSelectedMedia([]);
      showMessage("Đã đăng bài viết thành công", "success");
    } catch (error) {
      console.error("Lỗi khi tạo bài viết:", error);
      showMessage(error.message, "error");
    }
  };
  
  const handleImageAndVideoChange = (event) => {
    const files = Array.from(event.target.files);
    setSelectedMedia(prevImages => [...prevImages, ...files]);
    setImage(files);
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
      
      // showMessage("Đã thích bài viết", "success");
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
        
        setPosts(posts.map(post => 
          post.postId === postId 
            ? { ...post, likes: post.likes.filter(like => like.likeId !== likeToRemove.likeId) } 
            : post
        ));
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
    } catch (error) {
      showMessage(error.message);
    }
  };

  const commentOnPost = async (postId, newComment) => {
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
      
      setCommentingPostId(null);
      showMessage("Đã thêm bình luận", "success");
    } catch (error) {
      showMessage(error.message);
    }
  };

  const updatePost = async (postId, content, medias) => {
    try {
      const post = await postService.getPost(postId);
      if (post.data.code === 1000) {
        const updatedPost = await postService.updatePost(postId, content, medias); // Updated parameters
        setPosts(posts.map(post => 
          post.postId === postId ? { ...post, content: updatedPost.content } : post
        ));
      }
    } catch (error) {
      showMessage(error.message);
    }
  };

  const deletePost = async (postId) => {
    try {
      await postService.removePost(postId);
      setPosts(posts.filter(post => post.postId !== postId));
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
      getAllPosts(page); // Ensure this doesn't cause a loop
      getUserDetails();
    }
  }, [navigate, page, getAllPosts]);

  useEffect(() => {
    const debouncedLoadMore = debounce(() => {
      if (page < totalPages && !loadingMore) {
        loadMorePosts();
      }
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
  }, [loadingMore, page, totalPages, loadMorePosts]);

  return (
    <Scene>
      {check && (
        <Box
          component="form"
          onSubmit={addPassword}
          sx={{
            display: "flex",
            flexDirection: "column",
            gap: "10px",
            width: "100%",
          }}
        >
          <Typography>Do you want to create password?</Typography>
          <TextField
            label="Password"
            type="password"
            variant="outlined"
            fullWidth
            margin="normal"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
            size="large"
            fullWidth
            onClick={createPassword}
          >
            Create password
          </Button>
        </Box>
        )}
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
            bgcolor: "#0A0A0A", // Very dark background for dark mode
            color: "#E0E0E0", // Light text color for contrast
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
            paddingRight: "300px", // Increased left padding
            bgcolor: "#0A0A0A", // Very dark background for dark mode
            color: "#E0E0E0", // Light text color for contrast
            minHeight: "100vh",
          }}
        >
          {posts.map((post) => (
            <Post
              key={post.postId}
              post={post}
              userDetails={userDetails}
              handleMenuOpen={handleMenuOpen}
              likePost={likePost}
              unlikePost={unlikePost}
              commentOnPost={commentOnPost}
              volume={true}
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
            updatePost(selectedPost.postId, newContent, selectedMedia);
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