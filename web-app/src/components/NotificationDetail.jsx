import { useParams } from 'react-router-dom';
import { useState, useEffect } from 'react';
import notificationService from '../services/notificationService';

const NotificationDetail = () => {
  const { id } = useParams();
  const [notification, setNotification] = useState(null);

  useEffect(() => {
    const fetchNotification = async () => {
      try {
        const response = await notificationService.getUserNotification(id); // Adjust the service call as needed
        setNotification(response.data);
      } catch (error) {
        console.error('Error fetching notification details:', error);
      }
    };

    fetchNotification();
  }, [id]);

  if (!notification) return <div>Loading...</div>;

  return (
    <div>
      <h1>{notification.message}</h1>
      <p>{new Date(notification.timestamp).toLocaleString()}</p>
    </div>
  );
}; 

export default NotificationDetail;