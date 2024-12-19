# Social Platform  
A modern web-based platform designed to connect people, enabling seamless interaction and sharing of multimedia content.

## About  
Social Platform is a full-stack application that facilitates online social interaction. It allows users to create posts with images or videos, authenticate via multiple platforms (Google, GitHub), and manage their profiles with ease. Built with a microservices architecture, this project ensures scalability and maintainability for future enhancements.

## Features  
- User authentication with password and multi-platform OAuth 2.0 (Google, GitHub)
- Create, edit, and delete posts with multimedia uploads (images/videos) 
- Like, comment, and share functionality  
- Real-time chat and notifications  
- Privacy settings and content moderation  


## Technologies  
- **Backend**: Spring boot  
- **Frontend**: React
- **Database**: MySQL, MongoDB  
- **Authentication**: JWT (JSON Web Tokens), OAuth 2.0
- **Real-time Features**: Websocket
- **Messaging & Event Streaming**: Apache Kafka
- **Payment Integration**: VNPay
- **Cloud Storage**: Cloudinary

## Installation  

### Prerequisites  
- Java Development Kit (JDK) 
- MongoDB installed locally or accessible remotely
- Docker

### Steps  
1. Clone the repository:  
git clone https://github.com/0Hoag/BookStore.git <br>
cd social-platform <br>
3. Set up the backend: <br>
cd backend

Install JDK:
Download and install the Java Development Kit (JDK) from the official Oracle website.
Set the JAVA_HOME environment variable and verify the installation:

export JAVA_HOME=/path/to/your/jdk
export PATH=$JAVA_HOME/bin:$PATH
java -version
