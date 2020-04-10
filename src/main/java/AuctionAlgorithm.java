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
     * ����������������֮��Ķ��㡢�߼��ϣ������ˣ�һ���㣬���񣺶����㣬�ߣ�ִ�м�ֵ��
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
     * �����ڽӱ���������������������ڵ���������)
     */
    public void buildAjacencyList() {
        int u;
        int[] d = new int[Robot_num];
        cd = new int[Robot_num + 1];
        adj = new int[Robot_num * Task_num];
        weight = new double[Robot_num * Task_num];

        //��������˽ڵ��ж��ٱ�
        for (int i = 0; i < Robot_num * Task_num; ++i) {
            d[edges[i].u]++;
        }

        //�����ۻ���(�ڽӱ����ַ)
        cd[0] = 0;
        for (int i = 1; i < Robot_num + 1; ++i) {
            cd[i] = cd[i - 1] + d[i - 1];
            d[i - 1] = 0;
        }

        //�����ڽӱ�
        for (int i = 0; i < Robot_num * Task_num; ++i) {
            u = edges[i].u;
            adj[cd[u] + d[u]] = edges[i].v;
            weight[cd[u] + d[u]++] = edges[i].w;
        }
    }

    /*
     * �����ڽӱ���������С����������
     */
    void buildReverseAjacencyList() {
        int u;
        int[] d = new int[Task_num];
        cd = new int[Task_num + 1];
        adj = new int[Robot_num * Task_num];
        weight = new double[Robot_num * Task_num];

        //��������ڵ��ж��ٱ�
        for (int i = 0; i < Robot_num * Task_num; ++i) {
            d[edges[i].v]++;
        }

        //�����ۻ���(�ڽӱ����ַ)
        cd[0] = 0;
        for (int i = 1; i < Task_num + 1; ++i) {
            cd[i] = cd[i - 1] + d[i - 1];
            d[i - 1] = 0;
        }

        //�����ڽӱ�
        for (int i = 0; i < Robot_num * Task_num; ++i) {
            u = edges[i].v;
            adj[cd[u] + d[u]] = edges[i].u;
            weight[cd[u] + d[u]++] = edges[i].w;
        }
        int tmp = Robot_num;
        Robot_num = Task_num;
        Task_num = tmp;
    }


    //����ʽ�����㷨
    public void bidding(double eps) {
        int u, v;   //��һ��������
        double w;    //��Ȩ��
        double NetMax = 0;   //���ֵ
        double NetSecMax = 0;    //�δ�ֵ
        double MaxValue = 0; //��ѡ�����ֵ
        double MaxRes = 0;   //�ۻ������ֵ
        int AssignedTask = -1;   //��ǰ���ĵ�������
        double[] price = new double[Task_num];    //price[i] �� ����i�ļ۸�
        //����۸����
        for (int i = 0; i < Task_num; ++i) {
            price[i] = 0;
        }

        int[] bidder = new int[Task_num];   //bidder[i] = -1, ������iû�з��䣻 bidder[i] = robot��������i�������robot
        //�������б����
        for (int i = 0; i < Task_num; ++i) {
            bidder[i] = -1;
        }

        double[] bidderweight = new double[Task_num]; //�����������ֵ
        for (int i = 0; i < Task_num; ++i) {
            bidderweight[i] = 0;
        }

        int preRobotNum = Robot_num;   //���������������
        int[] preRobotList = new int[Robot_num];
        for (u = 0; u < preRobotNum; ++u) {
            preRobotList[u] = u;
        }

        while (preRobotNum > 0) {
            //ѡ��һ��������
            u = preRobotList[preRobotNum - 1];

            //ѡ��ֵ��������
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

            //ѡ��ֵ�ڶ��������
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

            //����
            price[AssignedTask] += NetMax - NetSecMax + eps;

            //��ͻ��������
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

        //�������
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
                //��δ��������񷵻�
                tasklist_res.add(i);
            }
        }

    }

    /*
     * ��ӡ������Ϣ(��������������������ڵ���������)
     */
    public void printAllocation(Tasklist tasklist) {
        System.out.println("***********************************");
        System.out.println("��ǰ�������");
        double ValueSum = 0; // ������
        for (int i = 0; i < Robot_num; ++i) {
            System.out.print("�����˱�ţ�");
            System.out.print(edges_new[i].u);
            System.out.print("������ţ�");
            System.out.print(tasklist.sendTaskQueue(edges_new[i].v).PointNo);
            System.out.print("�����ţ�");
            System.out.print(tasklist.sendTaskQueue(edges_new[i].v).TaskNo);
            System.out.print("���棺");
            System.out.println(edges_new[i].w);
            ValueSum += edges_new[i].w;
        }
        System.out.println("***********************************");
        System.out.println("***********************************");
        System.out.print("�����棺");
        System.out.println(ValueSum);
    }

    /*
     * ��ӡ������Ϣ������tasklist��ӡ (��������������������ڵ���������)
     */
    public void printAllocTasklist(Tasklist tasklist) {
        System.out.println("***********************************");
        System.out.println("��ǰ�������");
        double ValueSum = 0; // ������
        for (int i = 0; i < Robot_num; ++i) {
            System.out.print("AGV��");
            System.out.print(edges_new[i].u);
            System.out.print("  ");
            System.out.print("TASK��");
            System.out.println(edges_new[i].v);
        }
    }

    /*
     * ��ӡ������Ϣ����������С����������
     */
    public void printReverseAllocation(Tasklist tasklist){
        System.out.println("***********************************");
        System.out.println("��ǰ�������");
        double ValueSum = 0; // ������
        for (int i = 0; i < Robot_num; ++i) {
            System.out.print("�����˱�ţ�");
            System.out.print(edges_new[i].v);
            System.out.print("������ţ�");
            System.out.print(tasklist.sendTaskQueue(edges_new[i].u).PointNo);
            System.out.print("�����ţ�");
            System.out.print(tasklist.sendTaskQueue(edges_new[i].u).TaskNo);
            System.out.print("���棺");
            System.out.println(edges_new[i].w);
            ValueSum += edges_new[i].w;
        }
        System.out.println("***********************************");
        System.out.println("***********************************");
        System.out.print("�����棺");
        System.out.println(ValueSum);
    }

    /*
     * ��ӡ������Ϣ������tasklist��ӡ (��������С��������)
     */
    public void printReverseAllocTasklist(Tasklist tasklist) {
        System.out.println("***********************************");
        System.out.println("��ǰ�������");
        double ValueSum = 0; // ������
        for (int i = 0; i < Robot_num; ++i) {
            System.out.print("AGV��");
            System.out.print(edges_new[i].v);
            System.out.print("  ");
            System.out.print("TASK��");
            System.out.println(edges_new[i].u);
        }
    }

    /*
     * ���ͷ����Ķ���߼���
     */
    public Edge sendEdges_new(int i) {
        return edges_new[i];
    }

    /*
     * ����ʣ�������б����������
     */
    public int sendTasklist_res_length() {
        return tasklist_res.size();
    }

    /*
     *  ����ʣ������
     */
    public int sendTasklist_res(int i) {
        return tasklist_res.get(i);
    }


    private int Robot_num;  //��һ���㣨�����˽ڵ㣩������������
    private int Task_num;   //�ڶ����㣨����ڵ㣩����������
    private int e;   //�ߣ������˷�������
    private Edge[] edges;    //����߼���
    private int[] cd;   //�ۻ���
    private int[] adj;  // �ھ��б�
    private double[] weight; //�ߵ�Ȩ��(��ֵ)
    //�����
    private Edge[] edges_new;   //�����Ķ���߼���
    private ArrayList<Integer> tasklist_res;    //�����ʣ�������
}
