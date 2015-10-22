package cz.wikkiw.de;

import cz.wikkiw.fitnessfunctions.Ackley;
import cz.wikkiw.fitnessfunctions.FitnessFunction;
import cz.wikkiw.fitnessfunctions.Griewank;
import cz.wikkiw.fitnessfunctions.Quadric;
import cz.wikkiw.fitnessfunctions.Rosenbrock;
import cz.wikkiw.fitnessfunctions.Salomon;
import cz.wikkiw.fitnessfunctions.Schwefel;
import cz.wikkiw.fitnessfunctions.objects.Boundary;
import cz.wikkiw.fitnessfunctions.objects.Individual;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public class ResultComputing {

    public static List<Double> compute(int runs, Individual bestIndividual, int dimension, int individualCount, double crossoverProbability, double F, int maxFFE, FitnessFunction ffunction, Boundary boundaryRange, double ffOptimum, double fixedAccuracyLevel){
        
        List<Double> resultList = new ArrayList<>();
        double srate = 0;
        double sspeed = 0;
        double srTimesSs = 0;
        DErand1bin de;
        
        for(int i=0; i<runs; i++){
            
            de = new DErand1bin(bestIndividual, dimension, individualCount, crossoverProbability, F, maxFFE, ffunction, boundaryRange, ffOptimum, fixedAccuracyLevel);
            de.run();
            
            if(de.isSuccess()){
                srate += 1;
                sspeed += (maxFFE - (de.getEvaluations()-1))/(double)maxFFE;
            }
            
            
        }
        srate = srate / runs;
        sspeed = sspeed / runs;
        srTimesSs = srate * sspeed;
        resultList.add(srate);
        resultList.add(sspeed);
        resultList.add(srTimesSs);
        resultList.add(F);
        resultList.add(crossoverProbability);
        
        return resultList;
        
    }
    
    /**
     * 
     * @param Fmin
     * @param Fmax
     * @param Fstep
     * @param runs
     * @param bestIndividual
     * @param dimension
     * @param individualCount
     * @param crossoverProbability
     * @param maxFFE
     * @param ffunction
     * @param boundaryRange
     * @param ffOptimum
     * @param fixedAccuracyLevel
     * @return 
     */
    public static List<List<Double>> computeAllFs(double Fmin, double Fmax, double Fstep,int runs, Individual bestIndividual, int dimension, int individualCount, double crossoverProbability, int maxFFE, FitnessFunction ffunction, Boundary boundaryRange, double ffOptimum, double fixedAccuracyLevel){
        
        List<List<Double>> list = new ArrayList<>();
        
        for(double f=Fmin; f<Fmax; f+=Fstep){
            
            list.add(ResultComputing.compute(runs, bestIndividual, dimension, individualCount, crossoverProbability, f, maxFFE, ffunction, boundaryRange, ffOptimum, fixedAccuracyLevel));
            
        }
        
        return list;
        
    }
    
    /**
     * 
     * @param Fmin
     * @param Fmax
     * @param Fstep
     * @param CRmin
     * @param CRmax
     * @param CRstep
     * @param runs
     * @param bestIndividual
     * @param dimension
     * @param individualCount
     * @param maxFFE
     * @param ffunction
     * @param boundaryRange
     * @param ffOptimum
     * @param fixedAccuracyLevel
     * @return 
     */
    public static List<List<Double>> computeAllFsANDCRs(double Fmin, double Fmax, double Fstep, double CRmin, double CRmax, double CRstep ,int runs, Individual bestIndividual, int dimension, int individualCount, int maxFFE, FitnessFunction ffunction, Boundary boundaryRange, double ffOptimum, double fixedAccuracyLevel){
        
        List<List<Double>> list = new ArrayList<>();
        
        for(double cr=CRmin; cr<CRmax; cr+=CRstep){
            
            System.out.println("========== CR = " + cr + " ===========");
            
            for(double f=Fmin; f<Fmax; f+=Fstep){
                
                System.out.println("========== F = " + f + " ===========");
                
                list.add(ResultComputing.compute(runs, bestIndividual, dimension, individualCount, cr, f, maxFFE, ffunction, boundaryRange, ffOptimum, fixedAccuracyLevel));
                
            }

        }
        
        return list;
        
    }
    
    /**
     * 
     * @param fileName
     * @param Fmin
     * @param Fmax
     * @param Fstep
     * @param CRmin
     * @param CRmax
     * @param CRstep
     * @param runs
     * @param bestIndividual
     * @param dimension
     * @param individualCount
     * @param maxFFE
     * @param ffunction
     * @param boundaryRange
     * @param ffOptimum
     * @param fixedAccuracyLevel 
     */
    public static void computeAndPrintOutCRFs(String fileName, double Fmin, double Fmax, double Fstep, double CRmin, double CRmax, double CRstep, int runs, Individual bestIndividual, int dimension, int individualCount, int maxFFE, FitnessFunction ffunction, Boundary boundaryRange, double ffOptimum, double fixedAccuracyLevel){
        
        List<List<Double>> resList = ResultComputing.computeAllFsANDCRs(Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, bestIndividual, dimension, individualCount, maxFFE, ffunction, boundaryRange, ffOptimum, fixedAccuracyLevel);
        
        int i=0;
        
        PrintWriter writer;
        PrintWriter writer2;
        PrintWriter writer3;
        try {
            writer = new PrintWriter("SR-"+fileName, "UTF-8");
            writer2 = new PrintWriter("SS-"+fileName, "UTF-8");
            writer3 = new PrintWriter("SRSS-"+fileName, "UTF-8");
            
            writer.print("{");
            writer2.print("{");
            writer3.print("{");
            
            for(List<Double> list: resList){
//                System.out.println("====================");
//                System.out.println("F: " + list.get(3));
//                System.out.println("CR: " + list.get(4));
//                System.out.println("SRate: " + list.get(0));
//                System.out.println("SSpeed: " + list.get(1));
//                System.out.println("SRate x SSpeed: " + list.get(2));
                
                writer.print("{" + list.get(3) + "," + list.get(4) + "," + String.format(Locale.ENGLISH,"%.10f", list.get(0)) + "}");
                writer2.print("{" + list.get(3) + "," + list.get(4) + "," + String.format(Locale.ENGLISH,"%.10f", list.get(1)) + "}");
                writer3.print("{" + list.get(3) + "," + list.get(4) + "," + String.format(Locale.ENGLISH,"%.10f", list.get(2)) + "}");
                
                i++;
                if(i != resList.size()){
                    writer.print(",");
                    writer2.print(",");
                    writer3.print(",");
                }
            }
            
            writer.print("}");
            writer2.print("}");
            writer3.print("}");
        
            writer.close();
            writer2.close();
            writer3.close();
        
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ResultComputing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /**
        * 2 DIMENSIONS
        */
       int D = 10;
       int individuals = 10*D;
       double CRmin = 0.0;
       double CRmax = 1.;
       double CRstep = 0.1;
       double Fmin = 0.1;
       double Fmax = 1.5;
       double Fstep = 0.1;
       int ffe = 10000*D - (D*1000);
       int runs = 30;
       
      /**
        * ACKLEY
        */

       FitnessFunction ff = new Ackley();
       double[] features = new double[]{-1.995771118650989, -3.085904900193296, -2.329241769711479, 6.114845753982398, 1.3650166569330393, 3.6886789227073926, -5.832575977375992, -6.483963379930145, -2.8760682335031618, -5.681376434956892};
       Individual ind = new Individual(features, ff.getValue(features));
       double ffo = ff.getOptimum(D);
       double fal = ff.getFal(ffe);
       Boundary boundary = ff.getBoundary();

       ResultComputing.computeAndPrintOutCRFs("ackley10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       /**
        * GRIEWANK
        */
       ff = new Griewank();
       features = new double[]{15.32825268971196, -27.01720172350619, 1.1744829039618025, -91.44822018606287, -44.34169934779213, -77.51242610970355, 18.110199050087928, 62.25414461655226, -43.686170685013465, -65.42944717516819};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();

       ResultComputing.computeAndPrintOutCRFs("griewank10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       /**
        * SALOMON
        */
       ff = new Salomon();
       features = new double[]{-8.567307691057529, 27.996123075295426, -10.555243181804084, -0.43974690932067517, 9.85268929326314, 7.816553256672939, -5.38907469713626, 18.915904012188342, 11.003340510701797, -11.716057960267776};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();

       ResultComputing.computeAndPrintOutCRFs("salomon10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       /**
        * SCHWEFEL
        */
       ff = new Schwefel();
       features = new double[]{-38.23982609100307, -310.056282025009, 428.11382058126054, 418.200927456769, 419.2750621587144, 209.65251760460063, 387.8670172361624, 408.7564768716544, 436.11772530380176, 416.5180368381126};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();

       ResultComputing.computeAndPrintOutCRFs("schwefel10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       /**
        * ROSENBROCK
        */
       ff = new Rosenbrock();
       features = new double[]{-0.23827222872880974, 0.2782870920299678, 0.04001227354443937, -0.21712304920251424, 0.0809410789139482, 0.17740160100027075, 0.5783807069912307, 0.46729540565382294, 0.20127795184890912, 0.6609296730961296};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();

       ResultComputing.computeAndPrintOutCRFs("rosenbrock10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       /**
        * QUADRIC
        */
       ff = new Quadric();
       features = new double[]{15.038146785964512, -3.752777484303433, 0.37419888335876905, -8.779928107791275, 16.289240207975745, 6.0866612287112485, -1.611769336500462, 4.877668797744911, -9.380491215450927, -37.521417137321144};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();

       ResultComputing.computeAndPrintOutCRFs("quadric10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);
        
    }
    
}
