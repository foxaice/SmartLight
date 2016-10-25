package me.foxaice.controller_api.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpController {
    private final Object mLockMonitor = new Object();
    private volatile DatagramSocket mSocket = null;

    public UdpController(int port) {
        try {
            mSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public UdpController() {

    }

    public void closeSocket() {
        synchronized (mLockMonitor) {
            if (mSocket != null) mSocket.close();
            mSocket = null;
        }
    }

    public void sendMessage(byte[] message, String ipAddress, int port) throws IOException {
        DatagramPacket sentPacket = new DatagramPacket(message, message.length, InetAddress.getByName(ipAddress), port);
        initSocket();
        mSocket.send(sentPacket);
    }

    public void sendAdminMessage(UdpAdminCommands command, String ipAddress, int adminPort, String... params) throws IOException {
        byte[] data;
        if (params.length > 0) {
            StringBuilder sb = new StringBuilder(command.toString());
            data = sb.append(params[0]).append("\r").toString().getBytes();
        } else {
            data = command.getBytes();
        }
        DatagramPacket sentPacket = new DatagramPacket(data, data.length, InetAddress.getByName(ipAddress), adminPort);
        initSocket();
        mSocket.send(sentPacket);
        System.out.println("send " + mSocket.toString());
    }

    public String receiveAdminMessage(String ipAddress) throws IOException {
        byte[] data = new byte[1024];
        DatagramPacket receivedPacket = new DatagramPacket(data, data.length);
        while (true) {
            initSocket();
            receivedPacket.setLength(data.length);
            System.out.println("before receive " + mSocket.toString());
            mSocket.receive(receivedPacket);
            System.out.println("after receive " + mSocket.toString() + " ip:" + receivedPacket.getAddress().getHostAddress() + ipAddress + " \nmsg:" + BytesToString(receivedPacket.getData(), receivedPacket.getLength()));
            if (receivedPacket.getAddress().getHostAddress().equals(ipAddress) || ipAddress == null || ipAddress.equals("255.255.255.255")) {
                return BytesToString(receivedPacket.getData(), receivedPacket.getLength());
            }
        }
    }

    public int getSocketPort() {
        synchronized (mLockMonitor) {
            initSocket();
            return mSocket.getLocalPort();
        }
    }

    private void initSocket() {
        synchronized (mLockMonitor) {
            try {
                mSocket = mSocket == null ? new DatagramSocket() : mSocket;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    private String BytesToString(byte[] data, int length) throws IOException {
        return new String(data, 0, length, "UTF8").trim();
    }
}
