/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.model;

public class SocketReachableModel
{

    private String port;
    private boolean reachable;
    private String msg;

    public SocketReachableModel(int port, boolean reachable, String msg)
    {
        this.port = String.valueOf(port);
        this.reachable = reachable;
        this.msg = msg;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public boolean isReachable()
    {
        return reachable;
    }

    public void setReachable(boolean reachable)
    {
        this.reachable = reachable;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

}
