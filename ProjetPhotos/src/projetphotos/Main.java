/**
 *
 * Minimal examples for the project in JAVA
 *
 */


package projetphotos;
import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.Random;

/**
 * @author verel
 * @date 2015/11/07
 */
public class Main {
    // Distance between photos
    public static double [][] photoDist;

    // Inverse of the distance between positions in the album
    public static double [][] albumInvDist;

    /**
     *
     * Example of json file parsing
     *
     * see: https://code.google.com/p/json-simple/
     * for more example to decode json under java
     *
     */
    public static void readPhotoExample(String fileName) {
	try {
	    FileReader reader = new FileReader(fileName);

	    JSONParser parser = new JSONParser();
	    
	    // parser the json file
	    Object obj = parser.parse(reader);
	    //System.out.println(obj);

	    // extract the array of image information
	    JSONArray array = (JSONArray) obj;
	    System.out.println("The first element:\n" + array.get(0));

	    JSONObject obj2 = (JSONObject) array.get(0);
	    System.out.println("the id of the first element is: " + obj2.get("id"));    

	    JSONArray arraytag = (JSONArray) ((JSONObject)obj2.get("tags")).get("classes");
	    System.out.println("Tag list of the first element:");
	    for(int i = 0; i < arraytag.size(); i++)
		System.out.print(" " + arraytag.get(i));
	    System.out.println();

	} catch(ParseException pe) {	    
	    System.out.println("position: " + pe.getPosition());
	    System.out.println(pe);
	} catch (FileNotFoundException ex) {
	    ex.printStackTrace();
	} catch(IOException ex) {
	    ex.printStackTrace();
	}
    }



    /**
     *  Compute the matrice of distance between solutions 
     *                  and of inverse distance between positions 
     */
    public static void computeDistances(String photoFileName, String albumFileName) {
	computePhotoDistances(photoFileName);
	computeAlbumDistances(albumFileName);
    }

    public static void computeAlbumDistances(String fileName) {
	try {
	    FileReader reader = new FileReader(fileName);

	    JSONParser parser = new JSONParser();
	    Object obj = parser.parse(reader);

	    JSONObject album = (JSONObject) obj;

	    // number of pages
	    long nPage = (long) album.get("page");

	    // number of photo in each page
	    JSONArray pageSize = (JSONArray) album.get("pagesize");

	    // number on the first page
	    int size = (int) (long) pageSize.get(0);
	    // total number of photo in the album
	    int nbPhoto = 0;
	    for(int i = 0; i < pageSize.size(); i++) 
		nbPhoto += (int) (long) pageSize.get(i);

	    albumInvDist = new double[nbPhoto][nbPhoto];

	    // compute the distance
	    for(int i = 0; i < nbPhoto; i++) 
		for(int j = 0; j < nbPhoto; j++) 
		    albumInvDist[i][j] = inverseDistance(size, i, j);
	    
	    /*
	    for(int i = 0; i < albumDist.length; i++) {
		for(int j = 0; j < albumDist.length; j++) {
		    System.out.print(" " + albumDist[i][j]);
		}
		System.out.println();
	    }
	    */

	} catch(ParseException pe) {	    
	    System.out.println("position: " + pe.getPosition());
	    System.out.println(pe);
	} catch (FileNotFoundException ex) {
	    ex.printStackTrace();
	} catch(IOException ex) {
	    ex.printStackTrace();
	}
    }

    public static double inverseDistance(int size, int i, int j) {
	// number of pages
	int pagei = i / size;
	int pagej = j / size;

	if (pagei != pagej)
	    // not on the same page: distance is infinite. Another choice is possible of course!
	    return 0;
	else {
	    // positions in the page
	    int posi = i % size;
	    int posj = j % size;

	    // coordinate on the page
	    int xi = posi % 2;
	    int yi = posi / 2;
	    int xj = posj % 2;
	    int yj = posj / 2;

	    // Manhatthan distance
	    return ((double) 1) / (double) (Math.abs(xi - xj) + Math.abs(yi - yj));
	}
    }

    public static void computePhotoDistances(String fileName) {
	try {
	    FileReader reader = new FileReader(fileName);

	    JSONParser parser = new JSONParser();

	    Object obj = parser.parse(reader);

	    JSONArray array = (JSONArray) obj;

	    photoDist = new double[array.size()][array.size()];

	    // distance based on the distance between average hash
	    for(int i = 0; i < array.size(); i++) {
		JSONObject image = (JSONObject) array.get(i);
		JSONArray d = (JSONArray) image.get("ahashdist");		
		for(int j = 0; j < d.size(); j++) {
		    photoDist[i][j] = (double) d.get(j);
		}
	    }
            
            /*
	    for(int i = 0; i < photoDist.length; i++) {
		for(int j = 0; j < photoDist.length; j++) {
		    System.out.print(" " + photoDist[i][j]);
		}
		System.out.println();
	    }
	    */


	} catch(ParseException pe) {	    
	    System.out.println("position: " + pe.getPosition());
	    System.out.println(pe);
	} catch (FileNotFoundException ex) {
	    ex.printStackTrace();
	} catch(IOException ex) {
	    ex.printStackTrace();
	}
    }

    /**
     * Un exemple de fonction objectif (à minimiser):
     *   distance entre les photos pondérées par l'inverse des distances spatiales sur l'album
     *   Modélisaiton comme un problème d'assignement quadratique (QAP)
     *
     *   Dans cette fonction objectif, 
     *      pas de prise en compte d'un effet de page (harmonie/cohérence de la page)
     *      par le choix de distance, pas d'intéraction entre les photos sur des différentes pages
     */
    static double eval(int [] solution) {
	double sum = 0;

	for(int i = 0; i < albumInvDist.length; i++) {
	    for(int j = i + 1; j < albumInvDist.length; j++) {
		sum += photoDist[ solution[i] ][ solution[j] ] * albumInvDist[i][j] ;
	    }
	}

	return sum;
    }

    /* permet de récupérer et de changer le tableau de la meilleur solution pour le HillClimber */
    public static int [] BestSolHC ;
    
   public static int[] getBestSolHC(){
        
        return Main.BestSolHC;
    }
    
    public static void setBestSolHC(int[] newBestSol){
        
        Main.BestSolHC = newBestSol;
    }
    
    /* Algorithme Hill Cliber First Improvement */
    
    public static double hillClimberFirst(int iteration){
       
        double val = 1000 ;
        int numberOfPhoto = 55;
	int [] RandomSolution = randomize(numberOfPhoto);  
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
       
       setBestSolHC(solFinale);
       afficheTableau(Main.getBestSolHC());
        return val;
    }
    
    public static void swapSolution(int[] solution, int nb)
    {
       Random dom = new Random();
       int swapping = dom.nextInt(nb);
       for (int i =0; i<swapping;i++){   
        int r = dom.nextInt(solution.length);   
        int d = dom.nextInt(solution.length);

           int tempR = solution[r] ;       
             int tempD = solution[d];
             
             solution[r] = tempD;
             solution[d] = tempR; 
       } 

    }
    
    
   /* Algorithme Iterated Local Search */
    
    public static double iteratedLocalSearch(int iteration, int nbIterationHc, int swap){
         
        
        int numberOfPhoto = 55;
        double val = hillClimberFirst(nbIterationHc);
        System.out.print(val);
	int [] solution = Main.BestSolHC;  
        //afficheTableau(solution);
        int [] solFinale = new int [numberOfPhoto];
       
        Random dom = new Random();
           
        for (int j = 0; j<iteration;j++){
            
              boolean ok = false;
              int k = 0; 
            
            
         while ( !ok && k<solution.length ){
             
             
            int[] swapSol = solution;
             
             
           int r = dom.nextInt(swap);   
             int d = dom.nextInt(swap); 
             
             int tempR = swapSol[r] ;       
             int tempD = swapSol[d];
             
             swapSol[r] = tempD;
             swapSol[d] = tempR;
             
             double resultat = eval(swapSol); 
             System.out.print(resultat);
             
             if (resultat < val) {
               val = resultat;
              solFinale = swapSol;
               ok = true;
           } 
             
           else {
               
             swapSol[r] = tempR;
             swapSol[d] = tempD;
             
           }
            k++;
         }    
             
         } 

                
      //  System.out.println(val);
      // afficheTableau(solFinale);
       
       
       //System.out.println(val);
        return val;
    }
    
    
    public static void afficheTableau(int [] tableau){
        
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
    
    
    /**
     * @param args
     */
    public static void main(String[] args)  { 
	// Path to the photo information file in json format
	String photoFileName = "data/info-photo.json";
	// Path to the album information file in json format
	String albumFileName = "data/info-album.json";

	// uncomment to test it
	// readPhotoExample(photoFileName);

        
	computeDistances(photoFileName, albumFileName);

	// one basic solution : order of the index

	int numberOfPhoto = 55;
	int [] solution = new int[numberOfPhoto];
         
	for(int i = 0; i < 55; i++)
	    solution[i] = i;

	// compute the fitness
	//System.out.println(eval(solution));
        //hillClimberFirst(10000);
       iteratedLocalSearch(100000,10000,30);
    }


}