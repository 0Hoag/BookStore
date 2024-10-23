import React from "react";
import { Box, List, ListItem, ListItemButton, ListItemIcon, ListItemText } from "@mui/material";
import HomeIcon from "@mui/icons-material/Home";
import PeopleIcon from "@mui/icons-material/People";
import GroupsIcon from "@mui/icons-material/Groups";
import LibraryBooksIcon from "@mui/icons-material/LibraryBooks";
import StarOutlineIcon from "@mui/icons-material/StarOutline";
import { FaUserCircle } from "react-icons/fa";
import { Link } from "react-router-dom";
import { FaFacebookMessenger } from "react-icons/fa";

function SideMenu() {
  return (
    <Box sx={{ bgcolor: "#0A0A0A", color: "#E0E0E0", height: "100vh" }}> {/* Very dark background */}
      <List>
        <ListItem key={"Profile"} disablePadding>
          <ListItemButton component={Link} to="/profile">
            <ListItemIcon>
              <FaUserCircle style={{ color: "white", fontSize: "2rem" }} /> {/* Larger white icon */}
            </ListItemIcon>
            <ListItemText 
              primary={"Profile"} 
              primaryTypographyProps={{ style: { fontWeight: "bold", color: "white", margin: "10px" } }} 
            />
          </ListItemButton>
        </ListItem>
        <ListItem key={"messages"} disablePadding>
          <ListItemButton component={Link} to="/messages">
            <ListItemIcon>
              <FaFacebookMessenger style={{ color: "white", fontSize: "2rem" }} /> {/* Larger white icon */}
            </ListItemIcon>
            <ListItemText 
              primary={"Messages"} 
              primaryTypographyProps={{ style: { fontWeight: "bold", color: "white", margin: "7px" } }} 
            />
          </ListItemButton>
        </ListItem>
        <ListItem key={"home"} disablePadding>
          <ListItemButton component={Link} to="/">
            <ListItemIcon>
              <HomeIcon sx={{ color: "white", fontSize: "2rem" }} /> {/* Larger white icon */}
            </ListItemIcon>
            <ListItemText 
              primary={"Home"} 
              primaryTypographyProps={{ style: { fontWeight: "bold", color: "white", margin: "7px" } }} 
            />
          </ListItemButton>
        </ListItem>
        <ListItem key={"friends"} disablePadding>
          <ListItemButton component={Link} to="/friends">
            <ListItemIcon>
              <PeopleIcon sx={{ color: "white", fontSize: "2rem" }} /> {/* Larger white icon */}
            </ListItemIcon>
            <ListItemText 
              primary={"Friends"} 
              primaryTypographyProps={{ style: { fontWeight: "bold", color: "white", margin: "7px" } }} 
            />
          </ListItemButton>
        </ListItem>
        <ListItem key={"groups"} disablePadding>
          <ListItemButton component={Link} to="/groups">
            <ListItemIcon>
              <GroupsIcon sx={{ color: "white", fontSize: "2rem" }} /> {/* Larger white icon */}
            </ListItemIcon>
            <ListItemText 
              primary={"Groups"} 
              primaryTypographyProps={{ style: { fontWeight: "bold", color: "white", margin: "7px" } }} 
            />
          </ListItemButton>
        </ListItem>
        <ListItem key={"books"} disablePadding>
          <ListItemButton component={Link} to="/books">
            <ListItemIcon>
              <LibraryBooksIcon sx={{ color: "white", fontSize: "2rem" }} /> {/* Larger white icon */}
            </ListItemIcon>
            <ListItemText 
              primary={"Books"} 
              primaryTypographyProps={{ style: { fontWeight: "bold", color: "white", margin: "7px" } }} 
            />
          </ListItemButton>
        </ListItem>
        <ListItem key={"favorites"} disablePadding>
          <ListItemButton component={Link} to="/favorites">
            <ListItemIcon>
              <StarOutlineIcon sx={{ color: "white", fontSize: "2rem" }} /> {/* Larger white icon */}
            </ListItemIcon>
            <ListItemText 
              primary={"Favorites"} 
              primaryTypographyProps={{ style: { fontWeight: "bold", color: "white", margin: "7px" } }} 
            />
          </ListItemButton>
        </ListItem>
      </List>
    </Box>
  );
}

export default SideMenu;
