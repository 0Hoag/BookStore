import React, { ChangeEvent ,useRef ,useState, useEffect } from "react";
import {
  Box,
  Button,
  Card,
  CircularProgress,
  Snackbar,
  TextField,
  Typography,
  createTheme,
  ThemeProvider,
  
} from "@mui/material";
import { Alert } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { getToken } from "../services/localStorageService";
import userService from '../services/userService';
import profileService from "../services/profileService";
import Header from "../components/header/Header";


export default function Settings() {
  const navigate = useNavigate();
  const [userDetails, setUserDetails] = useState(null);
  const [editing, setEditing] = useState(false);
  const [password, setPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  const [snackType, setSnackType] = useState("error");
  const [city, setCity] = useState("");
  const [username, setUsername] = useState("");
  const [showUploadMessage, setShowUploadMessage] = useState(false); // New state for upload message
  const [file, setFile] = useState();
  const [imageOld, setImageOld] = useState("");

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
  
  const handleCloseSnackBar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setSnackBarOpen(false);
  };

  const fileInputRef = useRef(null);

  const showError = (message) => {
    setSnackType("error");
    setSnackBarMessage(message);
    setSnackBarOpen(true);
  };

  const showSuccess = (message) => {
    setSnackType("success");
    setSnackBarMessage(message);
    setSnackBarOpen(true);
  };

  const handleFileChange = async (e) => { // Make the function async
    const user = await userService.getMyInfo();
    const imageId = user.data.result.image;
    console.log("imageIdOld: {}", imageId);
    
    const file = e.target.files[0]; // Get the selected file
    console.log("File: {}", file);
    const response = await userService.updateImage(file);

    if (imageId != "null") {
      userService.deleteImage(imageOld);
    }

    console.log("response: {}", response);
  };

  const getUserDetails = async () => {
    try {
      const response = await userService.getMyInfo();
      const userInfo = response.data.result;
      setImageOld(userInfo.image);


      setUserDetails(userInfo);

      // Kiểm tra nếu người dùng chưa có mật khẩu thì không cho phép thay đổi mật khẩu
      if (userInfo.noPassword) {
        showError("You need to set a password before changing it.");
        setEditing(false); // Đóng chế độ chỉnh sửa
      }
    } catch (error) {
      showError(error.message);
    }
  };

  const handleUpdateInfo = async () => {
    const profile = {
      firstName: userDetails.firstName,
      lastName: userDetails.lastName,
      dob: userDetails.dob,
      city: city
    }

    const userData = {
      firstName: userDetails.firstName,
      lastName: userDetails.lastName,
      email: userDetails.email, // Added email field
    }

    try {
      const user = await userService.updateInformation(userData);
      console.log("User: ", user.data.result.userId);
      const userId = await profileService.getUserByProfile(user.data.result.userId);
      console.log("profileId: ", userId.data.result.id);
      await profileService.updateProfile(userId.data.result.id, profile);
      showError("You need to set a password before changing it.");
      setEditing(false); // Đóng chế độ chỉnh sửa
    }catch (error) {

    }
  };

  const updatePassword = async (event) => {
    event.preventDefault();

    if (!newPassword || !confirmPassword) {
      showError("Please enter both new password and confirm password");
      return;
    }

    if (newPassword.length < 6) {
      showError("New password must be at least 6 characters long");
      return;
    }

    if (newPassword !== confirmPassword) {
      showError("New password and confirm password do not match");
      return;
    }

    try {
      // await verifyPassword(password);
      const response = await userService.changePassword(password, newPassword);
      if (response && response.message) {
        showSuccess(response.message);
        setPassword("");
        setNewPassword("");
        setConfirmPassword("");
      } else {
        showError("Failed to change password. Please try again.");
      }
    } catch (error) {
      showError(error.response?.data?.message || error.message);
    }
  };

  useEffect(() => {
    const accessToken = getToken();
    if (!accessToken) {
      navigate("/login");
    } else {
      getUserDetails();
    }
  }, [navigate]);

  if (!userDetails) {
    return (
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          gap: "30px",
          justifyContent: "center",
          alignItems: "center",
          height: "100vh",
          bgcolor: "#121212", // Dark background
          color: "#e0e0e0", // Light text color
        }}
      >
        <CircularProgress color="inherit" />
        <Typography color="#e0e0e0">Loading ...</Typography>
      </Box>
    );
  }

  return (
    <ThemeProvider theme={darkTheme}>
      <Header />
      <Snackbar
        open={snackBarOpen}
        autoHideDuration={6000}
        onClose={handleCloseSnackBar}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          onClose={handleCloseSnackBar}
          severity={snackType}
          variant="filled"
          sx={{ width: "100%" }}
        >
          {snackBarMessage}
        </Alert>
      </Snackbar>
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        height="100vh"
        bgcolor="#1e1e1e" // Dark background
        color="#e0e0e0" // Light text color
      >
        <Card
          sx={{
            width: "90%",
            maxWidth: 800,
            boxShadow: 3,
            borderRadius: 2,
            padding: 4,
            bgcolor: "#1e1e1e", // Slightly lighter dark grey for the card
            color: "#e0e0e0", // Light text color
          }}
        >
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: "flex-start",
              width: "100%",
              gap: "10px",
            }}
          >
            <Typography
              sx={{
                fontSize: 18,
                mb: "40px",
                color: "#b0b0b0", // Lighter gray text color for email
              }}
            >
              Settings for {userDetails.username}
            </Typography>
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                  color: "#b0b0b0", // Lighter gray text color for email
                }}
              >
                User Id
              </Typography>
              <Typography
                sx={{
                  fontSize: 14,
                  color: "#b0b0b0", // Lighter gray text color for email
                }}
              >
                {userDetails.userId}
              </Typography>
            </Box>
            
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                  color: "#b0b0b0", // Lighter gray text color for email
                }}
              >
                Email
              </Typography>
              {editing ? (
                <TextField
                  value={userDetails.email} // Bind the email input to userDetails.email
                  onChange={(e) =>
                    setUserDetails({
                      ...userDetails,
                      email: e.target.value, // Update email in userDetails
                    })
                  }
                />
              ) : (
                <Typography>{userDetails.email}</Typography>
              )}
            </Box>

            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                  color: "#b0b0b0", // Lighter gray text color for email

                }}
              >
                First Name
              </Typography>
              {editing ? (
                <TextField
                  value={userDetails.firstName}
                  onChange={(e) =>
                    setUserDetails({
                      ...userDetails,
                      firstName: e.target.value,
                    })
                  }
                />
              ) : (
                <Typography>{userDetails.firstName}</Typography>
              )}
            </Box>
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                  color: "#b0b0b0", // Lighter gray text color for email
                }}
              >
                Last Name
              </Typography>
              {editing ? (
                <TextField
                  value={userDetails.lastName}
                  onChange={(e) =>
                    setUserDetails({
                      ...userDetails,
                      lastName: e.target.value,
                    })
                  }
                />
              ) : (
                <Typography>{userDetails.lastName}</Typography>
              )}
            </Box>
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                  color: "#b0b0b0", // Lighter gray text color for email
                }}
              >
                Date of birth
              </Typography>
              {editing ? (
                <TextField
                  value={userDetails.dob}
                  onChange={(e) =>
                    setUserDetails({
                      ...userDetails,
                      dob: e.target.value,
                    })
                  }
                />
              ) : (
                <Typography>{userDetails.dob}</Typography>
              )}
            </Box>
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                  color: "#b0b0b0",
                }}
              >
                City
              </Typography>
              {editing ? (
                <TextField
                  value={city}
                  onChange={(e) => setCity(e.target.value)} // Handle city input
                />
              ) : (
                <Typography>{userDetails.city}</Typography>
              )}
            </Box>
            {editing ? (
              <Button
                onClick={handleUpdateInfo}
                variant="contained"
                color="primary"
                size="large"
                sx={{ mt: 2 }}
              >
                Save Changes
              </Button>
            ) : (
              <Button
                onClick={() => setEditing(true)}
                variant="outlined"
                color="primary"
                size="large"
                sx={{ mt: 2 }}
              >
                Edit Profile
              </Button>
            )}
            {!userDetails.noPassword && (
              <Box
                component="form"
                onSubmit={updatePassword}
                sx={{
                  display: "flex",
                  flexDirection: "column",
                  gap: "10px",
                  width: "100%",
                  mt: 3,
                }}
              >
                <Typography>Change Password</Typography>
                <TextField
                  label="Current Password"
                  type="password"
                  variant="outlined"
                  fullWidth
                  margin="normal"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
                <TextField
                  label="New Password"
                  type="password"
                  variant="outlined"
                  fullWidth
                  margin="normal"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                />
                <TextField
                  label="Confirm New Password"
                  type="password"
                  variant="outlined"
                  fullWidth
                  margin="normal"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                />
                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  size="large"
                  fullWidth
                >
                  Change Password
                </Button>
              </Box>
            )}
                <Box
          component="form"
          sx={{
            display: "flex",
            flexDirection: "column",
            gap: "10px",
            width: "100%",
            mt: 3,
          }}
        >
          <Typography>Change Avatar</Typography>
          <Button
            variant="contained"
            color="primary"
            size="large"
            fullWidth
            onClick={() => fileInputRef.current.click()} // Trigger file input click
          >
            Upload Avatar
          </Button>
          <input
            type="file"
            ref={fileInputRef} // Reference to the file input
            onChange={handleFileChange} // Handle file change
            style={{ display: 'none' }} // Hide the input
          />

        </Box>
          </Box>
        </Card>
      </Box>
    </ThemeProvider>
  );
}

