import React from 'react';
import { Card, CardContent, Typography, Avatar, Button } from '@mui/material';
import { Link } from 'react-router-dom';
import { Popper } from '@mui/material';

const UserInfoPopup = ({ user, anchorEl, open, onClose, onMouseEnter, onMouseLeave }) => {
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
      modifiers={[
        {
          name: 'offset',
          options: {
            offset: [0, 5],
          },
        },
      ]}
    >
      <Card sx={{ padding: 2, maxWidth: 300 }}>
        <CardContent>
          <Avatar src={user.avatarUrl} alt={user.username} sx={{ width: 60, height: 60, mb: 2 }} />
          <Typography variant="h6">{user.username}</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            {user.bio || 'Không có mô tả.'}
          </Typography>
          <Button component={Link} to={`/profile/${user.userId}`} variant="contained" color="primary" fullWidth>
            Xem trang cá nhân
          </Button>
        </CardContent>
      </Card>
    </Popper>
  );
};

export default UserInfoPopup;