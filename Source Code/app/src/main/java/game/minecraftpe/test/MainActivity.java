package game.minecraftpe.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.io.File;

public class MainActivity extends Activity 
{
    private String outputpath;
    //private String defaultpath;
    private String leveloutputpath;
    private String sourceoutputpath;
    private String skinoutputpath;
    private String textureoutputpath;
    public int selectedmethod = 0;

    //最适合的游戏版本 如果全部适合可以留空 为 ""
    private String mcversionname = "0.15.0.1";
    //检测适合的游戏版本是最低适合的游戏版本(true)，还是唯一适合的游戏版本(false)
    private Boolean minversion = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		apppathset();
        buttonset();
        launcherscan();
        gamescan();
    }

    private void gameversionscan(String version)
    {
        Utils utils = new Utils();
        if (!utils.versionnameAvilible(this, "com.mojang.minecraftpe", version, minversion))
        {
            IOTool io = new IOTool();
            io.show(getApplicationContext(), getString(R.string.game_no_support) + version);
        }
    }

    private void game_optionfile_scan()
    {
        File opfile = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/com.mojang/minecraftpe/options.txt");
        if (!opfile.exists())
        {
            IOTool io = new IOTool();
            io.show(this, getString(R.string.game_option_no_found));
        }
    }

    private void launcherscan()
    {
        SupportScan sus = new SupportScan();
        if (sus.launcherscan(this) == false)
        {
            AlertDialog.Builder launcher = new AlertDialog.Builder(this);
            launcher.setTitle(R.string.modpe_powered_by);
            launcher.setMessage(R.string.system_message);
            launcher.setPositiveButton(R.string.done, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int choose)
                    {
                        SupportScan sus = new SupportScan();
                        sus.launcherget(getApplicationContext());
                    }
                });
            launcher.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int choose)
                    {
                        IOTool io = new IOTool();
                        io.show(getApplicationContext(), getString(R.string.system_no_download));
                    }
                });
            launcher.show();
        }
    }

    private void gamescan()
    {
        SupportScan sus = new SupportScan();
        if (sus.gamescan(this) == false)
        {
            AlertDialog.Builder game = new AlertDialog.Builder(this)
                .setTitle(R.string.game_powered_by)
                .setMessage(R.string.system_message)
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int choose)
                    {
                        SupportScan sus = new SupportScan();
                        sus.gameget(getApplicationContext());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int choose)
                    {
                        IOTool io = new IOTool();
                        io.show(getApplicationContext(), getString(R.string.system_no_download));
                    }
                });
            game.show();
        }
        else
        {
            gameversionscan(mcversionname);
            game_optionfile_scan();
        }
    }

    private void apppathset()
    {
        //仅隐藏modpe导出的文件夹
        String pn = this.getPackageName();
        outputpath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + pn + "/.File";
        //默认应用数据路径，暂不启用
        //defaultpath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + pn;
        textureoutputpath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/Texture";
        leveloutputpath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/com.mojang/minecraftWorlds/";
        sourceoutputpath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + pn + "/Source_File";
        skinoutputpath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/com.mojang/minecraftpe";
    }

    private void buttonset()
    {
        Button add = (Button) findViewById(R.id.button_add);
        Button about = (Button) findViewById(R.id.button_about);
        Button start = (Button) findViewById(R.id.button_start);
        add.setOnClickListener(new OnClickListener(){
                public void onClick(View v)
                {
                    set();
                }
            });
        about.setOnClickListener(new OnClickListener(){
                public void onClick(View v)
                {
                    Intent about_intent = new Intent(MainActivity.this, About.class);
                    startActivity(about_intent);
                }
            });
        start.setOnClickListener(new OnClickListener(){
                public void onClick(View v)
                {
                    gamestart();
                }
            });
    }

    private void set()
    {
        String[] methods = new String[]{getString(R.string.default_import_method),"Block Launcher","Block Launcher Pro",getString(R.string.mctool_name)};
        AlertDialog.Builder importset = new AlertDialog.Builder(this);
        importset.setTitle(R.string.import_set);
        importset.setSingleChoiceItems(methods, 0, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialoginterface, int which)
                {
                    selectedmethod = which;
                }
            });
        importset.setNegativeButton(R.string.cancel, null);
        importset.setPositiveButton(R.string.done, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialoginterface, int p)
                {
                    runOnUiThread(new Runnable(){
                            public void run()
                            {
                                ModPEManage modpeadd = new ModPEManage();
                                modpeadd.add(getApplicationContext(), outputpath, selectedmethod);
                                TextureManage textureadd = new TextureManage();
                                textureadd.add(getApplicationContext(), textureoutputpath, selectedmethod);
                                LevelManage leveladd = new LevelManage();
                                leveladd.add(getApplicationContext(), leveloutputpath);
                                SkinManage skinadd = new SkinManage();
                                skinadd.add(getApplicationContext(), skinoutputpath);
                                SourceManage sourceadd = new SourceManage();
                                sourceadd.add(getApplicationContext(), sourceoutputpath);

                            }});
                }
            });
        importset.show();
    }

    private boolean gamestart()
    {
        if (selectedmethod == 3)
        {
            Intent mctool_intent = new Intent();
            mctool_intent.setClassName("com.duowan.groundhog.mctools", "com.duowan.groundhog.mctools.activity.MainActivity");
            mctool_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try
            {
                startActivity(mctool_intent);
                finish();
                return true;
            }
            catch (ActivityNotFoundException ce)
            {
                return false;
            }
        }
        else 
        {
            Intent common_intent = new Intent();
            common_intent.setClassName("net.zhuoweizhang.mcpelauncher", "net.zhuoweizhang.mcpelauncher.LauncherAppActivity");
            common_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try
            {
                startActivity(common_intent);
                finish();
                return true;
            }
            catch (ActivityNotFoundException ce)
            {
                Intent pro_intent = new Intent();
                pro_intent.setClassName("net.zhuoweizhang.mcpelauncher.pro", "net.zhuoweizhang.mcpelauncher.LauncherAppActivity");
                pro_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try
                {
                    startActivity(pro_intent);
                    finish();
                    return true;
                }
                catch (ActivityNotFoundException pe)
                {
                    Intent game_intent = new Intent();
                    game_intent.setClassName("com.mojang.minecraftpe", ".MainActivity");
                    game_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try
                    {
                        startActivity(game_intent);
                        finish();
                        return true;
                    }
                    catch (ActivityNotFoundException ge)
                    {
                        return false;
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        // TODO: Implement this method
        IOTool filedelete = new IOTool();
        filedelete.delete(outputpath);
        super.onDestroy();
    }

    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK)  
        {  
            finish();
        }  
        return false;  
    }
}
