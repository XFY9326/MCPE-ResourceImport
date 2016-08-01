package game.minecraftpe.test;

import android.app.*;
import android.content.*;
import android.net.*;
import android.text.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import android.util.*;

public class ModPEManage
{
    private IOTool io = new IOTool();
    private Context ctx;

    public void add(Context ctx, String str,int importmethodset)
    {
        this.ctx = ctx;
        try
        {
            String[] list_file = ctx.getAssets().list("ModPE");
            String strget = ctx.getResources().getString(R.string.file_open_count);  
            String strfix = String.format(strget, list_file.length);  
            io.show(ctx, strfix);
            for (int i = 0;i < list_file.length ;i++)
            {
                if (list_file.length == 0)
                {
                    break;
                }
                InputStream file_input = ctx.getResources().getAssets().open("ModPE" + "/" + list_file[i]);
                //zip文件modpkg不能直接读取读出来复制,分开处理
                if (getExtensionName(list_file[i]).equalsIgnoreCase("modpkg"))
                {
                    if (!io.copy(ctx, "ModPE" + "/" + list_file[i], str, list_file[i]))
                    {
                        io.show(ctx, ctx.getString(R.string.file_no_export));
                        break;
                    }
                }
                else
                {
                    String file_data = io.readfile(ctx, file_input);
                    if (!io.writefile(str + "/" + list_file[i], file_data))
                    {
                        io.show(ctx, ctx.getString(R.string.file_no_export));
                        break;
                    }
                }
                if (importmethodset == 0)
                {
                    if (!open(str + "/" + list_file[i]))
                    {
                        io.show(ctx, ctx.getString(R.string.file_no_open));
                        break;
                    }
                }
                else if (importmethodset == 1)
                {
                    if (!open(str + "/" + list_file[i],1))
                    {
                        io.show(ctx, ctx.getString(R.string.file_no_open));
                        break;
                    }
                }
                else if (importmethodset == 2)
                {
                    if (!open(str + "/" + list_file[i],2))
                    {
                        io.show(ctx, ctx.getString(R.string.file_no_open));
                        break;
                    }
                }
                else if (importmethodset == 3)
                {
                    if (!open(str + "/" + list_file[i],3))
                    {
                        io.show(ctx, ctx.getString(R.string.file_no_open));
                        break;
                    }
                }
            }
        }
        catch (IOException e)
        {
            io.show(ctx, ctx.getString(R.string.file_no_found));
        }
    }

    private static String getExtensionName(String filename)
    { 
        if ((filename != null) && (filename.length() > 0))
        { 
            int dot = filename.lastIndexOf('.'); 
            if ((dot > -1) && (dot < (filename.length() - 1)))
            { 
                return filename.substring(dot + 1); 
            } 
        } 
        return filename; 
    }

    
    
    private boolean open(String path)
    {
        Intent common_intent = new Intent();
        common_intent.setClassName("net.zhuoweizhang.mcpelauncher", "net.zhuoweizhang.mcpelauncher.api.ImportScriptActivity");
        File file = new File(path);
        Uri data = Uri.fromFile(file);
        common_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        common_intent.setData(data);
        try
        {
            ctx.startActivity(common_intent);
            return true;
        }
        catch (ActivityNotFoundException ce)
        {
            Intent pro_intent = new Intent();
            pro_intent.setClassName("net.zhuoweizhang.mcpelauncher.pro", "net.zhuoweizhang.mcpelauncher.api.ImportScriptActivity");
            File pro_file = new File(path);
            Uri pro_data = Uri.fromFile(pro_file);
            pro_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pro_intent.setData(pro_data);
            try
            {
                ctx.startActivity(pro_intent);
                return true;
            }
            catch (ActivityNotFoundException pe)
            {
                return false;
            }
        }
    }

    private boolean open(String path, int num)
    {
        String pkgname;
        String apiname;
        switch (num)
        {
            case 1:
                pkgname = "net.zhuoweizhang.mcpelauncher";
                apiname = "net.zhuoweizhang.mcpelauncher.api.ImportScriptActivity";
                break;
            case 2:
                pkgname = "net.zhuoweizhang.mcpelauncher.pro";
                apiname = "net.zhuoweizhang.mcpelauncher.api.ImportScriptActivity";
                break;
            case 3:
                pkgname = "com.duowan.groundhog.mctools";
                apiname = "com.duowan.groundhog.mctools.activity.plug.PluginOutsideImportActivity";
                break;
            default:
                return open(path);
        }
        Intent intent = new Intent();
        intent.setClassName(pkgname, apiname);
        File file = new File(path);
        Uri data = Uri.fromFile(file);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(data);
        try
        {
            ctx.startActivity(intent);
            return true;
        }
        catch (ActivityNotFoundException ce)
        {
            return false;
        }
    }
}
