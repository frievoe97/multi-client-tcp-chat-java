import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    static final String LOCALHOST = "localhost";

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your name:");
        String username = scanner.nextLine();
        Socket socket = new Socket(LOCALHOST, Server.PORTNUMBER);
        Client client = new Client(socket, username);
        client.readMessages();
        client.sendMessage();

    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while (!socket.isClosed()) {
                String input = scanner.nextLine();
                bufferedWriter.write(username + ": " + input);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void readMessages() {
        new Thread(new Runnable() {
            String recievedMessage;

            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        recievedMessage = bufferedReader.readLine();
                        System.out.println(recievedMessage);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedWriter, bufferedReader);
                    }
                }
            }
        }).start();
    }

    private void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if (bufferedWriter != null) bufferedWriter.close();
            if (bufferedReader != null) bufferedReader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
