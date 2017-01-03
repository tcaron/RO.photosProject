/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetphotos;

import java.util.Random;
import static projetphotos.Main.eval_phash;

/**
 *
 * @author tcaron
 */
public class Algo extends Main{
    
  /* Algorithme Hill Cliber First Improvement */
    
    public  int[] hillClimberFirst(int iteration, int[] solution){
       
        double val = 1000 ;
        int numberOfPhoto = 55;
        int[] RandomSolution = new int[numberOfPhoto];
        if (solution == null)
        {RandomSolution = randomize(numberOfPhoto); }
       
        else{RandomSolution = solution;}
        
        int [] solFinale = new int [numberOfPhoto];
        double valFinale = 100000;
        int maxIteration = 0;
        Random dom = new Random();
        for (int j = 0; j<iteration;j++){
            
              boolean ok = false;
              int k = 0; 
            
            
         while ( !ok && k<RandomSolution.length ){
             
             int r = dom.nextInt(numberOfPhoto);   
             int d = dom.nextInt(numberOfPhoto); 
             
             int tempR = RandomSolution[r] ;       
             int tempD = RandomSolution[d];
             
             RandomSolution[r] = tempD;
             RandomSolution[d] = tempR;
             
             double resultat = eval(RandomSolution);
             
             
             if (val > resultat) {
               val = resultat;
              solFinale = RandomSolution;
               ok = true;
           } 
             
           else {
               
            RandomSolution[r] = tempR;
             RandomSolution[d] = tempD;
             
           }
            k++;
       
            
         
         }    
             
         } 

      System.out.println(val);
       
       //setBestSolHC(solFinale);
        // afficheTableau(solFinale);
        return solFinale;
    }
    
    public int[] swapSolution(int[] solution, int nb)
    {  
       int[] tabTemp = solution ;
       Random dom = new Random();
       int swapping = dom.nextInt(nb);
       for (int i =0; i<swapping;i++){   
        int r = dom.nextInt(tabTemp.length);   
        int d = dom.nextInt(tabTemp.length);

           int tempR = tabTemp[r] ;       
             int tempD = tabTemp[d];
             
             tabTemp[r] = tempD;
            tabTemp[d] = tempR; 
       } 
      return tabTemp;
    }
    
    
   /* Algorithme Iterated Local Search */
    
    public  int[] iteratedLocalSearch(int iteration, int nbIterationHc, int swap){
         
        
        int numberOfPhoto = 55;
        int[] RandomSol = randomize(numberOfPhoto);
        int [] solution = hillClimberFirst(nbIterationHc,RandomSol);
        double val = eval(solution);
        int [] solFinale = new int [numberOfPhoto];        
       
      //  Random dom = new Random();
            
        int k = 0;  
        
         while ( k<iteration ){  
             
         
           int [] current =  hillClimberFirst(nbIterationHc, swapSolution(solution,swap));
           double resultat = eval(current);
           
          if (resultat < val) {
             
            solFinale = current.clone(); 
             val = resultat;
          
           } 
         k++;   
         }    

         System.out.println(val);
        

        return solFinale;
    }
    
    
    public  void afficheTableau(int [] tableau){
        
         for (int i = 0; i< tableau.length;i++){
            
            System.out.print(tableau[i]+" ");
            
        }
        
       
    }
    public static int[] randomize(int n) {
    int[] returnArray = null;
    if (n > 0) {
        returnArray = new int[n];
        for (int index = 0; index < n; ++index) {
            returnArray[index] = index;
        }
        Random random = new Random(System.currentTimeMillis());
        for (int index = 0; index < n; ++index) {
            int j = (int) (random.nextDouble() * (n - index) + index);
            int tmp = returnArray[index];
            returnArray[index] = returnArray[j];
            returnArray[j] = tmp;
        }
    }
    return returnArray;
}
    
    
    
}
