package src.labs.pitfall.agents;


// SYSTEM IMPORTS
import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


// JAVA PROJECT IMPORTS
import edu.bu.labs.pitfall.Difficulty;
import edu.bu.labs.pitfall.Synchronizer;
import edu.bu.labs.pitfall.utilities.Coordinate;



public class BayesianAgent
    extends Agent
{

    public static class PitfallBayesianNetwork
        extends Object
    {
        private Map<Coordinate, Boolean>    knownBreezeCoordinates;
        private Set<Coordinate>             frontierPitCoordinates;
        private Set<Coordinate>             otherPitCoordinates;
        private final double                pitProb;

        public PitfallBayesianNetwork(Difficulty difficulty)
        {
            this.knownBreezeCoordinates = new HashMap<Coordinate, Boolean>();

            this.frontierPitCoordinates = new HashSet<Coordinate>();
            this.otherPitCoordinates = new HashSet<Coordinate>();

            this.pitProb = Difficulty.getPitProbability(difficulty);
        }

        public Map<Coordinate, Boolean> getKnownBreezeCoordinates() { return this.knownBreezeCoordinates; }
        public Set<Coordinate> getFrontierPitCoordinates() { return this.frontierPitCoordinates; }
        public Set<Coordinate> getOtherPitCoordinates() { return this.otherPitCoordinates; }
        public final double getPitProb() { return this.pitProb; }


        /**
         *  TODO: please replace this code. The code here will pick a **random** frontier square to explore next,
         *        which may be a pit! You should do the following steps:
         *          1) for each frontier square X, calculate the query Pr[Pit_X = true | evidence]
         *             we typically expand this to say:
         *                         Pr[Pit_X = true | evidence] = alpha * Pr[Pit_X = true && evidence]
         *             however you don't need to calculate alpha explicitly.
         *             If you calculate Pr[Pit_X = true && evidence] for every X, you can convert the values into
         *             probabilities by adding up all Pr[Pit_X = true && evidence] values and dividing each
         *             Pr[Pit_X = true && evidence] value by the sum.
         *
         *          2) pick the pit that is the least likely to have a pit in it to explore next!
         *
         *          As an aside here, you can certainly choose to calculate Pr[Pit_X = false | evidence] values
         *          instead (and then pick the coordinate with the highest prob), its up to you!
         **/

        //  // Helper function that generates binary combinations
        //  private List<List<Boolean>> generateCombinations(int n) {
        //     List<List<Boolean>> combinations = new ArrayList<>();
        //     boolean[] combination = new boolean[n];
        //     generateAllBinaryStrings(n, combination, 0, combinations);
        //     return combinations;
        // }

        // // Helper function that generates binary combinations
        // private void generateAllBinaryStrings(int n, boolean[] combination, int i, List<List<Boolean>> combinations) {
        //     if (i == n) {
        //         List<Boolean> combinationList = new ArrayList<>();
        //         for (boolean value : combination) {
        //             combinationList.add(value);
        //         }
        //         combinations.add(combinationList);
        //         return;
        //     }
    
        //     combination[i] = false;
        //     generateAllBinaryStrings(n, combination, i + 1, combinations);
    
        //     combination[i] = true;
        //     generateAllBinaryStrings(n, combination, i + 1, combinations);
        // }

        // // Checking if a current combination is Valid
        // private Boolean is_valid_pit(Map<Coordinate, Boolean> breezeCoordinates ,List<Coordinate> frontierCoordinates, List<Boolean> combination){
        //     List<Coordinate> frontierCoordinatesToCheck = new ArrayList<Coordinate>();

        //     for (int z = 0; z < combination.size(); z++) {
        //         if (combination.get(z)){
        //             frontierCoordinatesToCheck.add(frontierCoordinates.get(z));
        //         }
        //     }

        //     for(Coordinate coord: frontierCoordinatesToCheck){
        //         int currentCoordinateX = coord.getXCoordinate();
        //         int currentCoordinateY = coord.getYCoordinate();
                
        //         Coordinate north = new Coordinate(currentCoordinateX, currentCoordinateY+1);
        //         Coordinate south = new Coordinate(currentCoordinateX, currentCoordinateY-1);
        //         Coordinate east = new Coordinate(currentCoordinateX+1, currentCoordinateY);
        //         Coordinate west = new Coordinate(currentCoordinateX-1, currentCoordinateY);
    
        //         if(((breezeCoordinates.containsKey(north) && !breezeCoordinates.get(north)) || (breezeCoordinates.containsKey(south) && !breezeCoordinates.get(south)) || 
        //         (breezeCoordinates.containsKey(east) && !breezeCoordinates.get(east)) || (breezeCoordinates.containsKey(west) && !breezeCoordinates.get(west)))){
        //             // all discovered neighbors of the pit do not meet the requirement for the current frontier to have a pit
        //             // so it must not have a pit
        //             return false;
        //         }
        //     }
        //     return true;
        // }


        // // Checking if a current combination is Valid
        // private Boolean is_valid_breeze(Map<Coordinate, Boolean> breezeCoordinates ,List<Coordinate> frontierCoordinates, List<Boolean> combination){
        //     for(Coordinate breeze: breezeCoordinates.keySet()){

        //         Boolean has_breeze = breezeCoordinates.get(breeze);
        //         if(has_breeze){
        //             int currentCoordinateX = breeze.getXCoordinate();
        //             int currentCoordinateY = breeze.getYCoordinate();
    
        //             Coordinate north = new Coordinate(currentCoordinateX, currentCoordinateY+1);
        //             Coordinate south = new Coordinate(currentCoordinateX, currentCoordinateY-1);
        //             Coordinate east = new Coordinate(currentCoordinateX+1, currentCoordinateY);
        //             Coordinate west = new Coordinate(currentCoordinateX-1, currentCoordinateY);

        //             if (!breezeCoordinates.get(north)) {
        //                 // bruh i tried
        //                 int i;
        //                 for (i = 0; i < frontierCoordinates.size();i++){
        //                     Coordinate Temp = frontierCoordinates.get(i);
        //                     if (Temp.equals(north)){
        //                         break;
        //                     }
        //                 }

        //                 Boolean is_pit = combination.get(i);
        //                 if (is_pit){
        //                     continue;
        //                 }
        //             } else if (!breezeCoordinates.get(south)) {
        //                 int index = frontierCoordinates.indexOf(south);
        //                 if (index >= combination.size()){
        //                     return false;
        //                 }
        //                 Boolean is_pit = combination.get(index);
        //                 if (is_pit){
        //                     continue;
        //                 }
        //             } else if (!breezeCoordinates.get(west)) {
        //                 int index = frontierCoordinates.indexOf(west);
        //                 if (index >= combination.size()){
        //                     return false;
        //                 }
        //                 Boolean is_pit = combination.get(index);
        //                 if (is_pit){
        //                     continue;
        //                 }
        //             }  else if (!breezeCoordinates.get(east)) {
        //                 int index = frontierCoordinates.indexOf(east);
        //                 if (index >= combination.size()){
        //                     return false;
        //                 }
        //                 Boolean is_pit = combination.get(index);
        //                 if (is_pit){
        //                     continue;
        //                 }
        //             } else {
        //                 return false;
        //             }
        //         }
        //     }
        //     return true;
        // }

        // public Coordinate getNextCoordinateToExplore(){

        //     List<Coordinate> frontierCoordinates = new ArrayList<Coordinate>(this.getFrontierPitCoordinates());
        //     Map<Coordinate, Boolean> breezeCoordinates = getKnownBreezeCoordinates();
        //     double pitProbability = this.getPitProb();

        //     HashMap<Coordinate, Double> Frontier_probabilities = new HashMap<Coordinate, Double>();

        //     List<List<Boolean>> combinations = generateCombinations(frontierCoordinates.size());
        //     List<List<Boolean>> validCombinations = new ArrayList<>();
        //     System.out.println(frontierCoordinates);

        //     for (List<Boolean> combination: combinations){
        //         if (is_valid_pit(breezeCoordinates, frontierCoordinates, combination)){
        //             System.out.println(combination);
        //             if (is_valid_breeze(breezeCoordinates, frontierCoordinates, combination)){
        //                 validCombinations.add(combination);
        //             }
        //         }
        //     }
                 
        //     for (int i = 0; i < frontierCoordinates.size(); i++) {
        //         Coordinate coord = frontierCoordinates.get(i);
        //         Frontier_probabilities.put(coord, 0.0);

        //         for (List<Boolean> combination: validCombinations){
        //             if(combination.get(i)){ // If there's a pit in the current combination at this frontier
        //                 double combinationProbability = 1.0;
        //                 // Calculate the probability of this combination
        //                 for (int x = 0; x < combination.size(); x++) {
        //                     if (combination.get(x)){
        //                         combinationProbability *= pitProbability;
        //                     }else{
        //                         combinationProbability *= (1 - pitProbability);
        //                     }
        //                 }
        //                 double currentProbability = Frontier_probabilities.get(coord);
        //                 Frontier_probabilities.put(coord, currentProbability + combinationProbability);
        //             }
        //         }
        //     }

        //     // Find the coordinate with the minimum probability to explore
        //     Coordinate safestCoordinate = null;
        //     double minProbability = Double.MAX_VALUE;

        //     for (Coordinate coord : frontierCoordinates) {
        //         System.out.println(coord + ": " + Frontier_probabilities.get(coord));
        //     }
            
        //     for (Map.Entry<Coordinate, Double> entry : Frontier_probabilities.entrySet()) {
        //         if (entry.getValue() < minProbability) {
        //             minProbability = entry.getValue();
        //             safestCoordinate = entry.getKey();
        //         }
        //     }
        //     System.out.println("\n\n Our answer" + minProbability + " " + safestCoordinate);
        //     return safestCoordinate; // Return the safest coordinate to explore next

        //  }

        public Coordinate getNextCoordinateToExplore()
        {
            System.out.println("\nMy probs:\n");
            Coordinate toExplore = null;
            double minProb = Double.MAX_VALUE; // Use MAX_VALUE for minimum comparison

            Map<Coordinate, List<Coordinate>> guaranteedPits = new HashMap<>();
            Map<Coordinate, List<Coordinate>> frontToBreeze = new HashMap<>();
            for (Coordinate c : this.getFrontierPitCoordinates()) {

                int breezeCount = 0;
                List<Coordinate> breezesList = new ArrayList<>();
                List<Coordinate> neighbors = this.getNeighbors(c); 

                int unexploredNeighbors = 0;

                //breezes
                Map<Coordinate, Boolean> breezes = getKnownBreezeCoordinates();

                for (Coordinate neighbor : neighbors) {
                    if(breezes.containsKey(neighbor)){
                        if(breezes.get(neighbor)){
                            breezeCount++;
                            breezesList.add(neighbor);
                        }
                    }
                    if(!getKnownBreezeCoordinates().keySet().contains(neighbor) && getOtherPitCoordinates().contains(neighbor) && !getFrontierPitCoordinates().contains(neighbor) ){
                        unexploredNeighbors +=1;
                    }
                }
                // Assuming getUnexploredNeighbors returns a count of neighbors that are unexplored and could potentially be pits
                if (breezeCount > unexploredNeighbors) {
                    System.out.println("\n\n\n\\n" + c.toString() + breezesList.toString()+"\n\n\n\\n");
                    guaranteedPits.put(c, breezesList);
                }
                frontToBreeze.put(c, breezesList);
            }

            // Iterate through each frontier coordinate to calculate its pit probability
            for (Coordinate c : this.getFrontierPitCoordinates()) {
                double probOfPit = 0; // Probability of pit at coordinate c

                List<Coordinate> neighbors = this.getNeighbors(c); 

                // Total possible configurations: 2^neighbors.size() (each neighbor can either be a pit or not)
                int totalConfigs = (int) Math.pow(2, getFrontierPitCoordinates().size());
                for (int config = 0; config < totalConfigs; config++) {
                    List<Integer> pitConfig = getConfiguration(config, getFrontierPitCoordinates().size()); // Convert config to binary representation

                    if (isValidConfiguration(pitConfig, neighbors)) { // Check if config is valid based on known breezes
                        System.out.println(pitConfig.toString());
                        double configProb = calculateConfigProbability(c, pitConfig, neighbors, guaranteedPits, frontToBreeze); // Calculate probability of this configuration
                        probOfPit += configProb;
                        System.out.print(c.toString());
                    }
                }
                System.out.println(probOfPit);

                if (probOfPit < minProb) {
                    minProb = probOfPit;
                    toExplore = c;  
                }
            }
            return toExplore;
        }

        private List<Coordinate> getNeighbors(Coordinate frontier){
            // Calculate all possible valid configurations for pits around c
                // Considering a simplified model where we look at direct (cardinal) neighbors only
                int frontier_x = frontier.getXCoordinate();
                int frontier_y = frontier.getYCoordinate();

                Coordinate north = new Coordinate(frontier_x, frontier_y+1);
                Coordinate south = new Coordinate(frontier_x, frontier_y-1);
                Coordinate east = new Coordinate(frontier_x+1, frontier_y);
                Coordinate west = new Coordinate(frontier_x-1, frontier_y);

                // Creating a list to hold the coordinates
                List<Coordinate> neighbors = new ArrayList<>();
                // Adding each coordinate to the list
                neighbors.add(north);
                neighbors.add(south);
                neighbors.add(east);
                neighbors.add(west);

                return neighbors;
        }

        private List<Integer> getConfiguration(int config, int size) {
            String binaryString = Integer.toBinaryString(config);
            // Pad the binary string with leading zeros to match the size (number of neighbors)
            String paddedBinaryString = String.format("%" + size + "s", binaryString).replace(' ', '0');
        
            List<Integer> configuration = new ArrayList<>();
            for (char c : paddedBinaryString.toCharArray()) {
                // Add true for '1' (indicating a pit) and false for '0' (no pit)
                if (c == '1'){
                    configuration.add(1);
                }
                else{
                    configuration.add(0);
                }
            }
            return configuration;
        }

        private Boolean isValidConfiguration(List<Integer> pitConfig, List<Coordinate> neighbors){

            for(int frontierConfig : pitConfig){
                int countBreeze = 0;
                

                for(Coordinate neighbor: neighbors){
                    //breezes
                    Map<Coordinate, Boolean> breezes = getKnownBreezeCoordinates();
                    //if explored/in bounds node && breeze value false
                    if(breezes.containsKey(neighbor)){
                        if(breezes.get(neighbor)){
                            countBreeze+=1;
                        }
                    }
                }

                if(countBreeze == 0 && frontierConfig == 1){
                    return false;
                }  
                
            }
            return true;

        }
        private double calculateConfigProbability(Coordinate curr_frontier, List<Integer> pitConfig, List<Coordinate> neighbors,  Map<Coordinate, List<Coordinate>> guaranteedPits, Map<Coordinate, List<Coordinate>> frontToBreeze){
            int num_pits = 0;
            int num_frontier = 0;

            for(int frontier: pitConfig){
                if(frontier == 1){
                    num_pits  += 1;
                }
                num_frontier+=1;
                
            }

            double prob = Math.pow(this.pitProb, num_pits) * Math.pow((1-this.pitProb), num_frontier - num_pits);


            //more breezes = more likely pit

            int countBreeze = 0;
            int unexploredNeighbors = 0;



            for(Coordinate neighbor: neighbors){

                //breezes
                Map<Coordinate, Boolean> breezes = getKnownBreezeCoordinates();
                //if explored/in bounds node && breeze value false
                if(breezes.containsKey(neighbor)){
                    if(breezes.get(neighbor)){
                        for(Coordinate gf: guaranteedPits.keySet()){
                            List<Coordinate> brezloc = guaranteedPits.get(gf);
                            // if the breeze is not part of breezes for guarnteed pits then count
                            if (!curr_frontier.equals(gf) && !brezloc.contains(neighbor)){
                                countBreeze +=1;
                            }
                        }
                       
                    }
                    else{
                        if(!getOtherPitCoordinates().contains(neighbor)){
                            unexploredNeighbors = 100;
                        }
                    }       
      
                }
                    

        
            //     it is not a breeze and is part of other pits
            //    else if(getOtherPitCoordinates().contains(neighbor)&& !breezes.get(neighbor) && !getFrontierPitCoordinates().contains(neighbor)){
            //         prob = prob-1;
            //     }
            }

            

            return prob + countBreeze - unexploredNeighbors;
        }

    }

    private int                     myUnitID;
    private int                     enemyPlayerNumber;
    private Set<Coordinate>         gameCoordinates;
    private Set<Coordinate>         unexploredCoordinates;
    private Coordinate              coordinateIJustAttacked;
    private Coordinate              srcCoordinate;
    private Coordinate              dstCoordinate;
    private PitfallBayesianNetwork  bayesianNetwork;

    private final Difficulty        difficulty;

	public BayesianAgent(int playerNum, String[] args)
	{
        super(playerNum);

        if(args.length != 3)
		{
			System.err.println("[ERROR] BayesianAgent.BayesianAgent: need to provide args <playerID> <seed> <difficulty>");
		}

        this.myUnitID = -1;
        this.enemyPlayerNumber = -1;
        this.gameCoordinates = new HashSet<Coordinate>();
        this.unexploredCoordinates = new HashSet<Coordinate>();
        this.coordinateIJustAttacked = null;
        this.srcCoordinate = null;
        this.dstCoordinate = null;
        this.bayesianNetwork = null;

        this.difficulty = Difficulty.valueOf(args[2].toUpperCase());
	}

	public int getMyUnitID() { return this.myUnitID; }
    public int getEnemyPlayerNumber() { return this.enemyPlayerNumber; }
    public Set<Coordinate> getGameCoordinates() { return this.gameCoordinates; }
    public Set<Coordinate> getUnexploredCoordinates() { return this.unexploredCoordinates; }
    public final Coordinate getCoordinateIJustAttacked() { return this.coordinateIJustAttacked; }
    public final Coordinate getSrcCoordinate() { return this.srcCoordinate; }
    public final Coordinate getDstCoordinate() { return this.dstCoordinate; }
    public PitfallBayesianNetwork getBayesianNetwork() { return this.bayesianNetwork; }
    public final Difficulty getDifficulty() { return this.difficulty; }

    private void setMyUnitID(int i) { this.myUnitID = i; }
    private void setEnemyPlayerNumber(int i) { this.enemyPlayerNumber = i; }
    private void setCoordinateIJustAttacked(Coordinate c) { this.coordinateIJustAttacked = c; }
    private void setSrcCoordinate(Coordinate c) { this.srcCoordinate = c; }
    private void setDstCoordinate(Coordinate c) { this.dstCoordinate = c; }
    private void setBayesianNetwork(PitfallBayesianNetwork n) { this.bayesianNetwork = n; }

	@Override
	public Map<Integer, Action> initialStep(StateView state,
                                            HistoryView history)
	{

		// locate enemy and friendly units
        Set<Integer> myUnitIDs = new HashSet<Integer>();
		for(Integer unitID : state.getUnitIds(this.getPlayerNumber()))
        {
            myUnitIDs.add(unitID);
        }

        if(myUnitIDs.size() != 1)
        {
            System.err.println("[ERROR] PitfallAgent.initialStep: should only have 1 unit but found "
                + myUnitIDs.size());
            System.exit(-1);
        }

		// check that all units are archers units
	    if(!state.getUnit(myUnitIDs.iterator().next()).getTemplateView().getName().toLowerCase().equals("archer"))
	    {
		    System.err.println("[ERROR] PitfallAgent.initialStep: should only control archers!");
		    System.exit(1);
	    }

        // get the other player
		Integer[] playerNumbers = state.getPlayerNumbers();
		if(playerNumbers.length != 2)
		{
			System.err.println("ERROR: Should only be two players in the game");
			System.exit(-1);
		}
		Integer enemyPlayerNumber = null;
		if(playerNumbers[0] != this.getPlayerNumber())
		{
			enemyPlayerNumber = playerNumbers[0];
		} else
		{
			enemyPlayerNumber = playerNumbers[1];
		}

        // check enemy units
        Set<Integer> enemyUnitIDs = new HashSet<Integer>();
        for(Integer unitID : state.getUnitIds(enemyPlayerNumber))
        {
            if(!state.getUnit(unitID).getTemplateView().getName().toLowerCase().equals("hiddensquare"))
		    {
			    System.err.println("ERROR [BayesianAgent.initialStep]: Enemy should start off with HiddenSquare units!");
			        System.exit(-1);
		    }
            enemyUnitIDs.add(unitID);
        }


        // initially everything is unknown
        Coordinate coord = null;
        for(Integer unitID : enemyUnitIDs)
        {
            coord = new Coordinate(state.getUnit(unitID).getXPosition(),
                                   state.getUnit(unitID).getYPosition());
            this.getUnexploredCoordinates().add(coord);
            this.getGameCoordinates().add(coord);
        }

        this.setMyUnitID(myUnitIDs.iterator().next());
        this.setEnemyPlayerNumber(enemyPlayerNumber);
        this.setSrcCoordinate(new Coordinate(1, state.getYExtent() - 2));
        this.setDstCoordinate(new Coordinate(state.getXExtent() - 2, 1));
        this.setBayesianNetwork(new PitfallBayesianNetwork(this.getDifficulty()));

        Map<Integer, Action> initialActions = new HashMap<Integer, Action>();
        initialActions.put(
            this.getMyUnitID(),
            Action.createPrimitiveAttack(
                this.getMyUnitID(),
                state.unitAt(this.getSrcCoordinate().getXCoordinate(), this.getSrcCoordinate().getYCoordinate())
            )
        );
        this.getUnexploredCoordinates().remove(this.getSrcCoordinate());
		return initialActions;
	}

    public boolean isFrontierCoordiante(Coordinate src,
                                        StateView state)
    {
        int dirs[][] = new int[][]{{-1, 0}, {+1, 0}, {0, -1}, {0, +1}};
        for(int dir[] : dirs)
        {
            int x = src.getXCoordinate() + dir[0];
            int y = src.getYCoordinate() + dir[1];

            if(x >= 1 && x <= state.getXExtent() - 2 &&
               y >= 1 && y <= state.getYExtent() - 2 &&
               (!state.isUnitAt(x, y) ||
                !state.getUnit(state.unitAt(x, y)).getTemplateView().getName().toLowerCase().equals("hiddensquare")))
            {
                return true;
            }
        }
        return false;
    }

    public void makeObservations(StateView state,
                                 HistoryView history)
    {
        this.getBayesianNetwork().getKnownBreezeCoordinates().clear();
        this.getBayesianNetwork().getFrontierPitCoordinates().clear();
        this.getBayesianNetwork().getOtherPitCoordinates().clear();

        Set<Coordinate> exploredCoordinates = new HashSet<Coordinate>();
        for(Integer enemyUnitID : state.getUnitIds(this.getEnemyPlayerNumber()))
        {
            UnitView enemyUnitView = state.getUnit(enemyUnitID);
            if(enemyUnitView.getTemplateView().getName().toLowerCase().equals("breezesquare"))
            {
                this.getBayesianNetwork().getKnownBreezeCoordinates().put(
                    new Coordinate(enemyUnitView.getXPosition(),
                                   enemyUnitView.getYPosition()),
                    true
                );
            } else if(enemyUnitView.getTemplateView().getName().toLowerCase().equals("safesquare"))
            {
                this.getBayesianNetwork().getKnownBreezeCoordinates().put(
                    new Coordinate(enemyUnitView.getXPosition(),
                                   enemyUnitView.getYPosition()),
                    false
                );
            } else if(enemyUnitView.getTemplateView().getName().toLowerCase().equals("hiddensquare"))
            {
                this.getBayesianNetwork().getOtherPitCoordinates().add(
                    new Coordinate(enemyUnitView.getXPosition(),
                                   enemyUnitView.getYPosition())
                );
            }

            // now separate out the frontier from the "other" ones
            for(Coordinate unknownCoordinate : this.getBayesianNetwork().getOtherPitCoordinates())
            {
                if(this.isFrontierCoordiante(unknownCoordinate, state))
                {
                    this.getBayesianNetwork().getFrontierPitCoordinates().add(unknownCoordinate);
                }
            }
            this.getBayesianNetwork().getOtherPitCoordinates().removeAll(
                this.getBayesianNetwork().getFrontierPitCoordinates()
            );
        }
    }

	@Override
	public Map<Integer, Action> middleStep(StateView state,
                                           HistoryView history) {
		Map<Integer, Action> actions = new HashMap<Integer, Action>();

        if(Synchronizer.isMyTurn(this.getPlayerNumber(), state))
        {

            // get the observation from the past
            if(state.getTurnNumber() > 0)
            {
                this.makeObservations(state, history);
            }

            Coordinate coordinateOfUnitToAttack = this.getBayesianNetwork().getNextCoordinateToExplore();

            // could have won the game (and waiting for enemy units to die)
            // or we have a coordinate to attack
            // we need to check that the unit at that coordinate is a hidden square (not allowed to attack other units)
            if(coordinateOfUnitToAttack != null)
            {
                Integer unitID = state.unitAt(coordinateOfUnitToAttack.getXCoordinate(),
                                              coordinateOfUnitToAttack.getYCoordinate());
                if(unitID == null)
                {
                    System.err.println("ERROR: BayesianAgent.middleStep: deciding to attack unit at " +
                        coordinateOfUnitToAttack + " but no unit was found there!");
                    System.exit(-1);
                }

                String unitTemplateName = state.getUnit(unitID).getTemplateView().getName();
                if(!unitTemplateName.toLowerCase().equals("hiddensquare"))
                {
                    // can't attack non hidden-squares!
                    System.err.println("ERROR: BayesianAgent.middleStep: deciding to attack unit at " +
                        coordinateOfUnitToAttack + " but unit at that square is [" + unitTemplateName + "] " +
                        "and should be a HiddenSquare unit!");
                    System.exit(-1);
                }
                this.setCoordinateIJustAttacked(coordinateOfUnitToAttack);

                actions.put(
                    this.getMyUnitID(),
                    Action.createPrimitiveAttack(
                        this.getMyUnitID(),
                        unitID)
                );
                this.getUnexploredCoordinates().remove(coordinateOfUnitToAttack);
            }

        }

		return actions;
	}

    @Override
	public void terminalStep(StateView state, HistoryView history) {}

    @Override
	public void loadPlayerData(InputStream arg0) {}

	@Override
	public void savePlayerData(OutputStream arg0) {}

}

