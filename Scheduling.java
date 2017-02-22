import java.io.*;
import java.util.*;
import java.text.*;

public class Scheduling extends FitnessFunction{

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/


/*******************************************************************************
*                            STATIC VARIABLES                                  *
*******************************************************************************/


/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/
    int num_people = 7;
    int num_times = 35;

	public Scheduling(){
		name = "Scheduling Problem";
	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

	public void doRawFitness(Chromo X){
        /*
		X.rawFitness = 0;
		for (int z=0; z<Parameters.numGenes * Parameters.geneSize; z++){
			if (X.chromo.charAt(z) == '1') X.rawFitness += 1;
		}
        */

        // Chromosome contains a bit string of length: log_2P * T, where P is the number
        // of people being scheduled and T is the number of possible times
        // Each time has log_2P bits to tell what person is being scheduled there

        // Example Chromo:
        // 011 010 011 100 100 001 101 010 100 001 110 001 .........
        // First 3 bits are Monday 8-10, then Monday 10-12, etc. throughout the week

        int top_choice_reward = 10;
        int second_choice_reward = 7;
        int third_choice_reward = 3;
        int available = 1;

        int[] num_assigned = new int[7];

        for (int day = 0; day < 7; day++)
        {
            for (int time = 0; time < 5; time++)
            {
            
                int person_num = Character.getNumericValue(X.chromo.charAt(day*5+time)) - 1;
                
                if (person_num == 7)
                {
                    System.out.println("oops1");
                    X.rawFitness = 0;
                    return;
                }

                int[][] person_preferences = InputSchedule.people.get(person_num).preferences;

                // Keep track of how many times a person has been assigned
                if (person_preferences[day][time] != 0)
                {
                    if (++num_assigned[person_num] > 5)
                    {
                        X.rawFitness = 0;
                        return;
                    }
                }   

                if (person_preferences[day][time] == 1)
                    X.rawFitness += top_choice_reward;
                if (person_preferences[day][time] == 2)
                    X.rawFitness += second_choice_reward;
                if (person_preferences[day][time] == 3)
                    X.rawFitness += third_choice_reward;
                if (person_preferences[day][time] == 4)
                    X.rawFitness += available;
            }
        }
	}

    public static int gray_to_decimal(String gray)
    {
        switch (gray){
        case "000":
            return 0;
        case "001":
            return 1;
        case "011":
            return 2;
        case "010":
            return 3;
        case "110":
            return 4;
        case "111":
            return 5;
        case "101":
            return 6;
        case "100":
            return 7;
        default:
            return 0;
        }
    }

    public static String decimal_to_gray(int decimal)
    {
        switch (decimal){
        case 0:
            return "000";
        case 1:
            return "001";
        case 2:
            return "011";
        case 3:
            return "010";
        case 4:
            return "110";
        case 5:
            return "111";
        case 6:
            return "101";
        case 7:
            return "100";
        default:
            return "000";
        }

    }

//  PRINT OUT AN INDIVIDUAL GENE TO THE SUMMARY FILE *********************************

	public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException{

        System.out.println("Gene Alpha:");
		for (int i=0; i<Parameters.numGenes; i++){
			Hwrite.right(X.getGeneAlpha(i),11,output);
            System.out.println(X.getGeneAlpha(i));
		}
		output.write("   RawFitness");
		output.write("\n        ");
		for (int i=0; i<Parameters.numGenes; i++){
			Hwrite.right(X.getPosIntGeneValue(i),11,output);
		}
		Hwrite.right((int) X.rawFitness,13,output);
		output.write("\n\n");

        String[] days = new String[]{"Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun"};
        String[] times = new String[]{"(8-10)", "(10-12)", "(12-2)", "(2-4)", "(4-6)"};
        // Print genes in a time table friendly format
        for (int day = 0; day < 7; day++)
        {
            for (int time = 0; time < 5; time++)
            {
                int person_num = Character.getNumericValue(X.chromo.charAt(5*day+time)) - 1;

                System.out.printf("%30s", days[day] + " " + times[time] + ": " + InputSchedule.people.get(person_num).name);
            }
            System.out.println();
        }

		return;
	}

/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

}   // End of OneMax.java ******************************************************

