import { useState, useEffect } from "react";
import { getToken } from "../../services/localStorageService";
import { useNavigate, useParams } from "react-router-dom";
import userService from "../../services/userService";
import { Box, Typography, TextField, Button, Avatar, Grid, Paper } from '@mui/material';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import Header from "../../components/header/Header";

export default function ProfileSetting() {
    const [gender, setGender] = useState('Prefer not to say');
    const [showSuggestions, setShowSuggestions] = useState(true);
    const [userDetails, setUserDetails] = useState(null);
    const [image, setImage] = useState('');
    const [snackBarOpen, setSnackBarOpen] = useState(false);
    const [snackBarMessage, setSnackBarMessage] = useState("");
    const [snackType, setSnackType] = useState("error");
    const [username, setUsername] = useState('');
    const [firstname, setFirstname] = useState('');
    const [lastname, setLastname] = useState('');
    const [email, setEmail] = useState('');
    const { userId } = useParams();
    const [avatar, setAvatar] = useState("");
    const navigate = useNavigate();
    const [isChanged, setIsChanged] = useState(false); // Track if any changes have been made


    const themes = createTheme({
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

    const getUserInfo = async () => {
        const response = await userService.getMyInfo();
        if (response.data && response.data.result) {
            setUserDetails(response.data.result);
            fetchImage(response.data.result);
        }
    }

    const fetchImage = async (user) => {
        try {
            if (user && user.images && user.images.length > 0) {
            const imageIds = user.images; 
        
            const imageResponse = await Promise.all(imageIds.map(id => userService.viewImage(id)));
        
            const profileImageObj = imageResponse.find(img => img.data.result.imageType === 'PROFILE');
        
            // Delete the old profile image
            setAvatar(profileImageObj.data.result.imageUrl);
            } else {
            console.log("User does not have images or userDetails is not valid."); // Debugging log
            }
        } catch (error) {
            console.error("Error in updateImageProfile:", error); // Log any errors
        }
    };

    const handleFileChange = async (event) => {
    const files = event.target.files;
    console.log("Selected files:", files); // Debugging log
    if (files.length > 0) {
        try {
        await updateImageProfile(files[0]); // Call to update the profile image
        console.log("Profile image updated successfully."); // Debugging log
        } catch (error) {
        console.error("Error updating profile image:", error); // Log any errors
        }
    } else {
        console.log("No file selected!"); // Debugging log
    }
    };

    const updateImageProfile = async (file) => {
    try {
        const user = userDetails; // Ensure userDetails is correctly accessed
        if (user && user.images && user.images.length > 0) {
        const userId = user.userId; // Get user ID
        console.log("userId:", userId);
        const imageIds = user.images; 
    
        const imageResponse = await Promise.all(imageIds.map(id => userService.viewImage(id)));
    
        const profileImageObj = imageResponse.find(img => img.data.result.imageType === 'PROFILE');
        console.log("profileImageObj:", profileImageObj.data.result);
    
        // Delete the old profile image
        console.log("imageIds:", profileImageObj.data.result);
        await userService.deleteImage(userId, [profileImageObj.data.result.imageId] ); // Pass the correct ID
    
        // Upload the new profile image
        await userService.updateImageProfile(userId, file); // Call to update the profile image
        console.log("New profile image uploaded successfully."); // Debugging log
        } else {
        console.log("User does not have images or userDetails is not valid."); // Debugging log
        }
    } catch (error) {
        console.error("Error in updateImageProfile:", error); // Log any errors
    }
    };

    const showMessage = (message, type = "error") => {
        setSnackType(type);
        setSnackBarMessage(message);
        setSnackBarOpen(true);
    };

    const handleInputChange = (setter) => (event) => {
        setter(event.target.value);
        setIsChanged(true); // Set to true when any input changes
    };

    const handleSubmit = async () => {
        console.log("firstname: {}", firstname);
        console.log("lastname: {}", lastname);
        console.log("email: {}", email);

        const data = {
            firstName: firstname,
            lastName: lastname,
            email: email
        };

        await userService.updateInformation(data);
    };

    useEffect(() => {
        const token = getToken();
        if (!token) {
            navigate("/login");
        } else {
            getUserInfo();
        }
    }, [])

    return (
        <Box sx={{
            backgroundColor: '#0A0A0A',
            height: '100vh'
        }}>
        <Header />
        {userDetails ? (
            <Box sx={{ padding: 4, backgroundColor: '#0A0A0A' }}>
            <Paper elevation={3} sx={{ padding: 3, borderRadius: 2, backgroundColor: "#0A0A0A" }}>
                <Typography variant="h4" gutterBottom align="center" color="white">Edit Profile</Typography>
                <Grid container spacing={2} alignItems="center">
                    <Grid item xs={12} sm={4} container justifyContent="center" alignItems="center" sx={{ backgroundColor: '#1C1C1E', borderRadius: '25px'}}>
                        <Avatar src={avatar || userDetails?.image} alt="User" sx={{ width: 120, height: 120, border: '3px solid white',  marginBottom: '20px' }} />
                        <Box>
                            <Typography sx={{ color: 'white', fontSize: '20px', fontWeight: 'bold', marginRight: '100px', marginBottom: '20px', marginLeft: '15px'}}>{userDetails.username}</Typography>
                            <Typography sx={{ color: 'white', fontSize: '15px', marginLeft: '10px', marginTop: '-20px'  }}>{userDetails.firstName} {userDetails.lastName}</Typography>
                        </Box>
                        <input type="file" accept="image/*" onChange={handleFileChange} style={{ display: 'none' }} id="image-upload" />
                        <label htmlFor="image-upload" style={{ display: 'flex', alignItems: 'center', marginBottom: '45px', marginLeft: '90px' }}>
                            <Button variant="contained" component="span" sx={{ mt: 2 }}>
                                Change Image
                            </Button>
                        </label>
                    </Grid>
                    <Grid item xs={12} sm={8}>
                        <TextField 
                            fullWidth 
                            sx={{ border: '1px solid white', bgcolor: '#1C1C1E', borderRadius: '10px' }} 
                            label="First Name" 
                            value={firstname || userDetails?.firstName} 
                            onChange={handleInputChange(setFirstname)} // Update to use the new handler
                            margin="normal" 
                            InputProps={{ style: { color: 'white' } }} 
                        />
                        <TextField 
                            fullWidth 
                            sx={{ border: '1px solid white', bgcolor: '#1C1C1E', borderRadius: '10px'  }} 
                            label="Last Name" 
                            value={lastname || userDetails?.lastName} 
                            onChange={handleInputChange(setLastname)} // Update to use the new handler
                            margin="normal" 
                            InputProps={{ style: { color: 'white' } }} 
                        />
                        <TextField 
                            fullWidth 
                            sx={{ border: '1px solid white', bgcolor: '#1C1C1E', borderRadius: '10px'  }} 
                            label="Email" 
                            type="email" 
                            value={email || userDetails?.email} 
                            onChange={handleInputChange(setEmail)} // Update to use the new handler
                            margin="normal" 
                            InputProps={{ style: { color: 'white' } }} 
                        />
                    </Grid>
                </Grid>
                <Box sx={{ textAlign: 'center', mt: 3 }}>
                    <Button variant="contained" component="span" sx={{ mt: 2 }} onClick={handleSubmit} disabled={!isChanged}>
                        Save Changes
                    </Button>
                </Box>
            </Paper>
        </Box>
        ) : (
            <Box sx={{
                display: 'flex',
            }}>
                <Typography>Loading...</Typography>
            </Box>
        )}
        
        </Box>
    );
};
