package game.minecraftpe.test;

import android.content.Context;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipFile;
import java.io.BufferedInputStream;
import android.os.Debug;
import android.app.Activity;
import android.widget.ImageView;

public class IOTool
{
    public void delete(String str)
    {
        File file = new File(str);
        if (file.exists())
        {
            if (file.isDirectory())
            {
                File[] childFiles = file.listFiles();
                if (childFiles == null || childFiles.length == 0)
                {
                    file.delete();
                    return;
                }

                for (int i = 0; i < childFiles.length; i++)
                {
                    childFiles[i].delete();
                }
                file.delete();
            }
        }
    }

    public String readfile(Context ctx, InputStream file_stream)
    {
        try
        {
            String output = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(file_stream)); 
            String line = ""; 
            while ((line = reader.readLine()) != null)
            { 
                output += line + "\n";
            }
            reader.close();
            return output;
        }
        catch (IOException e)
        {
            show(ctx, ctx.getString(R.string.file_no_found));
            return "Failed";
        }
    }

    public boolean writefile(String path, String data)
    {
        try
        {
            File file = new File(path);
            pathset(path);
            byte[] Bytes = new String(data).getBytes();
            if (file.exists())
            {
                if (file.isFile())
                {
                    OutputStream writer = new FileOutputStream(file);
                    writer.write(Bytes);
                    writer.close();
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                file.createNewFile();
                OutputStream writer = new FileOutputStream(file);
                writer.write(Bytes);
                writer.close();
                return true;
            }
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public Boolean unZip(Context context, String assetName, String outputDirectory)
    { 
        try
        {
            File file = new File(outputDirectory); 
            pathset(outputDirectory);
            InputStream inputStream = null; 
            inputStream = context.getAssets().open(assetName); 
            ZipInputStream zipInputStream = new ZipInputStream(inputStream); 
            ZipEntry zipEntry = zipInputStream.getNextEntry(); 
            byte[] buffer = new byte[1024 * 1024]; 
            int count = 0; 
            while (zipEntry != null)
            { 
                if (zipEntry.isDirectory())
                { 
                    file = new File(outputDirectory + File.separator + zipEntry.getName()); 
                    file.mkdirs(); 
                }
                else
                { 
                    file = new File(outputDirectory + File.separator + zipEntry.getName()); 
                    file.createNewFile(); 
                    FileOutputStream fileOutputStream = new FileOutputStream(file); 
                    while ((count = zipInputStream.read(buffer)) > 0)
                    { 
                        fileOutputStream.write(buffer, 0, count); 
                    } 
                    fileOutputStream.close(); 
                }
                zipEntry = zipInputStream.getNextEntry(); 
            } 
            zipInputStream.close();
            return true;
        }
        catch (IOException e)
        {
            show(context, e.toString());
            return false;
        }
    }

    public void show(Context ctx, String str)
    {
        Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
    }

    public void pathset(String path)
    {
        String[] dirs = path.split("/");
        String pth = "";
        for (int i = 0;i < dirs.length;i++)
        {
            if (i != dirs.length - 1)
            {
                pth += "/" + dirs[i];
            }
        }
        File dir = new File(pth);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
    }

    public String readZipFile(String file, String str) throws Exception
    {  
        String data = "";
        ZipFile zf = new ZipFile(file);  
        InputStream in = new BufferedInputStream(new FileInputStream(file));  
        ZipInputStream zin = new ZipInputStream(in);  
        ZipEntry ze;  
        while ((ze = zin.getNextEntry()) != null)
        {  
            if (!ze.isDirectory())
            {
                if (ze.getName().toString().equalsIgnoreCase(str))
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)));  
                    String line = "";
                    while ((line = br.readLine()) != null)
                    {  
                        data += line;
                    }  
                    br.close();
                    break;
                }
            }  
        }  
        zin.closeEntry(); 
        return data;
    }  

    //文件大于200MB可能出现错误
    public boolean copy(Context context, String assetName, String outputDirectory, String file_name)
    {
        pathset(outputDirectory + "/" + file_name);
        try
        {
            InputStream inputStream = null; 
            inputStream = context.getAssets().open(assetName);
            File file = new File(outputDirectory + "/" + file_name);
            if (!file.exists())
            {
                file.createNewFile();
            }
            OutputStream Output = new FileOutputStream(outputDirectory + "/" + file_name);  
            byte[] buffer = new byte[1024];  
            int length = inputStream.read(buffer);
            while (length > 0)
            {
                Output.write(buffer, 0, length); 
                length = inputStream.read(buffer);
            }

            Output.flush();  
            inputStream.close();  
            Output.close();
            return true;
        }
        catch (IOException e)
        {
            show(context, "copy:" + e.toString());
            return false;
        }
    }


    //MD5计算
    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    protected static MessageDigest messageDigest = null;
    static {
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    //根据文件路径计算文件的MD5
    public static String getFileMD5String(String fileName) throws IOException
    {
        File f = new File(fileName);
        return getFileMD5String(f);
    }

    //根据文件对象计算文件的MD5
    public static String getFileMD5String(File file) throws IOException
    {
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                                             file.length());
        messageDigest.update(byteBuffer);
        return bufferToHex(messageDigest.digest());
    }

    private static String bufferToHex(byte bytes[])
    {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n)
    {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++)
        {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer)
    {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

}
