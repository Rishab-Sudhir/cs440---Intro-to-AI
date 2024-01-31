package src.labs.stealth.agents;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;       // will need for bfs
import java.util.Hashtable;
import java.util.Queue;         // will need for bfs
import java.util.LinkedList;    // will need for bfs
import java.util.List;
import java.util.Set;           // will need for bfs
import java.util.Stack;


// JAVA PROJECT IMPORTS


public class BFSMazeAgent
    extends MazeAgent
{
    public BFSMazeAgent(int playerNum)
    {
        super(playerNum);
    }

    @Override
    public Path search(Vertex src,
                       Vertex goal,
                       StateView state)
    {   
        HashMap<Vertex, Vertex> ParentsHash = new HashMap<>();    //keeps track of the nodes before the current node ie the parents                                             //adjacency list
        Queue<Vertex> queue = new LinkedList<>();                 //maintaining a queue
        queue.add(src);
        ParentsHash.put(src, null);

        Path p = new Path(src);

        while (!queue.isEmpty()){

            Vertex u = queue.poll();

            if (u.equals(goal)){ //found goal now trace back
                
                Stack<Vertex> reverse = new Stack<>(); 
                Vertex node = ParentsHash.get(goal);

                while(!node.equals(src)){
                    reverse.push(node);
                    node = ParentsHash.get(node);
                    
                }
                
                while(!reverse.isEmpty()){
                    Vertex node1 = reverse.pop();
                    p = new Path(node1, 1, p);
                }
                
                return p;
            }

            int currx = u.getXCoordinate();
            int curry = u.getYCoordinate();

            for (int x=-1; x<=1; x++) {
                for(int y=-1; y<=1; y++){
                    if(x==0 && y==0){
                        continue;
                    }
                    if (state.inBounds(currx + x, curry + y)){ 
                        if (!((state.hasUnit(currx + x, curry + y)) || (state.isResourceAt(currx + x, curry + y)))){
                            Vertex v = new Vertex(currx + x, curry + y);
                            if (!ParentsHash.containsKey(v)){
                                ParentsHash.put(v, u);
                                queue.add(v);
                        }
                    }
                }
            }
        }
    }
        return p; //return src path traced all neighbors and didn't find goal 
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
