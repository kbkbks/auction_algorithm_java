import java.util.ArrayList;

public class AuctionAlgorithm {
    public AuctionAlgorithm(int TasklistNum) {
        Robot_num = ConstValue.ROBOTNUM;
        Task_num = TasklistNum;
        e = 0;
        edges = new Edge[Robot_num * Task_num];
        edges_new = new Edge[Robot_num];
        tasklist_res = new ArrayList<Integer>();
    }

    /*
     * 建立机器人与任务之间的顶点、边集合（机器人：一顶点，任务：二顶点，边：执行价值）
     */
    public void buildEdgelist(Robot robot, Tasklist tasklist) {
        for (int i = 0; i < Task_num; ++i) {
            TaskTemplate TmpTask = tasklist.sendTaskQueue(i);
            edges[e] = new Edge();
            edges[e].u = robot.sendRobotNo();
            edges[e].v = i;
            edges[e].w = robot.sendValuelist(i);
            e++;
        }
    }

    /*
     * 建立邻接表（常规情况，机器人数大于等于任务数)
     */
    public void buildAjacencyList() {
        int u;
        int[] d = new int[Robot_num];
        cd = new int[Robot_num + 1];
        adj = new int[Robot_num * Task_num];
        weight = new double[Robot_num * Task_num];

        //计算机器人节点有多少边
        for (int i = 0; i < Robot_num * Task_num; ++i) {
            d[edges[i].u]++;
        }

        //计算累积度(邻接表基地址)
        cd[0] = 0;
        for (int i = 1; i < Robot_num + 1; ++i) {
            cd[i] = cd[i - 1] + d[i - 1];
            d[i - 1] = 0;
        }

        //建立邻接表
        for (int i = 0; i < Robot_num * Task_num; ++i) {
            u = edges[i].u;
            adj[cd[u] + d[u]] = edges[i].v;
            weight[cd[u] + d[u]++] = edges[i].w;
        }
    }

    /*
     * 建立邻接表（机器人数小于任务数）
     */
    void buildReverseAjacencyList() {
        int u;
        int[] d = new int[Task_num];
        cd = new int[Task_num + 1];
        adj = new int[Robot_num * Task_num];
        weight = new double[Robot_num * Task_num];

        //计算任务节点有多少边
        for (int i = 0; i < Robot_num * Task_num; ++i) {
            d[edges[i].v]++;
        }

        //计算累积度(邻接表基地址)
        cd[0] = 0;
        for (int i = 1; i < Task_num + 1; ++i) {
            cd[i] = cd[i - 1] + d[i - 1];
            d[i - 1] = 0;
        }

        //建立邻接表
        for (int i = 0; i < Robot_num * Task_num; ++i) {
            u = edges[i].v;
            adj[cd[u] + d[u]] = edges[i].u;
            weight[cd[u] + d[u]++] = edges[i].w;
        }
        int tmp = Robot_num;
        Robot_num = Task_num;
        Task_num = tmp;
    }


    //集中式竞拍算法
    public void bidding(double eps) {
        int u, v;   //第一、二顶点
        double w;    //边权重
        double NetMax = 0;   //最大净值
        double NetSecMax = 0;    //次大净值
        double MaxValue = 0; //所选任务价值
        double MaxRes = 0;   //累积任务价值
        int AssignedTask = -1;   //当前竞拍的任务编号
        double[] price = new double[Task_num];    //price[i] ＝ 任务i的价格
        //任务价格归零
        for (int i = 0; i < Task_num; ++i) {
            price[i] = 0;
        }

        int[] bidder = new int[Task_num];   //bidder[i] = -1, 则任务i没有分配； bidder[i] = robot，则任务i被分配给robot
        //竞拍者列表归零
        for (int i = 0; i < Task_num; ++i) {
            bidder[i] = -1;
        }

        double[] bidderweight = new double[Task_num]; //竞标者任务价值
        for (int i = 0; i < Task_num; ++i) {
            bidderweight[i] = 0;
        }

        int preRobotNum = Robot_num;   //待分配机器人数量
        int[] preRobotList = new int[Robot_num];
        for (u = 0; u < preRobotNum; ++u) {
            preRobotList[u] = u;
        }

        while (preRobotNum > 0) {
            //选择一个机器人
            u = preRobotList[preRobotNum - 1];

            //选择净值最大的任务
            NetMax = 0;
            for (int i = cd[u]; i < cd[u + 1]; ++i) {
                v = adj[i];
                w = weight[i];
                if ((w - price[v]) > NetMax) {
                    NetMax = w - price[v];
                    AssignedTask = v;
                    MaxValue = w;
                }
            }

            if (NetMax == 0) {
                preRobotNum--;
                continue;
            }

            //选择净值第二大的任务
            NetSecMax = 0;
            for (int i = cd[u]; i < cd[u + 1]; ++i) {
                v = adj[i];
                if (v != AssignedTask) {
                    w = weight[i];
                    if ((w - price[v]) > NetSecMax) {
                        NetSecMax = w - price[v];
                    }
                }
            }

            //出价
            price[AssignedTask] += NetMax - NetSecMax + eps;

            //冲突消除与检测
            if (bidder[AssignedTask] != -1) {
                preRobotList[preRobotNum - 1] = bidder[AssignedTask];
                MaxRes -= bidderweight[AssignedTask];
            } else {
                preRobotNum--;
            }
            bidder[AssignedTask] = u;
            bidderweight[AssignedTask] = MaxValue;
            MaxRes += MaxValue;
        }

        //分配后处理
        int k = 0;
        for (int i = 0; i < Robot_num; ++i) {
            edges_new[i] = new Edge();
        }
        for (int i = 0; i < Task_num; ++i) {
            if (bidder[i] != -1) {
                edges_new[k].u = bidder[i];
                edges_new[k].v = i;
                edges_new[k++].w = bidderweight[i];
            } else {
                //将未分配的任务返回
                tasklist_res.add(i);
            }
        }

    }

    /*
     * 打印分配信息(常规情况，机器人数大于等于任务数)
     */
    public void printAllocation(Tasklist tasklist) {
        System.out.println("***********************************");
        System.out.println("当前分配情况");
        double ValueSum = 0; // 总收益
        for (int i = 0; i < Robot_num; ++i) {
            System.out.print("机器人编号：");
            System.out.print(edges_new[i].u);
            System.out.print("任务点编号：");
            System.out.print(tasklist.sendTaskQueue(edges_new[i].v).PointNo);
            System.out.print("任务编号：");
            System.out.print(tasklist.sendTaskQueue(edges_new[i].v).TaskNo);
            System.out.print("收益：");
            System.out.println(edges_new[i].w);
            ValueSum += edges_new[i].w;
        }
        System.out.println("***********************************");
        System.out.println("***********************************");
        System.out.print("总收益：");
        System.out.println(ValueSum);
    }

    /*
     * 打印分配信息，根据tasklist打印 (常规情况，机器人数大于等于任务数)
     */
    public void printAllocTasklist(Tasklist tasklist) {
        System.out.println("***********************************");
        System.out.println("当前分配情况");
        double ValueSum = 0; // 总收益
        for (int i = 0; i < Robot_num; ++i) {
            System.out.print("AGV：");
            System.out.print(edges_new[i].u);
            System.out.print("  ");
            System.out.print("TASK：");
            System.out.println(edges_new[i].v);
        }
    }

    /*
     * 打印分配信息（机器人数小于任务数）
     */
    public void printReverseAllocation(Tasklist tasklist){
        System.out.println("***********************************");
        System.out.println("当前分配情况");
        double ValueSum = 0; // 总收益
        for (int i = 0; i < Robot_num; ++i) {
            System.out.print("机器人编号：");
            System.out.print(edges_new[i].v);
            System.out.print("任务点编号：");
            System.out.print(tasklist.sendTaskQueue(edges_new[i].u).PointNo);
            System.out.print("任务编号：");
            System.out.print(tasklist.sendTaskQueue(edges_new[i].u).TaskNo);
            System.out.print("收益：");
            System.out.println(edges_new[i].w);
            ValueSum += edges_new[i].w;
        }
        System.out.println("***********************************");
        System.out.println("***********************************");
        System.out.print("总收益：");
        System.out.println(ValueSum);
    }

    /*
     * 打印分配信息，根据tasklist打印 (机器人数小于任务数)
     */
    public void printReverseAllocTasklist(Tasklist tasklist) {
        System.out.println("***********************************");
        System.out.println("当前分配情况");
        double ValueSum = 0; // 总收益
        for (int i = 0; i < Robot_num; ++i) {
            System.out.print("AGV：");
            System.out.print(edges_new[i].v);
            System.out.print("  ");
            System.out.print("TASK：");
            System.out.println(edges_new[i].u);
        }
    }

    /*
     * 发送分配后的顶点边集合
     */
    public Edge sendEdges_new(int i) {
        return edges_new[i];
    }

    /*
     * 发送剩余任务列表的任务数量
     */
    public int sendTasklist_res_length() {
        return tasklist_res.size();
    }

    /*
     *  发送剩余任务
     */
    public int sendTasklist_res(int i) {
        return tasklist_res.get(i);
    }


    private int Robot_num;  //第一顶点（机器人节点），机器人数量
    private int Task_num;   //第二顶点（任务节点），任务数量
    private int e;   //边，机器人分配任务
    private Edge[] edges;    //顶点边集合
    private int[] cd;   //累积度
    private int[] adj;  // 邻居列表
    private double[] weight; //边的权重(价值)
    //分配后
    private Edge[] edges_new;   //分配后的顶点边集合
    private ArrayList<Integer> tasklist_res;    //分配后剩余的任务
}
