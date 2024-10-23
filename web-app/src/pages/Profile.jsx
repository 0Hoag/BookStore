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
  const [selectedImages, setSelectedImages] = useState([]);
  const [selectedVideos, setSelectedVideos] = useState([]);
  const [editingPostId, setEditingPostId] = useState(null);
  const [editedPostContent, setEditedPostContent] = useState("");
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  const [snackType, setSnackType] = useState("error");
  const [commentingPostId, setCommentingPostId] = useState(null);
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedPost, setSelectedPost] = useState(null);
  const [page, setPage] = useState(1);
  const [loadingMore, setLoadingMore] = useState(false); // New state for loading more posts
  const [loading, setLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(0);
  const [imageProfile, setImageProfile] = useState("/path/to/default/avatar.png");
  const [imageCover, setImageCover] = useState("/path/to/default/cover.png");
  const [image, setImage] = useState("");
  const [selectedComment, setSelectedComment] = useState(null);
  const [commentMenuAnchorEl, setCommentMenuAnchorEl] = useState(null);
  const [selectedMedia, setSelectedMedia] = useState([]);

  const darkTheme = createTheme({
    palette: {
      mode: 'dark',
      background: {
        default: '#0A0A0A',
        paper: '#0A0A0A',
      },
      text: {
        primary: '#e4e6eb',
        secondary: '#b0b3b8',
      },
      primary: {
        main: '#0A0A0A',
      },
    },
  });

  const getUserDetails = async () => {
    try {
      const response = await userService.getMyInfo();
      setUserDetails(response.data.result); // Set user details regardless of image
      
      if (response.data.result.images && response.data.result.images.length > 0) {
        const imageIds = response.data.result.images; // Get the array of image IDs
        // Use Promise.all to fetch all images concurrently
        const imagesResponse = await Promise.all(imageIds.map(id => userService.viewImage(id)));

        // Find the profile image from the responses
        const profileImageObj = imagesResponse.find(img => img.data.result.imageType === 'PROFILE');
        const coverImageObj = imagesResponse.find(img => img.data.result.imageType === 'COVER');

        if (profileImageObj) {
          setImageProfile(profileImageObj.data.result.imageUrl);
        } else {
          setImageProfile("/path/to/default/avatar.png");
        }
        
        if (coverImageObj) {
          setImageCover(coverImageObj.data.result.imageUrl); // Ensure cover image is set
        }
      }
    } catch (error) {
      showMessage(error.message);
    }
  };

  const updateImageProfile = async (file) => {
    try {
      const user = userDetails; // Ensure userDetails is correctly accessed
      const userId = user.userId; // Get user ID
      const imageIds = user.images; 

      // Check if there are images
      if (imageIds && imageIds.length > 0) {
        const imageResponse = await Promise.all(imageIds.map(id => userService.viewImage(id)));
        const profileImageObj = imageResponse.find(img => img.data.result.imageType === 'PROFILE');

        // If a profile image exists, delete it before uploading the new one
        if (profileImageObj) {
          console.log("Deleting old profile image:", profileImageObj.data.result);
          await userService.deleteImage(userId, [profileImageObj.data.result.imageId]); // Pass the correct ID
        }
      }

      // Upload the new profile image
      await userService.updateImageProfile(userId, file); // Call to update the profile image
      console.log("New profile image uploaded successfully."); // Debugging log
    } catch (error) {
      console.error("Error in updateImageProfile:", error); // Log any errors
    }
  };

  const updateImageCover = async (file) => {
    try {
      const user = userDetails;
      const userId = user.userId;
      const imageIds = user.images;

      // Check if there are images
      if (imageIds && imageIds.length > 0) {
        const imageResponse = await Promise.all(imageIds.map(id => userService.viewImage(id)));
        const coverImageObj = imageResponse.find(img => img.data.result.imageType === 'COVER');

        // If a cover image exists, delete it before uploading the new one
        if (coverImageObj) {
          console.log("Deleting old cover image:", coverImageObj.data.result);
          await userService.deleteImage(userId, [coverImageObj.data.result.imageId]);
        }
      }

      // Upload the new cover image
      await userService.updateImageCover(userId, file);
      console.log("New cover image uploaded successfully!");
    } catch (error) {
      console.error("Error in updateImageCover:", error); // Log any errors
    }
  }

  const handleChangeImageProfile = async (event) => {
    const files = event.target.files;
    console.log("Selected files:", files); // Debugging log
    if (files.length > 0) {
      try {
        await updateImageProfile(files[0]); // Call to update the profile image
        console.log("Profile image updated successfully."); // Debugging log
      } catch (error) {
        console.error("Error updating profile image:", error); // Log any errors
      }
    } else {
      console.log("No file selected!"); // Debugging log
    }
  };

  const handleChangeImageCover = async (event) => {
    const files = event.target.files;
    console.log("Selected files: ", files);
    if (files.length > 0) {
      try {
        await updateImageCover(files[0]);
        console.log("update image success!")
      }catch (error) {
        console.log("update image it found!")
        throw error;
      }
    }else {
      console.log("No file selected!");
    }
  }

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

  const deletePost = async (postId) => {
    try {
      await postService.removePost(postId);
      setPosts(posts.filter(post => post.postId !== postId));
    } catch (error) {
      showMessage(error.message);
    }
  };

  const handleCommentMenuClose = () => {
    setCommentMenuAnchorEl(null);
    setSelectedComment(null);
  };

  const showMessage = (message, type = "error") => {
    setSnackType(type);
    setSnackBarMessage(message);
    setSnackBarOpen(true);
  };

  const handleImageAndVideoChange = (event) => {
    const files = Array.from(event.target.files);
    setSelectedMedia(prevImages => [...prevImages, ...files]);
    setImage(files);
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

      console.log("ListPost: {}", posts.map(post => 
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

  const handleMenuOpen = (event, post) => {
    setAnchorEl(event.currentTarget);
    setSelectedPost(post);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedPost(null);
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
      <Header />
      {userDetails ? (
        <Box sx={{ bgcolor: '#0A0A0A', minHeight: "100vh", position: 'relative', paddingTop: '200px' }}>
          <Box
            sx={{
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              height: '600px',
              backgroundImage: `url(${imageCover})`,
              backgroundSize: 'cover',
              backgroundPosition: 'center',
              '&::after': {
                content: '""',
                position: 'absolute',
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                backgroundColor: 'rgba(0,0,0,0.3)', // Optional: adds a slight dark overlay
              },
            }}
          />
          <Grid container spacing={2} justifyContent="center" sx={{ position: 'relative', zIndex: 1 }}>
          <Grid item xs={12} md={4} sx={{ textAlign: 'center', bgcolor: 'transparent' }}>
            <label htmlFor="profile-image-upload">
              <Avatar
                src={imageProfile}
                alt={userDetails.username}
                sx={{
                  width: '10vw',
                  height: '10vw',
                  borderRadius: '50%',
                  margin: '-60px auto 0',
                  border: '4px solid white',
                  minWidth: '150px',
                  minHeight: '150px',
                  display: 'inline-block',
                  position: 'relative',
                  cursor: 'pointer',
                  transition: 'transform 0.3s ease, box-shadow 0.3s ease', // Add transition for smooth effect
                  '&:hover': {
                    transform: 'scale(1.05)', // Scale up on hover
                    boxShadow: '0 4px 20px rgba(0, 0, 0, 0.3)', // Add shadow on hover
                  },
                }}
              />
              <input
                id="profile-image-upload"
                type="file"
                hidden
                accept="image/*"
                onChange={handleChangeImageProfile} // Link to the change handler
              />
            </label>
            <Typography variant="h5" sx={{ color: 'white', mt: 2 }}>{userDetails.username}</Typography>
            <Typography variant="body2" sx={{ color: 'gray' }}>{userDetails.firstName} {userDetails.lastName}</Typography>
            <label htmlFor="cover-image-upload">
              <IconButton component="span" sx={{ mt: 2 }}>
                <CameraAltIcon sx={{ color: 'white' }} />
              </IconButton>
              <input
                id="cover-image-upload"
                type="file"
                hidden
                accept="image/*"
                onChange={handleChangeImageCover}
              />
            </label>
          </Grid>
            <Grid item xs={12} md={10} sx={{margin: '210px'}}>
            <Button variant="contained" sx={{ mt: 2, backgroundColor: "#3B3B3B", color: "#E0E0E0", display: 'flex', margin: 'auto' }} onClick={() => navigate(`/profile/edit/info`)}>Edit Profile</Button>
              <Card sx={{ bgcolor: 'black', p: 2, mb: 2 }}>
              <TextField
                  fullWidth
                  multiline
                  rows={3}
                  variant="outlined"
                  placeholder="Hoàng ơi, bạn đang nghĩ gì thế?"
                  value={newPost}
                  onChange={(e) => setNewPost(e.target.value)}
                  sx={{ mb: 2 }}
                />
                <Box sx={{ display: 'flex', gap: 2, marginBottom: 2 }}>
                  <Button
                    variant="contained"
                    component="label"
                    sx={{ backgroundColor: "#3B3B3B", color: "#E0E0E0" }} 
                  >
                    Ảnh/video
                    <input
                      type="file"
                      hidden
                      multiple
                      accept="image/*"
                      onChange={handleImageAndVideoChange}
                    />
                  </Button>
                  <Button
                    variant="contained"
                    component="label"
                    sx={{ backgroundColor: "#3B3B3B", color: "#E0E0E0" }} 
                  >
                    Cảm xúc/hành động
                    <input
                      type="file"
                      hidden
                      multiple
                      accept="image/*"
                      onChange={handleImageAndVideoChange}
                    />
                  </Button>
                </Box>
                {selectedMedia.length > 0 && (
                  <Typography variant="body2" sx={{ marginBottom: 1 }}>
                    {selectedMedia.length} ảnh hoặc video đã chọn
                  </Typography>
                )}
                <Button
                  variant="contained"
                  onClick={createPost}
                  disabled={!newPost.trim() && selectedMedia.length === 0}
                >
                  Đăng bài
                </Button>
              </Card>
              <Grid container spacing={2}>
                {posts.map((post) => (
                  <Grid item xs={4} key={post.postId}>
                    <Post
                      post={post}
                      userDetails={userDetails}
                      handleMenuOpen={handleMenuOpen}
                      likePost={likePost}
                      unlikePost={unlikePost}
                      commentOnPost={commentOnPost}
                      volume={false}
                    />
                  </Grid>
                ))}
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
              </Grid>
            </Grid>
          </Grid>
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
            bgcolor: '#0A0A0A',
          }}
        >
          <CircularProgress />
          <Typography color="text.primary">Loading ...</Typography>
        </Box>
      )}
    </ThemeProvider>
  );
};