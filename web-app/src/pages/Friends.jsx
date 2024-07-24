// Friend.js
import { useEffect, useState } from "react";
import { getFriends } from "../services/userService";
import { Box, Card, CircularProgress, Typography } from "@mui/material";
import Scene from "./Scene";

export default function Friend() {
  const [friends, setFriends] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchFriends = async () => {
    try {
      const response = await getFriends();
      const data = response.data;
      setFriends(data.result);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching friends:", error.message);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFriends();
  }, []);

  if (loading) {
    return (
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          gap: "30px",
          justifyContent: "center",
          alignItems: "center",
          height: "100vh",
        }}
      >
        <CircularProgress />
        <Typography>Loading friends...</Typography>
      </Box>
    );
  }

  return (
    <Scene>
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      height="100vh"
      bgcolor={"#f0f2f5"}
    >
      <Card
        sx={{
          minWidth: 350,
          maxWidth: 500,
          boxShadow: 3,
          borderRadius: 2,
          padding: 4,
        }}
      >
        <Typography
          sx={{
            fontSize: 18,
            mb: "20px",
          }}
        >
          Friends List
        </Typography>
        {friends.length > 0 ? (
          friends.map((friend, index) => (
            <Box
              key={index}
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%", // Ensure content takes full width
                mb: "10px",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                }}
              >
                {friend.username}
              </Typography>
              <Typography
                sx={{
                  fontSize: 14,
                }}
              >
                {friend.email}
              </Typography>
            </Box>
          ))
        ) : (
          <Typography>No friends found.</Typography>
        )}
      </Card>
    </Box>
    </Scene>
  );
}
