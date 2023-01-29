import java.util.ArrayList;
import java.util.List;

public class TennerGrid {

    private final int[][] grid;  // 2D array to represent the grid
    private static int[] columnSums; // Array to store the column sums
    private final List<Variable> unassignedVariables; // List to store the unassigned variables (i.e. empty cells in the grid)
    private final List<Solution> solutionPath;  // Solution Path
    private Heuristic heuristic; // Variable to store the current heuristic
    private int variableAssignment ;
    private  int consistencyChecks ;

    public TennerGrid(int rows, int columns, int[] columnSums) {
        // Initialize the grid with the given number of rows and columns
        this.grid = new int[rows][columns];
        // Store the column sums
        TennerGrid.columnSums = columnSums;
        // Initialize the list of unassigned variables
        this.unassignedVariables = new ArrayList<Variable>();
        
        this.solutionPath = new ArrayList<Solution>();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // Add each empty cell in the grid to the list of unassigned variables
                if(this.grid[i][j] == -1){
                    this.unassignedVariables.add(new Variable(i, j));
                }
            }
        }
        variableAssignment = 0;
        consistencyChecks = 0;
    }
    public TennerGrid(int[] columnSums) {
        // Initialize the grid with the given number of rows and columns
        int [][] grid0 = {
            {-1,-1,-1,5,2,7,3,-1,-1,4},
            {3,4,2,0,6,-1,9,7,-1,-1},
            {-1,1,-1,9,2,-1,6,-1,0,3},
            {3,-1,6,1,-1,8,-1,-1,-1,2},
            {8,1,4,-1,-1,3,2,6,0,5}
            };
        this.grid = grid0;
        // Store the column sums
        TennerGrid.columnSums = columnSums;
     
        // Initialize the list of unassigned variables
        this.unassignedVariables = new ArrayList<Variable>();
        
        // Initialize the list of solution variables
        this.solutionPath = new ArrayList<Solution>();
        for (int i = 0; i < grid0.length; i++) {
            for (int j = 0; j < grid0[0].length; j++) {
                // Add each empty cell in the grid to the list of unassigned variables
                if(this.grid[i][j] == -1){
                    this.unassignedVariables.add(new Variable(i, j));
                }                
            }
        }
        variableAssignment = 0;
        consistencyChecks = 0;
    }

    public void setHeuristic(Heuristic heuristic) {
        // Set the current heuristic
        this.heuristic = heuristic;
        
    }

    private boolean isValid(int row, int column, int value) {
        consistencyChecks++;
        // Check if the given value is already in the same row
        for (int i = 0; i < this.grid[row].length; i++) {
            if (this.grid[row][i] == value) {
                return false;
            }
        }
        // Check if the given value is already in the connecting cells
        if ((row > 0 && this.grid[row - 1][column] == value) || (column > 0 && this.grid[row][column - 1] == value)) {
            return false;
        }
        // Check if the column sum is valid
        int sum = 1;
        for (int i = 0; i < this.grid.length; i++) {            
            sum += this.grid[i][column];
        }
        if (sum + value > TennerGrid.columnSums[column]) {
            return false;
        }
        return true;
    }

    public boolean solve() {
        // Check if there are no more unassigned variables
        if (this.unassignedVariables.isEmpty()) {
            return true;
        }
        // Get the next variable to assign based on the current heuristic
        Variable variable = null;
        if (this.heuristic == Heuristic.MRV) {
            variable = this.getMRV();
        } else {
            variable = this.unassignedVariables.get(0);
        }
        
        // Try assigning each value to the variable
        for (int i = 0; i < 10; i++) {
            if (isValid(variable.row, variable.column, i)){
                // Assign the value to the variable
                this.grid[variable.row][variable.column] = i;
                variableAssignment++;
                // remeber solution
                Solution s = new Solution(variable.row, variable.column, i);
                this.solutionPath.add(s);
                // Remove the variable from the list of unassigned variables
                this.unassignedVariables.remove(variable);
                
                // Recursively try to solve the puzzle
                if (this.solve()) {
                    return true;
                } else {
                // Backtrack
                    this.grid[variable.row][variable.column] = -1;
                    this.unassignedVariables.add(variable);
                    this.solutionPath.remove(s);
                }
           }
        }
        return false;
    }
    private Variable getMRV() {
        // Find the variable with the smallest domain (i.e. least number of possible values)
        Variable variable = this.unassignedVariables.get(0);
        int minDomain = 10;
        for (Variable v : this.unassignedVariables) {
            int domain = 0;
            for (int i = 0; i < 10; i++) {
                if (isValid(v.row, v.column, i)) {
                    domain++;
                }
            }
            if (domain < minDomain) {
                variable = v;
                minDomain = domain;
            }
        }
        return variable;
    }
    public static void main(String[] args) {
        // Create a new Tenner Grid puzzle with 5 rows, 10 columns, and the given column sums
        int[][] grid ={
        {6,-1,1,5,7,-1,-1,-1,3,-1},
        {-1,9,7,-1,-1,2,1,-1,-1,-1},
        {-1,-1,-1,-1,-1,0,-1,-1,-1,1},
        {-1,9,-1,0,7,-1,3,5,4,-1},
        {6,-1,-1,5,-1,0,-1,-1,-1,-1}
        };
  
        int[] columnSums = {25,15,20,24,22,27,29,33,8,22};
        //---------------------------------------------------------------------------
        TennerGrid puzzle = new TennerGrid(columnSums);
        System.out.println("\nThe Initial State:");
        printGrid(puzzle.grid);
        puzzle.setHeuristic(Heuristic.MRV);
        // Solve the puzzle using Simple backtracking
        long start = System.currentTimeMillis();
        boolean solved = puzzle.solve();
        long end = System.currentTimeMillis();
        long time = end - start;
        if (solved) {
            System.out.println("Solution found using Simple backtracking:");
              System.out.println("solved the puzzle in " + time + "ms");
               System.out.println("The Number of Assignments for each unassigned variable: "+puzzle.variableAssignment);
                System.out.println("The Number of consistency checks for each unassigned variable: "+puzzle.consistencyChecks);
        
        } else {
            System.out.println("No solution found using Simple backtracking.");
             System.out.println(" failed to solve the puzzle in " + time + "ms");
        
        }
        printGrid(puzzle.grid);
        // find the coulmn sum
        for(int x : columnSums){
            System.out.print("["+x+"]" + " ");
        }
        System.out.println();
        
        System.out.println("The Possible Assignments for each unassigned variable:");
        for(Solution s : puzzle.solutionPath){
            System.out.println(s);
        }

        //---------------------------------------------------------------------------
        // Solve the puzzle using Backtracking with MRV heuristic
        TennerGrid puzzle2 = new TennerGrid(columnSums);
        System.out.println("The Initial State:");
        printGrid(puzzle2.grid);
        long start2 = System.currentTimeMillis();
        boolean solved2 = puzzle2.solve();
        long end2 = System.currentTimeMillis();
        long time2 = end2 - start2;
        puzzle2.setHeuristic(Heuristic.MRV);
        if (solved2) {
            System.out.println("Solution found using Backtracking with MRV heuristic:");
            System.out.println("solved the puzzle in " + time2 + "ms");
            System.out.println("The Number of Assignments for each unassigned variable: "+puzzle2.variableAssignment);
            System.out.println("The Number of consistency checks for each unassigned variable: "+puzzle2.consistencyChecks);
        } else {
            System.out.println("No solution found using Backtracking with MRV heuristic.");
            System.out.println(" failed to solve the puzzle in " + time2 + "ms");
        }
            printGrid(puzzle2.grid);
        // find the coulmn sum
        for(int x : columnSums){
            System.out.print("["+x+"]" + " ");
        }
        System.out.println();
        
        System.out.println("The Possible Assignments for each unassigned variable:");
        for(Solution s : puzzle2.solutionPath){
            System.out.println(s);
        }
   
         //---------------------------------------------------------------------------
        // Solve the puzzle using Forward checking
        TennerGrid puzzle3 = new TennerGrid(columnSums);
        System.out.println("The Initial State:");
        printGrid(puzzle3.grid);
        puzzle3.setHeuristic(Heuristic.FORWARD_CHECKING);
        long start3 = System.currentTimeMillis();
        boolean solved3 = puzzle3.solve();
        long end3 = System.currentTimeMillis();
        long time3 = end3 - start3;
        if (solved3) {
            System.out.println("Solution found using Forward checking:");
            System.out.println("solved the puzzle in " + time3 + "ms");
            System.out.println("The Number of Assignments for each unassigned variable: "+puzzle3.variableAssignment);
            System.out.println("The Number of consistency checks for each unassigned variable: "+puzzle3.consistencyChecks);
        } else {
            System.out.println("No solution found using Forward checking.");
            System.out.println(" failed to solve the puzzle in " + time3 + "ms");
        }
        printGrid(puzzle3.grid);
        // find the coulmn sum
        for(int x : columnSums){
            System.out.print("["+x+"]" + " ");
        }
        System.out.println();
        
        System.out.println("The Possible Assignments for each unassigned variable:");
        for(Solution s : puzzle3.solutionPath){
            System.out.println(s);
        }
        
        //---------------------------------------------------------------------------
        // Solve the puzzle using Forward checking with MRV heuristic
        TennerGrid puzzle4 = new TennerGrid(columnSums);
        System.out.println("The Initial State:");
        printGrid(puzzle4.grid);
        puzzle4.setHeuristic(Heuristic.FORWARD_CHECKING_MRV);
        long start4 = System.currentTimeMillis();
        boolean solved4 = puzzle4.solve();
        long end4 = System.currentTimeMillis();
        long time4 = end4 - start4;
        if (solved4) {
            System.out.println("Solution found using Forward checking with MRV heuristic");
            System.out.println("The Number of Assignments for each unassigned variable: "+puzzle4.variableAssignment);
            System.out.println("The Number of consistency checks for each unassigned variable: "+puzzle4.consistencyChecks);
        } else {
        System.out.println("No solution found using Forward checking with MRV heuristic.");
        System.out.println("solved the puzzle in " + time4 + "ms");
        }
        printGrid(puzzle4.grid);
        // find the coulmn sum
        for(int x : columnSums){
            System.out.print("["+x+"]" + " ");
        }
        System.out.println();
        
        System.out.println("The Possible Assignments for each unassigned variable:");
        for(Solution s : puzzle4.solutionPath){
            System.out.println(s);
        }

        }
    private static void printGrid(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print("["+grid[i][j]+"]" + " ");
            }
            System.out.println();
        }
    }
    // Enum to represent the different heuristics
    private enum Heuristic {
        NONE, MRV, FORWARD_CHECKING, FORWARD_CHECKING_MRV
    }
    // Class to represent a variable (i.e. an empty cell in the grid)
    private class Variable {
        int row;
        int column;
        public Variable(int row, int column) {
            this.row = row;
            this.column = column;
        }
        
        public String toString(){
            return "("+ this.row+ ","+ this.column+") ";
        }
    }
    private class Solution extends Variable{
        int value;
        public Solution(int row, int column, int value){
            super(row, column);
            this.value = value;
        }
        
        public String toString(){
            return "position:("+ this.row+ ","+ this.column+") | Value: " + this.value;
        }
    }
}
  
