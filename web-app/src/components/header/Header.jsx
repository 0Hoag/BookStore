import React, { useEffect, useState } from 'react';
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
import { FaFacebookMessenger } from "react-icons/fa";
import userService from "../../services/userService";
import { Bell } from 'lucide-react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import notificationService from "../../services/notificationService";
import { Popover, 
  List, ListItem, ListItemText, 
  ListItemAvatar, Avatar, Typography, Button, 
  Dialog, DialogTitle, DialogContent, 
  Table, TableBody, TableRow, TableCell } from '@mui/material';
import { getToken } from '../../services/localStorageService';

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

const NotificationItem = styled(ListItem)(({ theme, isRead }) => ({
  backgroundColor: isRead ? 'transparent' : alpha(theme.palette.primary.main, 0.1),
  '&:hover': {
    backgroundColor: theme.palette.action.hover,
  },
  padding: theme.spacing(2),
  borderBottom: `1px solid ${theme.palette.divider}`,
}));

const AppBarStyled = styled(AppBar)(({ theme }) => ({
  backgroundColor: "#0A0A0A", // Very dark background
  color: "#E0E0E0", // Light text color
}));

export default function Header() {
  const [anchorEl, setAnchorEl] = useState(null);
  const [mobileMoreAnchorEl, setMobileMoreAnchorEl] = useState(null);
  const [activeIcon, setActiveIcon] = useState('home');
  const [userId, setUserId] = useState(null);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [notificationAnchorEl, setNotificationAnchorEl] = useState(null);
  const [optionsDialogOpen, setOptionsDialogOpen] = useState(false);
  const [selectedNotification, setSelectedNotification] = useState(null);
  const navigate = useNavigate();

  const isMenuOpen = Boolean(anchorEl);
  const isMobileMenuOpen = Boolean(mobileMoreAnchorEl);
  const isNotificationsOpen = Boolean(notificationAnchorEl);

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

  // Function to handle marking as read
  const handleMarkAsRead = () => {
    if (selectedNotification) {
      markAsRead(selectedNotification.notificationId);
      setOptionsDialogOpen(false);
    }
  };

  // Function to handle deleting notification
  const handleDeleteNotification = async () => {
    if (selectedNotification) {
      await notificationService.deleteNotification(selectedNotification.notificationId);
    }
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

  const handleNotificationsClick = (event) => {
    setNotificationAnchorEl(event.currentTarget);
    loadAllNotifications(userId);
  };

  const handleNotificationsClose = () => {
    setNotificationAnchorEl(null);
  };

  const handleOptionsClick = (notification) => {
    console.log("notification: {}", notification);
    setSelectedNotification(notification);
    setOptionsDialogOpen(true);
  };

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

  useEffect(() => {
    const getMyInfo = async () => {
      const response = await userService.getMyInfo();
      if (response.data.result.userId) {
        setUserId(response.data.result.userId);
      }
    };
    getMyInfo();
    if (userId) {
      loadAllNotifications(userId);
    }
  }, [userId]);

  const BASE_URL = 'http://localhost:8082';
  const API_PREFIX = '/notification';

  useEffect(() => {
    let stompClient = null;

    const initializeWebSocket = () => {
        const wsUrl = `${BASE_URL}${API_PREFIX}/ws`;
        const socket = new SockJS(wsUrl);
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        client.onConnect = () => {
            client.subscribe(`/topic/messages/${userId}`, message => {
                try {
                    const notification = JSON.parse(message.body);
                    handleNewNotification(notification);
                } catch (error) {
                    console.error('Error processing notification:', error);
                }
            });
        };

        client.activate();
        return client;
    };

    if (userId) {
        stompClient = initializeWebSocket();
    }

    return () => {
        if (stompClient) {
            stompClient.deactivate();
        }
    };
  }, [userId]);

  const fetchUnreadNotifications = async (currentUserId) => {
    try {
      const response = await notificationService.getUnreadNotification(currentUserId);
      if (response?.data?.result) {
        const notificationsList = response.data.result;
        setNotifications(notificationsList);
        const unreadNotifications = notificationsList.filter(notif => !notif.isRead);
        setUnreadCount(unreadNotifications.length);
      }
    } catch (error) {
      console.error('Error fetching unread notifications:', error);
    }
  };

  const updateUnreadCount = (notifs) => {
    const unread = notifs.filter(n => !n.read).length;
    setUnreadCount(unread);
  };
    
  const loadAllNotifications = async (userId) => {
    try {
        const response = await notificationService.getAllNotifications(userId);
        if (response?.data?.result) {
            const notificationsList = response.data.result;

            // Count unread notifications where idRead is false
            const unreadCount = notificationsList.filter(n => !n.idRead).length;
            setUnreadCount(unreadCount); // Update unreadCount based on idRead

            // Sort notifications by timestamp
            const sortedNotifications = notificationsList.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));

            // Separate new notifications (e.g., within the last 24 hours)
            const now = new Date();
            const threshold = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
            const newNotifications = sortedNotifications.filter(n => (now - new Date(n.timestamp)) < threshold);
            const olderNotifications = sortedNotifications.filter(n => (now - new Date(n.timestamp)) >= threshold);

            // Combine new and older notifications
            setNotifications([...newNotifications, ...olderNotifications]);
        }
    } catch (error) {
        console.error('Error loading notifications:', error);
    }
  };

  const formatTimestampVietnamese = (timestamp) => {
    const now = new Date();
    const notificationTime = new Date(timestamp);
    const diffInSeconds = Math.floor((now - notificationTime) / 1000);

    if (diffInSeconds < 60) {
        return `${diffInSeconds} giây trước`;
    } else if (diffInSeconds < 3600) {
        const minutes = Math.floor(diffInSeconds / 60);
        return `${minutes} phút trước`;
    } else if (diffInSeconds < 86400) {
        const hours = Math.floor(diffInSeconds / 3600);
        return `${hours} giờ trước`;
    } else if (diffInSeconds < 604800) { // Less than a week
        const days = Math.floor(diffInSeconds / 86400);
        return `${days} ngày trước`;
    } else {
        const weeks = Math.floor(diffInSeconds / 604800);
        return `${weeks} tuần trước`;
    }
  };

    const handleNewNotification = (notification) => {
      setNotifications(prev => {
          // Kiểm tra xem notification đã tồn tại chưa
          const exists = prev.some(n => n.notificationId === notification.notificationId);
          if (!exists) {
              const newNotifications = [notification, ...prev];
              updateUnreadCount(newNotifications);
              return newNotifications;
          }
          return prev;
      });
  };

  const markAsRead = async (notificationId) => {
    try {
      console.log("Start");
      await notificationService.putMarkAsReadNotification(notificationId);
      console.log("End");
      setNotifications(prev =>
        prev.map(notif =>
          notif.notificationId === notificationId
            ? { ...notif, isRead: true }
            : notif
        )
      );
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const markAllAsRead = async () => {
    try {
      await notificationService.putMarkAsReadAllNotification(userId);
      setNotifications(prev =>
        prev.map(notif => ({ ...notif, isRead: true }))
      );
      setUnreadCount(0);
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  };


  const renderNotificationsPopover = (
    <Popover
      open={isNotificationsOpen}
      anchorEl={notificationAnchorEl}
      onClose={handleNotificationsClose}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'right',
      }}
      transformOrigin={{
        vertical: 'top',
        horizontal: 'right',
      }}
    >
      <Box sx={{ width: 400, maxHeight: 500 }}>
        <Box sx={{ 
          p: 2, 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          borderBottom: '1px solid',
          borderColor: 'divider'
        }}>
          <Typography variant="h6">Notifications</Typography>
          {unreadCount > 0 && (
            <Button 
              onClick={markAllAsRead}
              size="small"
              sx={{ color: 'primary.main' }}
            >
              Mark all as read
            </Button>
          )}
        </Box>
        <List sx={{ maxHeight: 400, overflow: 'auto', p: 0 }}>
          {notifications.length > 0 ? (
            notifications.map((notification) => (
              <NotificationItem
                key={notification.notificationId}
                isRead={notification.isRead}
                onClick={() => handleNotificationClick(notification)}
                sx={{ cursor: 'pointer' }}
              >
                <ListItemAvatar>
                  <Avatar
                    src={notification.bookImage}
                    alt={notification.bookTitle}
                    variant="rounded"
                  />
                </ListItemAvatar>
                <ListItemText
                  primary={
                    <Typography variant="subtitle2" component="div">
                      {notification.message}
                    </Typography>
                  }
                  secondary={
                    <Typography variant="caption" color="text.secondary">
                      {formatTimestampVietnamese(notification.timestamp)}
                    </Typography>
                  }
                />
                <IconButton
                  size="small"
                  onClick={(event) => {
                    event.stopPropagation(); // Prevent triggering the notification click
                    handleOptionsClick(notification); // Function to handle options
                  }}
                >
                  <MoreIcon />
                </IconButton>
              </NotificationItem>
            ))
          ) : (
            <ListItem>
              <ListItemText 
                primary={
                  <Typography align="center" color="text.secondary">
                    No notifications
                  </Typography>
                }
              />
            </ListItem>
          )}
        </List>
      </Box>
    </Popover>
  );

  const renderOptionsDialog = (
    <Dialog open={optionsDialogOpen} onClose={() => setOptionsDialogOpen(false)}>
      <DialogTitle>Notification Options</DialogTitle>
      <DialogContent>
        <Table>
          <TableBody>
            <TableRow>
              <TableCell>
                <Button onClick={handleMarkAsRead}>Mark as Read</Button>
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell>
                <Button onClick={handleDeleteNotification}>Delete Notification</Button>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </DialogContent>
    </Dialog>
  );

  const handleNotificationClick = (notification) => {
    markAsRead(notification.notificationId);
    if (notification.type === 'ADD_TO_CART') {
      navigate('/shoppingcart');
    }
    
    handleNotificationsClose();
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBarStyled position="static">
        <Toolbar sx={{ minHeight: '56px !important', justifyContent: 'space-between' }}>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <IconButton
              size="large"
              edge="start"
              color="inherit"
              aria-label="open drawer"
              sx={{ mr: 2 }}
            >
              
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                {/* <Box
                  component={"img"}
                  style={{
                    width: "50px", // Adjusted size for better visibility
                    height: "50px",
                    borderRadius: 6,
                  }}
                  src="/logo/Hoag.png"
                ></Box> */}
                <IconButton
                  size="large"
                  edge="start"
                  color="inherit"
                  aria-label="open drawer"
                  sx={{ mr: 2 }}
                  onClick={() => navigate('/')} // Navigate to home on click
                >
                    <span style={{ 
                      marginLeft: '8px', 
                      color: '#E0E0E0', 
                      fontSize: '1.5rem', 
                      fontWeight: 'bold', 
                      fontFamily: 'Dancing Script, cursive' // Apply the imported font
                    }}>
                      HOAG
                    </span>
                </IconButton>
              </Box>
            </IconButton>
            {/* <Search>
              <SearchIconWrapper>
                <SearchIcon sx={{ color: "white" }} />
              </SearchIconWrapper>
              <StyledInputBase
                placeholder="Search…"
                inputProps={{ "aria-label": "search" }}
              />
            </Search> */}
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
              <IconButton
                size="large"
                aria-label="show notifications"
                color="inherit"
                onClick={handleNotificationsClick}
              >
                <Badge badgeContent={unreadCount} color="error">
                  <NotificationsIcon sx={{ color: "white" }} />
                </Badge>
              </IconButton>
              <IconButton
                size="large"
                aria-label="show messages"
                color="inherit"
                onClick={handleMessagesClick}
              >
                <Badge color="error">
                  <FaFacebookMessenger style={{ color: "white", fontSize: "1.25rem" }} />
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
      </AppBarStyled>
      {renderMobileMenu}
      {renderMenu}
      {renderNotificationsPopover}
      {renderOptionsDialog}
    </Box>
  );
}