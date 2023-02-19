
// Bucket Structure contains a capacity for the bucket as well as a level of water currently in the bucket
class Bucket {
    int capacity;  // Capacity of the bucket
    int level;     // Water level in the bucket

    public Bucket(int cap, int lvl) {
        capacity = cap;
        level = lvl;
    }
}


// 动态图的节点 或 特殊状态节点
public class Node {
    // 用于创建节点的初始化数据
    Bucket[] buckets;    // 桶状态
    int goal;            // 目标状态

    // 记录状态和启发搜索函数
    int solution;        // 记录当前解
    double g;
    double h;
    Node parent;  // 父节点
    int sum;      // 记录桶内的水的总和

    // 初始节点状态
    public Node(Bucket[] bucks, int goal) {
        buckets = bucks;
        this.goal = goal;
    }

    // 从base节点创建邻居节点（可以实现的转移状态）
    public Node(Node base) {
        this.buckets = new Bucket[base.buckets.length];
        for (int i = 0; i < base.buckets.length; i++) {
            this.buckets[i] = new Bucket(base.buckets[i].capacity, base.buckets[i].level);
        }
        this.goal = base.goal;
        this.solution = base.solution;
        this.g = base.g;
        this.h = base.h;
        this.parent = base;
    }

    /**
     * Calculate the Heuristic Values for a Node in state：
              g(t) + 1   else
     g(t+1) = 
              0          t = 0
                
     h(t+1) = (goal - solution) / bucket_len
     */
    public void calculateHeuristic() {
        this.g = (this.parent == null ? 0 : this.parent.g) + 1;
        this.h = (this.goal - this.solution)/this.buckets.length;
    }

    // 计算当前所有桶内的水
    public int getSum(){
        this.sum = 0;
        for (Bucket bucket : this.buckets) {
            this.sum += bucket.level;
        }
        return this.sum;
    }

    // 将桶i中的水倒进桶j
    // 桶i > 桶j的剩余空间，则将剩余空间倒满
    // 否则, 将桶i的水全部导入j中,将桶i倒空
    public void pour(int i, int j) {
        // 计算j剩余的空间
        int remaining_space = this.buckets[j].capacity - this.buckets[j].level;
        // 如果桶i的水比剩余空间多
        if (this.buckets[i].level > remaining_space) {
            this.buckets[i].level -= remaining_space;
            this.buckets[j].level += remaining_space;
        } else {
            this.buckets[j].level += this.buckets[i].level;
            emptyBucket(i);
        }
    }

    // 将索引为i的桶倒满
    public void fillBucket(int i) {
        this.buckets[i].level = this.buckets[i].capacity;
    }

    // 清空第i个桶
    public void emptyBucket(int i) {
        this.buckets[i].level = 0;
    }

    // 将第i个桶的水倒入大桶solution中
    public void addToSolution(int i) {
        this.solution += this.buckets[i].level;
        emptyBucket(i);
    }
}
