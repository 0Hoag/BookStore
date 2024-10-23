import { Card, CardContent, Typography, Avatar, Button } from '@mui/material';
import { Link } from 'react-router-dom';
import { Popper } from '@mui/material';
import React, { useEffect, useState } from 'react';
import userService from '../services/userService';

const UserInfoPopup = ({ user, anchorEl, open, onClose, onMouseEnter, onMouseLeave }) => {
  const [image, setImage] = useState("");
  const [userInfo, setUserInfo] = useState("");

  useEffect(() => {
    if (user != null) {
      fetchImage(user);
    }
  }, [user]); // Run when user changes

  const fetchImage = async (user) => {
    try {
      const userResponse = await userService.getUser(user);
      setUserInfo(userResponse.data.result);

      if (userResponse && userResponse.data && userResponse.data.result) {
        
        const imageIds = userResponse.data.result.images;

        const imageResponse = await Promise.all(imageIds.map(id => userService.viewImage(id)));
        
        const profileImageObj = imageResponse.find(img => img.data.result.imageType === 'PROFILE');

        setImage(profileImageObj.data.result.imageUrl);
      } else {
        console.log("User does not have images or userDetails is not valid."); 
      }
    } catch (error) {
        console.error("Error in updateImageProfile:", error); // Log any errors
    }
  };

  if (!user) {
    return null; // Don't render anything if user is null
  }

  return (
    <Popper
      open={open}
      anchorEl={anchorEl}
      placement="bottom-start"
      onMouseEnter={onMouseEnter}
      onMouseLeave={onMouseLeave}
      sx={{ bgcolor: "#0A0A0A" }} // Set background color for Popper
      modifiers={[
        {
          name: 'offset',
          options: {
            offset: [0, 5],
          },
        },
      ]}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'center',
      }}
      transformOrigin={{
        vertical: 'top',
        horizontal: 'center',
      }}
    >
      <Card sx={{ padding: 2, maxWidth: 300 }}>
        <CardContent>
          <Avatar src={image} alt={userInfo.username} sx={{ width: 60, height: 60, mb: 2 }} />
          <Typography variant="h6">{userInfo.username}</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            {userInfo.bio || 'Không có mô tả.'}
          </Typography>
          <Button component={Link} to={`/profile/${userInfo.userId}`} variant="contained" sx={{ bgcolor: "#0A0A0A", color: "#E0E0E0" }} fullWidth>
            Xem trang cá nhân
          </Button>
        </CardContent>
      </Card>
    </Popper>
  );
};

export default UserInfoPopup;