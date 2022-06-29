package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SystemUtil {

    private static SystemInfo systemInfo = new SystemInfo();
    private static HardwareAbstractionLayer hardware = systemInfo.getHardware();
    private static OperatingSystem operatingSystem = systemInfo.getOperatingSystem();

    public static String getCPUUtilization() {
        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        // 获取CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice =ticks[CentralProcessor.TickType.NICE.getIndex()] -prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq =ticks[CentralProcessor.TickType.IRQ.getIndex()] -prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq =ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] -prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal =ticks[CentralProcessor.TickType.STEAL.getIndex()] -prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys =ticks[CentralProcessor.TickType.SYSTEM.getIndex()] -prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user =ticks[CentralProcessor.TickType.USER.getIndex()] -prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait =ticks[CentralProcessor.TickType.IOWAIT.getIndex()] -prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle =ticks[CentralProcessor.TickType.IDLE.getIndex()] -prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        log.info("cpuNum-核数" + processor.getLogicalProcessorCount());
        //cpu用户使用率
        log.info("CPU使用率" + new DecimalFormat("#.##%").format((totalCpu - idle - iowait) * 1.0 / totalCpu));
        return new DecimalFormat("#.##%").format((totalCpu - idle - iowait) * 1.0 / totalCpu);
    }

    public static String getMemUtilization() {
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        //总内存
        long totalByte = memory.getTotal();
        //剩余
        long acaliableByte = memory.getAvailable();
        //使用率
        return new DecimalFormat("#.##%").format((totalByte - acaliableByte) * 1.0 / totalByte);
    }

    public static String getDiskUtilization() {
        FileSystem fileSystem = operatingSystem.getFileSystem();
        final List<OSFileStore> fsArray = fileSystem.getFileStores();
        long totalSpace = 0;
        long usableSpace = 0;
        for (OSFileStore fs : fsArray) {
            totalSpace += fs.getTotalSpace();
            usableSpace += fs.getUsableSpace();
            //盘符路径
            log.info("dirName" + fs.getMount());
            //盘符类型
            log.info("sysTypeName" + fs.getType());
            //文件类型
            log.info("typeName" + fs.getName());
            //总大小
            log.info("total" + formatByte(fs.getTotalSpace()));
            //剩余大小
            log.info("free" + formatByte(fs.getUsableSpace()));
            //已经使用量
            log.info("used" + formatByte(fs.getTotalSpace() - fs.getUsableSpace()));
            if (fs.getTotalSpace() == 0) {
                //资源的使用率
                log.info("usage" + 0);
            } else {
                log.info("usage" + new DecimalFormat("#.##%").format((fs.getTotalSpace() - fs.getUsableSpace()) * 1.0 / fs.getTotalSpace()));
            }
        }
        log.info("总磁盘利用率：" + new DecimalFormat("#.##%").format((totalSpace - usableSpace) * 1.0 / totalSpace));
        return new DecimalFormat("#.##%").format((totalSpace - usableSpace) * 1.0 / totalSpace);
    }

    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        return "";
    }
//    public static void Mapdisk() {
//        // 磁盘使用情况
//        File[] files = File.listRoots();
//        for (File file : files) {
//            String total = new DecimalFormat("#.#").format(file.getTotalSpace() * 1.0 / 1024 / 1024 / 1024) + "G";
//            String free = new DecimalFormat("#.#").format(file.getFreeSpace() * 1.0 / 1024 / 1024 / 1024) + "G";
//            String un = new DecimalFormat("#.#").format(file.getUsableSpace() * 1.0 / 1024 / 1024 / 1024) + "G";
//            log.info("总空间"+total);
//            log.info("可用空间"+un);
//            log.info("空闲空间"+free);
//        }
//    }
    private static String formatByte(long byteNumber) {
        //换算单位
        double FORMAT = 1024.0;
        double kbNumber = byteNumber / FORMAT;
        if (kbNumber < FORMAT) {
            return new DecimalFormat("#.##KB").format(kbNumber);
        }
        double mbNumber = kbNumber / FORMAT;
        if (mbNumber < FORMAT) {
            return new DecimalFormat("#.##MB").format(mbNumber);
        }
        double gbNumber = mbNumber / FORMAT;
        if (gbNumber < FORMAT) {
            return new DecimalFormat("#.##GB").format(gbNumber);
        }
        double tbNumber = gbNumber / FORMAT;
        return new DecimalFormat("#.##TB").format(tbNumber);
    }
}
