package game.minecraftpe.test;
import android.content.Context;
import android.net.Uri;
import android.content.Intent;
import android.content.pm.PackageManager;
import java.util.List;
import android.content.pm.PackageInfo;

public class SupportScan
{
    public boolean launcherscan(Context ctx)
    {
        Utils utils = new Utils();
        if (!utils.isAvilible(ctx,"net.zhuoweizhang.mcpelauncher") && !utils.isAvilible(ctx,"net.zhuoweizhang.mcpelauncher.pro"))
        { 
            return false;
        }
        else
        {
            return true;
        }
    }
    
    public void launcherget(Context ctx)
    {
        Uri uri = Uri.parse("market://details?id=net.zhuoweizhang.mcpelauncher");
        //使用以下代码可以设置直接从网络下载
        // Uri uri = Uri.parse("http://");
        Intent market = new Intent(Intent.ACTION_VIEW, uri);
        market.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(market); 
    }
    
    public boolean gamescan(Context ctx)
    {
        Utils utils = new Utils();
        if (!utils.isAvilible(ctx,"com.mojang.minecraftpe"))
        { 
            return false;
        }
        else
        {
            return true;
        }
    }

    public void gameget(Context ctx)
    {
        Uri uri = Uri.parse("market://details?id=com.mojang.minecraftpe");
        // Uri uri = Uri.parse("http://");
        Intent market = new Intent(Intent.ACTION_VIEW, uri);
        market.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(market); 
    }
}
