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
     * 向任务发布点传递所有初始化参数
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
     * 打印任务点存放的任务信息
     */
    public void printTaskRepository() {
        for (int i = 0; i < ConstValue.TASKCAPACITY; ++i) {
            System.out.println("-------------------------------");
            System.out.println("任务发布点存放的任务信息：");
            System.out.print("任务编号（任务发布点中）：");
            System.out.println(TaskRepository[i].TaskNo);
            System.out.print("任务发布点编号：");
            System.out.println(TaskRepository[i].PointNo);
            System.out.print("任务起点：");
            System.out.print(TaskRepository[i].BeginPoint[0]);
            System.out.print(" ");
            System.out.println(TaskRepository[i].BeginPoint[1]);
            System.out.print("任务终点：");
            System.out.print(TaskRepository[i].EndPoint[0]);
            System.out.print(" ");
            System.out.println(TaskRepository[i].EndPoint[1]);
        }

    }

    /*
     * 向外发送一个任务
     */
    public TaskTemplate sendTask(int i) {
        return TaskRepository[i];
    }


    private TaskTemplate[] TaskRepository;

}
