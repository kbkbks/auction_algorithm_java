import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import javafx.concurrent.Task;

public class Taskpoint {
    public Taskpoint() {
        TaskRepository = new TaskTemplate[ConstValue.TASKCAPACITY];
        for (int i = 0; i < ConstValue.TASKCAPACITY; ++i) {
            TaskRepository[i] = new TaskTemplate();
        }
    }

    /*
     * �����񷢲��㴫�����г�ʼ������
     */
    public void setInitialvalue(int pointnum, double[][] bp, double[][] ep) {
        for (int i = 0; i < ConstValue.TASKCAPACITY; ++i) {
            TaskRepository[i].TaskNo = i;
            TaskRepository[i].PointNo = pointnum;
            TaskRepository[i].BeginPoint[0] = bp[i][0];
            TaskRepository[i].BeginPoint[1] = bp[i][1];
            TaskRepository[i].EndPoint[0] = ep[i][0];
            TaskRepository[i].EndPoint[1] = ep[i][1];

        }
    }

    /*
     * ��ӡ������ŵ�������Ϣ
     */
    public void printTaskRepository() {
        for (int i = 0; i < ConstValue.TASKCAPACITY; ++i) {
            System.out.println("-------------------------------");
            System.out.println("���񷢲����ŵ�������Ϣ��");
            System.out.print("�����ţ����񷢲����У���");
            System.out.println(TaskRepository[i].TaskNo);
            System.out.print("���񷢲����ţ�");
            System.out.println(TaskRepository[i].PointNo);
            System.out.print("������㣺");
            System.out.print(TaskRepository[i].BeginPoint[0]);
            System.out.print(" ");
            System.out.println(TaskRepository[i].BeginPoint[1]);
            System.out.print("�����յ㣺");
            System.out.print(TaskRepository[i].EndPoint[0]);
            System.out.print(" ");
            System.out.println(TaskRepository[i].EndPoint[1]);
        }

    }

    /*
     * ���ⷢ��һ������
     */
    public TaskTemplate sendTask(int i) {
        return TaskRepository[i];
    }


    private TaskTemplate[] TaskRepository;

}
