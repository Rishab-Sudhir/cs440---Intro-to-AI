package src.labs.stealth.agents;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.util.Direction;                           // Directions in Sepia


import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue; // heap in java
import java.util.Set;
import java.util.Stack;



// JAVA PROJECT IMPORTS


public class DijkstraMazeAgent
    extends MazeAgent
{

    public DijkstraMazeAgent(int playerNum)
    {
        super(playerNum);
    }

    @Override
    public Path search(Vertex src,
                       Vertex goal,
                       StateView state)
    {
        HashMap<Vertex, Float> pi = new HashMap<>();
        HashSet<Vertex> visitedNodes = new HashSet<>();
        HashMap<Vertex, Vertex> parentsHash = new HashMap<>();
        Comparator<Vertex> vertexComparator = (v1, v2) -> Float.compare(pi.getOrDefault(v1, Float.MAX_VALUE), pi.getOrDefault(v2, Float.MAX_VALUE));
        //This basically sets the ordering of the priority queue
        PriorityQueue<Vertex> q = new PriorityQueue<>(vertexComparator);
        
        pi.put(src, 0f);
        q.add(src);
        parentsHash.put(src, null);

        Path p = new Path(src);

        while (!q.isEmpty()) {
            Vertex curr = q.poll();
            visitedNodes.add(curr);

            if (curr.equals(goal)) {
                break;
            }

            int currx = curr.getXCoordinate();
            int curry = curr.getYCoordinate();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if(x==0 && y==0){
                        continue;
                    }
                    Vertex neighbor = new Vertex(currx + x, curry + y);
                    if (state.inBounds(currx + x, curry + y)){
                        if (!((state.hasUnit(currx + x, curry + y)) || (state.isResourceAt(currx + x, curry + y)))){
                            if (!curr.equals(neighbor)){
                                float newDist = pi.getOrDefault(curr, Float.MAX_VALUE) + w(getDirectionToMoveTo(curr, neighbor));
                                if (pi.getOrDefault(neighbor, Float.MAX_VALUE) > newDist) {
                                    pi.put(neighbor, newDist);
                                    parentsHash.put(neighbor, curr); 
                                    if (q.contains(neighbor)) {
                                        q.remove(neighbor);
                                    }
                                    q.add(neighbor);
                                }
                            }
                        }
                    }
                }
            }
        }

        LinkedList<Vertex> pathVertices = new LinkedList<>();
        Vertex current = parentsHash.get(goal);
    
        while (!current.equals(src)) {
            pathVertices.addFirst(current); // add v to beinging list
            current = parentsHash.get(current);
        }
    
        Path path = new Path(src); // start src
        Vertex prev = src;
        for (Vertex curr : pathVertices) {  
            path = new Path(curr, pi.get(curr)-pi.get(prev), path);
            prev = curr;
        }
    
        return path; 
        
    }
    
    public float w(Direction direction) { //Gravity Function
        if (direction == Direction.EAST || direction == Direction.WEST) {
            return 5f;
        }
        else if (direction == Direction.SOUTH) {
            return 1f;
        }
        else if (direction == Direction.NORTH) {
            return 10f;
        }
        else if (direction == Direction.NORTHEAST || direction == Direction.NORTHWEST  ) {
            return (float) Math.sqrt(Math.pow(10f, 2) + Math.pow(5f, 2));
        }
        else if (direction == Direction.SOUTHEAST|| direction == Direction.SOUTHWEST){
            return (float) Math.sqrt(Math.pow(1f, 2) + Math.pow(5f, 2));
        }
        else{
            return Float.MAX_VALUE;
        }
    }

    @Override
    public boolean shouldReplacePlan(StateView state)
    {
        Stack<Vertex> plan = this.getCurrentPlan();
        while (!plan.empty()){

            int x = plan.pop().getXCoordinate();
            int y = plan.pop().getYCoordinate();
            
            if (state.inBounds(x+1, y) && state.hasUnit(x+1, y)){ //right
                return true;
            }
            else if (state.inBounds(x+1, y) && state.hasUnit(x-1, y)){ //left
                return true;
            }
            else if (state.inBounds(x, y+1) && state.hasUnit(x, y+1)){ //up
                return true;
            }
            else if (state.inBounds(x, y-1) && state.hasUnit(x, y-1)){ //down
                return true;
            }
            else if (state.inBounds(x+1, y+1) && state.hasUnit(x+1, y+1)){ //top right
                return true;
            }
            else if (state.inBounds(x+1, y-1) && state.hasUnit(x+1, y-1)){ //bottom right
                return true;
            }
            else if (state.inBounds(x-1, y+1) && state.hasUnit(x-1, y+1)){ //top left
                return true;
            }
            else if (state.inBounds(x-1, y-1) && state.hasUnit(x-1, y-1)){ // bottom left
                return true;
            }
            if(state.inBounds(x, y) && state.isResourceAt(x, y)){
                return true;
            }
        }
        return false;
    }

}
