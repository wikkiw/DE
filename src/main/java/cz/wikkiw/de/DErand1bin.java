package cz.wikkiw.de;

import cz.wikkiw.fitnessfunctions.Ackley;
import cz.wikkiw.fitnessfunctions.FitnessFunction;
import cz.wikkiw.fitnessfunctions.objects.Boundary;
import cz.wikkiw.fitnessfunctions.objects.Individual;
import cz.wikkiw.sink.Sink;
import cz.wikkiw.sink.SinkKeysEnum;
import cz.wikkiw.sink.WolframSink;
import cz.wikkiw.sinkworker.Worker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

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
    private Sink sink;
    
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
     * @param sink
     */
    public DErand1bin(Individual bestIndividual, int dimension, int individualCount, double crossoverProbability, double F, int maxFFE, FitnessFunction ffunction, Boundary boundaryRange, double ffOptimum, double fixedAccuracyLevel, Sink sink) {
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
        this.sink = sink;

        this.evaluations = 0;
        this.success = false;
    }

    public boolean run() {
        
        this.successTask();

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
            
            this.sendToSink(0, this.evaluations, ind);
            
            if (this.isBestIndividual(ind)) {
                this.successTask();
            }

            if (this.evaluations == this.maxFFE) {
//                System.out.println("Unsuccessful run.");
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
        int gen = 0;
        
        
        while(true){
            
            gen++;
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
                    this.sendToSink(gen, this.evaluations, trial);
                    
                    if (this.isBestIndividual(trial)) {

                        this.successTask();

                    }
                    
                } else {
                    newGeneration.add(cur);
                    this.sendToSink(gen, this.evaluations, cur);
                }
                

                if (this.evaluations == this.maxFFE) {
//                    System.out.println("Unsuccessful run.");
                    return false;
                }
                
            }

            curGeneration = new ArrayList<>();
            curGeneration.addAll(newGeneration);
            
        }

    }

    /**
     * 
     * @param generation
     * @param cfe
     * @param ind 
     */
    private void sendToSink(int gen, int cfe, Individual ind){
        
        TreeMap<SinkKeysEnum, Object> record = new TreeMap<>();
        record.put(SinkKeysEnum.GENERATION, gen);
        record.put(SinkKeysEnum.CFE, cfe);
        record.put(SinkKeysEnum.INDIVIDUAL, ind);
        
        this.sink.addRecord(record);
        
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

    public Sink getSink() {
        return sink;
    }

    public void setSink(Sink sink) {
        this.sink = sink;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        
        FitnessFunction ff = new Ackley();
        double[] features = {1,1,1,1,1,1,1,1,1,1};
        
//        System.out.println(ff.getValue(features));
        
        Individual ind = new Individual(features, ff.getValue(features));
        int D = 10;
        int individuals = 10*D;
        double crossover = 0.5;
        double F = 0.2;
//        int ffe = 10000*D - (D*1000);
        int ffe = 5000;
        double ffo = ff.getOptimum(D);
        double fal = ff.getFal(ffe);
        
        TreeMap<SinkKeysEnum, Object> desc = new TreeMap<>();
        desc.put(SinkKeysEnum.ALGORITHM,"DErand1bin");
        desc.put(SinkKeysEnum.DIMENSION,D);
        desc.put(SinkKeysEnum.POPSIZE,individuals);
        desc.put(SinkKeysEnum.CR,crossover);
        desc.put(SinkKeysEnum.F,F);
        desc.put(SinkKeysEnum.CFE_MAX,ffe);
        desc.put(SinkKeysEnum.FAL,fal);
        desc.put(SinkKeysEnum.FITNESS_FUNCTION, ff.getName());
        
        Sink sink;
        Sink[] sinks = new Sink[10];
        
        DErand1bin de;

        int sum = 0;
        
        for(int i=0; i<10; i++){
            sink = new WolframSink(desc);
            de = new DErand1bin(ind, D, individuals, crossover, F, ffe, ff, ff.getBoundary(), ffo, fal, sink);
            de.run();
            sinks[i] = de.getSink();
        }
        
        System.out.println(Arrays.toString(Worker.getBestValues(sinks)));
        
//        System.out.println(sum/20.0);
        
//        System.out.println("Success: " + de.run());
//        System.out.println("Best: " + de.getBestIndividual());
//        System.out.println("Evaluations: " + de.getEvaluations());

    }

}
