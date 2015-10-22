package cz.wikkiw.de;

import cz.wikkiw.fitnessfunctions.Ackley;
import cz.wikkiw.fitnessfunctions.FitnessFunction;
import cz.wikkiw.fitnessfunctions.objects.Boundary;
import cz.wikkiw.fitnessfunctions.objects.Individual;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
        
        return resultList;
        
    }
    
    public static List<List<Double>> computeAllFs(double Fmin, double Fmax, double Fstep,int runs, Individual bestIndividual, int dimension, int individualCount, double crossoverProbability, int maxFFE, FitnessFunction ffunction, Boundary boundaryRange, double ffOptimum, double fixedAccuracyLevel){
        
        List<List<Double>> list = new ArrayList<>();
        
        for(double f=Fmin; f<Fmax; f+=Fstep){
            
            list.add(ResultComputing.compute(runs, bestIndividual, dimension, individualCount, crossoverProbability, f, maxFFE, ffunction, boundaryRange, ffOptimum, fixedAccuracyLevel));
            
        }
        
        return list;
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        FitnessFunction ff = new Ackley();
        double[] features = {1,1};
        
//        System.out.println(ff.getValue(features));
        
        Individual ind = new Individual(features, ff.getValue(features));
        int D = 2;
        int individuals = 10*D;
        double crossover = 0.1;
        double Fmin = 0.1;
        double Fmax = 1.5;
        double Fstep = 0.1;
        int ffe = 10000*D;
        double ffo = ff.getOptimum(D);
        double fal = ff.getFal(ffe);
        int runs = 30;
        
        List<List<Double>> ackley2 = ResultComputing.computeAllFs(Fmin, Fmax, Fstep, runs, ind, D, individuals, crossover, ffe, ff, ff.getBoundary(), ffo, fal);
        
        int i=0;
        
        PrintWriter writer;
        try {
            writer = new PrintWriter("ackley2.txt", "UTF-8");
            
            for(List<Double> list: ackley2){
                System.out.println("F: " + (Fmin+(i*Fstep)));
                System.out.println("SRate: " + list.get(0));
                System.out.println("SSpeed: " + list.get(1));
                System.out.println("SRate x SSpeed: " + list.get(2));
                writer.println("F: " + (Fmin+(i*Fstep)));
                writer.println("SRate: " + list.get(0));
                writer.println("SSpeed: " + list.get(1));
                writer.println("SRate x SSpeed: " + list.get(2));
                i++;
            }
        
            writer.close();
        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResultComputing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ResultComputing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
    }
    
}
