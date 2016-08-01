package game.minecraftpe.test;

import android.app.*;
import android.content.*;
import android.net.*;
import android.text.*;
import java.io.*;
import java.util.*;
import android.util.*;

public class SkinManage
{
    private IOTool io = new IOTool();
    private Context ctx;

    public void add(Context ctx, String str)
    {
        this.ctx = ctx;
        try
        {
            String[] list_file = ctx.getAssets().list("Skin");
            for (int i = 0;i < list_file.length ;i++)
            {
                if (list_file.length == 0)
                {
                    break;
                }
                //单皮肤文件限制
                if (list_file.length > 1)
                {
                    io.show(ctx, ctx.getString(R.string.skin_find_err));
                    break;
                }

                //强行代替启动器api
                File file = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/com.mojang/minecraftpe/custom.png");
                if (file.exists())
                {
                    File backupfile = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/com.mojang/minecraftpe/" + io.getFileMD5String(file) + ".png");
                    if (!backupfile.exists())
                    {
                        file.renameTo(backupfile);
                    }
                    file.delete();
                }
                File opfile = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/com.mojang/minecraftpe/options.txt");
                if (opfile.exists())
                {
                    String optionfile = io.readfile(ctx, new FileInputStream(new java.io.File(str + "/" + "options.txt")));
                    String[] optionline = optionfile.split("\n");
                    for (int oi = 0;oi < optionline.length;oi++)
                    {
                        if (optionline[oi].length() < optionline[oi].lastIndexOf(':') || optionline[oi].lastIndexOf(':') < 0)
                        {
                            io.show(ctx,ctx.getString(R.string.skin_options_can_not_use));
                            return;
                        }
                        else
                        {
                            String optiondata = optionline[oi].substring(0, optionline[oi].lastIndexOf(':'));
                            if (optiondata.equalsIgnoreCase("game_skintypefull") || optiondata.equalsIgnoreCase("game_lastcustomskinnew"))
                            {
                                optionline[oi] = optiondata + ":" + "Standard_Custom";
                            }
                        }
                    }
                    optionfile = "";
                    for (int pi = 0;pi < optionline.length;pi++)
                    {
                        optionfile += optionline[pi] + "\n";
                    }
                    io.writefile(str + "/" + "options.txt", optionfile);

                    if (!io.copy(ctx, "Skin" + "/" + list_file[i], str, "custom.png"))
                    {
                        io.show(ctx, ctx.getString(R.string.file_no_export));
                        break;
                    }
                    else
                    {
                        io.show(ctx, ctx.getString(R.string.skin_export_ok));
                    }
                }
                else
                {
                    io.show(ctx, ctx.getString(R.string.game_option_no_found));
                    break;
                }
            }
        }
        catch (IOException e)
        {
            io.show(ctx, ctx.getString(R.string.file_no_found));
        }
    }


}
