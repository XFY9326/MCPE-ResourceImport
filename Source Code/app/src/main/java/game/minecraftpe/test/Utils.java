package game.minecraftpe.test;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import java.util.logging.Handler;
import android.os.Message;
import android.content.pm.PackageManager;
import java.util.List;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;

public class Utils extends Application
{
    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    public boolean isAvilible(Context ctx, String packageName)
    {
        PackageManager packageManager = ctx.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++)
        {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    public boolean versionnameAvilible(Context ctx, String packageName, String versionname, Boolean minversion)
    {
        if (versionname == "")
        {
            return true;
        }
        PackageManager packageManager = ctx.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++)
        {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
            {
                if (minversion == true)
                {
                    String now_version = pinfo.get(i).versionName;
                    int now_version_int = versionfix(now_version);
                    int version_int = versionfix(versionname);
                    if(now_version_int == 0 || version_int == 0)
                    {
                        IOTool io = new IOTool();
                        io.show(ctx,ctx.getString(R.string.game_version_set_err));
                        break;
                    }
                    if (version_int <= now_version_int)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    if (pinfo.get(i).versionName.equalsIgnoreCase(versionname))
                    {
                        return true;
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }
        return false;
    }

    private int versionfix(String str)
    {
        if (str.indexOf(".") >= 0)
        {
            String[] fix = str.split("\\.");
            int result = 0;
            for (int i = 1;i <= fix.length;i++)
            {
                result += Integer.parseInt(fix[i - 1]) * (int)Math.pow(100, fix.length - i + 1);
            }
            return result;
        }
        else
        {
            return 0;
        }
    }
}
