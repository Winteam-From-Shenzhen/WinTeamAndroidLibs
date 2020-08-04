package net.yt.impl;

import net.yt.serialport.core.reader.SerialReader;

import java.io.InputStream;

/**
 * Auth : xiao.yunfei
 * Date : 2019/4/23 10:41
 * Package name : net.yt.impl
 * Des :
 */
public class CommonReader extends SerialReader {

    public CommonReader(String path , OnDataReceive onDataReceive) {
        super(path,onDataReceive);
    }

    @Override
    protected boolean readHeader(InputStream mInputStream) {

        byte[] buffer = new byte[1024];

        int readResult = 0;
        try {
            if (mInputStream.available() > 0) {
                readResult =  mInputStream.read(buffer);
            }


            if (readResult > 0){

                postReceiver(buffer,readResult);
            }

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }



        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}
