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
import messengerService from '../services/messengerService';
import 'normalize.css';
import CameraAltIcon from '@mui/icons-material/CameraAlt';
import AddIcon from '@mui/icons-material/Add';
import Header from "../components/header/Header";
import { IoPersonAddOutline } from "react-icons/io5";
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import CancelIcon from '@mui/icons-material/Cancel';
import CheckIcon from '@mui/icons-material/Check';
import PeopleIcon from '@mui/icons-material/People';
import friendService from "../services/friendService";

export default function ProfileDetail() {
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
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [totalPages, setTotalPages] = useState(0);
  const { userId } = useParams();
  const [imageCover, setImageCover] = useState("");
  const [image, setImage] = useState(null);
  const [selectedMedia, setSelectedMedia] = useState([]);
  const [isCurrentUser, setIsCurrentUser] = useState(false);
  const [myUserId, setMyUserId] = useState(null);
  const [requestStatus, setRequestStatus] = useState('none');
  const [requestId, setRequestId] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [relationship, setRelationship] = useState(null);

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

  const fetchRequestStatus = useCallback(async () => {
    try {
      if (myUserId && userId) {
        const response = await friendService.getFriendRequestStatus(myUserId, userId);
        setRequestStatus(response.status);
        setRequestId(response.requestId);
      }
    } catch (error) {
      console.error("Error fetching friend request status:", error);
      showMessage("Không thể lấy trạng thái kết bạn", "error");
    }
  }, [myUserId, userId]);

  const handleAddFriend = async () => {
    setIsLoading(true);
    try {
      const response = await friendService.createFriendRequest({ 
        senderId: myUserId, 
        receiverId: userDetails.userId 
      });
      setRequestStatus('sent');
      setRequestId(response.requestId);
      showMessage("Đã gửi lời mời kết bạn", "success");
    } catch (error) {
      console.error("Error sending friend request:", error);
      showMessage("Không thể gửi lời mời kết bạn", "error");
    } finally {
      setIsLoading(false);
    }
  };

  const handleCancelFriendRequest = async () => {
    setIsLoading(true);
    try {
      await friendService.removeFriendRequest(requestId);
      setRequestStatus('none');
      setRequestId(null);
      showMessage("Đã hủy lời mời kết bạn", "success");
    } catch (error) {
      console.error("Error canceling friend request:", error);
      showMessage("Không thể hủy lời mời kết bạn", "error");
    } finally {
      setIsLoading(false);
    }
  };

  const handleAcceptFriendRequest = async () => {
    setIsLoading(true);
    try {
      await friendService.updateFriendStatus(requestId, { condition: 'ACCEPTED' });
      await friendService.createFriendShip({
        userId1: myUserId,
        userId2: userDetails.userId
      });
      setRequestStatus('friends');
      await checkRelationship(); // Refresh relationship status
      showMessage("Đã chấp nhận lời mời kết bạn", "success");
    } catch (error) {
      console.error("Error accepting friend request:", error);
      showMessage("Không thể chấp nhận lời mời kết bạn", "error");
    } finally {
      setIsLoading(false);
    }
  };

  const renderFriendButton = () => {
    if (isLoading) {
      return <CircularProgress size={24} />;
    }

    switch (requestStatus) {
      case 'sent':
        return (
          <Button
            variant="outlined"
            startIcon={<CancelIcon />}
            onClick={handleCancelFriendRequest}
            sx={{
              color: '#9e9e9e',
              borderColor: '#9e9e9e',
              '&:hover': {
                backgroundColor: 'rgba(158, 158, 158, 0.1)',
              },
            }}
          >
            Hủy lời mời
          </Button>
        );
      case 'received':
        return (
          <Button
            variant="outlined"
            startIcon={<CheckIcon />}
            onClick={handleAcceptFriendRequest}
            sx={{
              color: '#4caf50',
              borderColor: '#4caf50',
              '&:hover': {
                backgroundColor: 'rgba(76, 175, 80, 0.1)',
              },
            }}
          >
            Chấp nhận
          </Button>
        );
      case 'friends':
        return (
          <Button
            variant="outlined"
            startIcon={<PeopleIcon />}
            disabled
            sx={{
              color: '#4caf50',
              borderColor: '#4caf50',
            }}
          >
            Bạn bè
          </Button>
        );
      default:
        return (
          <Button
            variant="outlined"
            startIcon={<PersonAddIcon />}
            onClick={handleAddFriend}
            sx={{
              color: '#4caf50',
              borderColor: '#4caf50',
              '&:hover': {
                backgroundColor: 'rgba(76, 175, 80, 0.1)',
              },
            }}
          >
            Kết bạn
          </Button>
        );
    }
  };

  const checkRelationship = useCallback(async () => {
    try {
      console.log("Begin");
      if (myUserId) {
        console.log("myUserId: ", myUserId);
        const response = await friendService.getUserRelationShip(myUserId);
        const friendships = response.friendships;
        console.log("friendships: ", friendships);
        
        // Kiểm tra xem có friendship nào match với cả 2 user không
        const friendship = friendships.find(fs => 
          (fs.userId1 === myUserId && fs.userId2 === userId) || 
          (fs.userId1 === userId && fs.userId2 === myUserId)
        );

        console.log("friendship1: ", friendship);

        if (friendship && friendship.relationShip === 'FRIENDS') {
          setRequestStatus('friends');
          console.log("set request status to friends");
        }
      }
    } catch (error) {
      console.error("Error checking relationship:", error);
      showMessage("Không thể kiểm tra mối quan hệ bạn bè", "error");
    }
  }, [myUserId, userId]);

  useEffect(() => {
    if (myUserId && userId) {
      checkRelationship();
      fetchRequestStatus();
    }
  }, [fetchRequestStatus, checkRelationship, myUserId, userId]);

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
      const response = await userService.getUser(userId);
      setUserDetails(response.data.result);
      if (response.data.result.images != null) {
        const imageIds = response.data.result.images;
        const imageResponse = await Promise.all(imageIds.map(id => userService.viewImage(id)));

        const imageProfile = imageResponse.find(img => img.data.result.imageType === 'PROFILE');
        const imageCover = imageResponse.find(img => img.data.result.imageType === 'COVER');
        setImage(imageProfile.data.result.imageUrl);
        setImageCover(imageCover.data.result.imageUrl);
        console.log("set image profile and cover success!");
      } else {
        setImage("/path/to/default/avatar.png");
      }
    } catch (error) {
      showMessage(error.message);
    }
  };

  const getMyInfo = async () => {
    const response = await userService.getMyInfo();
    setMyUserId(response.data.result.userId);
    if (userId === response.data.result.userId) {
      setIsCurrentUser(true);
    } else {
      setIsCurrentUser(false);
    }
  }

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
      setLoading(pageNum === 1);
      setIsLoadingMore(pageNum > 1);
      const response = await postService.getPostWithUser(userId, pageNum);
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
      setIsLoadingMore(false);
    }
  }, [userId]);

  const loadMorePosts = useCallback(() => {
    if (page < totalPages && !isLoadingMore) {
      setPage(prevPage => prevPage + 1);
    }
  }, [page, totalPages, isLoadingMore]);

  useEffect(() => {
    const handleScroll = () => {
      if (window.innerHeight + document.documentElement.scrollTop !== document.documentElement.offsetHeight) return;
      if (!isLoadingMore && page < totalPages) {
        loadMorePosts();
      }
    };

    getMyInfo();

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, [loadMorePosts, isLoadingMore, page, totalPages]);

  useEffect(() => {
    const accessToken = getToken();
    if (!accessToken) {
      navigate("/login");
    } else {
      getUserDetails();
      getMyPosts(page);
    }
  }, [navigate, page, getMyPosts, userId]);

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

  const handleImageAndVideoChange = (event) => {
    const files = Array.from(event.target.files);
    setSelectedMedia(prevImages => [...prevImages, ...files]);
    setImage(files);
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

  const createConversation = async () => {
    try {
      const myInfo = await userService.getMyInfo();
      const currentUser = myInfo.data.result;

      const conversation = {  
        name: `${currentUser.username},${userDetails.username}`,
        participantIds: [currentUser.userId, userDetails.userId]
      };

      const result = await messengerService.createConversation(conversation);
      if (result.data.code === 1000) {
        showMessage("Đã tạo cuộc trò chuyện", "success");
      } else {
        showMessage("Không thể tạo cuộc trò chuyện", "error");
      }
    } catch (error) {
      console.error("Error creating conversation:", error);
      showMessage("Đã xảy ra lỗi khi tạo cuộc trò chuyện", "error");
    }
  };

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
                backgroundColor: 'rgba(0,0,0,0.3)',
              },
            }}
          />
          <Grid container spacing={2} justifyContent="center" sx={{ position: 'relative', zIndex: 1 }}>
            <Grid item xs={12} md={4} sx={{ textAlign: 'center', bgcolor: 'transparent' }}>
              <Avatar
                src={image}
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
                }}
              />
              <Typography variant="h5" sx={{ color: 'white', mt: 2 }}>{userDetails.username}</Typography>
              <Typography variant="body2" sx={{ color: 'gray' }}>{userDetails.firstName} {userDetails.lastName}</Typography>
              
              <Grid container spacing={1} justifyContent="center" sx={{ marginTop: 2 }}>
                <Grid item>
                  <Button variant="contained" sx={{ backgroundColor: '#2e89ff', color: 'white' }} onClick={handleAddFriend}>Follow</Button>
                </Grid>
                <Grid item>
                  <Button variant="outlined" onClick={createConversation} sx={{ backgroundColor: 'transparent', color: 'white', borderColor: 'gray' }}>Message</Button>
                </Grid>
                {!isCurrentUser && (
                <Grid item>
                  {renderFriendButton()}
                </Grid>
              )}
              <Grid item>
                <Button 
                  variant="outlined" 
                  onClick={createConversation} 
                  sx={{ 
                    backgroundColor: 'transparent', 
                    color: 'white', 
                    borderColor: 'gray' 
                  }}
                >
                  Nhắn tin
                </Button>
              </Grid>
              </Grid>
            </Grid>
            <Grid item xs={12} md={10} sx={{ margin: '210px' }}>
            {isCurrentUser && (
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
            )}
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
}
