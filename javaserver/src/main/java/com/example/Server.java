package com.example;

import java.awt.FlowLayout;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.*;
import javax.swing.*;
import java.awt.*;
/**
 * Created by Gary on 16/5/28.
 */
public class Server extends JFrame implements Runnable{
    private Thread thread;
    private ServerSocket servSock;
    private ArrayList<ConnectionThread> threadPool;

    private JTextArea textArea;
    private JPanel panel;

    public Server(int w , int h){


        // GUI Setup
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize textArea
        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        this.textArea.setPreferredSize(new Dimension(500,550));
        JScrollPane scrollPane = new JScrollPane(this.textArea);
        scrollPane.setPreferredSize(new Dimension(500,550));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane);
        this.pack();
        this.setVisible(true);

        try {
            // Detect server ip
            InetAddress IP = InetAddress.getLocalHost();
            System.out.println("IP of my system is := "+IP.getHostAddress());
            System.out.println("Waitting to connect......");

            // Create server socket
            servSock = new ServerSocket(2000);

            // Create socket thread
            thread = new Thread(this);
            thread.start();
        } catch (java.io.IOException e) {
            System.out.println("Socket啟動有問題 !");
            System.out.println("IOException :" + e.toString());
        } finally{

        }
    }

    @Override
    public void run(){
        // Running for waitting multiple client
        BufferedReader r;
        int cnt = 1;
        while(true){
            try{
                // After client connected, create client socket connect with client
                Socket clntSock = servSock.accept();
                r = new BufferedReader(new InputStreamReader(clntSock.getInputStream()));

                addText("Connected!!");

                String s = r.readLine();
                addText("[Server Said]" + s + cnt);


                // Add to pool, start reading message
                ConnectionThread connection = new ConnectionThread(clntSock, cnt);
                connection.start();
                threadPool.add(connection);
                cnt++;
            }
            catch(Exception e){
                //System.out.println("Error: "+e.getMessage());
            }
        }
    }

    class ConnectionThread extends  Thread
    {
        private Socket socket;
        private BufferedReader read;
        private PrintWriter write;
        private int ID;

        ConnectionThread(Socket s, int id)
        {
            socket = s;
            try
            {
                read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                write = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                ID = id;
            }catch (Exception e){}
        }

        public void run()
        {
            // keep reading from client
            while(true)
            {
                String message;
                try
                {
                    message = this.read.readLine();
                }
                catch (Exception e){break;}

                // Result
                addText("Client " + ID + " ---------> " + message);
            }
        }
    }
    public void addText(String clientMessage) {
        if(clientMessage != null){
            if(!clientMessage.equals("null"))
                textArea.append(clientMessage + "\n");
        }
    }
}
