import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);


        new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        }).start();


        System.out.println("Enter 'UPLOAD <filename>' to upload a file, 'DOWNLOAD <filename>' to download, 'LIST' to list files, or type messages to chat.");
        while (true) {
            String userInput = scanner.nextLine();
            if (userInput.startsWith("UPLOAD")) {
                String fileName = userInput.substring(7);
                out.println("UPLOAD " + fileName);
                sendFile(socket, fileName);
            } else if (userInput.startsWith("DOWNLOAD")) {
                String fileName = userInput.substring(9);
                out.println("DOWNLOAD " + fileName);
                receiveFile(socket, fileName);
            } else {
                out.println(userInput);
            }
        }
    }


    private static void sendFile(Socket socket, String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) > 0) {
                        socket.getOutputStream().write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("File " + fileName + " uploaded.");
            } else {
                System.out.println("File not found.");
            }
        } catch (IOException e) {
            System.out.println("Error uploading file: " + e.getMessage());
        }
    }


    private static void receiveFile(Socket socket, String fileName) {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = socket.getInputStream().read(buffer)) > 0) {
                bos.write(buffer, 0, bytesRead);
            }
            System.out.println("File " + fileName + " downloaded.");
        } catch (IOException e) {
            System.out.println("Error downloading file: " + e.getMessage());
        }
    }
}
