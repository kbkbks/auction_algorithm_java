
public class TimeStamp {
    public TimeStamp() {
        start = 0;
        end = 0;
    }

    /*
     * ��¼��ʼʱ��
     */
    public void getStartTime() {
        start = System.currentTimeMillis(); // ��¼��ʼʱ��

//        try {
//            Thread.sleep(5000);                     // �߳�˯��5�룬������ʱ�䲻��ôС
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    /*
     * ��¼����ʱ��
     */
    public void getEndTime() {
        end = System.currentTimeMillis();       // ��¼����ʱ��

        System.out.println("-------------------------------");
        System.out.print("�㷨ִ��ʱ�䣺");
        System.out.println(end - start);             // ����ó�����ʱ��
    }


    private long start;
    private long end;
}
