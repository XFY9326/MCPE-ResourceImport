package game.minecraftpe.test;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.JSONObject;

public class TextureManage
{
    private IOTool io = new IOTool();
    private Context ctx;
    private String textureoutputpath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/com.mojang/resource_packs";
    private String texturesetfile = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/com.mojang/minecraftpe/resource_packs.txt";

    public void add(Context ctx, String str, int importmethodset)
    {
        this.ctx = ctx;
        Utils utils = new Utils();
        try
        {
            String[] list_file = ctx.getAssets().list("Texture");
            for (int i = 0;i < list_file.length ;i++)
            {
                if (list_file.length == 0)
                {
                    break;
                }
                //单材质文件限制
                if (list_file.length > 1)
                {
                    io.show(ctx, ctx.getString(R.string.texture_find_err));
                    break;
                }
                if (!utils.versionnameAvilible(ctx, "com.mojang.minecraftpe", "0.15.0.0", true))
                {
                    File file = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/Texture/Game_Texture.zip");
                    if (file.exists())
                    {
                        File backupfile = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/Texture/" + io.getFileMD5String(file) + ".zip");
                        if (!backupfile.exists())
                        {
                            file.renameTo(backupfile);
                        }
                        file.delete();
                    }

                    if (!io.copy(ctx, "Texture" + "/" + list_file[i], str, "Game_Texture.zip"))
                    {
                        io.show(ctx, ctx.getString(R.string.file_no_export));
                        break;
                    }
                    else
                    {
                        if (open(str + "/" + "Game_Texture.zip", importmethodset))
                        {
                            //启动器材质包导入api目前存在问题
                            //io.show(ctx,ctx.getString(R.string.texture_export_ok));
                            io.show(ctx,ctx.getString(R.string.texture_no_support_blocklauncher));
                        }
                        else
                        {
                            io.show(ctx, ctx.getString(R.string.file_no_open));
                            break;
                        }
                    }
                }
                else //材质包json读取获取名称
                {
                    File file = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/com.mojang/resource_packs/Game_Texture.zip");
                    if (file.exists())
                    {
                        File backupfile = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/com.mojang/resource_packs/" + io.getFileMD5String(file) + ".zip");
                        if (!backupfile.exists())
                        {
                            file.renameTo(backupfile);
                        }
                        file.delete();
                    }
                    try
                    {
                        if (!io.copy(ctx, "Texture" + "/" + list_file[i], textureoutputpath, "Game_Texture.zip"))
                        {
                            io.show(ctx, ctx.getString(R.string.file_no_export));
                            break;
                        }
                        else
                        {
                            try
                            {
                                String jsondata = io.readZipFile(textureoutputpath + "/Game_Texture.zip", "resources.json");
                                JSONObject json = new JSONObject(jsondata);
                                String texturename = json.getString("pack_id");
                                if (new_open(str + "/" + "Game_Texture.zip", texturename))
                                {
                                    io.show(ctx, ctx.getString(R.string.texture_export_ok));
                                }
                                else
                                {
                                    io.show(ctx, ctx.getString(R.string.file_no_open));
                                    break;
                                }
                            }
                            catch (Exception e)
                            {
                                io.readZipFile(textureoutputpath + "/Game_Texture.zip", "pack.mcmeta");
                                if (open(str + "/" + "Game_Texture.zip", importmethodset))
                                {
                                    //启动器材质包导入api目前存在问题
                                    //io.show(ctx,ctx.getString(R.string.texture_export_ok));
                                    io.show(ctx,ctx.getString(R.string.texture_no_support_blocklauncher));
                                }
                                else
                                {
                                    io.show(ctx, ctx.getString(R.string.file_no_open));
                                    break;
                                }

                            }
                        }
                    }
                    catch (Exception e)
                    {
                        io.show(ctx, ctx.getString(R.string.texture_build_err));
                    }
                }
            }
        }
        catch (IOException e)
        {
            io.show(ctx, ctx.getString(R.string.file_no_found));
        }
    }

    //启动器导入方式
    private boolean open(String path, int id)
    {
        String pkgname;
        String apiname;
        switch (id)
        {
            default:
                return defaultopen(path);
            case 1:
                pkgname = "net.zhuoweizhang.mcpelauncher";
                apiname = "net.zhuoweizhang.mcpelauncher.api.ImportTexturepackActivity";
                break;
            case 2:
                pkgname = "net.zhuoweizhang.mcpelauncher.pro";
                apiname = "net.zhuoweizhang.mcpelauncher.api.ImportTexturepackActivity";
                break;
            case 3:
                return mctoolopen(path);
        }
        Intent intent = new Intent();
        intent.setClassName(pkgname, apiname);
        File files = new File(path);
        Uri datas = Uri.fromFile(files);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(datas);
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

    private boolean mctoolopen(String path)
    {
        io.show(ctx, ctx.getString(R.string.texture_no_support_mctools));
        String mctoolpath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/mctools/texture/";
        return false;
    }

    private boolean defaultopen(String path)
    {
        Intent common_intent = new Intent();
        common_intent.setClassName("net.zhuoweizhang.mcpelauncher", "net.zhuoweizhang.mcpelauncher.api.ImportTexturepackActivity");
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
            pro_intent.setClassName("net.zhuoweizhang.mcpelauncher.pro", "net.zhuoweizhang.mcpelauncher.api.ImportTexturepackActivity");
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

    //0.15.0.1以上自带导入方式
    private boolean new_open(String path, String texturename)
    {
        File file = new File(texturesetfile);
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
            }
        }
        try
        {
            String data = io.readfile(ctx, new FileInputStream(file));
            if (data.indexOf("\n") < 0||data.equalsIgnoreCase("Minecraft"))
            {
                io.writefile(texturesetfile, texturename + "\nMinecraft");
            }
            else
            {
                String[] textures = data.split("\n");
                String output = "";
                for (int i = 0;i < textures.length;i++)
                {
                    if (textures[i] != "Minecraft")
                    {
                        output += textures[i] + "\n";
                    }
                }
                output += texturename + "\nMinecraft";
                io.writefile(texturesetfile, output);
            }
        }
        catch (FileNotFoundException e)
        {
            return false;
        }
        return true;
    }
}

