import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom"; // Add this import
import friendService from "../../services/friendService";
import { Box, CircularProgress, Typography, TextField, Grid, Divider } from "@mui/material";
import Scene from "../Scene";
import userService from "../../services/userService";
import UserCard from "./UserCard";

export default function Friend() {
  const navigate = useNavigate(); // Add this line
  const [users, setUsers] = useState([]);
  const [friends, setFriends] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentUserId, setCurrentUserId] = useState(null);
  const [selectedUser, setSelectedUser] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      await fetchCurrentUser();
      await fetchUsersAndRelationships();
    };
    fetchData();
  }, []);

  const fetchCurrentUser = async () => {
    try {
      const myInfo = await userService.getMyInfo();
      setCurrentUserId(myInfo.data.result.userId);
    } catch (error) {
      console.error("Error fetching current user:", error);
    }
  };

  const fetchUsersAndRelationships = async () => {
    setLoading(true);
    try {
      const myInfo = await userService.getMyInfo();
      const currentUserId = myInfo.data.result.userId;
      setCurrentUserId(currentUserId);

      const [usersResponse, relationshipResponse] = await Promise.all([
        userService.getAllUser(),
        friendService.getUserRelationShip(currentUserId)
      ]);

      if (usersResponse && usersResponse.data && usersResponse.data.code === 1000 && Array.isArray(usersResponse.data.result)) {
        const allUsers = usersResponse.data.result;
        const friendships = relationshipResponse.friendships || [];
        
        // Get friend IDs from friendships
        const friendIds = friendships.reduce((ids, friendship) => {
          if (friendship.userId1 !== currentUserId) ids.push(friendship.userId1);
          if (friendship.userId2 !== currentUserId) ids.push(friendship.userId2);
          return ids;
        }, []);

        // Classify users
        const friendUsers = allUsers.filter(user => friendIds.includes(user.userId));
        const nonFriendUsers = allUsers.filter(user => 
          !friendIds.includes(user.userId) && 
          user.userId !== currentUserId
        );

        setFriends(friendUsers);
        setUsers(nonFriendUsers);
      } else {
        console.log('Invalid response format or empty result');
        setUsers([]);
        setFriends([]);
      }
    } catch (error) {
      console.error("Error fetching users and relationships:", error);
      setUsers([]);
      setFriends([]);
    } finally {
      setLoading(false);
    }
  };

  const handleUserClick = (user, clickedElement) => {
    if (clickedElement !== 'addRequest' && clickedElement !== 'acceptRequest') {
      navigate(`/profile/${user.userId}`);
    }
  };

  const handleClosePopup = () => {
    setSelectedUser(null);
  };

  if (loading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh", bgcolor: "#1e1e1e" }}>
        <CircularProgress color="primary" />
      </Box>
    );
  }

  return (
    <Scene>
      <Box sx={{ bgcolor: "#1e1e1e", color: "#e0e0e0", minHeight: "100vh", padding: "20px" }}>
        <Typography variant="h4" sx={{ mb: 3, textAlign: "center" }}>User List</Typography>
        <TextField
          fullWidth
          variant="outlined"
          placeholder="Search users"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          sx={{
            mb: 3,
            maxWidth: 600,
            mx: "auto",
            display: "block",
            '& .MuiOutlinedInput-root': {
              color: '#e0e0e0',
              '& fieldset': { borderColor: '#555' },
              '&:hover fieldset': { borderColor: '#777' },
            },
          }}
        />
  
        {friends.length > 0 && (
          <>
            <Typography variant="h5" sx={{ mb: 2, mt: 4 }}>Danh sách bạn bè của bạn:</Typography>
            <Grid container spacing={3} justifyContent="center">
              {friends.map((friend) => (
                <Grid item xs={12} sm={6} md={4} key={friend.userId}>
                  <UserCard 
                    user={friend} 
                    currentUserId={currentUserId} 
                    onRefresh={fetchUsersAndRelationships}
                    isFriend={true}
                    onClick={(clickedElement) => handleUserClick(friend, clickedElement)}
                  />
                </Grid>
              ))}
            </Grid>
            <Divider sx={{ my: 4, bgcolor: '#555' }} />
          </>
        )}
  
        <Typography variant="h5" sx={{ mb: 2, mt: 4 }}>Gợi ý kết bạn:</Typography>
        <Grid container spacing={3} justifyContent="center">
          {users.length > 0 ? (
            users.map((user) => (
              <Grid item xs={12} sm={6} md={4} key={user.userId}>
                <UserCard 
                  user={user} 
                  currentUserId={currentUserId} 
                  onRefresh={fetchUsersAndRelationships} 
                  isFriend={false}
                  onClick={(clickedElement) => handleUserClick(user, clickedElement)}
                />
              </Grid>
            ))
          ) : (
            <Grid item>
              <Typography color="#e0e0e0">Không có gợi ý kết bạn nào.</Typography>
            </Grid>
          )}
        </Grid>
      </Box>
    </Scene>
  );
}