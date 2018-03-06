package com.binacodes.floatinghymns.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by eMedrep on 2/1/2018.
 */

public class CommonMethod {
    public static File root = android.os.Environment.getExternalStorageDirectory();

    public static File unzipDestination = new File(root.getAbsolutePath() + "/Androidhub4you/UNZIP");

    /**
     *
     * @param mArchivePath
     *
     * @param mOutPutStream
     * @return
     */
    public static boolean mUnpackZipFile(String mArchivePath,String mOutPutStream) {
        InputStream inputstream;
        ZipInputStream zipinputstream;
        try {
            String filename;
            inputstream = new FileInputStream(mArchivePath);
            zipinputstream = new ZipInputStream(new BufferedInputStream(inputstream));
            ZipEntry mZipEntry;
            byte[] buffer = new byte[1024];
            int count;

            while ((mZipEntry = zipinputstream.getNextEntry()) != null) {
                filename = mZipEntry.getName();

                if (mZipEntry.isDirectory()) {
                    File fmd = new File(mOutPutStream + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fileoutputstream = new FileOutputStream(mOutPutStream + filename);

                while ((count = zipinputstream.read(buffer)) != -1) {
                    fileoutputstream.write(buffer, 0, count);
                }


                fileoutputstream.close();
                zipinputstream.closeEntry();
            }

            zipinputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }




}
