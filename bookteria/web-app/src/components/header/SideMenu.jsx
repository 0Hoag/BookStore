import React from "react";
import Divider from "@mui/material/Divider";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import Toolbar from "@mui/material/Toolbar";
import HomeIcon from "@mui/icons-material/Home";
import PeopleIcon from "@mui/icons-material/People";
import GroupsIcon from "@mui/icons-material/Groups";
import LibraryBooksIcon from "@mui/icons-material/LibraryBooks"; // Import icon for Books
import StarOutlineIcon from "@mui/icons-material/StarOutline"; // Brighter star icon
import { FaUserCircle } from "react-icons/fa";
import { Link } from "react-router-dom";
import { FaFacebookMessenger } from "react-icons/fa"; // Add this import at the top of your file

function SideMenu() {
  return (
    <>
      <Toolbar />
      <List>
      <ListItem key={"Profile"} disablePadding>
          <ListItemButton component={Link} to="/profile">
            <ListItemIcon>
              <FaUserCircle sx={{ color: "#4CAF50", "&:hover": { color: "#388E3C" } }} />
            </ListItemIcon>
            <ListItemText
              primary={"Profile"}
              primaryTypographyProps={{ style: { fontWeight: "bold" } }}
            />
          </ListItemButton>
        </ListItem>
        <ListItem key={"messages"} disablePadding>
          <ListItemButton component={Link} to="/messages">
            <ListItemIcon>
              <FaFacebookMessenger style={{ color: "#0084FF", fontSize: "1.5rem" }} />
            </ListItemIcon>
            <ListItemText
              primary={"Messages"}
              primaryTypographyProps={{ style: { fontWeight: "bold" } }}
            />
          </ListItemButton>
        </ListItem>
        <ListItem key={"home"} disablePadding>
          <ListItemButton component={Link} to="/">
            <ListItemIcon>
              <HomeIcon sx={{ color: "#4CAF50", "&:hover": { color: "#388E3C", } }} />
            </ListItemIcon>
            <ListItemText
              primary={"Home"}
              primaryTypographyProps={{ style: { fontWeight: "bold" } }}
            />
          </ListItemButton>
        </ListItem>
        <ListItem key={"friends"} disablePadding>
          <ListItemButton component={Link} to="/friends">
            <ListItemIcon>
              <PeopleIcon sx={{ color: "#2196F3", "&:hover": { color: "#1976D2" } }} />
            </ListItemIcon>
            <ListItemText
              primary={"Friends"}
              primaryTypographyProps={{ style: { fontWeight: "bold" } }}
            />
          </ListItemButton>
        </ListItem>
        <ListItem key={"groups"} disablePadding>
          <ListItemButton component={Link} to="/groups">
            <ListItemIcon>
              <GroupsIcon sx={{ color: "#FF5722", "&:hover": { color: "#E64A19" } }} />
            </ListItemIcon>
            <ListItemText
              primary={"Groups"}
              primaryTypographyProps={{ style: { fontWeight: "bold" } }}
            />
          </ListItemButton>
        </ListItem>
        <ListItem key={"books"} disablePadding>
          <ListItemButton component={Link} to="/books">
            <ListItemIcon>
              <LibraryBooksIcon sx={{ color: "#9C27B0", "&:hover": { color: "#7B1FA2" } }} />
            </ListItemIcon>
            <ListItemText
              primary={"Books"}
              primaryTypographyProps={{ style: { fontWeight: "bold" } }}
            />
          </ListItemButton>
        </ListItem>
        <ListItem key={"favorites"} disablePadding>
          <ListItemButton component={Link} to="/favorites">
            <ListItemIcon>
              <StarOutlineIcon sx={{ color: "#FFC107", "&:hover": { color: "#FFB300" } }} />
            </ListItemIcon>
            <ListItemText
              primary={"Favorites"}
              primaryTypographyProps={{ style: { fontWeight: "bold" } }}
            />
          </ListItemButton>
        </ListItem>
      </List>
      <Divider />
    </>
  );
}

export default SideMenu;
