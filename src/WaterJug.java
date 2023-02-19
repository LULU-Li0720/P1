import java.util.ArrayList;

public class WaterJug {

    /**
     * This is the driver method for the water jug problem
     * @param args Default formatting for Java
     * @throws Exception Occurs when there is an issue reading the input file
     */
    public static void main(String[] args) throws Exception {
        // Read in the input file
        FileReader fR = new FileReader();
        String filename = "./data/" + "input";  // command line
        fR.readFile(filename);

        // Save the data about the pitchers and the goal water total
        int[] inBuckets = fR.pitchers;
        int goal = fR.goal;
        // System.out.println("bucket: " + inBuckets);
        // Perform the Search
        int result = aStarSearch(inBuckets, goal);
        // Display the results
        System.out.println("Steps: " + result);
    }

    // A*启发搜索算法
    public static int aStarSearch(int[] inBuckets, int goal) {
        // 无解时返回-1
        if (!solutionPossible(inBuckets, goal))  return -1;
        ArrayList<Node> openList = new ArrayList<>();    // not vistied list
        ArrayList<Node> closedList = new ArrayList<>();  // vistied list

        // 创建初始根节点状态
        Bucket[] startBuckets = new Bucket[inBuckets.length];
        for (int i = 0; i < inBuckets.length; i++)
            startBuckets[i] = new Bucket(inBuckets[i], 0);
        Node start = new Node(startBuckets, goal);

        openList.add(start);

        // iteration: 如果未访问列表中还有节点,则迭代
        while (openList.size() > 0) {
            // 计算所有节点最小f = g + h, 然后将这个节点移除, 然后将此节点加入visited list中.
            int minIndex = getMinIndex(openList);
            Node current = openList.remove(minIndex);
            closedList.add(current);

            // 如果未达到目标解,此时需要扩展当前节点(即当前状态的所有可能转移状态)
            if (current.solution != goal) {
                // 获取所有的扩展节点,并计算这些节点的启发值
                ArrayList<Node> neighbors = getAllState(current);
                for (int i = 0; i < neighbors.size(); i++) {
                    Node testNeighbor = neighbors.get(i);
                    // 节点不在访问列表且不在未访问列表中,将此节点加入访问节点,然后将当前节点设置未父节点,将步数 + 1
                    if (!nodeInClosedSet(testNeighbor, closedList) && nodeLocInOpenSet(testNeighbor, openList) == -1) {
                        openList.add(testNeighbor);
                        testNeighbor.parent = current;
                        testNeighbor.g = current.g + 1;
                    } else {
                        // 当前新路径的步数更优,则将他设置为新路径
                        if (testNeighbor.g > current.g + testNeighbor.getSum()) {
                            testNeighbor.g = current.g + 1;
                            testNeighbor.parent = current;
                            // 如果此时节点在closedList中,那么删除当前节点,同时将testNeighbor加入未访问列表
                            if (nodeInClosedSet(testNeighbor, closedList)) {
                                closedList.remove(testNeighbor);
                                openList.add(testNeighbor);
                            }
                        }
                    }
                }
            }
            // 得到解则计算步骤
            if (current.solution == goal)
                return findSteps(current);
        }
        // 通过计算,确实误解,则返回-1
        return -1;
    }

    // 判断node是否在openList中,并且node是否比openList中的节点的启发值更小,同时得到这个节点的索引
    public static int nodeLocInOpenSet(Node node, ArrayList<Node> openList) {
        for (int i = 0; i < openList.size(); i++) {
            Node test = openList.get(i);
            if (node.equals(test) && ((node.g + node.h) < (test.g + test.h)))
                return i;
        }
        return -1;
    }

    // 判断node是否在closedList中
    public static boolean nodeInClosedSet(Node node, ArrayList<Node> closedList) {
        for (int i = 0; i < closedList.size(); i++) 
            if (node.equals(closedList.get(i))) 
                return true;
        return false;
    }

    // 获取有子状态
    public static ArrayList<Node> getAllState(Node current) {
        ArrayList<Node> neighbors = new ArrayList<Node>();
        for (int i = 0; i < current.buckets.length; i++) {
            Bucket bucket = current.buckets[i];
            // 如果桶是空的,可以将水填满
            if (bucket.level == 0) {
                Node neighbor = new Node(current);
                neighbor.fillBucket(i);
                neighbor.calculateHeuristic();
                neighbors.add(neighbor);
            } else {
                // 如果桶剩余空间, 则可以将桶装满
                if (bucket.capacity > bucket.level) {
                    Node neighbor = new Node(current);
                    neighbor.fillBucket(i);
                    neighbor.calculateHeuristic();
                    neighbors.add(neighbor);
                }
                // 桶中有水
                if (bucket.level > 0) {
                    // 如果桶中有水,可以将桶中的水倒空
                    Node neighbor = new Node(current);
                    neighbor.emptyBucket(i);
                    neighbor.calculateHeuristic();
                    neighbors.add(neighbor);

                    // 如果比目标小的情况,则可以向目标桶倒水
                    if (current.solution + bucket.level <= current.goal) {
                        neighbor = new Node(current);
                        neighbor.addToSolution(i);
                        neighbor.calculateHeuristic();
                        neighbors.add(neighbor);
                    }
                }
                // 将桶i中的水倒入桶j中
                for (int j = 0; j < current.buckets.length; j++) {
                    // 自己不能向自己倒水
                    if (i == j) continue;
                    // j中还有空间
                    if ((current.buckets[j].capacity - current.buckets[j].level) > 0) {
                        Node neighbor = new Node(current);
                        neighbor.pour(i, j);
                        neighbor.calculateHeuristic();
                        neighbors.add(neighbor);
                    }
                }
            }
        }
        return neighbors;
    }

   
    // 计算找到解需要的步骤
    public static int findSteps(Node current) {
        if (current.parent == null)   return 0;
        return 1 + findSteps(current.parent);
    }

    // 计算最小启发值节点的索引,启发值计算g + h
    public static int getMinIndex(ArrayList<Node> openList) {
        int minIndex = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < openList.size(); i++) {
            Node temp = openList.get(i);
            if ((temp.g + temp.h) <= min) {
                min = (int) temp.g + (int) temp.h;
                minIndex = i;
            }
        }
        return minIndex;
    }

    // 本质上,这是一个多元方程整数解问题,  ax + by + cz + ... + dw = A
    // 因此,如果有解的话那么必须保证这些整数中必须存在至少两个数的最大公约数必须是A的约数
    public static boolean solutionPossible(int[] inBuckets, int goal) {
        int gcd = inBuckets[0];
        for (int i = 1; i < inBuckets.length; i++) {
            gcd = calculateGCD(gcd, inBuckets[i]);
            if (goal % gcd == 0)   return true;
        }
        if (goal % gcd == 0)   return true;
        return false;
    }

    // 计算a和b两个整数的最大公约数
    public static int calculateGCD(int a, int b) {
        if (a % b == 0)
            return b;
        return calculateGCD(b, a % b);
    }
}
