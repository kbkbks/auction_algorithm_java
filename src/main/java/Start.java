/*
 * Xinyan Han
 * 2020-03-15
 * metatron96@126.com
 * info:
 * -------------------------------------------------------------------------------------------------------------------------
 * ����ʽ�����㷨���������򣬵�ǰ�����߳�ִ�С�
 * ����ֱ�Ӵ���ں���main���롣
 * ��ֵ���������֣�
 * GenerateCalculate1���� ������������룬��������
 * GenerateCalculate2���� �������
 *
 * 3��15��������������������С����������������������ڽӱ����Լ�һϵ����ش��롣
 */

public class Start {
    public static void main(String[] args) {
        System.out.println("****************************");
        System.out.println("�ֲ�ʽ�������뾺�Ļ����㷨");
        System.out.println("****************************");
        System.out.println();

        //���������ʼ������
        double[][][] BeginInitial = new double[ConstValue.TASKPOINT][ConstValue.TASKCAPACITY][2];
        double[][][] EndInitial = new double[ConstValue.TASKPOINT][ConstValue.TASKCAPACITY][2];
        for (int j = 0; j < ConstValue.TASKPOINT; ++j) {
            for (int i = 0; i < ConstValue.TASKCAPACITY; ++i) {
                BeginInitial[j][i][0] = j;  //�������x����
                BeginInitial[j][i][1] = 3;  //�������y����
                EndInitial[j][i][0] = j;    //�����յ�x����
                EndInitial[j][i][1] = 4 + i;    //�����յ�y����
            }
        }

        //�������񷢲��㲢��ʼ��
        Taskpoint[] taskpoint = new Taskpoint[ConstValue.TASKPOINT];
        for (int i = 0; i < ConstValue.TASKPOINT; ++i) {
            taskpoint[i] = new Taskpoint();
            taskpoint[i].setInitialvalue(i, BeginInitial[i], EndInitial[i]);
            //taskpoint[i].printTaskRepository();
        }

        //���������б�
        int TasklistNum = 0;
        Tasklist tasklist = new Tasklist();
        //�����б��������
        for (int i = 0; i < ConstValue.TASKPOINT; ++i) {
            tasklist.getTask(taskpoint[i], 0);
        }

        //���������
        Robot[] robot = new Robot[ConstValue.ROBOTNUM];
        for (int i = 0; i < ConstValue.ROBOTNUM; ++i) {
            robot[i] = new Robot();
            robot[i].setInitialValue(i, i, 1);
            robot[i].printRobotInfo();
        }

        //�����������ʱ��
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.getStartTime();

        TasklistNum = tasklist.sendTaskNumber();    //��ȡ��ǰ��Ҫ�������������
        //���ɼ�ֵ�б�
        for (int j = 0; j < ConstValue.ROBOTNUM; ++j) {
            for (int i = 0; i < TasklistNum; ++i) {
                robot[j].calculateValue(tasklist, i);
            }
        }

        //����ʽ�����㷨�������
        AuctionAlgorithm auctionAlgorithm = new AuctionAlgorithm(TasklistNum);

        for (int k = 1; k < ConstValue.TASKCAPACITY + 1; ++k) {
            for (int i = 0; i < ConstValue.ROBOTNUM; ++i) {
                auctionAlgorithm.buildEdgelist(robot[i], tasklist);
            }
            if (ConstValue.ROBOTNUM < ConstValue.TASKPOINT) {
                auctionAlgorithm.buildAjacencyList();   //�����ڽӱ����ھ����㷨
                auctionAlgorithm.bidding(1);    //�����㷨ִ��
                //auctionAlgorithm.printAllocation(tasklist);
                auctionAlgorithm.printAllocTasklist(tasklist); //�µĴ�ӡ�������¼tasklist��λ��
            } else {
                auctionAlgorithm.buildReverseAjacencyList();   //�����ڽӱ����ھ����㷨
                auctionAlgorithm.bidding(1);    //�����㷨ִ��
                //auctionAlgorithm.printReverseAllocation(tasklist);
                auctionAlgorithm.printReverseAllocTasklist(tasklist);   //�µĴ�ӡ�������¼tasklist��λ��
            }

            //�����˽����䵽�������������ִ���б�
            if (ConstValue.ROBOTNUM < ConstValue.TASKPOINT) {
                for (int i = 0; i < ConstValue.ROBOTNUM; ++i) {
                    robot[auctionAlgorithm.sendEdges_new(i).u].saveTaskExecutionQueue(
                            tasklist.sendTaskQueue(auctionAlgorithm.sendEdges_new(i).v));
                }
            } else {
                for (int i = 0; i < ConstValue.TASKPOINT; ++i) {
                    robot[auctionAlgorithm.sendEdges_new(i).v].saveTaskExecutionQueue(
                            tasklist.sendTaskQueue(auctionAlgorithm.sendEdges_new(i).u));
                }

            }

            //���»�����λ��
            if (ConstValue.ROBOTNUM < ConstValue.TASKPOINT) {
                for (int i = 0; i < ConstValue.ROBOTNUM; ++i) {
                    robot[auctionAlgorithm.sendEdges_new(i).u].updateRobotLocation(
                            tasklist.sendTaskQueue(auctionAlgorithm.sendEdges_new(i).v).EndPoint[0],
                            tasklist.sendTaskQueue(auctionAlgorithm.sendEdges_new(i).v).EndPoint[1]
                    );
                    robot[auctionAlgorithm.sendEdges_new(i).u].printRobotInfo();
                }
            } else {
                for (int i = 0; i < ConstValue.ROBOTNUM; ++i) {
                    robot[i].updateRobotLocation();
                    robot[i].printRobotInfo();
                }
            }


            //ˢ�»����˵���
            for (int i = 0; i < ConstValue.ROBOTNUM; ++i) {
                robot[i].updateElectricity();
            }

            if (k == ConstValue.TASKCAPACITY) {
                break;
            }

            //����ʣ�������µ������б�
            Tasklist tasklist2 = new Tasklist();
            System.out.println("-------------------------------");
            System.out.println("�����б��������");
            int tasklist_res_length;    //ʣ����������
            if (ConstValue.ROBOTNUM < ConstValue.TASKPOINT) {
                tasklist_res_length = auctionAlgorithm.sendTasklist_res_length();
                for (int i = 0; i < tasklist_res_length; ++i) {
                    tasklist2.getTask(tasklist, auctionAlgorithm.sendTasklist_res(i));
                }
            } else {
                tasklist_res_length = 0;
            }

            //����ɵ������б�
            tasklist = tasklist2;
            //���ԭ�Ȼ����˼�ֵ�б�
            for (int i = 0; i < ConstValue.ROBOTNUM; ++i) {
                robot[i].deleteValueList();
            }
            //����������
            for (int i = 0; i < ConstValue.TASKPOINT; ++i) {
                tasklist.getTask(taskpoint[i], k);
            }
            TasklistNum = tasklist.sendTaskNumber();    //��ȡ��ǰ��Ҫ�������������
            //���ɼ�ֵ�б�
            for (int j = 0; j < ConstValue.ROBOTNUM; ++j) {
                for (int i = 0; i < TasklistNum; ++i) {
                    robot[j].calculateValue(tasklist, i);
                }
            }

            AuctionAlgorithm auctionAlgorithm2 = new AuctionAlgorithm(TasklistNum);
            auctionAlgorithm = auctionAlgorithm2;
        }

        timeStamp.getEndTime();

        System.out.println("git commit test");

    }

}
