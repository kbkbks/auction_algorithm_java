
public class TimeStamp {
    public TimeStamp() {
        start = 0;
        end = 0;
    }

    /*
     * 记录起始时间
     */
    public void getStartTime() {
        start = System.currentTimeMillis(); // 记录起始时间

//        try {
//            Thread.sleep(5000);                     // 线程睡眠5秒，让运行时间不那么小
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    /*
     * 记录结束时间
     */
    public void getEndTime() {
        end = System.currentTimeMillis();       // 记录结束时间

        System.out.println("-------------------------------");
        System.out.print("算法执行时间：");
        System.out.println(end - start);             // 相减得出运行时间
    }


    private long start;
    private long end;
}
