package com.prp.detectionsystemclient.function;

import android.content.Context;
import com.prp.detectionsystemclient.MyApplication;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TraceRoute {

    private String app_path;
    private static TraceRoute traceRoute = new TraceRoute();

    public static TraceRoute getInstance(){
        return traceRoute;
    }

    public TraceRoute(){
        app_path = MyApplication.instance.getContext().getFilesDir().getAbsolutePath();
        varifyFile(MyApplication.instance.getContext(), "busybox");
        varifyFile(MyApplication.instance.getContext(), "traceroute");
    }

    /** 验证文件是否存在, 如果不存在就拷贝 */
    public void varifyFile(Context context, String fileName) {


        try {
        	/* 查看文件是否存在, 如果不存在就会走异常中的代码 */
            context.openFileInput(fileName);
        } catch (FileNotFoundException notfoundE) {
            try {
            	/* 拷贝文件到app安装目录的files目录下 */
                copyFromAssets(context, fileName, fileName);
                /* 修改文件权限脚本 */
                String script = "chmod 700 " + app_path + "/" + fileName;
                /* 执行脚本 */
                exe(script);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** 将文件从assets目录中拷贝到app安装目录的files目录下 */
    private void copyFromAssets(Context context, String source,
                                String destination) throws IOException {
		/* 获取assets目录下文件的输入流 */
        InputStream is = context.getAssets().open(source);
		/* 获取文件大小 */
        int size = is.available();
		/* 创建文件的缓冲区 */
        byte[] buffer = new byte[size];
		/* 将文件读取到缓冲区中 */
        is.read(buffer);
		/* 关闭输入流 */
        is.close();
		/* 打开app安装目录文件的输出流 */
        FileOutputStream output = context.openFileOutput(destination,
                Context.MODE_PRIVATE);
		/* 将文件从缓冲区中写出到内存中 */
        output.write(buffer);
		/* 关闭输出流 */
        output.close();
    }

    /** 执行 shell 脚本命令 */
    public List<String> exe(String cmd) {
		/* 获取执行工具 */
        Process process = null;
		/* 存放脚本执行结果 */
        List<String> list = new ArrayList<String>();
        try {  
        	/* 获取运行时环境 */
            Runtime runtime = Runtime.getRuntime();
        	/* 执行脚本 */
            process = runtime.exec(cmd); 
            /* 获取脚本结果的输入流 */
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            /* 逐行读取脚本执行结果 */
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
            br.close();
            /*读取错误信息*/
            is = process.getErrorStream();
            br = new BufferedReader(new InputStreamReader(is));
            line = null;
            /* 逐行读取脚本执行结果 */
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
