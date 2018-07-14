/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author ntdat
 */
public class Client {
    public static final int _serverPort = 9000;
    public static final String _serverIP = "127.1.0.1";

    public static void main(String argv[]) throws Exception {
        //Setup kết nối đến Server và Node
        Scanner scanner = new Scanner(System.in);
        char sc = '0';
        System.out.println("Nhập: 0-> Thoát");
        System.out.println("\t1-> Kết nối đến Server");
        System.out.println("\t2-> Kết nối đến Node");
        System.out.print("Nhập: ");
        sc = scanner.next().charAt(0);
        do {
            switch (sc) {
                case '1':
                    try {
                        Socket socket = new Socket(_serverIP, _serverPort);
                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                        dos.writeUTF("Clt"); // clt la cleint de server phan biet sua nod va client
                        dos.flush();
                        DataInputStream dis = new DataInputStream(socket.getInputStream());
                        String result = dis.readUTF();
                        System.out.println(result);
                        dis.close();
                        dos.close();
                        socket.close();
                    } catch (IOException ex) {
                        System.out.println("Server chưa mở kết nối!");
                    }
                    break;
                case '2': // kết nối Server Node    
                    String _ipNode, _path, _fileName;//F:\\HocTap\\HCDH\\HK2\\MMTNC\\MMT\\Client_Nhan_File
                    int _portNode; // ip cua node
                    scanner.nextLine();
                    System.out.print("Nhập IP:   ");
                    _ipNode = scanner.nextLine();
                    System.out.print("Nhập Port: ");
                    _portNode = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Nhập Tên:  ");
                    _fileName = scanner.nextLine();
                    
                    System.out.print("Nhập đường dẫn thư mục lưu file: ");
                    _path = scanner.nextLine();

                    try {
                        // Đọc và truyên file dữ liệu
                        byte[] sendData = new byte[1024];
                        byte[] receiveData = new byte[1024];
                        String sentence = _fileName;
                        sendData = sentence.getBytes();
                        //Connect đến Node
                        DatagramSocket ds = new DatagramSocket();
                        InetAddress iaddr = InetAddress.getByName(_ipNode);
                        DatagramPacket pk = new DatagramPacket(sendData, sendData.length, iaddr, _portNode);
                        ds.send(pk);
                        System.out.println("Gửi yêu Node cầu truyền tập tin... ");
                        //Nhận thông tin xác nhận từ Node khi đã nhận dc file
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        ds.receive(receivePacket);
                        System.out.println("Đã Nhận Được file từ Node!");
                        // lưu file vào đường dẫn
                        String filePath = _path + "\\" + _fileName;
                        FileOutputStream fos = new FileOutputStream(filePath.trim());
                        byte[] data = receivePacket.getData();
                        fos.write(data);
                        fos.flush();
                        ds.close();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                default:
                    System.out.println("Vui lòng nhập lại!");
            }
            System.out.print("Nhập: ");
            sc = scanner.next().charAt(0);
        } while (sc != '0');
    }
}
