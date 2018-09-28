package com.chavgun.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.chavgun.ConsoleHelper.writeMessage;

public class BotClient extends Client {


    @Override
    protected String getUserName() {
        int rundomName = (int)(Math.random() *100);
        return "date_bot_" + rundomName;
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }


    public static void main(String[] args) {
        new BotClient().run();
    }

    public class BotSocketThread extends SocketThread{

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }


        @Override
        protected void processIncomingMessage(String message) {
           // super.processIncomingMessage(message);
            writeMessage(message);
            try {


                if (message != null && message.contains(": ")) {
                    String[] splitMsg = message.split(": ");
                    String usrName = splitMsg[0];
                    String msg = splitMsg[1];
                    SimpleDateFormat simpleDateFormat;
                    String format = "Информация для %s: %s";
                    Calendar calendar;

                    switch (msg) {
                        case ("дата"):
                            calendar = new GregorianCalendar();
                            simpleDateFormat = new SimpleDateFormat("d.MM.YYYY");
                            sendTextMessage(String.format(format, usrName, simpleDateFormat.format(calendar.getTime())));
                            break;
                        case ("день"):
                            calendar = new GregorianCalendar();
                            simpleDateFormat = new SimpleDateFormat("d");
                            sendTextMessage(String.format(format, usrName, simpleDateFormat.format(calendar.getTime())));
                            break;
                        case ("месяц"):
                            calendar = new GregorianCalendar();
                            simpleDateFormat = new SimpleDateFormat("MMMM");
                            sendTextMessage(String.format(format, usrName, simpleDateFormat.format(calendar.getTime())));
                            break;
                        case ("год"):
                            calendar = new GregorianCalendar();
                            simpleDateFormat = new SimpleDateFormat("YYYY");
                            sendTextMessage(String.format(format, usrName, simpleDateFormat.format(calendar.getTime())));
                            break;
                        case ("время"):
                            calendar = new GregorianCalendar();
                            simpleDateFormat = new SimpleDateFormat("H:mm:ss");
                            sendTextMessage(String.format(format, usrName, simpleDateFormat.format(calendar.getTime())));
                            break;
                        case ("час"):
                            calendar = new GregorianCalendar();
                            simpleDateFormat = new SimpleDateFormat("H");
                            sendTextMessage(String.format(format, usrName, simpleDateFormat.format(calendar.getTime())));
                            break;
                        case ("минуты"):
                            calendar = new GregorianCalendar();
                            simpleDateFormat = new SimpleDateFormat("m");
                            sendTextMessage(String.format(format, usrName, simpleDateFormat.format(calendar.getTime())));
                            break;
                        case ("секунды"):
                            calendar = new GregorianCalendar();
                            simpleDateFormat = new SimpleDateFormat("s");
                            sendTextMessage(String.format(format, usrName, simpleDateFormat.format(calendar.getTime())));
                            break;

                    }
                }

            }catch (Exception e){
                writeMessage("Произошла ошибка при анализе сообшения");
            }
        }
    }
}
