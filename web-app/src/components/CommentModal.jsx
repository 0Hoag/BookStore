import React, { useEffect, useState, useRef } from 'react';
import { Modal, Box, Typography, IconButton, Avatar, Button, Menu, MenuItem } from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import CloseIcon from '@mui/icons-material/Close';
import userService from '../services/userService';
import { getToken } from '../services/localStorageService';
import { useNavigate } from 'react-router-dom';
import postService from '../services/postService';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import UserInfoPopup from './UserInfoPopup';

const CommentModal = ({ open, onClose, comments, onCommentSubmit, post }) => {
  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const timeoutRef = useRef(null);
  const [currentImageIndex, setCurrentImageIndex] = useState(0); // Track current image index
  const [commentMenuAnchor, setCommentMenuAnchor] = useState(null); // State for comment menu
  const [newComment, setNewComment] = React.useState("");
  const [usernames, setUsernames] = React.useState({}); // Store usernames
  const [userId, setUserId] = useState("");
  const [user, setUser] = useState(null);
  const [userIdInfo, setUserIdInfo] = useState("");
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedPost, setSelectedPost] = useState(null);
  const [image, setImage] = useState("");
  const [imageAvatar, setImageAvatar] = useState("");
  const [mediaPosts, setMediaPosts] = useState([]); // Change to an array
  const [avatars, setAvatars] = useState({}); 
  const [userAvatar, setUserAvatar] = useState(""); // my user avatar
  const [userFullName, setUserFullName] = useState(""); 
  const navigate = useNavigate();
  const [editingCommentId, setEditingCommentId] = useState(null); // State to track which comment is being edited
  const [commentsState, setCommentsState] = useState(comments); // State to manage comments
  const [selectedCommentId, setSelectedCommentId] = useState(null); // State to track the selected comment ID for deletion
  const [selectedUser, setSelectedUser] = useState(null); // State for selected user
  const [mainMedia, setMainMedia] = useState("");
  const [fullSizeMedia, setFullSizeMedia] = useState("");

  const handlePopoverOpen = (event, user) => {
    setAnchorEl(event.currentTarget);
    setIsPopupOpen(true);
    setSelectedUser(user); // Set the user for the popup
  };

  const handlePopoverClose = () => {
    setIsPopupOpen(false);
    setSelectedUser(null); // Clear selected user
  };

  const userDetails = async () => {
    const response = await userService.getMyInfo();
    return response.data.result;
  }


  const postDetails = async (postId) => {
    const response = await postService.getPost(postId);
    return response.data.result;
  }
  
  const fetchFirstName = async (userId) => {
    try {
      const getUser = await userService.getUser(userId);
      return getUser.data.result.firstName;
    } catch (error) {
      throw error;
    }
  }

  const fetchLastName = async (userId) => {
    try {
      const getUser = await userService.getUser(userId);
      return getUser.data.result.lastName;
    } catch (error) {
      throw error;
    }
  }

  const fetchImageAvatar = async (userId) => {
    try {
      const userDetails = await userService.getUser(userId);
      setUser(userDetails.data.result);
      const imageIds = userDetails.data.result.images;

      const imageResponse = await Promise.all(imageIds.map(id => userService.viewImage(id)));
      const imageObj = imageResponse.find(img => img.data.result.imageType === 'PROFILE'); 
      setImageAvatar(imageObj.data.result.imageUrl);

      return imageObj.data && imageObj.data.result && imageObj.data.result.imageData
      ? imageObj.data.result.imageUrl
      : "/path/to/default/avatar.png";
    } catch (error) {
      throw error;
    }
  }

  const fetchUserAvatar = async (post) => {
    const userId = post.userId; // Ensure this is the correct user ID
    const users = await userService.getUser(userId);
    setUserId(users.data.result.userId);
    const imageIds = users.data.result.images;

    const imageResponse = await Promise.all(imageIds.map(id => userService.viewImage(id)));
    const imageObj = imageResponse.find(img => img.data.result.imageType === 'PROFILE'); 
    return imageObj.data && imageObj.data.result && imageObj.data.result.imageUrl
      ? imageObj.data.result.imageUrl
      : "/path/to/default/avatar.png"; // Default avatar if not found
  }

  const fetchUserFullName = async (postId) => {
    const postDetail = await postDetails(postId);
    const userId = postDetail.userId;
    const userDetail = await userService.getUser(userId);
    const firstName = userDetail.data.result.firstName;
    const lastName = userDetail.data.result.lastName;
    return firstName + " " + lastName;
  }

  const fecthPostImage = async (postId) => {
    const postDetail = await postDetails(postId);
    const imageIds = postDetail.images;
    const images = await Promise.all(imageIds.map(async (imageId) => {
      const response = await postService.viewImage(imageId);
      return response.data && response.data.result && response.data.result.imageData 
      ? `data:image/jpeg;base64,${response.data.result.imageData}`
      : `/path/to/default/avatar.png`;
    }));
    return images.filter(image => image);
  };

  const handleNextImage = () => {
    setCurrentImageIndex((prevIndex) => (prevIndex + 1) % mediaPosts.length);
  };

  const handlePrevImage = () => {
    setCurrentImageIndex((prevIndex) => (prevIndex - 1 + mediaPosts.length) % mediaPosts.length);
  };

  const commentOnPost = async (postId, newComment) => {
    const userId = await userDetails();
    try {
      const commentData = {
        userId: userId.userId,
        content: newComment,
        postId: postId
      };

      const comment = await postService.createComment(commentData);
      await postService.addCommentToPost(postId, { commentId: [comment.data.result.commentId]});
      console.log("run commentOnPost success!");
    } catch (error) {
      throw error;
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

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (newComment.trim()) {
      const userId = await userDetails(); // Get user ID
      console.log("userId: {}", userId);
      const commentData = {
        userId: userId.userId,
        content: newComment,
        postId: post.postId
      };
      console.log("commentData: {}", commentData);
      await commentOnPost(post.postId, newComment); // Wait for the comment function to complete
      setCommentsState([...commentsState, { ...commentData, commentId: Date.now() }]); // Add new comment to state
      setNewComment(""); // Clear the input field
    }
  };

  const handleCommentMenuOpen = (event, commentId) => {
    setCommentMenuAnchor(event.currentTarget);
    setEditingCommentId(commentId); // Store the ID of the comment being edited
    setSelectedCommentId(commentId); // Store the ID of the comment for deletion
  };

  const handleCommentMenuClose = () => {
    setCommentMenuAnchor(null);
    setEditingCommentId(null); // Clear the editing comment ID
  };

  const handleDeleteComment = async (postId, commentId) => {
    try {
      const response = await postService.removeCommentToPost(postId, commentId);
      if (response.data && response.data.result && response.data.result.length > 0) {
        setCommentsState(commentsState.filter(comment => comment.commentId !== commentId));
      }
    } catch (error) {
      throw error;
    }
  };

  const handleEditComment = async (postId, commentId, newComment) => {
    try {
      const response = await postService.updateCommentToPost(postId, commentId, newComment);
      if (response.data && response.data.result) {
        setCommentsState(commentsState.map(comment => 
          comment.commentId === commentId ? { ...comment, content: newComment.content } : comment
        ));
      } 
    } catch (error) {
      throw error;
    } finally {
      handleCommentMenuClose();
      // setUserIdInfo("");
      // setNewComment("");
    }
  };

  const formatRelativeTime = (dateString) => {
    const now = new Date();
    const date = new Date(dateString);
    const seconds = Math.floor((now - date) / 1000);
    
    if (seconds < 60) return `${seconds}s`;
    if (seconds < 3600) return `${Math.floor(seconds / 60)}m`;
    if (seconds < 86400) return `${Math.floor(seconds / 3600)}h`;
    return `${Math.floor(seconds / 86400)}d`;
  };

  useEffect(() => {
    if (post.mediaMetadata) {
      const mediaKeys = Object.keys(post.mediaMetadata);
      mediaKeys.forEach(key => {
        const media = post.mediaMetadata[key];
        if (media) {
          if (media.type === 'video') {
            setMainMedia(media.originalUrl); // Set video URL
            setFullSizeMedia(media.originalUrl); // Set full size video URL
          } else if (media.type === 'image') {
            setMainMedia(media.versions.w1080 || media.originalUrl); // Set image URL
            setFullSizeMedia(media.originalUrl); // Set full size image URL
          }
        }
      });
    }
    
    const fetchUsernames = async () => {
      const fetchedUsernames = {};
      for (const comment of comments) {
        if (!fetchedUsernames[comment.userId]) {
          const firstName = await fetchFirstName(comment.userId);
          const lastName = await fetchLastName(comment.userId);

          fetchedUsernames[comment.userId] = firstName + " " + lastName;
        }
      }

      setUsernames(fetchedUsernames);
    };

    const fetchAvatars = async () => {
        const fetchedAvatars = {};
        for (const comment of comments) {
            if (!fetchedAvatars[comment.userId]) {
              const user = await userService.getUser(comment.userId);
              const imageIds = user.data.result.images;
              const imageResponse = await Promise.all(imageIds.map(id => userService.viewImage(id)));
              const imageObj = imageResponse.find(img => img.data.result.imageType === 'PROFILE');
              fetchedAvatars[comment.userId] = imageObj.data.result.imageUrl;
            }
        }
        setAvatars(fetchedAvatars);
    };

    const loadUserAvatar = async () => {
      const imageAvatar = await fetchUserAvatar(post);
      setUserAvatar(imageAvatar);
    };

    const loadUserFullName = async () => {
      const firstName = await fetchUserFullName(post.postId);
      setUserFullName(firstName);
    }

    const loadUserMyInfo = async () => {
      const response = await userDetails();
      setUserIdInfo(response.userId);
    }

    if (open) {
      fetchUsernames(); 
      fetchAvatars();
      loadUserAvatar();
      loadUserFullName();
      loadUserMyInfo();
    }
  }, [open, comments, post]);

  return (
    <Modal open={open} onClose={onClose}>
      <Box sx={{ 
        display: 'flex', 
        flexDirection: 'column',
        padding: 1, 
        bgcolor: '#0A0A0A', 
        width: 1300, 
        height: 920,
        margin: 'auto', 
        top: '2.5%',
        position: 'relative'
      }}>
        <Button
          onClick={onClose}
          sx={{
            position: 'absolute',
            top: 10,
            right: 10,
            bgcolor: 'transparent',
            color: 'white',
            fontSize: '20px',
          }}
        >
        <CloseIcon />
        </Button>
  
        <Box sx={{ display: 'flex', overflow: 'hidden' }}>
          <Box sx={{ flex: 2, 
            marginRight: 2, 
            display: 'flex', 
            justifyContent: 'flex-end', // Change to flex-end to align image to the right
            alignItems: 'center', 
            overflowY: 'hidden'  }}>
              {mainMedia.endsWith('.mp4') ? ( // Check if the media is a video
                <video 
                  controls 
                  style={{ width: '100%', marginBottom: '10px' }}
                >
                  <source src={mainMedia} type="video/mp4" />
                  Your browser does not support the video tag.
                </video>
              ) : (
                <img 
                  src={mainMedia} 
                  alt={""} 
                  style={{ width: '100%', marginBottom: '10px', display: 'flex' }} 
                />
              )}
              {mediaPosts.length > 1 && ( // Only show buttons if more than one image
              <>
                <IconButton 
                  onClick={handlePrevImage} 
                  sx={{ position: 'absolute', left: '1%', top: '50%', transform: 'translateY(-50%)' }}
                >
                  <ArrowBackIcon sx={{ color: "#E0E0E0" }} />
                </IconButton>
                <IconButton 
                  onClick={handleNextImage} 
                  sx={{ position: 'absolute', right: '35%', top: '50%', transform: 'translateY(-50%)' }}
                >
                  <ArrowForwardIcon sx={{ color: "#E0E0E0" }} />
                </IconButton>
              </>
            )}
          </Box>
  
          <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', marginBottom: '10px'}}>
              <Avatar src={userAvatar} sx={{ marginRight: "10px" }} onClick={() => navigate(`/profile/${userId}`)}/>
              <Typography style={{ color: "white" }} onClick={() => navigate(`/profile/${userId}`)}>{userFullName}</Typography>
            </Box>
          <hr style={{ color: "white", width: '100%' }} />

          <Box sx={{ flexGrow: 1, overflowY: 'auto', marginBottom: 2 }}>
          {commentsState.map((comment) => (
            <Typography key={comment.commentId} sx={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
              <Avatar
                src={avatars[comment.userId] || "/path/to/default/avatar.png"}
                sx={{ marginRight: "10px" }}
                onMouseEnter={(event) => handlePopoverOpen(event, comment.userId)} // Open popup on hover
                onMouseLeave={handlePopoverClose} // Close popup on leave
                onClick={() => navigate(`/profile/${comment.userId}`)} // Navigate to profile on click
              />
              <strong style={{ color: "white" }}>
                {usernames[comment.userId] || "Unknown User"}: {comment.content || "No content"}
                <Typography sx={{ color: "lightgray", fontSize: '10px', marginLeft: '5px' }}>
                  {formatRelativeTime(comment.createdAt)} 
                </Typography>
              </strong>
                  
                  <IconButton onClick={(event) => handleCommentMenuOpen(event, comment.commentId)} sx={{ marginLeft: 'auto' }}>
                    <MoreVertIcon sx={{ color: "white", marginRight: 0 }} />
                  </IconButton>
                  <Menu
                    anchorEl={commentMenuAnchor}
                    open={Boolean(commentMenuAnchor)}
                    onClose={handleCommentMenuClose}
                  >
                    <MenuItem onClick={() => {
                      const newContent = prompt("Enter new comment content", comment.content);
                      if (newContent) {
                        const formData = {
                          userId: userIdInfo,
                          content: newContent
                        };
                        handleEditComment(post.postId, comment.commentId, formData);
                      }
                      handleCommentMenuClose();
                    }}>Edit</MenuItem>

                    <MenuItem onClick={async () => {
                      await handleDeleteComment(post.postId, [selectedCommentId]); // Use selectedCommentId for deletion
                      handleCommentMenuClose();
                    }}>Delete</MenuItem>
                  </Menu>
                </Typography>
              ))}
            </Box>

            <UserInfoPopup
              user={selectedUser} // Pass the selected user data
              anchorEl={anchorEl}
              open={isPopupOpen}
              onClose={handlePopoverClose}
              onMouseEnter={handlePopoverOpen}
              onMouseLeave={handlePopoverClose}
            />

            <Box sx={{ mt: 'auto', mb: '10px' }}>
              <form onSubmit={handleCommentSubmit}>
                <input
                  type="text"
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                  placeholder="Add a comment..."
                  style={{ 
                    width: '98%', 
                    padding: '10px', 
                    border: '1px solid black',
                    backgroundColor: "black",
                    color: 'white',
                    fontSize: '15px',
                  }}
                />
              </form>
            </Box>
          </Box>
        </Box>
      </Box>
    </Modal>
  );
};

export default CommentModal;
