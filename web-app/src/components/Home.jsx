// Home.js
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getToken } from "../services/localStorageService";
import { getMyInfo, createPassword } from "../services/userService";
import Header from "./header/Header";
import {
  Alert,
  Box,
  Button,
  Card,
  CircularProgress,
  Snackbar,
  TextField,
  Typography,
} from "@mui/material";
import Scene from "../pages/Scene";

export default function Home() {
  const navigate = useNavigate();
  const [userDetails, setUserDetails] = useState({});
  const [password, setPassword] = useState("");
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  const [snackType, setSnackType] = useState("error");

  const handleCloseSnackBar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setSnackBarOpen(false);
  };

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

  const getUserDetails = async () => {
    try {
      const response = await getMyInfo();
      const data = response.data;
      setUserDetails(data.result);
      console.log("response:", response.data);
    } catch (error) {
      showError(error.message);
    }
  };

  const addPassword = async (event) => {
    event.preventDefault();
    try {
      const response = await createPassword(password);
      const data = response.data;
      if (data.code != 1000) throw new Error(data.message);
      await getUserDetails();
      showSuccess(data.message);
    } catch (error) {
      if (error.response && error.response.data) {
        showError(error.response.data.message); // display message form backend
      } else {
        showError(error.message); // display message of JS
      }
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

  return (
    <Scene>
      <Header/>
      <Snackbar
        open={snackBarOpen}
        onClose={handleCloseSnackBar}
        autoHideDuration={6000}
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
      {userDetails ? (
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
                }}
              >
                Welcome back to HoagTeria, {userDetails.username}!
              </Typography>
              <Box
                sx={{
                  display: "flex",
                  flexDirection: "row",
                  justifyContent: "space-between",
                  alignItems: "flex-start",
                  width: "100%", // Ensure content takes full width
                }}
              >
                <Typography
                  sx={{
                    fontSize: 14,
                    fontWeight: 600,
                  }}
                >
                  User Id
                </Typography>
                <Typography
                  sx={{
                    fontSize: 14,
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
                  width: "100%", // Ensure content takes full width
                }}
              >
                <Typography
                  sx={{
                    fontSize: 14,
                    fontWeight: 600,
                  }}
                >
                  First Name
                </Typography>
                <Typography
                  sx={{
                    fontSize: 14,
                  }}
                >
                  {userDetails.firstName}
                </Typography>
              </Box>
              <Box
                sx={{
                  display: "flex",
                  flexDirection: "row",
                  justifyContent: "space-between",
                  alignItems: "flex-start",
                  width: "100%", // Ensure content takes full width
                }}
              >
                <Typography
                  sx={{
                    fontSize: 14,
                    fontWeight: 600,
                  }}
                >
                  Last Name
                </Typography>
                <Typography
                  sx={{
                    fontSize: 14,
                  }}
                >
                  {userDetails.lastName}
                </Typography>
              </Box>
              <Box
                sx={{
                  display: "flex",
                  flexDirection: "row",
                  justifyContent: "space-between",
                  alignItems: "flex-start",
                  width: "100%", // Ensure content takes full width
                }}
              >
                <Typography
                  sx={{
                    fontSize: 14,
                    fontWeight: 600,
                  }}
                >
                  Date of birth
                </Typography>
                <Typography
                  sx={{
                    fontSize: 14,
                  }}
                >
                  {userDetails.dob}
                </Typography>
              </Box>
              {userDetails.noPassword && (
                <Box
                  component="form"
                  onSubmit={addPassword}
                  sx={{
                    display: "flex",
                    flexDirection: "column",
                    gap: "10px",
                    width: "100%",
                  }}
                >
                  <Typography>Do you want to create a password?</Typography>
                  <TextField
                    label="Password"
                    type="password"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                  />
                  <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    size="large"
                    fullWidth
                  >
                    Create password
                  </Button>
                </Box>
              )}
            </Box>
          </Card>
        </Box>
      ) : (
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
          <Typography>Loading ...</Typography>
        </Box>
      )}
    </Scene>
  );
}
