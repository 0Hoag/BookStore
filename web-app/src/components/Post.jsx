import React, { useEffect, useState, useRef, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Box, Typography, Avatar, IconButton, Card, Grid } from '@mui/material';
import FavoriteIcon from '@mui/icons-material/Favorite';
import CommentIcon from '@mui/icons-material/Comment';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import UserInfoPopup from './UserInfoPopup';
import userService from '../services/userService';
import postService from '../services/postService';
import ShareIcon from '@mui/icons-material/Share';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import ChatBubbleOutlineIcon from '@mui/icons-material/ChatBubbleOutline';
import BookmarkBorderIcon from '@mui/icons-material/BookmarkBorder';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CommentModal from './CommentModal';
import Modal from 'react-modal';
import Picker from '@emoji-mart/react';
import data from '@emoji-mart/data';

const Post = ({ post, userDetails, handleMenuOpen, likePost, unlikePost, commentOnPost, volume }) => {

  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const timeoutRef = useRef(null);
  const [anchorEl, setAnchorEl] = useState(null);
  const [image, setImage] = useState("");
  const [username, setUsername] = useState("");
  const [userId, setUserId] = useState("");
  const [imagePosts, setImagePosts] = useState([]);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [showHeart, setShowHeart] = useState(false);
  const [comment, setComment] = useState("");
  const [isContentExpanded, setIsContentExpanded] = useState(false);
  const navigate = useNavigate();
  const [isCommentModalOpen, setIsCommentModalOpen] = useState(false);
  const [thumbnail, setThumbnail] = useState("");
  const [mainMedia, setMainMedia] = useState("");
  const [fullSizeMedia, setFullSizeMedia] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImageLoaded, setIsImageLoaded] = useState(false);
  const videoRef = useRef(null);
  const videoNoRef = useRef(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [isPickerVisible, setIsPickerVisible] = useState(false); // Add state for emoji picker
  const [pickerPosition, setPickerPosition] = useState({ top: 0, left: 0 });
  const emojiButtonRef = useRef(null);
  const observerRef = useRef(null);


  const handlePopoverOpen = (event) => {
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    setAnchorEl(event.currentTarget);
    setIsPopupOpen(true);
  };

  const handleEmojiButtonClick = (event) => {
    // Lấy vị trí của nút emoji
    const buttonRect = event.currentTarget.getBoundingClientRect();
    const scrollY = window.scrollY;

    // Tính toán vị trí cho emoji picker
    setPickerPosition({
      top: buttonRect.top + scrollY - 430, // Tăng lên cao hơn 30px
      left: buttonRect.right - 5, // Giảm 20px để dịch sang phải
    });
    setIsPickerVisible(!isPickerVisible);
  };

  const handlePopoverClose = () => {
    timeoutRef.current = setTimeout(() => {
      setIsPopupOpen(false);
    }, 300);
  };

  const handleVideoClick = () => {
    if (!videoRef.current) return;
    
    if (videoRef.current.paused) {
      videoRef.current.play()
        .then(() => {
          setIsPlaying(true);
          videoRef.current.muted = false;
        })
        .catch((error) => {
          console.log('Play failed:', error);
          // Try to play muted if unmuted playback fails
          videoRef.current.muted = true;
          videoRef.current.play().then(() => setIsPlaying(true));
        });
    } else {
      videoRef.current.pause();
      setIsPlaying(false);
    }
  };

  const handleVideoClickNoAutoplay = () => {
    if (!videoNoRef.current) return;
    
    if (videoNoRef.current.paused) {
      videoNoRef.current.play()
        .then(() => {
          setIsPlaying(true);
          videoNoRef.current.muted = false;
        })
        .catch((error) => {
          console.log('Play failed:', error);
          // Try to play muted if unmuted playback fails
          videoNoRef.current.muted = true;
          videoNoRef.current.play().then(() => setIsPlaying(true));
        });
    } else {
      videoNoRef.current.pause();
      setIsPlaying(false);
    }
  };



  const openModal = () => {
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const handleCommentModalOpen = () => {
    setIsCommentModalOpen(true);
  };

  const handleCommentModalClose = () => {
    setIsCommentModalOpen(false);
  };

  const handleEmojiSelect = (emoji) => {
    setComment((prevComment) => prevComment + emoji.native); // Append the selected emoji to the comment
  };

  useEffect(() => {
    console.log("volume: ", volume);
    if (post.userId) {
      fetchImage(post.userId);
    }
    if (post.mediaMetadata) {
      const mediaKeys = Object.keys(post.mediaMetadata);
      const validImages = mediaKeys
        .map(imageId => {
          const media = post.mediaMetadata[imageId];
          return media?.versions?.w1080 || media?.originalUrl;
        })
        .filter(url => url);
      setImagePosts(validImages);
    }
  }, [post.userId, post.mediaMetadata]);

  // Memoized function to handle intersection
  const handleIntersection = useCallback((entries) => {
    entries.forEach((entry) => {
      if (!videoRef.current) return;

      if (entry.isIntersecting) {
        videoRef.current.play()
          .then(() => {
            setIsPlaying(true);
            videoRef.current.muted = false;
          })
          .catch((error) => {
            console.log('Autoplay failed:', error);
            videoRef.current.muted = true;
            videoRef.current.play().then(() => setIsPlaying(true));
          });
      } else {
        videoRef.current.pause();
        videoRef.current.currentTime = 0;
        videoRef.current.muted = true;
        setIsPlaying(false);
      }
    });
  }, []);

  const handleIntersectionNoAutoplay = useCallback((entries) => {
    entries.forEach((entry) => {
      if (!videoNoRef.current) return;

      if (entry.isIntersecting) {
        // Keep video paused by default
        setIsPlaying(false); // Ensure video is not playing when intersecting
      } else {
        videoNoRef.current.pause();
        videoNoRef.current.currentTime = 0;
        videoNoRef.current.muted = true;
        setIsPlaying(false);
      }
    });
  }, []);

  useEffect(() => {
    if (typeof window.IntersectionObserver !== 'undefined' && 
        videoRef.current && 
        imagePosts[currentImageIndex]?.endsWith('.mp4')) {
      
      if (observerRef.current) {
        observerRef.current.disconnect();
      }

      observerRef.current = new IntersectionObserver(handleIntersection, {
        threshold: 0.6
      });

      observerRef.current.observe(videoRef.current);

      return () => {
        if (observerRef.current) {
          observerRef.current.disconnect();
        }
      };
    }
  }, [imagePosts, currentImageIndex, handleIntersection]);

  useEffect(() => {
    // No autoplay version
    if (typeof window.IntersectionObserver !== 'undefined' && 
      videoNoRef.current && 
        imagePosts[currentImageIndex]?.endsWith('.mp4')) {
      
      if (observerRef.current) {
        observerRef.current.disconnect();
      }

      observerRef.current = new IntersectionObserver(handleIntersectionNoAutoplay, {
        threshold: 0.6
      });

      observerRef.current.observe(videoNoRef.current);

      return () => {
        if (observerRef.current) {
          observerRef.current.disconnect();
        }
      };
    }
  }, [imagePosts, currentImageIndex, handleIntersectionNoAutoplay]);

  const fetchImage = async (userId) => {
    try {
      const user = await userService.getUser(userId);
      setUsername(user.data.result.username);
      setUserId(user.data.result.userId);
      const imageIds = user.data.result.images;

      const imageResponse = await Promise.all(imageIds.map(id => userService.viewImage(id)));
      const imageObj = imageResponse.find(img => img.data.result.imageType === 'PROFILE');
      setImage(imageObj.data.result.imageUrl);
    } catch (error) {
      throw error;
    }
  };

  const fetchImagePost = async (imagese) => {
      const resolvedImage = imagese;
      if (!imagePosts.includes(resolvedImage)) {
        console.log("resolvedImage: ", resolvedImage);
        setImagePosts(prevImages => [...prevImages, resolvedImage]);
      } else {
      const defaultImage = "/path/to/default/avatar.png";
      if (!imagePosts.includes(defaultImage)) {
        setImagePosts(prevImages => [...prevImages, defaultImage]);
      }
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  const navigatePost = (postId) => {
    navigate(`/p/${postId}`);
  }

  const formatContent = (content) => {
    return content.split('\n').map((line, index) => (
      <React.Fragment key={index}>
        {line}
        <br />
      </React.Fragment>
    ));
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

  const checkVolume = (volume) => {
    if (volume) {
      return true;
    } else {
      return false;
    }
  }

  const handleNextImage = () => {
    if (imagePosts.length > 0) {
      setCurrentImageIndex((prevIndex) => (prevIndex + 1) % imagePosts.length);
    }
  };

  const handlePrevImage = () => {
    if (imagePosts.length > 0) {
      setCurrentImageIndex((prevIndex) => (prevIndex - 1 + imagePosts.length) % imagePosts.length);
    }
  };

  const handleDoubleClick = () => {
    const isLiked = post.likes.some(like => like.userId === userDetails.userId);
    if (!isLiked) {
      likePost(post.postId); 
    } else {
      unlikePost(post.postId); 
    }
    setShowHeart(true);
    setTimeout(() => setShowHeart(false), 1000); 
  };

  const handleCommentSubmit = (e) => {
    e.preventDefault();
    if (comment.trim()) {
      commentOnPost(post.postId, comment); 
      setComment(""); 
    }
  };

  const handleImageLoad = () => {
    setIsImageLoaded(true);
  };

return (
  <Card style={{ 
    padding: '12px', 
    width: '100%',
    maxWidth: '600px', 
    height: 'auto', 
    margin: '20px auto', 
    borderBottom: '1px solid #ccc',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)', 
    backgroundColor: "#0A0A0A",
  }}>
    {/* Header Section */}
    <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "10px" }}>
      <Box sx={{ display: "flex", flexDirection: "column" }}>
        <Box sx={{ display: "flex", alignItems: "center" }}>
          <Avatar 
            src={image} 
            alt={username} 
            sx={{ marginRight: "10px" }} 
            onMouseEnter={handlePopoverOpen} 
            onMouseLeave={handlePopoverClose} 
            onClick={() => navigate(`/profile/${userId}`)} 
          />
          <Box onMouseEnter={handlePopoverOpen} onMouseLeave={handlePopoverClose} sx={{ position: 'relative', color: 'white' }}>
            <Typography 
              onClick={() => navigate(`/profile/${userId}`)} 
              variant="subtitle1" 
              sx={{ lineHeight: 1.2, '&:hover': { textDecoration: 'underline', cursor: 'pointer' } }}
            >
              {username}
              {username === 'admin' && (
                <CheckCircleIcon sx={{ color: '#0064e0', fontSize: '1rem', marginLeft: '5px' }} />
              )}
            </Typography>
            <Typography variant="caption" sx={{ fontSize: '0.75rem', color: '#a0a0a0', lineHeight: 1 }}>
              {formatRelativeTime(post.createdAt || post.created)}
            </Typography>
          </Box>
        </Box>
      </Box>
      {userId === userDetails.userId && (
        <IconButton onClick={(event) => handleMenuOpen(event, post)}>
          <MoreVertIcon sx={{ color: "#E0E0E0" }} />
        </IconButton>
      )}
    </Box>
    <Box sx={{ marginBottom: "10px", width: '100%' }}>
        {imagePosts.length > 0 && (
          <Box sx={{ 
            position: 'relative', 
            width: '100%',
            paddingTop: '100%',
            overflow: 'hidden',
            borderRadius: '10px',
          }}>
            {imagePosts[currentImageIndex] && (
              <>
                {imagePosts[currentImageIndex].endsWith('.mp4') ? (
                  checkVolume(volume) === true ? (
                    <video 
                    ref={videoRef}
                    playsInline
                    loop
                    muted
                    onClick={handleVideoClick}
                    style={{
                      position: 'absolute',
                      top: 0,
                      left: 0,
                      width: '100%',
                      height: '100%',
                      objectFit: 'contain',
                    }}
                  >
                    <source src={imagePosts[currentImageIndex]} type="video/mp4" />
                    Your browser does not support the video tag.
                  </video>
                  ) : (
                    <video 
                      ref={videoNoRef}
                      playsInline
                      loop
                      muted
                      onClick={handleVideoClickNoAutoplay}
                      style={{
                      position: 'absolute',
                      top: 0,
                      left: 0,
                      width: '100%',
                      height: '100%',
                      objectFit: 'contain',
                    }}
                  >
                    <source src={imagePosts[currentImageIndex]} type="video/mp4" />
                    Your browser does not support the video tag.
                  </video>
                )) : (
                  <>
                    <img 
                      src={imagePosts[currentImageIndex]} 
                      className="main-image" 
                      alt={`Image ${currentImageIndex + 1}`}
                      onLoad={handleImageLoad}
                      onDoubleClick={handleDoubleClick}
                      style={{ 
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        width: '100%',
                        height: '100%',
                        objectFit: 'cover',
                        display: isImageLoaded ? 'block' : 'none',
                      }} 
                    />
                    {/* Thêm chỉ số ảnh hiện tại */}
                    <Typography
                      sx={{
                        position: 'absolute',
                        top: '10px',
                        right: '10px',
                        color: 'white',
                        backgroundColor: 'rgba(0,0,0,0.5)',
                        padding: '5px 10px',
                        borderRadius: '10px',
                        fontSize: '14px',
                      }}
                    >
                      {currentImageIndex + 1} / {imagePosts.length}
                    </Typography>
                    {imagePosts.length > 1 && (
                      <>
                        <IconButton 
                          onClick={handlePrevImage}
                          disabled={currentImageIndex === 0}
                          sx={{ 
                            position: 'absolute', 
                            left: '10px', 
                            top: '50%', 
                            transform: 'translateY(-50%)',
                            backgroundColor: 'rgba(0,0,0,0.5)',
                            '&:hover': { backgroundColor: 'rgba(0,0,0,0.7)' },
                          }}
                        >
                          <ArrowBackIcon sx={{ color: "#E0E0E0" }} />
                        </IconButton>
                        <IconButton 
                          onClick={handleNextImage}
                          disabled={currentImageIndex === imagePosts.length - 1}
                          sx={{ 
                            position: 'absolute', 
                            right: '10px', 
                            top: '50%', 
                            transform: 'translateY(-50%)',
                            backgroundColor: 'rgba(0,0,0,0.5)',
                            '&:hover': { backgroundColor: 'rgba(0,0,0,0.7)' },
                          }}
                        >
                          <ArrowForwardIcon sx={{ color: "#E0E0E0" }} />
                        </IconButton>
                      </>
                    )}
                  </>
                )}
              </>
            )}
          </Box>
        )}
      </Box>
      <Box sx={{ display: "left", justifyContent: "space-between", marginBottom: "10px" }}>
        <IconButton 
          onClick={() => post.likes.some(like => like.userId === userDetails.userId) ? unlikePost(post.postId) : likePost(post.postId)} 
          sx={{ color: "#E0E0E0" }}
        >
          {post.likes.some(like => like.userId === userDetails.userId) ? (
            <FavoriteIcon sx={{ color: "red" }} />
          ) : (
            <FavoriteBorderIcon sx={{ color: "#a0a0a0" }} />
          )}
        </IconButton>
        <IconButton onClick={handleCommentModalOpen} sx={{ color: "#E0E0E0" }}>
          <ChatBubbleOutlineIcon sx={{ color: "#E0E0E0" }} />
        </IconButton>
        <IconButton onClick={() => {/* Add your share functionality here */}} sx={{ color: "#E0E0E0" }}>
          <BookmarkBorderIcon sx={{ color: "#E0E0E0" }} />
        </IconButton>
      </Box>
      <Typography variant="body2" sx={{ color: "#E0E0E0", marginTop: "5px" }}>
        {post.likes.length} {post.likes.length === 1 ? 'like' : 'likes'}
      </Typography>
      <Typography variant="body1" sx={{ marginBottom: "10px", whiteSpace: 'pre-wrap', wordBreak: 'break-word', color: "#E0E0E0" }}>
        {isContentExpanded ? formatContent(post.content) : formatContent(post.content.slice(0, 200) + (post.content.length > 200 ? '... ' : ''))}
        {post.content.length > 150 && !isContentExpanded && (
          <span onClick={() => setIsContentExpanded(true)} style={{ color: '#E0E0E0', cursor: 'pointer' }}>
            ... more
          </span>
        )}
      </Typography>
      <Typography variant="body2" sx={{ color: "#E0E0E0", marginTop: "5px"}} onClick={handleCommentModalOpen}>
        {post.comments.length} {post.comments.length === 1 ? 'comment' : 'comments'}
      </Typography>
      <Box sx={{ display: "flex", alignItems: "center", marginBottom: "10px", position: 'relative' }}>
        <form onSubmit={handleCommentSubmit} style={{ flexGrow: 1 }}>
          <input 
            type="text" 
            value={comment} 
            onChange={(e) => setComment(e.target.value)} 
            placeholder="Add a comment..." 
            style={{ 
              width: '100%', 
              padding: '10px', 
              borderRadius: '5px', 
              border: '1px solid black', 
              backgroundColor: "black",
              color: "white",
            }} 
          />
        </form>
        <IconButton 
          ref={emojiButtonRef}
          onClick={handleEmojiButtonClick}
          sx={{ color: '#E0E0E0' }}
        >
          😊
        </IconButton>
      </Box>

      {isPickerVisible && (
        <div style={{ 
          position: 'absolute',
          top: `${pickerPosition.top}px`,
          left: `${pickerPosition.left}px`,
          zIndex: 1000,
        }}>
          <Picker 
            data={data} 
            previewPosition="none"
            onEmojiSelect={handleEmojiSelect}
            theme="dark"
          />
        </div>
      )}
      <UserInfoPopup
        user={userId}
        anchorEl={anchorEl}
        open={isPopupOpen}
        onClose={handlePopoverClose}
        onMouseEnter={handlePopoverOpen}
        onMouseLeave={handlePopoverClose}
      />

      <CommentModal
        open={isCommentModalOpen} 
        onClose={handleCommentModalClose}
        comments={post.comments} // Pass the comments to the modal
        onCommentSubmit={commentOnPost} // Pass the comment function
        post={post} // Pass the post 
      />

      <Modal 
        isOpen={isModalOpen} 
        onRequestClose={closeModal} 
        contentLabel="Full Size Image"
      >
        <button onClick={closeModal}>Close</button>
        <img 
          src={fullSizeMedia} 
          alt="Full Size" 
          style={{ 
            width: '100%', 
            height: 'auto' 
          }} 
        />
      </Modal>
    </Card>
  );
};

export default Post;
