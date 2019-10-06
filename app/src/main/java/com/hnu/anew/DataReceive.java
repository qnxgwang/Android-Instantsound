package com.hnu.anew;

import android.widget.Button;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * �ͻ��˽�����Ϣ���߳�
 * 
 * @author 
 * 
 */
public class DataReceive extends Thread {
    private Socket socket;
    private InputStream is;
    public Button button;
    private static final String TAG = "*************socket";

    public DataReceive(Socket socket,Button b) throws IOException {
        this.socket = socket;
        this.button=b;
        this.is = socket.getInputStream();
    }

    @Override
    public void run() {
       // while (true) {
            try {
                DataInputStream dis = new DataInputStream(is);
                byte length = dis.readByte();
                System.out.println((int)(length));
                dis.read();
                byte[] b = new byte[(int)(length)];
                dis.read(b);
                String str = "http"+new String(b);
                System.out.println(str);
                button.setText(str);
                button.setClickable(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

     //   }
    }
}
