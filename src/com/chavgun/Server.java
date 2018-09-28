package com.chavgun;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.chavgun.ConsoleHelper.readInt;
import static com.chavgun.ConsoleHelper.writeMessage;


public class Server {
private static Map<String, Connection>  connectionMap = new ConcurrentHashMap<>();

 public static void sendBroadcastMessage(Message message){
     for (Map.Entry entry : connectionMap.entrySet()) {
         Connection connection = (Connection) entry.getValue();
         try {
             connection.send(message);
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

 }


    public static void main(String[] args0)  {
        Handler handler;
        writeMessage("Введите номер порта сервера:");

        try(ServerSocket serverSocket = new ServerSocket(readInt())){
            writeMessage("Сервер запущен!");

            while (true){
                handler = new Handler(serverSocket.accept());
                handler.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }




    private static class Handler extends Thread{
        private Socket socket;

        public Handler(Socket socket) throws IOException {

            this.socket = socket;
        }


       private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException{
            String data;
            do{
            connection.send(new Message(MessageType.NAME_REQUEST));
            Message nameMsg = connection.receive();
            if(nameMsg.getType().equals(MessageType.USER_NAME)){
                data = nameMsg.getData();
                if(data!=null && !data.equals("") && !connectionMap.containsKey(data)){
                    connectionMap.put(data, connection);
                    connection.send(new Message(MessageType.NAME_ACCEPTED));
                    return data;
                }
            }
            } while (true);
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException{
            for (Map.Entry<String, Connection> entry :connectionMap.entrySet()) {
                if(!entry.getKey().equals(userName)){
                    Connection connectionUsers = entry.getValue();
                    connection.send(new Message(MessageType.USER_ADDED, entry.getKey()));
                }
            }

        }
       private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{
            while (true){
                Message message = connection.receive();
                if(message.getType()==MessageType.TEXT){
                    String inMsg = message.getData();
                    String outMsg = userName + ": " + inMsg;
                    sendBroadcastMessage(new Message(MessageType.TEXT, outMsg));

                }else{
                    writeMessage("Принятые данные не являються сообщением.");
                }




            }
       }


        @Override
        public void run() {
            String nameClient = "";
            writeMessage("Установлено соединение с " +socket.getRemoteSocketAddress());
            try(Connection connection = new Connection(socket)) {
                nameClient = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, nameClient));
                sendListOfUsers(connection,nameClient);
                serverMainLoop(connection, nameClient);

            } catch (IOException | ClassNotFoundException e) {
                //e.printStackTrace();
            }
            connectionMap.remove(nameClient);
            sendBroadcastMessage(new Message(MessageType.USER_REMOVED, nameClient));
            writeMessage("Соединение с аддресом закрыто:" + socket.getRemoteSocketAddress());
        }
    }




}
