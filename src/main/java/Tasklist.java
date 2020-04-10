import java.util.ArrayList;

public class Tasklist {
    public Tasklist() {
        TaskNumber = 0;
        TaskQueue = new ArrayList<TaskTemplate>();
    }

    /*
     * 从任务发布点接收任务
     */
    public void getTask(Taskpoint taskpoint, int i) {
        TaskQueue.add(taskpoint.sendTask(i));
        ++TaskNumber;
    }

    /*
     * 发送任务列表中的任务数量
     */
    public int sendTaskNumber() {
        return TaskNumber;
    }

    /*
     * 发送任务列表中的任务
     */
    public TaskTemplate sendTaskQueue(int i)
    {
        return TaskQueue.get(i);
    }

    /*
     * 从任务发布点接收任务
     */
    public void getTask(Tasklist tasklist, int i) {
        TaskQueue.add(tasklist.sendTaskQueue(i));
        ++TaskNumber;
    }


    private int TaskNumber;
    private ArrayList<TaskTemplate> TaskQueue; //任务列表

}
