package src.pas.battleship.agents;


// SYSTEM IMPORTS
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

// JAVA PROJECT IMPORTS
import edu.bu.battleship.agents.Agent;
import edu.bu.battleship.game.Game.GameView;
import edu.bu.battleship.game.EnemyBoard;
import edu.bu.battleship.game.EnemyBoard.Outcome;
import edu.bu.battleship.utils.Coordinate;
import edu.bu.battleship.game.Constants;
import edu.bu.battleship.game.ships.Ship;
import edu.bu.battleship.game.ships.Ship.ShipType;


public class ProbabilisticAgent
    extends Agent
{

    public ProbabilisticAgent(String name)
    {
        super(name);
        System.out.println("[INFO] ProbabilisticAgent.ProbabilisticAgent: constructed agent");
    }

    private static boolean valid_movement(Ship current_Ship, EnemyBoard.Outcome[][] current_board, GameView game){
        // Get the coordinates that the ship occupies
        Set<Coordinate> ship_Coordinates = current_Ship.getCoordinates();
 
        // For Coordinate in Ship Coordniates

        for(Coordinate coord : ship_Coordinates){
            int x = coord.getXCoordinate();
            int y = coord.getYCoordinate();
 
            // First check if the coordinate is in the bounds of the mpa
            if(!game.isInBounds(coord)){
                // ship is placed outside bounds, so return false
                return false;
            }
            if(current_board[x][y].equals(Outcome.MISS) || current_board[x][y].equals(Outcome.SUNK)){
                // The current placement has already been checked, either by a miss or a hit so not valid
                return false;
            }
        }
        // Nothing in the current coordinates of this placement so return true
        return true;
    }
 
    public double[][] getBoardOfProb(ShipType ship_Type, EnemyBoard.Outcome[][] current_Board, int board_rows, int board_cols , final GameView game)
    {
        // starter code to initialize the ship
        Ship current_Ship = null;
        Coordinate start_Coord = new Coordinate(0,0);

        // to divide the counts in each position in the map
        int total_Placements = 0;

        // Map of battle ship coordinates
        double[][] board_Probabilities = new double[board_rows][board_cols];

        // Points that the ship could occupy
        Set<Coordinate> possible_ship_Coordinates = null;

        for (Ship.Orientation orientation : Ship.Orientation.values()) {
            
            current_Ship = Ship.create(1, ship_Type, start_Coord, orientation);
            int ship_Size = current_Ship.size();

            int to_minus_rows;
            int to_minus_cols;

            if (orientation == Ship.Orientation.VERTICAL){
                to_minus_rows = 0;
                to_minus_cols = (ship_Size - 1);
            } else {
                to_minus_rows = (ship_Size - 1);
                to_minus_cols = 0;
            }

            for (int row = 0; row < board_rows - to_minus_rows; row++){
                for (int col = 0; col < board_cols - to_minus_cols; col++){
                    // get the coordinate at this spot

                    Coordinate next_Possible_Coordinate = new Coordinate(row, col);

                    // move
                    current_Ship = current_Ship.moveTo(next_Possible_Coordinate);

                    if (!valid_movement(current_Ship, current_Board, game)){
                        continue;
                    } else {
                        // Now we know the board is valid

                        // increment the total number of moves
                        total_Placements++;

                        // get the possible locations the ship could be at
                        possible_ship_Coordinates = current_Ship.getCoordinates();

                        // If the current coordinate is a hit we want to increase this probability
                        // so it checks this more urgently
                        if (current_Board[row][col].equals(Outcome.HIT)){
                            // loop through those coordnates and increment their probability
                            for(Coordinate coord : possible_ship_Coordinates){
                                int x = coord.getXCoordinate();
                                int y = coord.getYCoordinate();
                                board_Probabilities[x][y] += 10;
                            }
                        } else {
                            // loop through those coordnates and increment their probability
                            for(Coordinate coord : possible_ship_Coordinates){
                                int x = coord.getXCoordinate();
                                int y = coord.getYCoordinate();
                                board_Probabilities[x][y]++;
                            }
                        }
                    }
               }
            }
        }

        // then, go through the map dividing each count by the number of valid ship placements
        // to get the probability of that placement
        for(int rows = 0; rows < board_rows; rows++){
            for(int cols = 0; cols < board_cols; cols++){
                
                // To avoid dividing by zero
                if (total_Placements != 0){
                    board_Probabilities[rows][cols] = board_Probabilities[rows][cols] / total_Placements;
                }
            }
        }

        return board_Probabilities;
    }

    @Override

    public Coordinate makeMove(final GameView game)
    {
        // Useful constants to have
        Constants game_Constants = game.getGameConstants();
        int board_rows = game_Constants.getNumRows();
        int board_cols = game_Constants.getNumCols();
        EnemyBoard.Outcome[][] current_Board_State = game.getEnemyBoardView();
        // counts of how many of each ship we have
        Map<ShipType, Integer> number_Of_Ships = game_Constants.getShipTypeToPopulation();

        // This will hold probabilties for all the ships
        double[][] total_Board_Probabilities = new double[game_Constants.getNumRows()][game_Constants.getNumCols()];

        // Iterate through each ship type and 
        for (Map.Entry<ShipType, Integer> entry : number_Of_Ships.entrySet()){
                Ship.ShipType shipType = entry.getKey(); // The type of the ship patrol_boat, battleship ...
                int shipCount = entry.getValue(); // Number of ships of this type
                double[][] current_ship_board = getBoardOfProb(shipType, current_Board_State, board_rows, board_cols, game);

                for(int rows = 0; rows < board_rows; rows++){
                    for(int cols = 0; cols < board_cols; cols++){
                        double probability_of_this_position = 0.0;

                        probability_of_this_position += current_ship_board[rows][cols] * shipCount;

                        // += so that we accumulate probabilities
                        total_Board_Probabilities[rows][cols] += probability_of_this_position;
                    }
                }
            }

        Coordinate best_Coordinate = null;
        double best_Probabilty = 0;

        for(int rows = 0; rows < board_rows; rows++){
            for(int cols = 0; cols < board_cols; cols++){

                double p = total_Board_Probabilities[rows][cols];

                if(p > best_Probabilty){

                    // check we haven't tried this coordinate already
                    if(!current_Board_State[rows][cols].equals(Outcome.UNKNOWN)){
                        continue;
                    }
                    best_Probabilty = p;
                    best_Coordinate = new Coordinate(rows, cols);
                }
            }
        }
        return best_Coordinate;
    }

    @Override
    public void afterGameEnds(final GameView game) {}

}
