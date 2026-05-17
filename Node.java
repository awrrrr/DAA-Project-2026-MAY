public class Node {
    public int x;
    public int y;
    public double gCost;
    public double hCost;
    public double fCost;
    public Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.gCost = 0;
        this.hCost = 0;
        this.fCost = 0;
        this.parent = null;
    }
}
