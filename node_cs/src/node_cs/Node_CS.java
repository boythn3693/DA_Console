/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node_cs;

import java.io.ByteArrayOutputStream;
import java.util.Scanner;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author boythn3693
 */
public class Node_CS {

    //F:\\HocTap\\HCDH\\HK2\\MMTNC\\MMT\\Node_Test\\Node + 1_2_3
    public static String _Paths = "F:\\HocTap\\HCDH\\HK2\\MMTNC\\MMT\\Node_Test\\Node2";
    public static final int _serverPort = 9000;
    //Đổi port mỗi khi thay node
    public static int _nodePort = 2000;
    //public static final int _nodePort = 2000; // Port node 1
    //public static final int _nodePort = 3000; // Port node 2
    //public static final int _nodePort = 4000; // Port node 3
    public static final String _serverIP = "127.1.0.1";
    public static final String _nodeIP = "127.1.0.1";
    
    private static String[] getListFile() {
        File dir = new File(_Paths);
        if (dir.exists()) {
            String[] paths = dir.list();
            return paths;
        }
        return null;
    }

    private static String[] getListFile(String _path) {
        File dir = new File(_path);
        if (dir.exists()) {
            String[] paths = dir.list();
            return paths;
        }
        return null;
    }
    
    //START UDP

    //END UDP
    /**
     * @param argv the command line arguments
     */
    public static void main(String argv[]) throws Exception {

        Scanner sc = new Scanner(System.in);
        char ch = '0';
        System.out.println("Nhập: 0 => Thoát ");
        System.out.println("\t1 => Chọn đường dẫn cố định: " + _Paths);
        System.out.println("\t2 => Nhập đường dẫn đến thư mục chứa file.");
        System.out.print("Nhập: ");
        ch = sc.next().charAt(0);
        String[] paths = null;
        
        do {
            switch (ch) {
                case '1':
                    paths = getListFile(); //Lấy danh sách file trên Node
                    break;
                case '2':
                    System.out.print("Tạo Port cho Node: ");
                    sc.nextLine();
                    _nodePort =  Integer.parseInt(sc.nextLine());
                    System.out.print("Nhập Đường dẫn đến thư mục chứa file: ");
                    //sc.nextLine();
                    _Paths = sc.nextLine();
                    paths = getListFile(_Paths);
                    System.out.println(_nodePort+ "-" + _Paths);
                    break;
                default:
                    System.out.println("Vui lòng nhập lại!");
            }
            if (ch == '1' || ch == '2') {
                ch = '0';
            } else {
                System.out.print("Nhập: ");
                ch = sc.next().charAt(0);
            }
        } while (ch != '0');

        if (paths == null) {
            System.out.println("Đường dẫn này không đúng! ");
        } else {
            try {
                String _str = "Nod," + _nodePort; // Node or Client 
                for (String path : paths) {
                    boolean flag;
                    flag = path.contains(".");
                    if (flag == true) {
                        _str = _str + ", " + path;
                    }
                }
                Socket sk = new Socket(_serverIP, _serverPort);
                DataOutputStream os = new DataOutputStream(sk.getOutputStream());
                DataInputStream is = new DataInputStream(sk.getInputStream());

                os.writeUTF(_str);
                os.flush();

                String result = is.readUTF();
                System.out.println(result);
                is.close();
                os.close();
                sk.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            // Mở kết nối UDP chờ Client kết nối đến
            System.out.println("Đang chờ Client kết nối đến, Nhấn phím 0 để thoát!");

            try {
                DatagramSocket ds = new DatagramSocket(_nodePort);
                byte[] receivedData = new byte[1024];
                byte[] dataSend = new byte[1024];
                while (true) {
                    MyThread myThread = new MyThread();
                    myThread.start();
                    
                    // nhận kết nối từ client
                    DatagramPacket pk = new DatagramPacket(receivedData, receivedData.length);
                    ds.receive(pk);
                    System.out.println("Đã nhận yêu cầu tải tập tin từ Client !");
                    InetAddress inetAddress = InetAddress.getLocalHost();

                    String _fileName = new String(pk.getData());
                    String filePath = _Paths + "\\" + _fileName;
                    System.out.println("Client:" + filePath);
                    //phản hồi đến client khi kết nối thành công

                    FileInputStream fis = new FileInputStream(filePath.trim());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    fis.read(dataSend);
                    baos.write(dataSend);

                    int port = pk.getPort();
                    DatagramPacket sendPacket = new DatagramPacket(dataSend, dataSend.length, inetAddress, port);
                    ds.send(sendPacket);
                    System.out.println("Đã gửi tập tin " + _fileName + " đến cho Client!");
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
