package cz.wikkiw.de;

import cz.wikkiw.fitnessfunctions.FitnessFunction;
import cz.wikkiw.fitnessfunctions.f1;
import cz.wikkiw.fitnessfunctions.f10;
import cz.wikkiw.fitnessfunctions.f11;
import cz.wikkiw.fitnessfunctions.f12;
import cz.wikkiw.fitnessfunctions.f13;
import cz.wikkiw.fitnessfunctions.f14;
import cz.wikkiw.fitnessfunctions.f15;
import cz.wikkiw.fitnessfunctions.f2;
import cz.wikkiw.fitnessfunctions.f3;
import cz.wikkiw.fitnessfunctions.f4;
import cz.wikkiw.fitnessfunctions.f5;
import cz.wikkiw.fitnessfunctions.f6;
import cz.wikkiw.fitnessfunctions.f7;
import cz.wikkiw.fitnessfunctions.f8;
import cz.wikkiw.fitnessfunctions.f9;
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
                if(i != resList.size()){                    writer3.print(",");

                    writer.print(",");
                    writer2.print(",");
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
       
//      /**
//        * ACKLEY
//        */
//
//       FitnessFunction ff = new Ackley();
//       double[] features = new double[]{-1.995771118650989, -3.085904900193296, -2.329241769711479, 6.114845753982398, 1.3650166569330393, 3.6886789227073926, -5.832575977375992, -6.483963379930145, -2.8760682335031618, -5.681376434956892};
//       Individual ind = new Individual(features, ff.getValue(features));
//       double ffo = ff.getOptimum(D);
//       double fal = ff.getFal(ffe);
//       Boundary boundary = ff.getBoundary();
//
//       ResultComputing.computeAndPrintOutCRFs("ackley10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);
//
//       /**
//        * GRIEWANK
//        */
//       ff = new Griewank();
//       features = new double[]{15.32825268971196, -27.01720172350619, 1.1744829039618025, -91.44822018606287, -44.34169934779213, -77.51242610970355, 18.110199050087928, 62.25414461655226, -43.686170685013465, -65.42944717516819};
//       ind = new Individual(features, ff.getValue(features));
//       ffo = ff.getOptimum(D);
//       fal = ff.getFal(ffe);
//       boundary = ff.getBoundary();
//
//       ResultComputing.computeAndPrintOutCRFs("griewank10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);
//
//       /**
//        * SALOMON
//        */
//       ff = new Salomon();
//       features = new double[]{-8.567307691057529, 27.996123075295426, -10.555243181804084, -0.43974690932067517, 9.85268929326314, 7.816553256672939, -5.38907469713626, 18.915904012188342, 11.003340510701797, -11.716057960267776};
//       ind = new Individual(features, ff.getValue(features));
//       ffo = ff.getOptimum(D);
//       fal = ff.getFal(ffe);
//       boundary = ff.getBoundary();
//
//       ResultComputing.computeAndPrintOutCRFs("salomon10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);
//
//       /**
//        * SCHWEFEL
//        */
//       ff = new Schwefel();
//       features = new double[]{-38.23982609100307, -310.056282025009, 428.11382058126054, 418.200927456769, 419.2750621587144, 209.65251760460063, 387.8670172361624, 408.7564768716544, 436.11772530380176, 416.5180368381126};
//       ind = new Individual(features, ff.getValue(features));
//       ffo = ff.getOptimum(D);
//       fal = ff.getFal(ffe);
//       boundary = ff.getBoundary();
//
//       ResultComputing.computeAndPrintOutCRFs("schwefel10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);
//
//       /**
//        * ROSENBROCK
//        */
//       ff = new Rosenbrock();
//       features = new double[]{-0.23827222872880974, 0.2782870920299678, 0.04001227354443937, -0.21712304920251424, 0.0809410789139482, 0.17740160100027075, 0.5783807069912307, 0.46729540565382294, 0.20127795184890912, 0.6609296730961296};
//       ind = new Individual(features, ff.getValue(features));
//       ffo = ff.getOptimum(D);
//       fal = ff.getFal(ffe);
//       boundary = ff.getBoundary();
//
//       ResultComputing.computeAndPrintOutCRFs("rosenbrock10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);
//
//       /**
//        * QUADRIC
//        */
//       ff = new Quadric();
//       features = new double[]{15.038146785964512, -3.752777484303433, 0.37419888335876905, -8.779928107791275, 16.289240207975745, 6.0866612287112485, -1.611769336500462, 4.877668797744911, -9.380491215450927, -37.521417137321144};
//       ind = new Individual(features, ff.getValue(features));
//       ffo = ff.getOptimum(D);
//       fal = ff.getFal(ffe);
//       boundary = ff.getBoundary();
//
//       ResultComputing.computeAndPrintOutCRFs("quadric10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);
//       
       
       
       
       double[] features = new double[]{-61.67713722207517, -7.684525402507525, 87.94238891851492, -43.54210399352583, -62.59488750718182, 97.35187950444471, 68.33052512544774, 10.072597485805419, 56.10503687634504, -69.18168336936716};
       FitnessFunction ff = new f1();
       
       Individual ind = new Individual(features, ff.getValue(features));
       double ffo = ff.getOptimum(D);
       double fal = ff.getFal(ffe);
       Boundary boundary = ff.getBoundary();
       ff.init(10);

       ResultComputing.computeAndPrintOutCRFs("f1-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f2();
       features = new double[]{30.242517057909044, -1.3389157573791164, -56.56894237690362, -51.612955606876525, -65.49795547197891, 32.099286316992206, -78.97687612853157, -24.01676701354655, 31.99925853756012, -66.39076658797497};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
       ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f2-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f3();
       features = new double[]{16.97634799777022, -79.91198508875064, 77.46448004829409, -69.98104482717201, 38.832241590461976, 49.33910328927058, -56.1955407341447, 82.28484029053955, 28.972014372637858, -71.39437658402943};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
       ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f3-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f4();
       features = new double[]{-67.17314124444904, -74.46129984420489, 64.34085808167757, 19.101518666679958, -71.4711527299858, -27.872722203657002, -19.918644580712787, -93.15560189302548, 57.37753464939102, -17.390231995809927};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
       ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f4-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f5();
       features = new double[]{84.67672990931405, 99.70515918353644, 87.72518425320985, 50.834271573691, -78.97844159153891, -76.10989677290179, -74.8369493743183, -87.85813929850048, -19.49224669123444, -40.05280703218027};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
       ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f5-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f6();
       features = new double[]{58.282302024522366, -78.18623429848688, 50.62344648032797, -31.963671007618263, -19.965620163406683, 90.51439966215287, -84.1242843595067, -14.536617463729158, -34.44726047193677, 43.58958653028904};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
              ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f6-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f7();
       features = new double[]{84.42067428578207, 6.722682283600291, -55.7554231013217, -88.86633872895888, -48.90856352414863, 26.493222385702303, 75.37977814591991, -8.639679245359863, 42.46158305144891, 24.041721017120164};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
              ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f7-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f8();
       features = new double[]{-2.7338691359577165, 41.57948316148812, -58.28051737345582, 20.987861243193088, -27.222054581055744, 21.194215955134126, 80.71078317453772, 84.45418647945546, -67.4842976858072, 98.21715614406611};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
              ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f8-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f9();
       features = new double[]{0.4782650442298966, 12.975979346748927, 58.82646867563104, 78.5391436227055, -52.96940742517774, -16.760032807370834, -76.77139193164639, 82.24127616120154, 27.121561478262514, 57.99189301467895};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
              ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f9-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f10();
       features = new double[]{-71.84121241961168, -57.6469144562266, -81.46085802845681, 14.495224335422304, 47.618988378206225, -50.49265816798562, -42.6804831111432, 17.694462194954937, -12.86553325117602, 38.15723187171582};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
              ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f10-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f11();
       features = new double[]{-40.87207736674745, -40.599215350188544, 21.823966855976884, 43.744092263714556, -55.63619526290841, 39.82879783957419, 58.31645314557919, 55.95886473070661, 41.25751810125345, 89.91656250744752};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
              ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f11-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f12();
       features = new double[]{28.165579395007562, -36.54945742929885, -71.87926409235058, -91.82252063014187, -48.987728633397694, 45.76207695815014, 84.34358466237327, 71.7686901301632, 63.53689402319647, -63.53200937483225};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
              ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f12-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f13();
       features = new double[]{-68.30284550568715, 53.01503195890277, -91.99282171378789, 22.181734974259275, -5.216706296044816, -85.27362718413828, -27.013312366849462, 86.11362910377457, -15.815110624444452, -89.73464089498802};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
              ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f13-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f14();
       features = new double[]{89.66417495880359, 63.93896320872217, -55.270087173605724, -11.054071512059764, 30.796973951805803, 57.68082654920511, -10.132342182297137, 27.64791928408402, 60.212967037720354, 73.74626604394287};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
              ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f14-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       ff = new f15();
       features = new double[]{-60.15326099231142, 85.34846513468047, 37.934199578935036, 34.46935852302357, 1.49241393665908, 88.99525995083044, -66.99196837365398, -6.1609573822510155, 67.1540132271268, 6.633069290728301};
       ind = new Individual(features, ff.getValue(features));
       ffo = ff.getOptimum(D);
       fal = ff.getFal(ffe);
       boundary = ff.getBoundary();
              ff.init(10);


       ResultComputing.computeAndPrintOutCRFs("f15-10.txt", Fmin, Fmax, Fstep, CRmin, CRmax, CRstep, runs, ind, D, individuals, ffe, ff, boundary, ffo, fal);

       
       
    }
    
}
