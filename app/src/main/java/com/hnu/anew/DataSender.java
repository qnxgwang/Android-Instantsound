package com.hnu.anew;

import android.widget.Button;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class DataSender {
    public Socket sco = null;
	public OutputStream os = null;
	public DataReceive dr = null;
	public Button button;

	public DataSender(Button b){
		this.button=b;
	}

	public void connectSo() throws UnknownHostException, IOException  {
		this.sco = new Socket("192.168.31.153",8000);
		this.os = sco.getOutputStream();
		this.dr=new DataReceive(sco,button);
		dr.start();
	}
	public void sendText(String msg) throws IOException {
		os.write(msg.getBytes("gbk"));
	}
	public void close() throws IOException {
		sco.close();
		os.close();
	}
}
