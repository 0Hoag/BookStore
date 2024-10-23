import React, { useState, useEffect, useCallback } from 'react';
import { Card, CardContent, Typography, CardMedia, Button, Box, CircularProgress } from '@mui/material';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import CancelIcon from '@mui/icons-material/Cancel';
import CheckIcon from '@mui/icons-material/Check';
import PeopleIcon from '@mui/icons-material/People';
import friendService from '../../services/friendService';
import userService from '../../services/userService';


const UserCard = ({ user, currentUserId, onRefresh, isFriend, onClick }) => {
  const [requestStatus, setRequestStatus] = useState('none');
  const [isLoading, setIsLoading] = useState(false);
  const [requestId, setRequestId] = useState(null);
  const [image, setImage] = useState("");
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  const [snackType, setSnackType] = useState("error");

  const fetchRequestStatus = useCallback(async () => {
    try {
      const { status, requestId } = await friendService.getFriendRequestStatus(currentUserId, user.userId);
      setRequestStatus(status);
      setRequestId(requestId);
    } catch (error) {
      console.error("Error fetching friend request status:", error);
    }
  }, [user.userId, currentUserId]);

  const showMessage = (message, type = "error") => {
    setSnackType(type);
    setSnackBarMessage(message);
    setSnackBarOpen(true);
  };

  const fetchImage = async () => {
    try {
      const users = await userService.getUser(user.userId);
      const imageIds = users.data.result.images;

      const imageResponse = await Promise.all(imageIds.map(id => userService.viewImage(id)));
      const imageObj = imageResponse.find(img => img.data.result.imageType === 'PROFILE');
      setImage(imageObj.data.result.imageUrl);
    }catch (error) {
      showMessage(error.message);

    }
  }

  useEffect(() => {
    fetchRequestStatus();
    fetchImage();
  }, [fetchRequestStatus]);

  const handleAddFriend = async () => {
    setIsLoading(true);
    try {
      const response = await friendService.createFriendRequest({ 
        senderId: currentUserId, 
        receiverId: user.userId 
      });
      setRequestStatus('sent');
      setRequestId(response.requestId);
      console.log("Friend request sent successfully");
      if (onRefresh) onRefresh();
    } catch (error) {
      console.error("Error sending friend request:", error);
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
      console.log("Friend request canceled successfully");
      if (onRefresh) onRefresh();
    } catch (error) {
      console.error("Error canceling friend request:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleAcceptFriendRequest = async () => {
    setIsLoading(true);
    try {
      const status = 'ACCEPTED';
      await friendService.updateFriendStatus(requestId, {condition: status});
      setRequestStatus('friends');
      await friendService.createFriendShip({
        userId1: currentUserId, 
        userId2: user.userId
      });
      console.log("Friend request accepted successfully");
      if (onRefresh) onRefresh();
    } catch (error) {
      console.error("Error accepting friend request:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const renderActionButton = () => {
    if (isLoading) {
      return <CircularProgress size={24} />;
    }

    if (isFriend) {
      return (
        <Button
          variant="outlined"
          startIcon={<PeopleIcon />}
          fullWidth
          disabled
          sx={{
            color: '#4caf50',
            borderColor: '#4caf50',
          }}
        >
          Bạn bè
        </Button>
      );
    }

    switch (requestStatus) {
      case 'sent':
        return (
          <Button
            variant="outlined"
            startIcon={<CancelIcon />}
            fullWidth
            onClick={handleCancelFriendRequest}
            sx={{
              color: '#9e9e9e',
              borderColor: '#9e9e9e',
              '&:hover': {
                backgroundColor: 'rgba(158, 158, 158, 0.1)',
              },
            }}
          >
            Cancel Request
          </Button>
        );
      case 'received':
        return (
          <Button
            variant="outlined"
            startIcon={<CheckIcon />}
            fullWidth
            onClick={handleAcceptFriendRequest}
            sx={{
              color: '#4caf50',
              borderColor: '#4caf50',
              '&:hover': {
                backgroundColor: 'rgba(76, 175, 80, 0.1)',
              },
            }}
          >
            Accept Request
          </Button>
        );
      case 'friends':
        return (
          <Button
            variant="outlined"
            startIcon={<PeopleIcon />}
            fullWidth
            disabled
            sx={{
              color: '#4caf50',
              borderColor: '#4caf50',
            }}
          >
            Friends
          </Button>
        );
      default:
        return (
          <Button
            variant="outlined"
            startIcon={<PersonAddIcon />}
            fullWidth
            onClick={handleAddFriend}
            sx={{
              color: '#4caf50',
              borderColor: '#4caf50',
              '&:hover': {
                backgroundColor: 'rgba(76, 175, 80, 0.1)',
              },
            }}
          >
            Add Friend
          </Button>
        );
    }
  };

  const handleCardClick = (event) => {
    if (!event.target.closest('button')) {
      onClick(user);
    }
  };

  return (
    <Card 
      sx={{ 
        bgcolor: '#2c2c2c', 
        color: '#e0e0e0', 
        cursor: 'pointer',
        '&:hover': {
          bgcolor: '#3c3c3c',
          transition: 'background-color 0.3s'
        }
      }} 
      onClick={handleCardClick}
    >
      <div style={{ position: 'relative', width: '100%', height: '190px' }}>
        <CardMedia
          component="img"
          height="200"
          image={image || "/path/to/default/avatar.png"} // Display the user image or default
          alt={`${user.username}'s avatar`}
          style={{ objectFit: 'cover' }}
        />
      </div>
      <CardContent>
        <Typography gutterBottom variant="h5" component="div">
        {user.firstName} {user.lastName}
        </Typography>
        <Box mt={2}>
          {renderActionButton()}
        </Box>
      </CardContent>
    </Card>
  );
};

export default UserCard;