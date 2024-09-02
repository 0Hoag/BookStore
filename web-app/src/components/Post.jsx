import React, { useState, useRef } from 'react';
import { Link } from 'react-router-dom';
import { Box, Typography, Avatar, IconButton, Card } from '@mui/material';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import CommentIcon from '@mui/icons-material/Comment';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import UserInfoPopup from './UserInfoPopup';

const Post = ({ post, userDetails, handleMenuOpen, likePost, unlikePost, commentOnPost }) => {
  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const timeoutRef = useRef(null);
  const [anchorEl, setAnchorEl] = useState(null);

  const handlePopoverOpen = (event) => {
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    setAnchorEl(event.currentTarget);
    setIsPopupOpen(true);
  };

  const handlePopoverClose = () => {
    timeoutRef.current = setTimeout(() => {
      setIsPopupOpen(false);
    }, 300); // Đợi 300ms trước khi đóng popup
  };
  
  return (
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
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: "10px" }}>
        <Box sx={{ display: "flex", flexDirection: "column" }}>
          <Box sx={{ display: "flex", alignItems: "center" }}>
            <Avatar src={post.userId.avatarUrl} alt={post.userId.username} sx={{ marginRight: "10px" }} />
            <Box
              onMouseEnter={handlePopoverOpen}
              onMouseLeave={handlePopoverClose}
              sx={{ position: 'relative' }}
            >
              <Typography 
                component={Link}
                to={`/profile/${post.userId.userId}`}
                variant="subtitle1" 
                sx={{ 
                  lineHeight: 1.2,
                  '&:hover': { textDecoration: 'underline', cursor: 'pointer' }
                }}
              >
                {post.userId.username}
              </Typography>
              <Typography 
              variant="caption" 
              sx={{ 
                fontSize: '0.75rem', 
                color: '#a0a0a0', 
                lineHeight: 1
              }}
            >
              {post.created}
            </Typography>
            </Box>
          </Box>
        </Box>
        {post.userId.userId === userDetails.userId && (
          <IconButton onClick={(event) => handleMenuOpen(event, post)}>
            <MoreVertIcon />
          </IconButton>
        )}
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
        <IconButton onClick={() => commentOnPost(post.postId)} color="primary">
          <CommentIcon /> {post.comments ? post.comments.length : 0}
        </IconButton>
      </Box>
      <UserInfoPopup
        user={post.userId}
        anchorEl={anchorEl}
        open={isPopupOpen}
        onClose={handlePopoverClose}
        onMouseEnter={handlePopoverOpen}
        onMouseLeave={handlePopoverClose}
      />
    </Card>
  );
};

export default Post;