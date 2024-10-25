import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static List<String> fileDirectory = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("Server started on port " + PORT);
        ServerSocket serverSocket = new ServerSocket(PORT);


        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket);
            new ClientHandler(clientSocket).start();
        }
    }

    
    private static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

            
                broadcastMessage("A new user has joined the chat!");

                
                while (true) {
                    String request = in.readLine();
                    if (request == null) break;

                    if (request.startsWith("UPLOAD")) {
                        receiveFile(request.substring(7));
                    } else if (request.startsWith("DOWNLOAD")) {
                        sendFile(request.substring(9));
                    } else if (request.equals("LIST")) {
                        listFiles();
                    } else {
                        broadcastMessage("Client: " + request);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                broadcastMessage("A user has left the chat.");
            }
        }

        
        private void receiveFile(String fileName) throws IOException {
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("server_files/" + fileName))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = socket.getInputStream().read(buffer)) > 0) {
                    bos.write(buffer, 0, bytesRead);
                }
            }
            synchronized (fileDirectory) {
                fileDirectory.add(fileName);
            }
            out.println("File " + fileName + " uploaded successfully.");
        }

        
        private void sendFile(String fileName) throws IOException {
            File file = new File("server_files/" + fileName);
            if (file.exists()) {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) > 0) {
                        socket.getOutputStream().write(buffer, 0, bytesRead);
                    }
                }
                out.println("File " + fileName + " downloaded successfully.");
            } else {
                out.println("File not found.");
            }
        }

        
        private void listFiles() {
            synchronized (fileDirectory) {
                out.println("Available files: " + String.join(", ", fileDirectory));
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }
}
