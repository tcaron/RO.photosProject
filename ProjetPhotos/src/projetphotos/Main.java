/**
 *
 * Minimal examples for the project in JAVA
 *
 */


package projetphotos;
import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
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
    public static double [] colorsDist;
    public static String [][] tagsClasses;
    public static double [] greyavg;
    public static double [] dhash;
    // Inverse of the distance between positions in the album
    public static double [][] albumInvDist;

    /**
     *  Compute the matrice of distance between solutions 
     *                  and of inverse distance between positions 
     */
    public static void computeDistances(String photoFileName, String albumFileName) {
	computePhotoDistances(photoFileName);
       // computeTagsDistance(photoFileName);
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

      public static void computeDHash(String filename){
   
             try {
	    FileReader reader = new FileReader(filename);

	    JSONParser parser = new JSONParser();
           
            Object obj = parser.parse(reader);
            
            JSONArray array = (JSONArray) obj;
            dhash = new double[array.size()];
           for(int i =0;i<array.size();i++){
                
               JSONObject arrayobj = (JSONObject)array.get(i);
              dhash[i] = arrayobj.get("dhash").hashCode();
                          
           }
            
        }
         catch(ParseException pe) {	    
	    System.out.println("position: " + pe.getPosition());
	    System.out.println(pe);
	} catch (FileNotFoundException ex) {
	    ex.printStackTrace();
	} catch(IOException ex) {
	    ex.printStackTrace();
	}
        
    }
          
    public static void computeGreyAvg(String filename){
   
             try {
	    FileReader reader = new FileReader(filename);

	    JSONParser parser = new JSONParser();
           
            Object obj = parser.parse(reader);
            
            JSONArray array = (JSONArray) obj;
            greyavg = new double[array.size()];
           for(int i =0;i<array.size();i++){
                
               JSONObject arrayobj = (JSONObject)array.get(i);
               
             
              
              greyavg[i] = (long) arrayobj.get("greyavg");
          
         
   
             
           }
            
        }
         catch(ParseException pe) {	    
	    System.out.println("position: " + pe.getPosition());
	    System.out.println(pe);
	} catch (FileNotFoundException ex) {
	    ex.printStackTrace();
	} catch(IOException ex) {
	    ex.printStackTrace();
	}
        
    }
        
        
        
        
    
    public static void computeTagsDistance(String filename){
        try {
	    FileReader reader = new FileReader(filename);

	    JSONParser parser = new JSONParser();
           
            Object obj = parser.parse(reader);
            
            JSONArray array = (JSONArray) obj;
            //System.out.println(array.size());
            tagsClasses= new String[array.size()][array.size()];
           for(int i =0;i<array.size();i++){
                
               JSONObject arrayobj = (JSONObject)array.get(i);
               JSONObject tags = (JSONObject) arrayobj.get("tags");
               JSONArray classes = (JSONArray) tags.get("classes");
              // JSONArray probs = (JSONArray) tags.get("probs");
              
               for (int j = 0 ;j<classes.size();j++){
               
               tagsClasses[i][j] = classes.get(j).toString();
               
               System.out.println(tagsClasses[i][j]);
               //System.out.println(tagsClasses[i][j]);
               }
   
             
           }
            
        }
         catch(ParseException pe) {	    
	    System.out.println("position: " + pe.getPosition());
	    System.out.println(pe);
	} catch (FileNotFoundException ex) {
	    ex.printStackTrace();
	} catch(IOException ex) {
	    ex.printStackTrace();
	}
        
    }
    
    public static void computePhotoDistances(String fileName) {
	try {
	    FileReader reader = new FileReader(fileName);

	    JSONParser parser = new JSONParser();

	    Object obj = parser.parse(reader);
            long s = 0;
	    JSONArray array = (JSONArray) obj;

	    photoDist = new double[array.size()][array.size()];
            colorsDist = new double[array.size()];
	    // distance based on the distance between average hash
	    for(int i = 0; i < array.size(); i++) {
		JSONObject image = (JSONObject) array.get(i);
		JSONArray ahash = (JSONArray) image.get("ahashdist");
               
                
               JSONObject color = (JSONObject) image.get("color1");
              
             
                   s  = (long)color.get("r") + (long)color.get("g")+ (long) color.get("b");
                   colorsDist[i] = (double) s/765;
                 
		for(int j = 0; j <  array.size(); j++) {
                   
                        photoDist[i][j] = (double) ahash.get(j);
               
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

    static double eval_greyAvg(int[] solution){
        
        double sum = 0;
        for (int i = 0; i<greyavg.length-1;i++){
            //Math.abs afin de passer outre les résultats négatifs
            sum += Math.abs(greyavg[solution[i]]-greyavg[solution[i+1]]);     
            
        }
       return sum; 
    }
    
    static double eval_dhash (int[] solution){
        
      double sum = 0;
      for (int i =0;i<dhash.length-1;i++){
          
        sum += Math.abs(dhash[solution[i]] + dhash[solution[i+1]]); 
          
      }
      // System.out.println(sum);
       return sum; 
    }
    static double eval_phash(int [] solution){
        
        double sum = 0;
     for (int i = 0; i<albumInvDist.length;i++){
      
         for (int j =i+1;j<albumInvDist.length;j++)
          sum += (photoDist[solution[i]][solution[j-1]] - photoDist[ solution[i]] [solution[j]]);
        
     }   
       return sum; 
    }
    
    
    static double eval_tags(int[]solution){
        double sum=0;
        double diff = 0;  
       
        for (int i = 0 ; i<solution.length-1;i++) { 
            
                 String[] photo1 = new String[tagsClasses[i].length] ;
                 String[]photo2 =new String[tagsClasses[i].length];
            
             for (int j = 0; j<tagsClasses[solution[i]].length;j++){  
               
                 System.out.println(tagsClasses[solution[i]][solution[j]]);
                 photo1[i] = tagsClasses[solution[i]][solution[j]];
                 
                 
                 photo2[i] = tagsClasses[solution[i+1]][solution[j]];
                 // System.out.println(photo1[i]);
                  if ( photo2[i] != photo1[i] )
                  diff ++;
                  sum +=  (diff/photo2.length);
                        
              
             } 
       
        }
        // System.out.println(tagsClasses[50]);
        //System.out.println(diff);
        return sum;
                  
    }
    
    static double eval_color1(int [] solution ){
        
        
        double sum = 0;
       for (int i = 0; i<colorsDist.length-1;i++){
       
           
           System.out.println(colorsDist[i]);
         sum += (colorsDist[solution[i]]+colorsDist[solution[i+1]])/2;
        
     }   
    
       return sum; 
        
    }

    
   
    
    
    /**
     * @param args
     */
    public static void main(String[] args)  { 
	// Path to the photo information file in json format
	String photoFileName = "data/info-photo.json";
	// Path to the album information file in json format
	String albumFileName = "data/info-album.json";

	
        Algo algo = new Algo();
       
        Main main = new Main();
        
       
        computeTagsDistance(photoFileName);
	computeDistances(photoFileName, albumFileName);
        computeGreyAvg(photoFileName);
        computeDHash(photoFileName);
	// one basic solution : order of the index
        
	int numberOfPhoto = 55;
	int [] solution = new int[numberOfPhoto];
   int solution2[] = { 29 ,24 ,34 ,51, 49, 38, 1 ,20, 52 ,6 ,41, 32, 0, 33 ,50, 8, 28, 3 ,5, 48, 43, 42, 2, 36, 14, 26, 25, 37, 30, 46, 7, 17, 40 ,16, 19, 4, 12, 29, 27, 10, 13, 11, 44, 18, 15, 39, 54, 21, 23, 45, 35, 47, 9, 31, 53};
    for(int i = 0; i < 55; i++){
	 solution[i] = i;
      } 
    
    
   // algo.hillClimberFirst(10000, solution);
   // System.out.println(eval_color1(solution));
    //System.out.println(eval_dhash(solution));
   System.out.println(eval_tags(solution));
    }



}
