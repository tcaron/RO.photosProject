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
    public static void computeDistances(String photoFileName, String albumFileName, String choice) {
	computePhotoDistances(photoFileName,choice);
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

    public static void computePhotoDistances(String fileName, String choice) {
	try {
	    FileReader reader = new FileReader(fileName);

	    JSONParser parser = new JSONParser();

	    Object obj = parser.parse(reader);

	    JSONArray array = (JSONArray) obj;

	    photoDist = new double[array.size()][array.size()];
    
	    // distance based on the distance between average hash
	    for(int i = 0; i < array.size(); i++) {
		JSONObject image = (JSONObject) array.get(i);
		JSONArray ahash = (JSONArray) image.get("ahashdist");
                JSONArray dhash = (JSONArray) image.get("dhashdist");
                JSONArray phash = (JSONArray) image.get("phashdist");
                
                JSONArray color = (JSONArray) image.get("color1");
               // JSONArray tags = (JSONArray) image.get("tags");
                
		for(int j = 0; j <  color.size(); j++) {
                    if (choice == "ahashdist")
                        photoDist[i][j] = (double) ahash.get(j);
                    
                    if(choice == "dhashdist" )
                        photoDist[i][j] = (double) dhash.get(j);
                    
                     if(choice == "phashdist" )
                         photoDist[i][j] = (double) phash.get(j);
                     
                     if(choice == "color")
                         System.out.println(phash.get(j));
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
    public int [] BestSolHC ;
    
   public  int[] getBestSolHC(){
        
        return this.BestSolHC;
    }
    
    public void setBestSolHC(int[] newBestSol){
        
        this.BestSolHC = newBestSol;
    }
    
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

     //  System.out.println(val);
       
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
         System.out.println(eval(solFinale));

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

       
	//computeDistances(photoFileName, albumFileName, "color");

	// one basic solution : order of the index

	//int numberOfPhoto = 55;
	//int [] solution = new int[numberOfPhoto];
   // int solution[] = { 38 ,34 ,24 ,51, 49, 22, 1 ,20, 0 ,33 ,41, 32, 52, 6 ,50, 8, 28, 3 ,5, 48, 43, 42, 2, 36, 14, 26, 25, 37, 30, 46, 7, 17, 40 ,16, 19, 4, 12, 29, 27, 10, 13, 11, 44, 18, 15, 39, 54, 21, 23, 45, 35, 47, 9, 31, 53 };
	//for(int i = 0; i < 55; i++){
	//    solution[i] = i;
       // System.out.println(solution[i]);}
       // Main main = new Main();
       // }
        
      //  for (int j = 1; j<solution.length;j++){
            
         //   System.out.println( solution[j-1]+" "+solution[j] );
         
            
       // }
        try{
            FileReader reader = new FileReader(photoFileName);
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(reader);
             JSONArray array = (JSONArray) obj; double sum=0;
            //System.out.println(obj);
            for (int i = 0; i< array.size();i++){
             JSONObject image =  (JSONObject) array.get(i);
               JSONArray phash = (JSONArray) image.get("phashdist");
              
                 sum = sum + (double)phash.get(j) ;
                // System.out.println(j);
             }
           /*  JSONObject colors = (JSONObject) image.get("color1");
             System.out.print(colors.get("g"));
              System.out.print(" +++++ ");
             System.out.print(colors.get("r"));
              System.out.print(" +++++ ");
             System.out.print(colors.get("b"));
             System.out.println();*/
            }
            System.out.println(sum/64);
        } 
        catch (FileNotFoundException ex) {
	    ex.printStackTrace();
        
        }
         catch(ParseException pe) {	    
	    System.out.println("position: " + pe.getPosition());
	    System.out.println(pe);}
        catch(IOException oe) {	    }
	   
       
	// compute the fitness
  //System.out.println(eval(solution));
    //  main.hillClimberFirst(1000,null);
      // main.iteratedLocalSearch(100,100,55);
      //  System.out.println("---------------------------");
     //  main.afficheTableau(main.getBestSolHC());
    }


}