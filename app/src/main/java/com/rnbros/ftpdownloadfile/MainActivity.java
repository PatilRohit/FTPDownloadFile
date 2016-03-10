package com.rnbros.ftpdownloadfile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.util.ByteArrayBuffer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ImageView Cam1 = (ImageView) findViewById(R.id.imageView);
        Cam1.setImageBitmap(GetImageBitmapFromUrl("ftp://username:password@www.exapmle.com/folder/subfolder/image.png"));
    }


    public Bitmap GetImageBitmapFromUrl(String url) {
        Bitmap bitmap = null;
        String[] remoteFileNames = url.split("/");
        String[] serverInfo = remoteFileNames[2].split("@");
        String server = serverInfo[1];
        int port = 21;
        String[] serverCredentials = serverInfo[0].split(":");
        String user = serverCredentials[0];
        String pass = serverCredentials[1];
        String remoteFileName = remoteFileNames[3];
        for(int count = 4;count < remoteFileNames.length; count++){
            remoteFileName = remoteFileName +"/"+ remoteFileNames[count];
        }
        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFileName);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            byte[] buffer = new byte[512];
            int readLen = 0;
            while ((readLen = inputStream.read(buffer)) > 0) {
                baf.append(buffer, 0, readLen);
            }
            bitmap = BitmapFactory.decodeByteArray(baf.toByteArray(), 0, baf.length());
            inputStream.close();
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }
}
