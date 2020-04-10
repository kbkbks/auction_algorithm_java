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
     * ������˴������г�ʼ������
     */
    public void setInitialValue(int r_NO, double location_x, double location_y) {
        Robot_No = r_NO;
        RobotLocation[0] = location_x;
        RobotLocation[1] = location_y;
    }

    /*
     * ��ӡ�����������Ϣ
     */
    public void printRobotInfo() {
        System.out.println("-------------------------------");
        System.out.print("�����˱�ţ�");
        System.out.println(Robot_No);
        System.out.print("������λ�ã�");
        System.out.print(RobotLocation[0]);
        System.out.print(" ");
        System.out.println(RobotLocation[1]);
    }

    /*
     * ��������˼�ֵ�б�
     */
    public void calculateValue(Tasklist tasklist, int i) {
        TaskTemplate TmpTask;   //�������ֵ������
        TmpTask = tasklist.sendTaskQueue(i);
        GenerateCalculate1(TmpTask);
    }

    /*
     * ���������ֵ,���� ·�̡���������������
     */
    private void GenerateCalculate1(TaskTemplate TmpTask) {
        double Distance; //��������������·��
        double Value;    //�����ֵ
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
     * ���������ֵ,���� ·��
     */
    private void GenerateCalculate2(TaskTemplate TmpTask) {
        double Distance; //��������������·��
        double Value;    //�����ֵ

        Distance = Math.sqrt(Math.pow(TmpTask.BeginPoint[0] - RobotLocation[0], 2) +
                Math.pow(TmpTask.BeginPoint[1] - RobotLocation[1], 2)) +
                Math.sqrt(Math.pow(TmpTask.EndPoint[0] - TmpTask.BeginPoint[0], 2) +
                        Math.pow(TmpTask.EndPoint[1] - TmpTask.BeginPoint[1], 2));
        Value = 1 / Distance;

        ValueList.add(Value);
    }

    /*
     * ���ͻ����˱��
     */
    public int sendRobotNo() {
        return Robot_No;
    }

    /*
     * ���ͻ����˼�ֵ�б��������ֵ
     */
    public double sendValuelist(int i) {
        return ValueList.get(i);
    }

    /*
     * ���»�����λ������
     */
    public void updateRobotLocation(double location_x, double location_y) {
        RobotLocation[0] = location_x;
        RobotLocation[1] = location_y;
    }

    /*
     * ��ռ�ֵ�б�
     */
    public void deleteValueList() {
        ValueList.clear();
    }

    /*
     * ˢ�µ���
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
     * �����䵽�����񱣴浽����ִ���б�
     */
    public void saveTaskExecutionQueue(TaskTemplate TmpTask) {
        TaskExecutionQueue.add(TmpTask);
    }

    /*
     * ���»�����λ�����꣨��������ִ�ж��У�
     */
    public void updateRobotLocation() {
        if (TaskExecutionQueue.size() != 0) {
            RobotLocation[0] = TaskExecutionQueue.get(TaskExecutionQueue.size() - 1).EndPoint[0];
            RobotLocation[1] = TaskExecutionQueue.get(TaskExecutionQueue.size() - 1).EndPoint[1];
        } else {

        }
    }


    private int Robot_No;   //�����˱��
    private double[] RobotLocation;  //������λ��
    public ArrayList<Double> ValueList;    //�����������ֵ�б�
    private double electricity;   //�����˵���
    private ArrayList<TaskTemplate> TaskExecutionQueue; //����������ִ�ж���
}
