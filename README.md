# **Client-Server Messaging and File Sharing Application**

### **Description**

This Java-based client-server application allows real-time messaging and file sharing between multiple clients connected to a central server. Using TCP sockets, the server facilitates a shared chatroom where clients can send messages to each other and upload files, which are accessible to other users. The server is multi-threaded, supporting simultaneous client connections and ensuring a smooth, interactive experience for all connected clients.

### **How to Run**

#### **Server Setup:**
1. Open a terminal and navigate to the directory containing `Server.java`.
2. Compile the server code:
   ```bash
   javac Server.java
   ```
3. Start the server:
   ```bash
   java Server
   ```
   The server will begin listening for client connections on the default port (e.g., port 12345).

#### **Client Setup:**
1. Open a separate terminal for each client.
2. Navigate to the directory containing `Client.java`.
3. Compile the client code:
   ```bash
   javac Client.java
   ```
4. Start each client:
   ```bash
   java Client
   ```
5. When prompted, enter the server's IP address and port number (e.g., `localhost` and `12345` if running locally).

--- 

This README provides a high-level overview and basic instructions for running the application. Let me know if you'd like to add any other details!
