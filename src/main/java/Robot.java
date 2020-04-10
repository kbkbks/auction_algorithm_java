import com.sun.corba.se.impl.io.ValueUtility;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import javafx.concurrent.Task;

import java.util.ArrayList;

public class Robot {
    public Robot() {
        Robot_No = -1;
        RobotLocation = new double[2];
        ValueList = new ArrayList<Double>();
        electricity = 100;
        TaskExecutionQueue = new ArrayList<TaskTemplate>();
    }

    /*
     * 向机器人传递所有初始化参数
     */
    public void setInitialValue(int r_NO, double location_x, double location_y) {
        Robot_No = r_NO;
        RobotLocation[0] = location_x;
        RobotLocation[1] = location_y;
    }

    /*
     * 打印机器人相关信息
     */
    public void printRobotInfo() {
        System.out.println("-------------------------------");
        System.out.print("机器人编号：");
        System.out.println(Robot_No);
        System.out.print("机器人位置：");
        System.out.print(RobotLocation[0]);
        System.out.print(" ");
        System.out.println(RobotLocation[1]);
    }

    /*
     * 计算机器人价值列表
     */
    public void calculateValue(Tasklist tasklist, int i) {
        TaskTemplate TmpTask;   //待计算价值的任务
        TmpTask = tasklist.sendTaskQueue(i);
        GenerateCalculate1(TmpTask);
    }

    /*
     * 计算任务价值,包括 路程、电量、任务数量
     */
    private void GenerateCalculate1(TaskTemplate TmpTask) {
        double Distance; //机器人完成任务的路程
        double Value;    //任务价值
        int TaskExecutionQueueNum = TaskExecutionQueue.size();

        Distance = Math.sqrt(Math.pow(TmpTask.BeginPoint[0] - RobotLocation[0], 2) +
                Math.pow(TmpTask.BeginPoint[1] - RobotLocation[1], 2)) +
                Math.sqrt(Math.pow(TmpTask.EndPoint[0] - TmpTask.BeginPoint[0], 2) +
                        Math.pow(TmpTask.EndPoint[1] - TmpTask.BeginPoint[1], 2));

        //Value = ((electricity - 19d) / 80d * ConstValue.ParaElectricity) / (Distance *ConstValue.ParaDistance + TaskExecutionQueueNum * ConstValue.ParaTaskNumber);
        Value = (electricity * ConstValue.ParaElectricity) / (Distance * ConstValue.ParaDistance + TaskExecutionQueueNum * ConstValue.ParaTaskNumber);
        ValueList.add(Value);
    }

    /*
     * 计算任务价值,包括 路程
     */
    private void GenerateCalculate2(TaskTemplate TmpTask) {
        double Distance; //机器人完成任务的路程
        double Value;    //任务价值

        Distance = Math.sqrt(Math.pow(TmpTask.BeginPoint[0] - RobotLocation[0], 2) +
                Math.pow(TmpTask.BeginPoint[1] - RobotLocation[1], 2)) +
                Math.sqrt(Math.pow(TmpTask.EndPoint[0] - TmpTask.BeginPoint[0], 2) +
                        Math.pow(TmpTask.EndPoint[1] - TmpTask.BeginPoint[1], 2));
        Value = 1 / Distance;

        ValueList.add(Value);
    }

    /*
     * 发送机器人编号
     */
    public int sendRobotNo() {
        return Robot_No;
    }

    /*
     * 发送机器人价值列表中任务价值
     */
    public double sendValuelist(int i) {
        return ValueList.get(i);
    }

    /*
     * 更新机器人位置坐标
     */
    public void updateRobotLocation(double location_x, double location_y) {
        RobotLocation[0] = location_x;
        RobotLocation[1] = location_y;
    }

    /*
     * 清空价值列表
     */
    public void deleteValueList() {
        ValueList.clear();
    }

    /*
     * 刷新电量
     */
    public double updateElectricity() {
        if (electricity > 20) {
            electricity = electricity - (Math.random() * 5d + 1d);
        } else {
            electricity = 100;
        }

        return electricity;
    }

    /*
     * 将分配到的任务保存到任务执行列表
     */
    public void saveTaskExecutionQueue(TaskTemplate TmpTask) {
        TaskExecutionQueue.add(TmpTask);
    }

    /*
     * 更新机器人位置坐标（依靠任务执行队列）
     */
    public void updateRobotLocation() {
        if (TaskExecutionQueue.size() != 0) {
            RobotLocation[0] = TaskExecutionQueue.get(TaskExecutionQueue.size() - 1).EndPoint[0];
            RobotLocation[1] = TaskExecutionQueue.get(TaskExecutionQueue.size() - 1).EndPoint[1];
        } else {

        }
    }


    private int Robot_No;   //机器人编号
    private double[] RobotLocation;  //机器人位置
    public ArrayList<Double> ValueList;    //机器人任务价值列表
    private double electricity;   //机器人电量
    private ArrayList<TaskTemplate> TaskExecutionQueue; //机器人任务执行队列
}
