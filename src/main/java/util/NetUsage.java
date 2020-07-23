
import java.io.BufferedReader;
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.PrintWriter;  
import java.io.StringWriter;

/** 
 * 采集网络带宽使用率 
 */  
public class NetUsage{  


    private static NetUsage INSTANCE = new NetUsage();  
    private final static float TotalBandwidth = 1000;   //网口带宽,Mbps  这里需要替换为自己的带宽

    private NetUsage(){  

    }  

    public static NetUsage getInstance(){  
        return INSTANCE;  
    }  

    /** 
     * @Purpose:采集网络带宽使用率 
     * @return float,网络带宽使用率,小于1
     */  
    public float get() {  
        System.out.println("开始收集带宽率");
        float netUsage = 0.0f;  
        Process pro1,pro2;  
        Runtime r = Runtime.getRuntime();  
        try {  
            String command = "cat /proc/net/dev";  
            //第一次采集流量数据  
            long startTime = System.currentTimeMillis();  
            pro1 = r.exec(command);  
            BufferedReader in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));  
            String line = null;  
            long inSize1 = 0, outSize1 = 0;  
            while((line=in1.readLine()) != null){     
                line = line.trim();  
                if(line.startsWith("eth0")){  

                    System.out.println(line);
                    String[] temp = line.split("\\s+");
                    System.out.println("temp: "+temp.length+"temp[0]="+temp[0]);
                    //这里可能因为不同操作系统 展示的结果不同，导致下标不同，
                    //自己对照 cat /proc/net/dev 该指令执行后展示出的结构 找到Receive bytes 是数组里第几个元素，替换下标即可
                    inSize1 = Long.parseLong(temp[1].substring(5)); //Receive bytes,单位为Byte  
                    outSize1 = Long.parseLong(temp[9]);             //Transmit bytes,单位为Byte  
                    break;  
                }                 
            }     
            in1.close();  
            pro1.destroy();  
            try {  
                Thread.sleep(1000);  
            } catch (InterruptedException e) {  
                StringWriter sw = new StringWriter();  
                e.printStackTrace(new PrintWriter(sw));  
                System.out.println("NetUsage休眠时发生InterruptedException. " + e.getMessage());  
                System.out.println(sw.toString());
            }  
            //第二次采集流量数据  
            long endTime = System.currentTimeMillis();  
            pro2 = r.exec(command);  
            BufferedReader in2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));  
            long inSize2 = 0 ,outSize2 = 0;  
            while((line=in2.readLine()) != null){     
                line = line.trim();  
                if(line.startsWith("eth0")){  

                    System.out.println(line);
                    String[] temp = line.split("\\s+");
                    //这里数组下标也需要修改 
                    inSize2 = Long.parseLong(temp[1].substring(5));  
                    outSize2 = Long.parseLong(temp[9]);  
                    break;  
                }                 
            }  
            if(inSize1 != 0 && outSize1 !=0 && inSize2 != 0 && outSize2 !=0){  
                float interval = (float)(endTime - startTime)/1000;  
                //网口传输速度,单位为bps  
                float curRate = (float)(inSize2 - inSize1 + outSize2 - outSize1)*8/(1000000*interval);  
                netUsage = curRate/TotalBandwidth;  
                System.out.println("本节点网口速度为: " + curRate + "Mbps");
                System.out.println("本节点网络带宽使用率为: " + netUsage);

            }                 
            in2.close();  
            pro2.destroy();  
        } catch (IOException e) {  
            StringWriter sw = new StringWriter();  
            e.printStackTrace(new PrintWriter(sw));  
            System.out.println("NetUsage发生InstantiationException. " + e.getMessage());
            System.out.println(sw.toString());
        }     
        return netUsage;  
    }  

    /** 
     * @param args 
     * @throws InterruptedException  
     */  
    public static void main(String[] args) throws InterruptedException {  
        while(true){
            System.out.println(NetUsage.getInstance().get());
            Thread.sleep(2000);
        }
    }  
}