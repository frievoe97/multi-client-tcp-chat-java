import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = bufferedReader.readLine();
            clientHandlers.add(this);
            sendMessage("Server: " + username + " has entered the chat!");
        } catch (IOException e) {
            closeEverything();
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                sendMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything();
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.username.equals(username)) {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything();
                e.printStackTrace();
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        sendMessage("SERVER: " + username + " has left the chat.");
    }

    public void closeEverything() {
        removeClientHandler();
        try {
            if (bufferedWriter != null) bufferedWriter.close();
            if (bufferedReader != null) bufferedReader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
