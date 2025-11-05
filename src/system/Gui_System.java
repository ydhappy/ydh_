package system;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public class Gui_System {
  public static long getUsedMemoryMB() {
    return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
  }
  
  public static long getTotalMemoryMB() {
    return Runtime.getRuntime().maxMemory() / 1024L / 1024L;
  }
  
  public static long getMemoryMB() {
    return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
  }
  
  public static long getToTalMemoryMB() {
    return Runtime.getRuntime().totalMemory() / 1024L / 1024L;
  }
  
  public static long getFreeMemoryMB() {
    return Runtime.getRuntime().freeMemory() / 1024L / 1024L;
  }
  
  public static double getUseCpu() {
    OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
    return operatingSystemMXBean.getSystemCpuLoad() * 100.0D;
  }
  
  public static int getThread() {
    return Thread.activeCount();
  }
}
