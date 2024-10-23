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
  Button,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from "@mui/material";
import SendIcon from '@mui/icons-material/Send';
import CallIcon from '@mui/icons-material/Call';
import InfoIcon from '@mui/icons-material/Info';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
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
  const [chatPartnerProfile, setChatPartnerProfile] = useState(null);
  const [chatPartnerName, setChatPartnerName] = useState("");
  const [image, setImage] = useState("");

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
      console.log("conv:", conv);
      // Determine chat partner's name
      let partnerName = "Unknown User";
      if (participants && participants.length > 0) {
        const chatPartner = participants.find(p => p.userId !== currentUserId);
        if (chatPartner) {
          const partnerInfo = await userService.getUser(chatPartner.userId);
          const viewImage = await userService.viewImage(partnerInfo.data.result.image);
          if (viewImage.data && viewImage.data.result && viewImage.data.result.image_data) {
            const resolvedImage = `data:image/jpeg;base64,${viewImage.data.result.image_data}`;
            setImage(resolvedImage);
        } else {
            setImage("/path/to/default/avatar.png");
        }
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

  const fetchChatPartnerProfile = async () => {
    if (selectedConversation && currentUserId) {
      try {
        const participants = await messengerService.getParticipantIds(selectedConversation.id);
        const partnerId = participants.data.result.find(p => p.userId !== currentUserId)?.userId;
        
        if (partnerId) {
          const partnerInfo = await userService.getUser(partnerId);
          setChatPartnerProfile(partnerInfo.data.result);
        }
      } catch (error) {
        console.error("Error fetching chat partner profile:", error);
      }
    }
  };

  const handleViewProfile = () => {
    if (chatPartnerProfile && chatPartnerProfile.userId) {
      navigate(`/profile/${chatPartnerProfile.userId}`);
    } else {
      console.error("Chat partner profile not available");
    }
  };

  return (
    <Box sx={{ 
      bgcolor: '#1C1C1E', // Dark background similar to Instagram
      minHeight: '100vh', 
      display: 'flex' 
    }}>
      {/* Sidebar */}
      <Box sx={{ width: 240, borderRight: '1px solid #3e4042', p: 2 }}>
        <Typography variant="h6" color="white">Your Note</Typography>
        <Avatar sx={{ width: 56, height: 56 }} />
        <Typography variant="body2" color="gray">Your note...</Typography>
        <Typography variant="h6" color="white">Messages</Typography>
        <List>
          {conversations.map((conv) => (
            <ListItem 
              key={conv.id}
              onClick={() => handleSelectConversation(conv)}
              selected={selectedConversation && selectedConversation.id === conv.id}
            >
              <ListItemAvatar>
                <Avatar src={conv.avatarUrl}>{conv.title[0]}</Avatar>
              </ListItemAvatar>
              <ListItemText primary={conv.title} secondary={conv.lastMessageContent} primaryTypographyProps={{ color: 'white' }} />
            </ListItem>
          ))}
        </List>
      </Box>

      {/* Main Messaging Area */}
      <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
        {selectedConversation ? (
          <>
            <Box sx={{ p: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
              <Typography variant="h6" color="white">{chatPartnerName}</Typography>
              <Box>
                <IconButton><CallIcon sx={{ color: 'white' }} /></IconButton>
                <IconButton><InfoIcon sx={{ color: 'white' }} /></IconButton>
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
                          bgcolor: isCurrentUser ? '#0095F6' : '#2C2C2E',
                          color: 'white',
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
            <Box sx={{ p: 2, borderTop: '1px solid #3e4042', width: '100%' }}>
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
                    backgroundColor: '#3A3A3C', // Dark input background
                    color: 'white',
                  },
                  '& .MuiOutlinedInput-input': {
                    color: 'white', // Input text color
                  },
                  '& .MuiOutlinedInput-notchedOutline': {
                    borderColor: '#3A3A3C', // Input border color
                  }
                }}
              />
              <IconButton 
                color="primary" 
                onClick={handleSendMessage}
                disabled={!message.trim()}
                sx={{ bgcolor: '#0095F6', color: 'white' }} // Send button color
              >
                <SendIcon />
              </IconButton>
            </Box>
          </>
        ) : (
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
            <Typography variant="h6" color="gray">Select a conversation to start messaging</Typography>
          </Box>
        )}
      </Box>

      {/* Right Sidebar (optional) */}
      {selectedConversation && (
        <Box sx={{ width: 360, borderLeft: '1px solid #3e4042', p: 2 }}>
          <Typography variant="h6" color="white">{chatPartnerName}</Typography>
          <Button variant="contained" color="primary" sx={{ mt: 2, bgcolor: '#0095F6' }}>View Profile</Button>
          <Accordion disableGutters elevation={0} sx={{ bgcolor: 'transparent' }}>
            <AccordionSummary expandIcon={<ExpandMoreIcon sx={{ color: 'white' }} />}>
              <Typography color="white">Chat Info</Typography>
            </AccordionSummary>
            <AccordionDetails>
              <Typography variant="body2" color="gray">End-to-end encrypted</Typography>
            </AccordionDetails>
          </Accordion>
        </Box>
      )}
    </Box>
  );
}