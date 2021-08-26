package main.java;
import java.util.ArrayList;
import java.util.Collections;

public class FeedForward {
    private ArrayList<Double> features;
    private final ArrayList<ArrayList<ArrayList<Double>>> weights;
    private final ArrayList<Integer> numberOfLayersNeuron;
    private final int numberOfLayers;
    private final ActivationFunction af;
    private final ArrayList<ArrayList<Double>> biases;

    FeedForward(ArrayList<Double> features, ArrayList<ArrayList<ArrayList<Double>>> weights, ArrayList<ArrayList<Double>> biases,
                ArrayList<Integer> numberOfLayersNeuron, int numberOfLayers, ActivationFunction af) {
        this.features = features;
        this.weights = weights;
        this.numberOfLayers = numberOfLayers;
        this.numberOfLayersNeuron = numberOfLayersNeuron;
        this.af = af;
        this.biases = biases;

    }

    public double[] forward() {
        for (int i = 0; i < numberOfLayers; i++) {
            ArrayList<Double> nextFeature = new ArrayList<>();
            for (int j = 0; j < numberOfLayersNeuron.get(i); j++) {
                double neuronValue = 0;
                int k = 0;
                for (Double feature : features) {
                    neuronValue = neuronValue + (feature * weights.get(i).get(k).get(j));
                    k++;
                }
                neuronValue += biases.get(i).get(j);
                if (i != numberOfLayers - 1) {
                    neuronValue = af.activation(neuronValue);
                }
                nextFeature.add(neuronValue);
            }
            features.clear();
            features = nextFeature;
        }
        return softMax(features);
    }

    public static double[] softMax(ArrayList<Double> arr) {
        int length = arr.size();
        double max = Collections.max(arr);
        double[] exp_a = new double[arr.size()];
        for (int i = 0; i < length; i++) {
            exp_a[i] = Math.pow(Math.E, arr.get(i) - max);
        }
        double sum = 0;
        for (int i = 0; i < length; i++) {
            sum += exp_a[i];
        }
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = exp_a[i] / sum;
        }
        return result;
    }

}
