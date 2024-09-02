import React, { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getToken } from "../../services/localStorageService";
import Header from "../../components/header/Header";
import {
  Box,
  Typography,
  TextField,
  IconButton,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  Paper,
  Grid,
  createTheme,
  ThemeProvider,
  Button,
  Tabs,
  Tab,
  Divider,
  Menu,
  MenuItem,
  InputAdornment,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from "@mui/material";
import SendIcon from '@mui/icons-material/Send';
import SearchIcon from '@mui/icons-material/Search';
import CallIcon from '@mui/icons-material/Call';
import VideocamIcon from '@mui/icons-material/Videocam';
import InfoIcon from '@mui/icons-material/Info';
import MoreHorizIcon from '@mui/icons-material/MoreHoriz';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import PersonIcon from '@mui/icons-material/Person';
import NotificationsIcon from '@mui/icons-material/Notifications';
import messengerService from "../../services/messengerService";
import userService from "../../services/userService";

const formatTime = (date) => {
  const hours = date.getHours();
  const minutes = date.getMinutes();
  const period = hours >= 12 ? 'chiều' : 'sáng';
  return `${hours % 12 || 12}:${minutes.toString().padStart(2, '0')} ${period}`;
};

const formatFacebookStyle = (timestamp) => {
  const now = new Date();
  const messageDate = new Date(timestamp);
  const diffInSeconds = Math.floor((now - messageDate) / 1000);
  const diffInMinutes = Math.floor(diffInSeconds / 60);
  const diffInHours = Math.floor(diffInMinutes / 60);
  const diffInDays = Math.floor(diffInHours / 24);

  const vietnameseDays = [
    'Chủ nhật', 'Thứ hai', 'Thứ ba', 'Thứ tư', 'Thứ năm', 'Thứ sáu', 'Thứ bảy'
  ];

  if (diffInSeconds < 60) {
    return 'Vừa xong';
  } else if (diffInMinutes < 60) {
    return `${diffInMinutes} phút trước`;
  } else if (diffInHours < 24) {
    return `${diffInHours} giờ trước`;
  } else if (diffInDays === 1) {
    return `Hôm qua ${formatTime(messageDate)}`;
  } else if (diffInDays < 7) {
    return `${vietnameseDays[messageDate.getDay()]} ${formatTime(messageDate)}`;
  } else {
    const day = messageDate.getDate();
    const month = messageDate.getMonth() + 1;
    return `${day}/${month} ${formatTime(messageDate)}`;
  }
};

const shouldShowTimestamp = (currentMsg, prevMsg) => {
  if (!prevMsg) return true;
  const currentTime = new Date(currentMsg.timestamp);
  const prevTime = new Date(prevMsg.timestamp);
  const diffHours = (currentTime - prevTime) / (1000 * 60 * 60);
  return diffHours >= 5;
};

const formatTimestampHeader = (timestamp) => {
  const date = new Date(timestamp);
  const today = new Date();
  const yesterday = new Date(today);
  yesterday.setDate(yesterday.getDate() - 1);

  if (date.toDateString() === today.toDateString()) {
    return `Hôm nay ${formatTime(date)}`;
  } else if (date.toDateString() === yesterday.toDateString()) {
    return `Hôm qua ${formatTime(date)}`;
  } else {
    return `${date.getDate()}/${date.getMonth() + 1} ${formatTime(date)}`;
  }
};

const messengerTheme = createTheme({
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

export default function Messenger() {
  const navigate = useNavigate();
  const [conversations, setConversations] = useState([]);
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [currentUserId, setCurrentUserId] = useState(null);
  const messagesEndRef = useRef(null);
  const [tabValue, setTabValue] = useState(0);
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedMessage, setSelectedMessage] = useState(null);
  const [hoveredMessageId, setHoveredMessageId] = useState(null);
  const [chatPartnerProfile, setChatPartnerProfile] = useState(null);
  const [chatPartnerName, setChatPartnerName] = useState("");

  const getUserConversationsList = async () => {
    try {
      const getMyInfo = await userService.getMyInfo();
      const userId = getMyInfo.data.result.userId;
      console.log("Current User ID:", userId); // Debug log
      setCurrentUserId(userId);
      const response = await messengerService.getUserConversationsList(userId, 10);
      setConversations(response.data.result);
    } catch (error) {
      console.error("Error fetching conversations:", error);
    }
  };

  const getMessagesForConversation = async (conversationId, page = 1) => {
    try {
      const response = await messengerService.getMessageForConversation(conversationId, page);
      const { data, totalPages: total } = response.data.result;
      setMessages(prevMessages => {
        // Combine previous messages with new messages
        const allMessages = [...prevMessages, ...data];
        // Sort messages by timestamp, oldest first
        const sortedMessages = allMessages.sort((a, b) => 
          new Date(a.timestamp) - new Date(b.timestamp)
        );
        return sortedMessages;
      });
      setTotalPages(total);
      setCurrentPage(page);
    } catch (error) {
      console.error("Error fetching messages:", error);
    }
  };

  const sendMessage = async (message) => {
    try {
      await messengerService.sendMessage(message);
    } catch (error) {
      console.error("Error sending message:", error);
    }
  };

  const handleSelectConversation = async (conv) => {
    setSelectedConversation(conv);
    setMessages([]);
    setCurrentPage(1);
    getMessagesForConversation(conv.id, 1);
  
    try {
      const response = await messengerService.getParticipantIds(conv.id);
      const participants = response.data.result;
  
      // Determine chat partner's name
      let partnerName = "Unknown User";
      if (participants && participants.length > 0) {
        const chatPartner = participants.find(p => p.userId !== currentUserId);
        if (chatPartner) {
          const partnerInfo = await userService.getUser(chatPartner.userId);
          partnerName = partnerInfo.data.result.username;
        } else {
          console.log("No chat partner found with a different userId than currentUserId");
        }
      } else {
        console.log("No participants found");
      }
      setChatPartnerName(partnerName);
    } catch (error) {
      console.error("Error fetching participants:", error);
    }
  };

  const loadMoreMessages = () => {
    if (currentPage < totalPages && selectedConversation) {
      getMessagesForConversation(selectedConversation.id, currentPage + 1);
    }
  };

  useEffect(() => {
    const accessToken = getToken();
    if (!accessToken) {
      navigate("/login");
    } else {
      getUserConversationsList();
    }
  }, [navigate]);

  const handleSendMessage = async () => {
    if (message.trim() && selectedConversation) {
      const newMessage = { 
        id: Date.now().toString(), 
        content: message, 
        senderId: currentUserId,
        timestamp: new Date().toISOString()
      };
      setMessages(prevMessages => [...prevMessages, newMessage]);
      setMessage("");
      
      try {
        await sendMessage({
          conversationId: selectedConversation.id,
          content: message,
          senderId: currentUserId
        });
      } catch (error) {
        console.error("Error sending message:", error);
      }
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(scrollToBottom, [messages]);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleMessageMenuOpen = (event, message) => {
    event.preventDefault(); // Prevent default context menu
    setSelectedMessage(message);
    setAnchorEl(event.currentTarget);
  };

  const handleMessageMenuClose = () => {
    setSelectedMessage(null);
    setAnchorEl(null);
  };

  const handleEditMessage = () => {
    // Implement edit functionality
    console.log('Edit message:', selectedMessage);
    handleMessageMenuClose();
  };

  const handleRemoveMessage = () => {
    // Implement remove functionality
    console.log('Remove message:', selectedMessage);
    handleMessageMenuClose();
  };

  const handleRevokeMessage = () => {
    // Implement revoke functionality
    console.log('Revoke message:', selectedMessage);
    handleMessageMenuClose();
  };

  const fetchChatPartnerProfile = async () => {
    if (selectedConversation && selectedConversation.lastMessageSenderId && currentUserId) {
      try {
        const partnerId = selectedConversation.lastMessageSenderId !== currentUserId ? selectedConversation.lastMessageSenderId : null;
        if (partnerId) {
          const partnerInfo = await userService.getUser(partnerId);
          setChatPartnerProfile(partnerInfo.data.result);
          navigate(`/profile/${partnerId}`);
        }
      } catch (error) {
        console.error("Error fetching chat partner profile:", error);
      }
    }
  };

  return (
    <ThemeProvider theme={messengerTheme}>
      <Box sx={{ 
        bgcolor: 'background.default', 
        minHeight: '100vh',
        display: 'flex', 
        flexDirection: 'column'
      }}>
        <Header />
        <Box sx={{ 
          display: 'flex', 
          flexGrow: 1, 
          overflow: 'hidden',
          height: 'calc(100vh - 56px)', // Adjust this value based on your Header height
        }}>
          {/* Left Sidebar */}
          <Box sx={{ width: 360, borderRight: '1px solid #3e4042', display: 'flex', flexDirection: 'column', height: '100%' }}>
            <Box sx={{ p: 2 }}>
              <Typography variant="h6" color="text.primary">Đoạn chat</Typography>
              <TextField
                fullWidth
                variant="outlined"
                placeholder="Tìm kiếm trên Messenger"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <SearchIcon sx={{ color: 'text.secondary' }} />
                    </InputAdornment>
                  ),
                }}
                sx={{ 
                  mt: 2,
                  '& .MuiOutlinedInput-root': {
                    borderRadius: '20px',
                    bgcolor: 'background.paper',
                    '& fieldset': { borderColor: 'transparent' },
                    '&:hover fieldset': { borderColor: 'transparent' },
                    '&.Mui-focused fieldset': { borderColor: 'transparent' },
                  },
                }}
              />
            </Box>
            <Tabs 
              value={tabValue} 
              onChange={handleTabChange} 
              sx={{ 
                borderBottom: '1px solid #3e4042',
                '& .MuiTab-root': { color: 'text.secondary' },
                '& .Mui-selected': { color: 'primary.main' },
              }}
            >
              <Tab label="Hộp thư" />
              <Tab label="Cộng đồng" />
            </Tabs>
            <List sx={{ flexGrow: 1, overflow: 'auto' }}>
              {conversations.map((conv) => (
                <ListItem 
                  key={conv.id}
                  onClick={() => handleSelectConversation(conv)}
                  selected={selectedConversation && selectedConversation.id === conv.id}
                  sx={{ 
                    cursor: 'pointer',
                    '&.Mui-selected': { 
                      bgcolor: 'rgba(45, 136, 255, 0.1)',
                      '&:hover': { bgcolor: 'rgba(45, 136, 255, 0.2)' },
                    },
                  }}
                >
                  <ListItemAvatar>
                    <Avatar src={conv.avatarUrl}>{conv.title[0]}</Avatar>
                  </ListItemAvatar>
                  <ListItemText 
                    primary={conv.title} 
                    secondary={conv.lastMessageContent}
                    primaryTypographyProps={{ color: 'text.primary' }}
                    secondaryTypographyProps={{ 
                      color: 'text.secondary',
                      sx: { 
                        whiteSpace: 'nowrap',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                      }
                    }}
                  />
                </ListItem>
              ))}
            </List>
          </Box>

          {/* Chat Area */}
          <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', height: '100%' }}>
            {selectedConversation ? (
              <>
                <Box sx={{ p: 2, borderBottom: '1px solid #3e4042', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                      <Avatar sx={{ mr: 2 }}>{chatPartnerName ? chatPartnerName[0] : 'U'}</Avatar>
                      <Typography variant="h6" color="text.primary">{chatPartnerName}</Typography>
                    </Box>
                    <Box>
                      <IconButton><CallIcon sx={{ color: 'text.primary' }} /></IconButton>
                      <IconButton><VideocamIcon sx={{ color: 'text.primary' }} /></IconButton>
                      <IconButton><InfoIcon sx={{ color: 'text.primary' }} /></IconButton>
                    </Box>
                  </Box>
                <Box sx={{ flexGrow: 1, overflow: 'auto', p: 2, display: 'flex', flexDirection: 'column' }}>
                  {currentPage < totalPages && (
                    <Button onClick={loadMoreMessages} sx={{ alignSelf: 'center', mb: 2, color: 'primary.main' }}>Load More</Button>
                  )}
                  {messages.map((msg, index) => {
                    const isLastMessage = index === messages.length - 1;
                    const isCurrentUser = msg.senderId === currentUserId;
                    return (
                      <React.Fragment key={msg.id}>
                        {shouldShowTimestamp(msg, messages[index - 1]) && (
                          <Typography 
                            variant="caption" 
                            align="center" 
                            sx={{ 
                              display: 'block', 
                              my: 2, 
                              color: 'text.secondary' 
                            }}
                          >
                            {formatTimestampHeader(msg.timestamp)}
                          </Typography>
                        )}
                        <Box 
                          sx={{
                            display: 'flex', 
                            flexDirection: 'column',
                            alignItems: isCurrentUser ? 'flex-end' : 'flex-start',
                            mb: 1,
                            position: 'relative',
                          }}
                        >
                          <Paper 
                            sx={{ 
                              p: 1, 
                              px: 2,
                              bgcolor: isCurrentUser ? 'primary.main' : 'background.paper',
                              color: isCurrentUser ? 'white' : 'text.primary',
                              borderRadius: '18px',
                              maxWidth: '70%',
                              '&:hover + .message-timestamp': {
                                opacity: 1,
                              },
                            }}
                          >
                            <Typography>{msg.content}</Typography>
                          </Paper>
                          <Typography 
                            variant="caption" 
                            className="message-timestamp"
                            sx={{ 
                              mt: 0.5, 
                              color: 'text.secondary',
                              opacity: isLastMessage ? 1 : 0,
                              transition: 'opacity 0.2s',
                              position: 'absolute',
                              bottom: -20,
                              [isCurrentUser ? 'right' : 'left']: 0,
                              pointerEvents: 'none', // Prevents the timestamp from interfering with hover
                            }}
                          >
                            {formatFacebookStyle(msg.timestamp)}
                          </Typography>
                        </Box>
                      </React.Fragment>
                    );
                  })}
                  <div ref={messagesEndRef} />
                </Box>
                <Box sx={{ p: 2, borderTop: '1px solid #3e4042' }}> 
                  <Grid container spacing={2} alignItems="center">
                    <Grid item xs>
                      <TextField
                        fullWidth
                        variant="outlined"
                        placeholder="Aa"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                        sx={{ 
                          '& .MuiOutlinedInput-root': { 
                            borderRadius: '20px',
                            backgroundColor: 'background.paper',
                            color: 'text.primary',
                            '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.23)' },
                            '&:hover fieldset': { borderColor: 'rgba(255, 255, 255, 0.23)' },
                            '&.Mui-focused fieldset': { borderColor: 'primary.main' },
                          }
                        }}
                      />
                    </Grid>
                    <Grid item>
                      <IconButton 
                        color="primary" 
                        onClick={handleSendMessage}
                        disabled={!message.trim()}
                      >
                        <SendIcon />
                      </IconButton>
                    </Grid>
                  </Grid>
                </Box>
              </>
            ) : (
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
                <Typography variant="h6" color="text.secondary">
                  Select a conversation to start messaging
                </Typography>
              </Box>
            )}
          </Box>

          {/* Right Sidebar */}
          {selectedConversation && (
            <Box sx={{ width: 360, borderLeft: '1px solid #3e4042', p: 2, overflow: 'auto', height: '100%' }}>
              <Box sx={{ p: 2, textAlign: 'center' }}>
                <Avatar 
                  sx={{ width: 80, height: 80, margin: 'auto' }}
                  src={selectedConversation.avatarUrl}
                >
                  {selectedConversation.title[0]}
                </Avatar>
                <Typography variant="h6" color="text.primary" sx={{ mt: 1 }}>
                  {selectedConversation.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Đang hoạt động
                </Typography>
                <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center', gap: 2 }}>
                  <IconButton color="primary" onClick={fetchChatPartnerProfile}>
                    <PersonIcon />
                  </IconButton>
                  <IconButton color="primary"><NotificationsIcon/></IconButton>
                  <IconButton color="primary"><SearchIcon /></IconButton>
                </Box>
              </Box>

              {/* Display chat partner profile if available */}
              {chatPartnerProfile && (
                <Box sx={{ p: 2 }}>
                  <Typography variant="h6" color="text.primary">
                    {chatPartnerProfile.name}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {chatPartnerProfile.email}
                  </Typography>
                  {/* Add more profile details as needed */}
                </Box>
              )}

              <Accordion disableGutters elevation={0} sx={{ bgcolor: 'transparent' }}>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  <Typography>Thông tin về đoạn chat</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <Typography variant="body2" color="text.secondary">
                    Được mã hóa đầu cuối
                  </Typography>
                </AccordionDetails>
              </Accordion>

              <Accordion disableGutters elevation={0} sx={{ bgcolor: 'transparent' }}>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  <Typography>Tùy chỉnh đoạn chat</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  {/* Add customization options here */}
                </AccordionDetails>
              </Accordion>

              <Accordion disableGutters elevation={0} sx={{ bgcolor: 'transparent' }}>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  <Typography>File phương tiện & file</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  {/* Add media and file list here */}
                </AccordionDetails>
              </Accordion>

              <Accordion disableGutters elevation={0} sx={{ bgcolor: 'transparent' }}>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  <Typography>Quyền riêng tư & hỗ trợ</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  {/* Add privacy and support options here */}
                </AccordionDetails>
              </Accordion>
            </Box>
          )}
        </Box>

        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={handleMessageMenuClose}
        >
          <MenuItem onClick={handleEditMessage}>Edit</MenuItem>
          <MenuItem onClick={handleRemoveMessage}>Remove</MenuItem>
          <MenuItem onClick={handleRevokeMessage}>Revoke</MenuItem>
        </Menu>
      </Box>
    </ThemeProvider>
  );
}