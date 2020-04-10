/*
 * Xinyan Han
 * 2020-03-15
 * metatron96@126.com
 * info:
 * -------------------------------------------------------------------------------------------------------------------------
 * 集中式竞拍算法任务分配程序，当前程序单线程执行。
 * 程序直接从入口函数main进入。
 * 价值函数有两种：
 * GenerateCalculate1包括 电量，任务距离，任务数量
 * GenerateCalculate2包括 任务距离
 *
 * 3月15日新增，当机器人数量小于任务数量的情况，更改邻接表函数以及一系列相关代码。
 */

public class Start {
    public static void main(String[] args) {
        System.out.println("****************************");
        System.out.println("分布式任务处理与竞拍机制算法");
        System.out.println("****************************");
        System.out.println();

        //任务点对象初始化参数
        double[][][] BeginInitial = new double[ConstValue.TASKPOINT][ConstValue.TASKCAPACITY][2];
        double[][][] EndInitial = new double[ConstValue.TASKPOINT][ConstValue.TASKCAPACITY][2];
        for (int j = 0; j < ConstValue.TASKPOINT; ++j) {
            for (int i = 0; i < ConstValue.TASKCAPACITY; ++i) {
                BeginInitial[j][i][0] = j;  //任务起点x坐标
                BeginInitial[j][i][1] = 3;  //任务起点y坐标
                EndInitial[j][i][0] = j;    //任务终点x坐标
                EndInitial[j][i][1] = 4 + i;    //任务终点y坐标
            }
        }

        //定义任务发布点并初始化
        Taskpoint[] taskpoint = new Taskpoint[ConstValue.TASKPOINT];
        for (int i = 0; i < ConstValue.TASKPOINT; ++i) {
            taskpoint[i] = new Taskpoint();
            taskpoint[i].setInitialvalue(i, BeginInitial[i], EndInitial[i]);
            //taskpoint[i].printTaskRepository();
        }

        //定义任务列表
        int TasklistNum = 0;
        Tasklist tasklist = new Tasklist();
        //任务列表接收任务
        for (int i = 0; i < ConstValue.TASKPOINT; ++i) {
            tasklist.getTask(taskpoint[i], 0);
        }

        //定义机器人
        Robot[] robot = new Robot[ConstValue.ROBOTNUM];
        for (int i = 0; i < ConstValue.ROBOTNUM; ++i) {
            robot[i] = new Robot();
            robot[i].setInitialValue(i, i, 1);
            robot[i].printRobotInfo();
        }

        //计算程序运行时间
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.getStartTime();

        TasklistNum = tasklist.sendTaskNumber();    //获取当前需要分配的任务数量
        //生成价值列表
        for (int j = 0; j < ConstValue.ROBOTNUM; ++j) {
            for (int i = 0; i < TasklistNum; ++i) {
                robot[j].calculateValue(tasklist, i);
            }
        }

        //集中式竞拍算法任务分配
        AuctionAlgorithm auctionAlgorithm = new AuctionAlgorithm(TasklistNum);

        for (int k = 1; k < ConstValue.TASKCAPACITY + 1; ++k) {
            for (int i = 0; i < ConstValue.ROBOTNUM; ++i) {
                auctionAlgorithm.buildEdgelist(robot[i], tasklist);
            }
            if (ConstValue.ROBOTNUM < ConstValue.TASKPOINT) {
                auctionAlgorithm.buildAjacencyList();   //生成邻接表，用于竞拍算法
                auctionAlgorithm.bidding(1);    //竞拍算法执行
                //auctionAlgorithm.printAllocation(tasklist);
                auctionAlgorithm.printAllocTasklist(tasklist); //新的打印，任务记录tasklist的位置
            } else {
                auctionAlgorithm.buildReverseAjacencyList();   //生成邻接表，用于竞拍算法
                auctionAlgorithm.bidding(1);    //竞拍算法执行
                //auctionAlgorithm.printReverseAllocation(tasklist);
                auctionAlgorithm.printReverseAllocTasklist(tasklist);   //新的打印，任务记录tasklist的位置
            }

            //机器人将分配到的任务存入任务执行列表
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

            //更新机器人位置
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


            //刷新机器人电量
            for (int i = 0; i < ConstValue.ROBOTNUM; ++i) {
                robot[i].updateElectricity();
            }

            if (k == ConstValue.TASKCAPACITY) {
                break;
            }

            //复制剩余任务到新的任务列表
            Tasklist tasklist2 = new Tasklist();
            System.out.println("-------------------------------");
            System.out.println("任务列表接收任务");
            int tasklist_res_length;    //剩余任务数量
            if (ConstValue.ROBOTNUM < ConstValue.TASKPOINT) {
                tasklist_res_length = auctionAlgorithm.sendTasklist_res_length();
                for (int i = 0; i < tasklist_res_length; ++i) {
                    tasklist2.getTask(tasklist, auctionAlgorithm.sendTasklist_res(i));
                }
            } else {
                tasklist_res_length = 0;
            }

            //清除旧的任务列表
            tasklist = tasklist2;
            //清空原先机器人价值列表
            for (int i = 0; i < ConstValue.ROBOTNUM; ++i) {
                robot[i].deleteValueList();
            }
            //发布新任务
            for (int i = 0; i < ConstValue.TASKPOINT; ++i) {
                tasklist.getTask(taskpoint[i], k);
            }
            TasklistNum = tasklist.sendTaskNumber();    //获取当前需要分配的任务数量
            //生成价值列表
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
