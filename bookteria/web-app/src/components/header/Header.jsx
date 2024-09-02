import * as React from "react";
import { styled, alpha } from "@mui/material/styles";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import InputBase from "@mui/material/InputBase";
import Badge from "@mui/material/Badge";
import MenuItem from "@mui/material/MenuItem";
import Menu from "@mui/material/Menu";
import SearchIcon from "@mui/icons-material/Search";
import AccountCircle from "@mui/icons-material/AccountCircle";
import MailIcon from "@mui/icons-material/Mail";
import NotificationsIcon from "@mui/icons-material/Notifications";
import MoreIcon from "@mui/icons-material/MoreVert";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import HomeIcon from '@mui/icons-material/Home';
import GroupIcon from '@mui/icons-material/Group';
import OndemandVideoIcon from '@mui/icons-material/OndemandVideo';
import StorefrontIcon from '@mui/icons-material/Storefront';
import { logOut } from "../../services/authenticationService";
import { useNavigate } from "react-router-dom";
import { FaFacebookMessenger } from "react-icons/fa"; // Add this import at the top of your file


const Search = styled('div')(({ theme }) => ({
  position: 'relative',
  borderRadius: theme.shape.borderRadius,
  backgroundColor: theme.palette.grey[800],
  '&:hover': {
    backgroundColor: theme.palette.grey[700],
  },
  marginRight: theme.spacing(2),
  marginLeft: 0,
  width: '100%',
  [theme.breakpoints.up('sm')]: {
    marginLeft: theme.spacing(3),
    width: 'auto',
  },
}));

const SearchIconWrapper = styled('div')(({ theme }) => ({
  padding: theme.spacing(0, 2),
  height: '100%',
  position: 'absolute',
  pointerEvents: 'none',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
}));

const StyledInputBase = styled(InputBase)(({ theme }) => ({
  color: "inherit",
  "& .MuiInputBase-input": {
    padding: theme.spacing(1, 1, 1, 0),
    paddingLeft: `calc(1em + ${theme.spacing(4)})`,
    transition: theme.transitions.create("width"),
    width: "100%",
    [theme.breakpoints.up("md")]: {
      width: "20ch",
    },
  },
}));

const AppIconButton = styled(IconButton)(({ theme, active }) => ({
  color: active ? theme.palette.primary.main : 'white',
  '&:hover': {
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
  },
  borderBottom: active ? `2px solid ${theme.palette.primary.main}` : 'none',
  borderRadius: 0,
  padding: theme.spacing(1),
  margin: theme.spacing(0, 1),
}));

export default function Header() {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [mobileMoreAnchorEl, setMobileMoreAnchorEl] = React.useState(null);
  const [activeIcon, setActiveIcon] = React.useState('home');
  const navigate = useNavigate();

  const isMenuOpen = Boolean(anchorEl);
  const isMobileMenuOpen = Boolean(mobileMoreAnchorEl);

  const appIcons = [
    { name: 'home', icon: <HomeIcon />, label: 'Home', path: '/' },
    { name: 'friends', icon: <GroupIcon />, label: 'Friends', path: '/friends' },
    { name: 'watch', icon: <OndemandVideoIcon />, label: 'Watch', path: '/watch' },
    { name: 'marketplace', icon: <StorefrontIcon />, label: 'Marketplace', path: '/marketplace' },
  ];

  const handleProfileMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMobileMenuClose = () => {
    setMobileMoreAnchorEl(null);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    handleMobileMenuClose();
  };

  const handleMobileMenuOpen = (event) => {
    setMobileMoreAnchorEl(event.currentTarget);
  };

  const handleLogout = () => {
    handleMenuClose();
    logOut();
    window.location.href = "/login";
  };

  const handleIconClick = (path) => {
    navigate(path);
    setActiveIcon(path);
  };

  const handleProfileClick = () => {
    handleMenuClose();
    navigate("/profile");
  };

  const handleSettingsClick = () => {
    handleMenuClose();
    navigate("/settings");
  };

  const handleShoppingCartClick = () => {
    handleMobileMenuClose();
    navigate("/shoppingcart");
  };

  const handleMessagesClick = () => {
    handleMenuClose();
    navigate("/messages");
  };

  const handleMyOrdersClick = () => {
    handleMenuClose();
    navigate("/myOrders");
  }

  const menuId = "primary-search-account-menu";
  const renderMenu = (
    <Menu
      anchorEl={anchorEl}
      anchorOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      id={menuId}
      keepMounted
      transformOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      open={isMenuOpen}
      onClose={handleMenuClose}
    >
      <MenuItem onClick={handleProfileClick}>Profile</MenuItem>
      <MenuItem onClick={handleMyOrdersClick}>My Orders</MenuItem>
      <MenuItem onClick={handleSettingsClick}>Settings</MenuItem>
      <MenuItem onClick={handleLogout}>Log Out</MenuItem>
    </Menu>
  );

  const mobileMenuId = "primary-search-account-menu-mobile";
  const renderMobileMenu = (
    <Menu
      anchorEl={mobileMoreAnchorEl}
      anchorOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      id={mobileMenuId}
      keepMounted
      transformOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      open={isMobileMenuOpen}
      onClose={handleMobileMenuClose}
    >
      <MenuItem onClick={handleMessagesClick}>
        <IconButton size="large" aria-label="show 2 new mails" color="inherit">
          <Badge badgeContent={2} color="error">
            <MailIcon sx={{ color: "white" }} />
          </Badge>
        </IconButton>
        <p>Messages</p>
      </MenuItem>
      <MenuItem>
        <IconButton
          size="large"
          aria-label="show 4 new notifications"
          color="inherit"
        >
          <Badge badgeContent={4} color="error">
            <NotificationsIcon sx={{ color: "white" }} />
          </Badge>
        </IconButton>
        <p>Notifications</p>
      </MenuItem>
      <MenuItem onClick={handleProfileMenuOpen}>
        <IconButton
          size="large"
          aria-label="account of current user"
          aria-controls="primary-search-account-menu"
          aria-haspopup="true"
          color="inherit"
        >
          <AccountCircle sx={{ color: "white" }} />
        </IconButton>
        <p>Profile</p>
      </MenuItem>
      <MenuItem onClick={handleShoppingCartClick}>
        <IconButton
          size="large"
          aria-label="show shopping cart"
          color="inherit"
        >
          <ShoppingCartIcon sx={{ color: "white" }} />
        </IconButton>
        <p>Shopping Cart</p>
      </MenuItem>
    </Menu>
  );

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static" sx={{ backgroundColor: "#333", height: '56px' }}>
        <Toolbar sx={{ minHeight: '56px !important', justifyContent: 'space-between' }}>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <IconButton
              size="large"
              edge="start"
              color="inherit"
              aria-label="open drawer"
              sx={{ mr: 2 }}
            >
              <Box
                component={"img"}
                style={{
                  width: "35px",
                  height: "35px",
                  borderRadius: 6,
                }}
                src="/logo/Hoag.png"
              ></Box>
            </IconButton>
            <Search>
              <SearchIconWrapper>
                <SearchIcon sx={{ color: "white" }} />
              </SearchIconWrapper>
              <StyledInputBase
                placeholder="Search…"
                inputProps={{ "aria-label": "search" }}
              />
            </Search>
          </Box>

          <Box sx={{ display: { xs: 'none', md: 'flex' }, justifyContent: 'center' }}>
            {appIcons.map((item) => (
              <AppIconButton
                key={item.name}
                active={activeIcon === item.path}
                onClick={() => handleIconClick(item.path)}
                aria-label={item.label}
              >
                {item.icon}
              </AppIconButton>
            ))}
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Box sx={{ display: { xs: "none", md: "flex" } }}>
              <IconButton
                size="large"
                aria-label="show shopping cart"
                color="inherit"
                onClick={handleShoppingCartClick}
              >
                <ShoppingCartIcon sx={{ color: "white" }} />
              </IconButton>
              {/* New Messages icon */}
              <IconButton
                size="large"
                aria-label="show 17 new notifications"
                color="inherit"
              >
                <Badge badgeContent={17} color="error">
                  <NotificationsIcon sx={{ color: "white" }} />
                </Badge>
              </IconButton>
              <IconButton
                size="large"
                aria-label="show messages"
                color="inherit"
                onClick={handleMessagesClick}
              >
                <Badge badgeContent={4} color="error">
                  <FaFacebookMessenger sx={{ color: "white", fontSize: "1.0rem" }} />
                </Badge>
              </IconButton>
              <IconButton
                size="large"
                edge="end"
                aria-label="account of current user"
                aria-controls={menuId}
                aria-haspopup="true"
                onClick={handleProfileMenuOpen}
                color="inherit"
              >
                <AccountCircle sx={{ color: "white" }} />
              </IconButton>
            </Box>
            <Box sx={{ display: { xs: "flex", md: "none" } }}>
              <IconButton
                size="large"
                aria-label="show more"
                aria-controls={mobileMenuId}
                aria-haspopup="true"
                onClick={handleMobileMenuOpen}
                color="inherit"
              >
                <MoreIcon sx={{ color: "white" }} />
              </IconButton>
            </Box>
          </Box>
        </Toolbar>
      </AppBar>
      {renderMobileMenu}
      {renderMenu}
    </Box>
  );
}