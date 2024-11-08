import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Button, Card, CardContent, Divider, TextField, Typography, Snackbar, Alert } from "@mui/material";
import GoogleIcon from "@mui/icons-material/Google";
import GitHubIcon from "@mui/icons-material/GitHub";
import { logIn, isAuthenticated } from "../services/authenticationService";
import { getToken, setToken } from "../services/localStorageService";
import { OAuthConfig } from "../configurations/configuration";
import { OAuthConfigGithub } from "../configurations/configuration";
import './Login.css';
export default function Login() {
  const navigate = useNavigate();

  const handleCloseSnackBar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setSnackBarOpen(false);
  };

  const handleGoogleLogin = () => {
    const callbackUrl = OAuthConfig.redirectUri;
    const authUrl = OAuthConfig.authUri;
    const googleClientId = OAuthConfig.clientId;

    const targetUrl = `${authUrl}?redirect_uri=${encodeURIComponent(
      callbackUrl
    )}&response_type=code&client_id=${googleClientId}&scope=openid%20email%20profile`;

    console.log(targetUrl);
    window.location.href = targetUrl;
  };

  const handleGithubLogin = () => {
    const callbackUrl = OAuthConfigGithub.redirectUri;
    const authUrl = OAuthConfigGithub.authUri;
    const githubClientId = OAuthConfigGithub.clientId;
    
    const targetUrl = `${authUrl}?redirect_uri=${encodeURIComponent(
      callbackUrl
    )}&client_id=${githubClientId}&scope=user%20user:email`;
    
    console.log(targetUrl);
    window.location.href = targetUrl;
  };

  const handleCreateAccount = () => {
    navigate("/register");
  };

  useEffect(() => {
    createTwinklingStars();
    createShootingStars();
    const accessToken = getToken();
    if (accessToken) {
      navigate("/");
    }
  }, [navigate]);

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();
  
    try {
      const response = await logIn(username, password);
      console.log("Response body:", response.data);
      navigate("/");
    } catch (error) {
      const errorResponse = error.response.data;
      setSnackBarMessage(errorResponse.message || 'Login failed');
      setSnackBarOpen(true);
    }
  };

  function createTwinklingStars() {
    const container = document.createElement('div');
    container.className = 'twinkling-stars';
    
    const starCount = 400;
    
    for (let i = 0; i < starCount; i++) {
      const star = document.createElement('div');
      star.className = 'star';
      star.style.left = `${Math.random() * 100}%`;
      star.style.top = `${Math.random() * 100}%`;
      container.appendChild(star);
    }
    
    document.querySelector('.night-sky').appendChild(container);
  }

  function createShootingStars() {
    const container = document.querySelector('.night-sky');
    const shootingStarCount = 1;
  
    for (let i = 0; i < shootingStarCount; i++) {
      const star = document.createElement('div');
      star.className = 'shooting-star';
  
      const startX = Math.random() * 100;
      const startY = Math.random() * 100;
  
      const duration = Math.random() * 2 + 2;
  
      star.style.left = `${startX}%`;
      star.style.top = `${startY}%`;
      star.style.animationDuration = `${duration}s`;
      star.style.animationDelay = `${Math.random() * 5}s`;
  
      container.appendChild(star);
  
      star.addEventListener('animationend', () => {
        container.removeChild(star);
        createShootingStars();
      });
    }
  }

  return (
    <Box className="background">
    <div className="night-sky">
      <div className="twinkling-stars">
      </div>
    </div>

      <Box className="card-container" display="flex" alignItems="center" justifyContent="center" height="100%">
        <Card
          sx={{
            minWidth: 400,
            maxWidth: 500,
            boxShadow: 4,
            borderRadius: 4,
            padding: 4,
            backgroundColor: "#161b22",
            zIndex: 2,
            position: "relative",
          }}
        >
          <CardContent>
            <Typography variant="h4" component="h1" gutterBottom color="white" textAlign="center">
              Welcome to Hoag Page
            </Typography>
            <Box
              component="form"
              display="flex"
              flexDirection="column"
              alignItems="center"
              justifyContent="center"
              width="100%"
              onSubmit={handleSubmit}
            >
              <TextField
                label="Username"
                variant="filled"
                fullWidth
                margin="normal"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                sx={{ backgroundColor: "#21262d", borderRadius: 1, input: { color: 'white' }, label: { color: 'white' } }}
              />
              <TextField
                label="Password"
                type="password"
                variant="filled"
                fullWidth
                margin="normal"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                sx={{ backgroundColor: "#21262d", borderRadius: 1, input: { color: 'white' }, label: { color: 'white' } }}
              />
              <Button
                type="submit"
                variant="contained"
                color="primary"
                size="large"
                fullWidth
                sx={{
                  mt: "15px",
                  mb: "25px",
                  backgroundColor: "#238636",
                  ":hover": { backgroundColor: "#2ea043" },
                }}
              >
                Login
              </Button>
              <Divider sx={{ width: "100%", backgroundColor: "white" }} />
            </Box>
            <Box display="flex" flexDirection="column" width="100%" gap="25px" mt="25px">
              <Button
                type="button"
                variant="contained"
                color="secondary"
                size="large"
                onClick={handleGoogleLogin}
                fullWidth
                sx={{ backgroundColor: "#db4437", ":hover": { backgroundColor: "#c23321" }, gap: "10px" }}
              >
                <GoogleIcon />
                Continue with Google
              </Button>
              <Button
                type="button"
                variant="contained"
                size="large"
                onClick={handleGithubLogin}
                fullWidth
                sx={{ backgroundColor: "#0A0A0A", ":hover": { backgroundColor: "#0A0A0A" }, gap: "10px" }}
              >
                <GitHubIcon />
                Continue with Github
              </Button>
              <Button
                type="button"
                variant="contained"
                color="success"
                size="large"
                fullWidth
                onClick={handleCreateAccount}
                sx={{ backgroundColor: "#42b72a", ":hover": { backgroundColor: "#0A0A0A" } }}
              >
                Create an account
              </Button>
            </Box>
          </CardContent>
        </Card>
      </Box>
    </Box>
  );
}
