/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

public class Chromo
{
/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public String chromo;
	public double rawFitness;
	public double sclFitness;
	public double proFitness;

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	private static double randnum;

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public Chromo(){

		ArrayList<Integer> chromolist = new ArrayList<Integer>();
		for(int i=1;i<8;i++)
			for(int j=0;j<5;j++)
				chromolist.add(i);

		Collections.shuffle(chromolist);

		String chromo = "";
		while(!chromolist.isEmpty())
			chromo = chromo + Integer.toString(chromolist.remove(0));
		
		this.chromo = chromo;
		this.rawFitness = -1;
		this.sclFitness = -1;
		this.proFitness = -1;
	}



/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

	//  Get Alpha Represenation of a Gene **************************************

	public String getGeneAlpha(int geneID){
		int start = geneID * Parameters.geneSize;
		int end = (geneID+1) * Parameters.geneSize;
		String geneAlpha = this.chromo.substring(start, end);
		return (geneAlpha);
	}

	//  Get Integer Value of a Gene (Positive or Negative, 2's Compliment) ****

	public int getIntGeneValue(int geneID){
		String geneAlpha = "";
		int geneValue;
		char geneSign;
		char geneBit;
		geneValue = 0;
		geneAlpha = getGeneAlpha(geneID);
		for (int i=Parameters.geneSize-1; i>=1; i--){
			geneBit = geneAlpha.charAt(i);
			if (geneBit == '1') geneValue = geneValue + (int) Math.pow(2.0, Parameters.geneSize-i-1);
		}
		geneSign = geneAlpha.charAt(0);
		if (geneSign == '1') geneValue = geneValue - (int)Math.pow(2.0, Parameters.geneSize-1);
		return (geneValue);
	}

	//  Get Integer Value of a Gene (Positive only) ****************************

	public int getPosIntGeneValue(int geneID){
		String geneAlpha = "";
		int geneValue;
		char geneBit;
		geneValue = 0;
		geneAlpha = getGeneAlpha(geneID);
		for (int i=Parameters.geneSize-1; i>=0; i--){
			geneBit = geneAlpha.charAt(i);
			if (geneBit == '1') geneValue = geneValue + (int) Math.pow(2.0, Parameters.geneSize-i-1);
		}
		return (geneValue);
	}

	//  Mutate a Chromosome Based on Mutation Type *****************************

	public void doMutation(){

		String mutChromo = "";
		char x;

		switch (Parameters.mutationType){

		case 1:     //  Replace with new random number

			for (int j=0; j<(Parameters.geneSize * Parameters.numGenes); j++){
				x = this.chromo.charAt(j);
				randnum = Search.r.nextDouble();
				if (randnum < Parameters.mutationRate){
					if (x == '1') x = '0';
					else x = '1';
				}
				mutChromo = mutChromo + x;
			}
			this.chromo = mutChromo;
			break;

		case 2: 	// Mutation with new representation.

			if(Search.r.nextDouble() < Parameters.mutationRate)
			{
				int whichGene = (int)(Math.random()*35);

				ArrayList<Integer> chromolist = new ArrayList<Integer>();
				for(int i=0; i < 35; i++)
					chromolist.add(Character.getNumericValue(this.chromo.charAt(i)));
				
				int before = chromolist.get(whichGene);
				int after = (int)((Math.random()%7)+1);
				
				int whichInstance = (int)(Math.random()%5) + 1;
				int afterIndex = -1;
				for(int i=0;i<35;i++)
				{
					if(chromolist.get(i) == after)
					{
						whichInstance--;
						if(whichInstance == 0)
							afterIndex = i;
					}
				}
				
				chromolist.set(whichGene, after);
				chromolist.set(afterIndex, before);

				while(!chromolist.isEmpty())
					mutChromo = mutChromo + Integer.toString(chromolist.remove(0));

				this.chromo = mutChromo;
			}
			break;


		default:
			System.out.println("ERROR - No mutation method selected");
		}
	}

/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

	//  Select a parent for crossover ******************************************

	public static int selectParent(){

		double rWheel = 0;
		int j = 0;
		int k = 0;

		switch (Parameters.selectType){

		case 1:     // Proportional Selection
			randnum = Search.r.nextDouble();
			for (j=0; j<Parameters.popSize; j++){
				rWheel = rWheel + Search.member[j].proFitness;
				if (randnum < rWheel) return(j);
			}
			break;

		case 3:     // Random Selection
			randnum = Search.r.nextDouble();
			j = (int) (randnum * Parameters.popSize);
			return(j);

		case 2:     //  Tournament Selection
		            // Assuming k value for tournament selection == 2
		  
		  double tourndifficulty = 0.8;
		  
		  randnum = Search.r.nextDouble();
		  k = (int) (randnum * Parameters.popSize);
		  randnum = Search.r.nextDouble();
		  j = (int) (randnum * Parameters.popSize);

		  if (Search.r.nextDouble() < tourndifficulty)
		  	return (Search.member[k].rawFitness > Search.member[j].rawFitness) ? k : j;
		  else 
		  	return (Search.member[k].rawFitness > Search.member[j].rawFitness) ? j : k;


		default:
			System.out.println("ERROR - No selection method selected");
		}
	return(-1);
	}

	//  Produce a new child from two parents  **********************************

	public static void mateParents(int pnum1, int pnum2, Chromo parent1, Chromo parent2, Chromo child1, Chromo child2){

		int xoverPoint1;
		int xoverPoint2;

		switch (Parameters.xoverType){

		case 1:     //  Single Point Crossover

			//  Select crossover point
			xoverPoint1 = 1 + (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize-1));

			//  Create child chromosome from parental material
			child1.chromo = parent1.chromo.substring(0,xoverPoint1) + parent2.chromo.substring(xoverPoint1);
			child2.chromo = parent2.chromo.substring(0,xoverPoint1) + parent1.chromo.substring(xoverPoint1);
			break;

		case 2:     //  Two Point Crossover
			ArrayList<Integer> parentlist1 = new ArrayList<Integer>();
			ArrayList<Integer> parentlist2 = new ArrayList<Integer>();

			for(int i=0; i<35; i++)
			{
				parentlist1.add(Character.getNumericValue(parent1.chromo.charAt(i)));
				parentlist2.add(Character.getNumericValue(parent2.chromo.charAt(i)));
			}
			
			xoverPoint1 = 1 + (int)(Math.random()*34);

			ArrayList<Integer> sublist1a = new ArrayList<Integer>(parentlist1.subList(0,xoverPoint1));
			ArrayList<Integer> sublist1b = new ArrayList<Integer>(parentlist1.subList(xoverPoint1,parentlist1.size()));
			ArrayList<Integer> sublist2a = new ArrayList<Integer>(parentlist2.subList(0, xoverPoint1));
			ArrayList<Integer> sublist2b = new ArrayList<Integer>(parentlist2.subList(xoverPoint1,parentlist2.size()));

			for(int i=0; i<sublist2b.size();i++)
			{
				parentlist1.set(xoverPoint1 + i, sublist2b.get(i));
			}
			for(int i=0; i<sublist1b.size();i++)
			{
				parentlist2.set(xoverPoint1 + i, sublist1b.get(i));
			}
			ArrayList<Integer> child1list = new ArrayList<Integer>();
			ArrayList<Integer> child2list = new ArrayList<Integer>();
			child1list.addAll(sublist1a);
			child2list.addAll(sublist2a);
			child1list.addAll(sublist2b);
			child2list.addAll(sublist1b);

			int[] checkarray1 = new int[8]; // int[] checkarray1 = new int[7];
			int[] checkarray2 = new int[8];


			ArrayList<Integer> morethanfive1 = new ArrayList<Integer>();
			ArrayList<Integer> lessthanfive1 = new ArrayList<Integer>();
			ArrayList<Integer> morethanfive2 = new ArrayList<Integer>();
			ArrayList<Integer> lessthanfive2 = new ArrayList<Integer>();

			for(int i=0; i < 35; i++)
			{
				checkarray1[child1list.get(i)]++;
				checkarray2[child2list.get(i)]++;
			}
			for(int i=1;i<8;i++)
			{
				if (checkarray1[i] != 5)
				{
					if (checkarray1[i] > 5)
						morethanfive1.add(i);
					else
						lessthanfive1.add(i);
				}
				if (checkarray2[i] != 5)
				{
					
					if (checkarray2[i] > 5)
						morethanfive2.add(i);
					else
						lessthanfive2.add(i);
				}
			}

			while(!morethanfive1.isEmpty())
			{

				int decElement = morethanfive1.remove(0);
				int incElement = lessthanfive1.remove(0);

				checkarray1[incElement]++;
				checkarray1[decElement]--;
				if(checkarray1[incElement] < 5)
					lessthanfive1.add(incElement);
				if(checkarray1[decElement] > 5)
					morethanfive1.add(decElement);

				child1list.set(child1list.indexOf(decElement), incElement);
			}

			while(!morethanfive2.isEmpty())
			{
				int decElement = morethanfive2.remove(0);
				int incElement = lessthanfive2.remove(0);

				checkarray2[incElement]++;
				checkarray2[decElement]--;
				if(checkarray2[incElement] < 5)
					lessthanfive2.add(incElement);
				if(checkarray2[decElement] > 5)
					morethanfive2.add(decElement);

				child2list.set(child2list.indexOf(decElement), incElement);

			}

			String child1string = "";
			String child2string = "";


			for(int i=0; i<35; i++)
			{
				child1string = child1string + Integer.toString(child1list.get(i));
				child2string = child2string + Integer.toString(child2list.get(i));

			}

			child1.chromo = child1string;
			child2.chromo = child2string;
			break;


		case 3:     //  Uniform Crossover

		default:
			System.out.println("ERROR - Bad crossover method selected");
		}

		//  Set fitness values back to zero
		child1.rawFitness = -1;   //  Fitness not yet evaluated
		child1.sclFitness = -1;   //  Fitness not yet scaled
		child1.proFitness = -1;   //  Fitness not yet proportionalized
		child2.rawFitness = -1;   //  Fitness not yet evaluated
		child2.sclFitness = -1;   //  Fitness not yet scaled
		child2.proFitness = -1;   //  Fitness not yet proportionalized
	}

	//  Produce a new child from a single parent  ******************************

	public static void mateParents(int pnum, Chromo parent, Chromo child){

		//  Create child chromosome from parental material
		child.chromo = parent.chromo;

		//  Set fitness values back to zero
		child.rawFitness = -1;   //  Fitness not yet evaluated
		child.sclFitness = -1;   //  Fitness not yet scaled
		child.proFitness = -1;   //  Fitness not yet proportionalized
	}



	//  Copy one chromosome to another  ***************************************

	public static void copyB2A (Chromo targetA, Chromo sourceB){

		targetA.chromo = sourceB.chromo;

		targetA.rawFitness = sourceB.rawFitness;
		targetA.sclFitness = sourceB.sclFitness;
		targetA.proFitness = sourceB.proFitness;
		return;
	}

}   // End of Chromo.java ******************************************************
