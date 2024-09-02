import { useEffect, useState, useRef, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getToken } from "../services/localStorageService";
import Post from "../components/Post";
import postService from "../services/postService";
import userService from '../services/userService';
import {
  Alert,
  Box,
  Card,
  CircularProgress,
  Snackbar,
  Typography,
  Avatar,
  Button,
  Divider,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  createTheme,
  ThemeProvider,
  Grid,
  TextField,
  IconButton,
  Menu,
  MenuItem,
} from "@mui/material";
import EmailIcon from '@mui/icons-material/Email';
import WorkIcon from '@mui/icons-material/Work';
import LockIcon from '@mui/icons-material/Lock';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import CommentIcon from '@mui/icons-material/Comment';
import SendIcon from '@mui/icons-material/Send';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import Header from "../components/header/Header";
import 'normalize.css';
import CameraAltIcon from '@mui/icons-material/CameraAlt';
import AddIcon from '@mui/icons-material/Add';

export default function Profile() {
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
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedPost, setSelectedPost] = useState(null);
  const [commentMenuAnchorEl, setCommentMenuAnchorEl] = useState(null);
  const [selectedComment, setSelectedComment] = useState(null);
  const [editingPostId, setEditingPostId] = useState(null);
  const [editedPostContent, setEditedPostContent] = useState("");
  const [page, setPage] = useState(1);
  const [loadingMore, setLoadingMore] = useState(false); // New state for loading more posts
  const [totalPages, setTotalPages] = useState(0);

  const darkTheme = createTheme({
    palette: {
      mode: 'dark',
      background: {
        default: '#18191a',
        paper: '#242526',
      },
      text: {
        primary: '#e4e6eb',
        secondary: '#b0b3b8',
      },
      primary: {
        main: '#2e89ff',
      },
    },
  });

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
      if (response) {
        setUserDetails(response.data.result);
      }
    } catch (error) {
      showMessage(error.message);
    }
  };

  const handleEditPost = (post) => {
    setEditingPostId(post.postId);
    setEditedPostContent(post.content);
    handleMenuClose();
  };
  
  const saveEditedPost = async () => {
    try {
      await updatePost(editingPostId, editedPostContent);
      setEditingPostId(null);
      setEditedPostContent("");
      showMessage("Bài viết đã được cập nhật", "success");
    } catch (error) {
      showMessage("Không thể cập nhật bài viết", "error");
    }
  };

  const getMyPosts = useCallback(async (pageNum) => {
    try {
      setLoading(pageNum === 1); // Đặt trạng thái loading cho trang đầu tiên
      setLoadingMore(pageNum > 1); // Đặt trạng thái loadingMore cho các trang sau
      const response = await postService.getMyPost(pageNum);
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
      getUserDetails();
      getMyPosts(page);
    }
  }, [navigate, page, getMyPosts]);

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
    <ThemeProvider theme={darkTheme}>
      <Header/>
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
        {userDetails ? (
          <Box
            sx={{
              bgcolor: 'background.default',
              minHeight: "calc(100vh - 64px)",
            }}
          >
            <Box sx={{ maxWidth: 940, margin: "0 auto", overflow: "visible" }}>
              <Card sx={{ overflow: "visible", bgcolor: 'background.paper' }}>
                <Box sx={{ height: 350, bgcolor: "#2e89ff", position: "relative" }}>
                  {/* Cover photo */}
                  <IconButton 
                    sx={{ position: 'absolute', right: 16, bottom: 16, bgcolor: 'rgba(0,0,0,0.6)' }}
                  >
                    <CameraAltIcon />
                  </IconButton>
                </Box>
                <Box sx={{ display: "flex", justifyContent: "space-between", px: 4, mb: 3, position: 'relative' }}>
                  <Avatar
                    sx={{
                      width: 168,
                      height: 168,
                      border: "4px solid #242526",
                      marginTop: "-84px",
                      bgcolor: '#3a3b3c',
                      color: 'text.primary',
                    }}
                  >
                    {userDetails.username.charAt(0).toUpperCase()}
                  </Avatar>
                  <Box sx={{ display: 'flex', alignItems: 'flex-end', mb: 2, gap: 2 }}>
                    <Button variant="contained" startIcon={<AddIcon />}>
                      Thêm vào tin
                    </Button>
                    <Button variant="contained">
                      Chỉnh sửa trang cá nhân
                    </Button>
                  </Box>
                </Box>
                <Box sx={{ px: 4, pb: 3 }}>
                  <Typography variant="h4" gutterBottom color="text.primary">
                    {userDetails.username}
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
                    {['Bài viết', 'Giới thiệu', 'Bạn bè', 'Ảnh', 'Video', 'Reels', 'Xem thêm'].map((item) => (
                      <Button key={item} sx={{ color: 'text.primary' }}>
                        {item}
                      </Button>
                    ))}
                  </Box>
                </Box>
              </Card>
              
              <Grid container spacing={3} sx={{ mt: 3 }}>
                <Grid item xs={12} md={5}>
                  <Card sx={{ bgcolor: 'background.paper', p: 2 }}>
                    <Typography variant="h6" gutterBottom>Giới thiệu</Typography>
                    <Button fullWidth variant="outlined" sx={{ mt: 1 }}>Thêm tiểu sử</Button>
                    <Button fullWidth variant="outlined" sx={{ mt: 1 }}>Chỉnh sửa chi tiết</Button>
                    <Button fullWidth variant="outlined" sx={{ mt: 1 }}>Thêm sở thích</Button>
                  </Card>
                </Grid>
                <Grid item xs={12} md={7}>
                  <Card sx={{ bgcolor: 'background.paper', p: 2, mb: 2 }}>
                    <TextField
                      fullWidth
                      multiline
                      rows={3}
                      variant="outlined"
                      placeholder="Bạn đang nghĩ gì?"
                      value={newPost}
                      onChange={(e) => setNewPost(e.target.value)}
                      sx={{ mb: 2 }}
                    />
                    <Button
                      variant="contained"
                      onClick={createPost}
                      disabled={!newPost.trim()}
                    >
                      Đăng
                    </Button>
                  </Card>
                  {posts.map((post) => (
                    <Card
                      key={post.postId}
                      sx={{
                        bgcolor: 'background.paper',
                        padding: 2,
                        marginBottom: 2
                      }}
                    >
                      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "10px" }}>
                        <Box sx={{ display: "flex", alignItems: "center" }}>
                          <Avatar src={post.userId.avatarUrl} alt={post.userId.username} sx={{ marginRight: "10px" }} />
                          <Typography variant="subtitle1">{post.userId.username}</Typography>
                        </Box>
                        <IconButton onClick={(event) => handleMenuOpen(event, post)}>
                          <MoreVertIcon />
                        </IconButton>
                      </Box>
                      <Typography variant="body1" sx={{ marginBottom: "10px" }}>
                        {post.content}
                      </Typography>
                      <Box sx={{ display: "flex", justifyContent: "space-between", marginBottom: "10px" }}>
                        <IconButton 
                          onClick={() => post.likes.some(like => like.userId === userDetails.userId) ? unlikePost(post.postId) : likePost(post.postId)} 
                          color={post.likes.some(like => like.userId === userDetails.userId) ? "primary" : "default"}
                        >
                          <ThumbUpIcon /> {post.likes ? post.likes.length : 0}
                        </IconButton>
                        <IconButton onClick={() => setCommentingPostId(post.postId)} color="primary">
                          <CommentIcon /> {post.comments ? post.comments.length : 0}
                        </IconButton>
                      </Box>
                      {commentingPostId === post.postId && (
                        <Box sx={{ display: "flex", marginTop: "10px" }}>
                          <TextField
                            fullWidth
                            variant="outlined"
                            placeholder="Viết bình luận..."
                            value={newComment}
                            onChange={(e) => setNewComment(e.target.value)}
                            size="small"
                          />
                          <IconButton onClick={() => commentOnPost(post.postId)} color="primary">
                            <SendIcon />
                          </IconButton>
                        </Box>
                      )}
                      {post.comments && post.comments.map((comment) => (
                        <Box key={comment.commentId} sx={{ marginTop: "10px", bgcolor: "#444", padding: "10px", borderRadius: "4px" }}>
                          <Box sx={{ display: "flex", alignItems: "center", marginBottom: "5px", justifyContent: "space-between" }}>
                            <Box sx={{ display: "flex", alignItems: "center" }}>
                              <Avatar src={comment.userId.avatarUrl} alt={comment.userId.username} sx={{ width: 24, height: 24, marginRight: "10px" }} />
                              <Typography variant="subtitle2">{comment.userId.username}</Typography>
                            </Box>
                            <IconButton size="small" onClick={(event) => handleCommentMenuOpen(event, comment)}>
                              <MoreVertIcon fontSize="small" />
                            </IconButton>
                          </Box>
                          <Typography variant="body2">{comment.content}</Typography>
                        </Box>
                      ))}
                    </Card>
                  ))}
                </Grid>
              </Grid>
            </Box>
          </Box>
        ) : (
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: "30px",
              justifyContent: "center",
              alignItems: "center",
              height: "calc(100vh - 64px)",
              bgcolor: 'background.default',
            }}
          >
            <CircularProgress />
            <Typography color="text.primary">Loading ...</Typography>
          </Box>
        )}
    </ThemeProvider>
  );
}