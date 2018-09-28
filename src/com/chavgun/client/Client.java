package com.chavgun.client;



import com.chavgun.Connection;
import com.chavgun.Message;
import com.chavgun.MessageType;

import java.io.IOException;
import java.net.Socket;

import static com.chavgun.ConsoleHelper.readInt;
import static com.chavgun.ConsoleHelper.readString;
import static com.chavgun.ConsoleHelper.writeMessage;


public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;


    protected String getServerAddress(){
        writeMessage("Введите IP адресс сервера:");
        return readString();
    }

    protected int getServerPort(){
        writeMessage("Введите порт сервера:");
        return readInt();
    }

    protected String getUserName(){
        writeMessage("Введите ваше имя:");
        return readString();
    }
    protected  boolean shouldSendTextFromConsole(){
        return true;
    }

    protected SocketThread getSocketThread(){
        return new SocketThread();
    }

    protected void sendTextMessage(String text){

        try {
            connection.send(new Message(MessageType.TEXT,text));
        } catch (IOException e) {
            e.printStackTrace();
            clientConnected = false;
        }
    }

    public void run(){
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(clientConnected == true){
            writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
            String msg;
            while (clientConnected){
                msg = readString();
            if(msg.equals("exit"))
                break;
                if(shouldSendTextFromConsole() == true)
                    sendTextMessage(msg);
            }
        }else{
            writeMessage( "Произошла ошибка во время работы клиента.");
        }


    }

    public static void main(String[] args) {
        new Client().run();
    }





    public class SocketThread extends Thread {
        protected void processIncomingMessage(String message) {
            writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            writeMessage(String.format("участник с именем %s присоединился к чату", userName));
        }

        protected void informAboutDeletingNewUser(String userName) {
            writeMessage(String.format("частник с именем %s покинул чат.", userName));
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            if (clientConnected == true) {
                synchronized (Client.this) {
                    Client.this.notifyAll();
                }
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                MessageType type = message.getType();
                if (type == MessageType.NAME_REQUEST) {
                    connection.send(new Message(MessageType.USER_NAME, getUserName()));

                } else if (type == MessageType.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    return;
                } else {
                    throw new IOException("Unexpected " + type);
                }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (!isInterrupted()) {
                Message message = connection.receive();
                MessageType type = message.getType();
                if (type == MessageType.TEXT) {
                    processIncomingMessage(message.getData());

                } else if (type == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(message.getData());

                } else if (type == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(message.getData());

                } else {
                    throw new IOException("Unexpected " + type);
                }
            }
        }


        @Override
        public void run() {
            String IP = getServerAddress();
            int port = getServerPort();
            try (Socket socket = new Socket(IP, port);
                 Connection connection = new Connection(socket)) {
                Client.this.connection = connection;
                clientHandshake();
                clientMainLoop();





            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                notifyConnectionStatusChanged(false);
                writeMessage("Произошла ошибка...");
            }
        }
    }
}
