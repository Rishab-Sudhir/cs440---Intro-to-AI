package src.labs.infexf.agents;

import java.util.HashSet;
import java.util.Stack;

// SYSTEM IMPORTS
import edu.bu.labs.infexf.agents.SpecOpsAgent;
import edu.bu.labs.infexf.distance.DistanceMetric;
import edu.bu.labs.infexf.graph.Vertex;
import edu.bu.labs.infexf.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;


// JAVA PROJECT IMPORTS


public class InfilExfilAgent
    extends SpecOpsAgent
{

    public InfilExfilAgent(int playerNum)
    {
        super(playerNum);
    }

    // if you want to get attack-radius of an enemy, you can do so through the enemy unit's UnitView
    // Every unit is constructed from an xml schema for that unit's type.
    // We can lookup the "range" of the unit using the following line of code (assuming we know the id):
    //     int attackRadius = state.getUnit(enemyUnitID).getTemplateView().getRange();
    @Override
    public float getEdgeWeight(Vertex src,
                               Vertex dst,
                               StateView state)
    {
        Integer dstX = dst.getXCoordinate();
        Integer dstY = dst.getYCoordinate();
        float tempweight = 1f;

        if (((state.hasUnit(dstX, dstY)) || (state.isResourceAt(dstX, dstX)))){
            tempweight += 1000;
        }

        //checking if my next destination is within the attack radius of an enemy
        if (getOtherEnemyUnitIDs() == null){
            return tempweight;
        }
        for (Integer enemyId: getOtherEnemyUnitIDs()){
            // Check if the enemy unit is not null
            if (state.getUnit(enemyId) == null){
                continue;
            }else{
    
                int enemyX = state.getUnit(enemyId).getXPosition();
                int enemyY = state.getUnit(enemyId).getYPosition();
                int attackRadius = state.getUnit(enemyId).getTemplateView().getRange();
    
                double distance = Math.sqrt(Math.pow(dstX - enemyX, 2) + Math.pow(dstY - enemyY, 2));

                
    
                if (distance <= attackRadius) {
                    // within attack range, very high weight
                    tempweight += 1000;
                } else if (distance <= attackRadius*2) {
                    // within danger zone but not immediate attack range, high weight
                    tempweight += 900;
                } else if (distance <= attackRadius*3) {
                    // decently close but not immediate attack range, mid weight
                    tempweight += 400;
                } else {
                    // safe distance, standard weight
                    tempweight += 100;
                }
            }
        }
        return tempweight;
    }

    @Override
    public boolean shouldReplacePlan(StateView state)
    {
            Stack<Vertex> plan = this.getCurrentPlan();
            if (plan == null || plan.isEmpty()) {
                return true;
            }
            for (Vertex point : plan){
                int pointX = point.getXCoordinate();
                int pointY = point.getYCoordinate();
    
                if (((state.hasUnit(pointX, pointY)) || (state.isResourceAt(pointX, pointY)))){
                    return true;
                }

                // Check for danger based on enemy proximity
                for (Integer enemyId : getOtherEnemyUnitIDs()) {
                    UnitView enemyUnit = state.getUnit(enemyId);
                    if (state.getUnit(enemyId) == null){
                        continue;
                    }
                    int enemyX = state.getUnit(enemyId).getXPosition();
                    int enemyY = state.getUnit(enemyId).getYPosition();
                    int attackRadius = state.getUnit(enemyId).getTemplateView().getRange();
                    double distance = Math.sqrt(Math.pow(pointX - enemyX, 2) + Math.pow(pointY - enemyY, 2));
                    //hardcoding for the timebeing
                    double enemyToTownhallDistance = Math.sqrt(Math.pow(enemyX - 1, 2) + Math.pow(enemyY - 8, 2));
    
                    if (distance <= 3 * attackRadius) {
                        return true;
                    }
                    if (enemyToTownhallDistance <= 3 * attackRadius){
                        return true;
                    }
                }
        }
        return false;
    }
}
