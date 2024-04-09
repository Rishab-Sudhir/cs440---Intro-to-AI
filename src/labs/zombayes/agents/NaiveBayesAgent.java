package src.labs.zombayes.agents;


// SYSTEM IMPORTS
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


// JAVA PROJECT IMPORTS
import edu.bu.labs.zombayes.linalg.Shape;
import edu.bu.labs.zombayes.agents.SurvivalAgent;
import edu.bu.labs.zombayes.features.Features.FeatureType;
import edu.bu.labs.zombayes.linalg.Matrix;
import edu.bu.labs.zombayes.utils.Pair;



public class NaiveBayesAgent
    extends SurvivalAgent
{

    public static class NaiveBayes
        extends Object
    {

        public static final FeatureType[] FEATURE_HEADER = {FeatureType.CONTINUOUS,
                                                            FeatureType.CONTINUOUS,
                                                            FeatureType.DISCRETE,
                                                            FeatureType.DISCRETE};
        
        public Map<Integer, Double> classPriors = new HashMap<>();
        public Map<Integer, Map<Integer, Double>> means = new HashMap<>();
        public Map<Integer, Map<Integer, Double>> variances = new HashMap<>();
        public Map<Integer, Map<Integer, Map<Integer, Double>>> frequencies = new HashMap<>();

                      

        // TODO: complete me!
        public NaiveBayes()
        {

        }

        public void fit(Matrix X, Matrix y_gt)
        {
            // Initialize matrices for the two classes
            Matrix X_class0 = null;
            Matrix X_class1 = null;

            try {
                // Create masks for each class
                Matrix maskClass0 = y_gt.getRowMaskEq(0.0, 0); // For class label 0
                Matrix maskClass1 = y_gt.getRowMaskEq(1.0, 0); // For class label 1

                // Filter X based on the masks to separate the data by class
                X_class0 = X.filterRows(maskClass0);
                X_class1 = X.filterRows(maskClass1);

                // Step 2 & 3: Separate data by class and Calculate priors
                Shape shapeObject = y_gt.getShape();
                int totalInstances = shapeObject.getNumRows();
                Matrix countClass1Matrix = y_gt.sum();

                double countClass1 = countClass1Matrix.item();
                double countClass0 = totalInstances - countClass1;
                classPriors.put(0, (double) countClass0 / totalInstances);
                classPriors.put(1, (double) countClass1 / totalInstances);
                
                // Calculating the Mean and Varience
                // For the continous features

                for (int i = 0; i < 2; i++) { 
                    // Only for the first two features
                    // Extract the column for the feature
                    Matrix featureClass0 = X_class0.getCol(i);
                    Matrix featureClass1 = X_class1.getCol(i);

                    // Calculate the mean for class 0
                    Matrix sumClass0 = featureClass0.sum();
                    double meanClass0 = sumClass0.item() / featureClass0.getShape().getNumRows();

                    // Calculate the variance for class 0
                    // Assuming featureClass0 is a column vector and the class provides a method full_like or similar
                    Matrix meanVectorClass0 = Matrix.full_like(featureClass0, meanClass0);
                    Matrix diffFromMeanClass0 = featureClass0.subtract(meanVectorClass0);
                    Matrix sqDiffClass0 = diffFromMeanClass0.pow(2);
                    double varianceClass0 = sqDiffClass0.sum().item() / (featureClass0.getShape().getNumRows() - 1);

                    // Calculate the mean for class 1
                    Matrix sumClass1 = featureClass1.sum();
                    double meanClass1 = sumClass1.item() / featureClass1.getShape().getNumRows();

                    // Calculate the variance for class 1
                    // Assuming featureClass0 is a column vector and the class provides a method full_like or similar
                    Matrix meanVectorClass1 = Matrix.full_like(featureClass1, meanClass1);
                    Matrix diffFromMeanClass1 = featureClass1.subtract(meanVectorClass1);
                    Matrix sqDiffClass1 = diffFromMeanClass1.pow(2);
                    double varianceClass1 = sqDiffClass1.sum().item() / (featureClass1.getShape().getNumRows() - 1);
                    
                    // Initialization for means and variances
                    means.putIfAbsent(i, new HashMap<>());
                    variances.putIfAbsent(i, new HashMap<>());
                    
                    //  you just put the values without re-initializing the HashMap
                    means.get(i).put(0, meanClass0);
                    variances.get(i).put(0, varianceClass0);
                    means.get(i).put(1, meanClass1);
                    variances.get(i).put(1, varianceClass1);
                }

                // Calculating the Mean and Varience
                // For the Discrete features

                for (int i = 2; i < 4; i++) { 
                    // Only for the last two features
                    // Extract the column for the feature
                    Matrix featureClass0 = X_class0.getCol(i);
                    Matrix featureClass1 = X_class1.getCol(i);
    
                    // Calculate frequencies for class 0
                    Pair<Matrix, Matrix> uniqueAndCountsClass0 = featureClass0.unique();
                    Matrix uniqueValuesClass0 = uniqueAndCountsClass0.getFirst();
                    Matrix countsClass0 = uniqueAndCountsClass0.getSecond();
    
                    // Calculate frequencies for class 1
                    Pair<Matrix, Matrix> uniqueAndCountsClass1 = featureClass1.unique();
                    Matrix uniqueValuesClass1 = uniqueAndCountsClass1.getFirst();
                    Matrix countsClass1 = uniqueAndCountsClass1.getSecond();
    
                    // Class 0
                    frequencies.putIfAbsent(i, new HashMap<>());
                    for (int valueIndex = 0; valueIndex < uniqueValuesClass0.numel(); valueIndex++) {
                        // uniqueValuesClass0 is a vector with the unique feature values and countsClass0 contains the corresponding counts
                        int featureValue = (int) uniqueValuesClass0.get(valueIndex, 0);
                        double count = countsClass0.get(valueIndex, 0);
                        
                        frequencies.get(i).putIfAbsent(0, new HashMap<>());
                        frequencies.get(i).get(0).put(featureValue, count / countClass0);
                    }
    
                    // Class 1
                    frequencies.putIfAbsent(i, new HashMap<>());
                    for (int valueIndex = 0; valueIndex < uniqueValuesClass1.numel(); valueIndex++) {
    
                        int featureValue = (int) uniqueValuesClass1.get(valueIndex, 0);
                        double count = countsClass1.get(valueIndex, 0);
                    
                        frequencies.get(i).putIfAbsent(1, new HashMap<>());
                        frequencies.get(i).get(1).put(featureValue, count / countClass1);
                    }
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }

        public int predict(Matrix x) {
            double maxPosterior = Double.NEGATIVE_INFINITY;
            int predictedClass = -1;
            
            // Iterate over each class
            for (Integer classVal : classPriors.keySet()) {
                double classPrior = classPriors.get(classVal);
                double posteriorProbability = Math.log(classPrior); // Use log to prevent underflow
        
                // Iterate over each feature
                for (int i = 0; i < x.getShape().getNumCols(); i++) {
                    double featureValue = x.get(0, i); // Get the feature value from instance 'x'
        
                    if (i < 2) { // For the first two continuous features 
                        // Retrieve the mean and variance for the class and feature
                        double mean = means.get(i).get(classVal);
                        double variance = variances.get(i).get(classVal);
                        
                        // Calculate the probability density of the Gaussian distribution
                        double likelihood = Math.log(1 / Math.sqrt(2 * Math.PI * variance)) - 
                                            Math.pow(featureValue - mean, 2) / (2 * variance);
                        posteriorProbability += likelihood;
                    } else { // For the 2 discrete features
                        // Retrieve the frequency map for the class and feature
                        Map<Integer, Double> freqMap = frequencies.get(i).get(classVal);
                        double frequency = freqMap.containsKey((int)featureValue) ? freqMap.get((int)featureValue) : 1.0 / (freqMap.size() + 1);
                        posteriorProbability += Math.log(frequency);
                    }
                }
        
                // Check if the current class has the highest posterior probability
                if (posteriorProbability > maxPosterior) {
                    maxPosterior = posteriorProbability;
                    predictedClass = classVal;
                }
            }
        
            return predictedClass;
        }
        

    }
    
    private NaiveBayes model;

    public NaiveBayesAgent(int playerNum, String[] args)
    {
        super(playerNum, args);
        this.model = new NaiveBayes();
    }

    public NaiveBayes getModel() { return this.model; }

    @Override
    public void train(Matrix X, Matrix y_gt)
    {
        System.out.println(X.getShape() + " " + y_gt.getShape());
        this.getModel().fit(X, y_gt);
    }

    @Override
    public int predict(Matrix featureRowVector)
    {
        return this.getModel().predict(featureRowVector);
    }

}
