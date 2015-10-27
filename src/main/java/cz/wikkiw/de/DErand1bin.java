package cz.wikkiw.de;

import cz.wikkiw.fitnessfunctions.Ackley;
import cz.wikkiw.fitnessfunctions.FitnessFunction;
import cz.wikkiw.fitnessfunctions.objects.Boundary;
import cz.wikkiw.fitnessfunctions.objects.Individual;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author adam
 */
public class DErand1bin {

    private Individual bestIndividual;
    final private int dimension;
    final private int individualCount;
    final private double crossoverProbability;
    final private double F;
    final private int maxFFE;
    final private FitnessFunction ffunction;
    final private Boundary boundaryRange;
    final private double ffOptimum;
    final private double fixedAccuracyLevel;

    private int evaluations;
    private boolean success;

    /**
     *
     * @param bestIndividual
     * @param dimension
     * @param individualCount
     * @param crossoverProbability
     * @param F
     * @param maxFFE
     * @param ffunction
     * @param boundaryRange
     * @param ffOptimum
     * @param fixedAccuracyLevel
     */
    public DErand1bin(Individual bestIndividual, int dimension, int individualCount, double crossoverProbability, double F, int maxFFE, FitnessFunction ffunction, Boundary boundaryRange, double ffOptimum, double fixedAccuracyLevel) {
        this.bestIndividual = bestIndividual;
        this.dimension = dimension;
        this.individualCount = individualCount;
        this.crossoverProbability = crossoverProbability;
        this.F = F;
        this.maxFFE = maxFFE;
        this.ffunction = ffunction;
        this.boundaryRange = boundaryRange;
        this.ffOptimum = ffOptimum;
        this.fixedAccuracyLevel = fixedAccuracyLevel;

        this.evaluations = 0;
        this.success = false;
    }

    public boolean run() {
        
        if (this.successTask()) {
//            System.out.println("Best individual from PRWm is sufficient");
            return true;
        }

        List<Individual> curGeneration = new ArrayList<>();
        curGeneration.add(this.bestIndividual);

        /**
         * First generation
         */
        double[] features;
        Individual ind;

        while (curGeneration.size() < this.individualCount) {

            features = this.generateFeatures();
            ind = new Individual(features, this.ffunction.getValue(features));
            curGeneration.add(ind);
            this.evaluations++;
            if (this.isBestIndividual(ind)) {

                if (this.successTask()) {
                    System.out.println("Successful run.");
                    return true;
                }

            }

            if (this.evaluations == this.maxFFE) {
                System.out.println("Unsuccessful run.");
                return false;
            }

        }
        
        /**
         * Generation iteration
         */
        List<Individual> newGeneration;
        List<Individual> parents;
        double[] tmpFeatures;
        double[] trialFeatures;
        Individual trial;
        int R;
        
        
        while(true){
            
            newGeneration = new ArrayList<>();
            
            for(Individual cur : curGeneration){
                
                parents = this.getParents(curGeneration, cur);

                tmpFeatures = new double[this.dimension];
                trialFeatures = new double[this.dimension];
                R = this.generateRndInt(0, this.dimension - 1);
                Random rnd = new Random();
                
                for(int i = 0; i < this.dimension; i++){
                    tmpFeatures[i] = this.checkBoundaries(parents.get(1).getFeatures()[i] + this.F*(parents.get(2).getFeatures()[i] - parents.get(3).getFeatures()[i]));
                    
                    if(rnd.nextDouble() <= this.crossoverProbability || i == R){
                        trialFeatures[i] = tmpFeatures[i];
                    } else {
                        trialFeatures[i] = cur.getFeatures()[i];
                    }
                    
                }

                trial = new Individual(trialFeatures, this.ffunction.getValue(trialFeatures));
                
                this.evaluations++;
                if(trial.getFitness() <= cur.getFitness()){
                    newGeneration.add(trial);
                    
                    if (this.isBestIndividual(trial)) {

                        if (this.successTask()) {
                            System.out.println("Successful run.");
                            return true;
                        }

                    }
                    
                } else {
                    newGeneration.add(cur);
                }
                

                if (this.evaluations == this.maxFFE) {
                    System.out.println("Unsuccessful run.");
                    return false;
                }
                
            }

            curGeneration = new ArrayList<>();
            curGeneration.addAll(newGeneration);
            
        }

    }

    /**
     * 
     * @param min
     * @param max
     * @return 
     */
    private int generateRndInt(int min, int max){
        
        Random rnd = new Random();
        
        int toRet = (int) Math.round((rnd.nextDouble() * (max - min)) + min);
        
        return toRet;
        
    }
    
    /**
     * 
     * @param feature
     * @return 
     */
    private double checkBoundaries(double feature){
        
//        if(feature >= this.boundaryRange.getMin() && feature <= this.boundaryRange.getMax()){
//            return feature;
//        } else {
//            return ((this.boundaryRange.getRange() * Math.random()) + this.boundaryRange.getMin());
//        }
        
        if(feature < this.boundaryRange.getMin()){
            return this.boundaryRange.getMin();
        }
        else if(feature > this.boundaryRange.getMax()){
            return this.boundaryRange.getMax();
        } else {
            return feature;
        }
        
    }
    
    /**
     * 
     * @param generation
     * @param current
     * @return 
     */
    private List<Individual> getParents(List<Individual> generation, Individual current){
        
        List<Individual> listToRet = new ArrayList<>();
        listToRet.add(current);
        
        Random rnd = new Random();
        int index;
        
        while(listToRet.size() < 4){
            
            index = (int) Math.round(rnd.nextDouble() * (generation.size()-1));
            if(!listToRet.contains(generation.get(index))){
                listToRet.add(generation.get(index));
            }
            
        }
        
        return listToRet;
    }
    
    /**
     *
     * @return
     */
    private boolean successTask() {
        if (Math.abs(this.ffOptimum - this.bestIndividual.getFitness()) <= this.fixedAccuracyLevel) {
            this.success = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param ind
     * @return
     */
    private boolean isBestIndividual(Individual ind) {
        if (ind.getFitness() <= this.bestIndividual.getFitness()) {
            this.bestIndividual = ind;
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param dimension
     * @param boundary
     * @return
     */
    private double[] generateFeatures() {

        double[] features = new double[this.dimension];
        Random rnd = new Random();

        for (int i = 0; i < this.dimension; i++) {

            features[i] = (this.boundaryRange.getRange() * rnd.nextDouble()) + this.boundaryRange.getMin();

        }

        return features;
    }

    public Individual getBestIndividual() {
        return bestIndividual;
    }

    public void setBestIndividual(Individual bestIndividual) {
        this.bestIndividual = bestIndividual;
    }

    public int getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(int evaluations) {
        this.evaluations = evaluations;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        
        FitnessFunction ff = new Ackley();
        double[] features = {1,1,1,1,1,1,1,1,1,1};
        
        System.out.println(ff.getValue(features));
        
        Individual ind = new Individual(features, ff.getValue(features));
        int D = 10;
        int individuals = 10*D;
        double crossover = 0.3;
        double F = 0.1;
        int ffe = 10000*D;
        double ffo = ff.getOptimum(D);
        double fal = ff.getFal(ffe);
        
        DErand1bin de;

        int sum = 0;
        
        for(int i=0; i<30; i++){
            de = new DErand1bin(ind, D, individuals, crossover, F, ffe, ff, ff.getBoundary(), ffo, fal);
            de.run();
            sum += de.getEvaluations();
            System.out.println(de.getEvaluations());
        }
        
        System.out.println(sum/30.0);
        
//        System.out.println("Success: " + de.run());
//        System.out.println("Best: " + de.getBestIndividual());
//        System.out.println("Evaluations: " + de.getEvaluations());

    }

}
