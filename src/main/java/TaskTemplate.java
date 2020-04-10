public class TaskTemplate {
    public TaskTemplate() {
        BeginPoint = new double[2];
        EndPoint = new double[2];
    }

    public int TaskNo; //任务编号
    public int PointNo;   //任务点编号
    public double[] BeginPoint;    //任务起点
    public double[] EndPoint; // 任务终点
}
