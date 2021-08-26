package main.java;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;


public class FileReader {

    public static void main(String[] args) throws IOException {
        run(preProcess("شارژ بخر"));

    }

    public static ArrayList<Integer> preProcess(String input) throws IOException {
        HashMap<String, Integer> token2ID = readTokens2ID();
        //WordTokenizer tokenizer = new WordTokenizer();
        //List<String> words = tokenizer.tokenize(input);
        ArrayList<String> words = new ArrayList<>(Arrays.asList(input.split(" ")));
        ArrayList<String> stopWords = loadStopWords();
        ArrayList<String> tokens = new ArrayList<>();
        ArrayList<Integer> sentence = new ArrayList<>();
        for (String word : words) {
            if (!stopWords.contains(word)) {
                tokens.add(word);
            }
        }
        for (int i=0; i<15 ; i++){
            if (i< tokens.size() && token2ID.containsKey(tokens.get(i))){
                sentence.add(token2ID.get(tokens.get(i)));
            }
            else {
                sentence.add(0);
            }
        }
        return sentence;

    }

    public static void run(ArrayList<Integer> sentence) throws FileNotFoundException {
        ArrayList<ArrayList<Double>> vectors = new ArrayList<>();
        for (int id : sentence) {
            vectors.add(getVector(id));
        }
        int numberOfLayers = 2;
        int[] numberOfNeurons = {50, 39};
        ArrayList<Integer> numberOfLayersNeuron = new ArrayList<>();
        numberOfLayersNeuron.add(50);
        numberOfLayersNeuron.add(39);
        ArrayList<ArrayList<ArrayList<Double>>> weights = getWeights();
        ArrayList<ArrayList<Double>> biases = getBiases();
        //ArrayList<Double> features = getFeatures();
        ArrayList<Double> features = calcStatistics(vectors);

        FeedForward feedForward = new FeedForward(features, weights, biases, numberOfLayersNeuron,
                numberOfLayers, new ActivationFunction());
        double[] probabilities = feedForward.forward();
        int maxIndex = 0;
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > probabilities[maxIndex]) {
                maxIndex = i;
            }
        }
        System.out.println(probabilities[maxIndex]);
        System.out.println(maxIndex);
    }

    private static ArrayList<String> loadStopWords() throws FileNotFoundException {
        File stopWords = new File("python\\Copy of stopwords.txt");
        ArrayList<String> stopWordsArray = new ArrayList<>();
        Scanner scannerStopWords = new Scanner(stopWords);
        while (scannerStopWords.hasNextLine()) {
            stopWordsArray.add(scannerStopWords.nextLine());
        }
        return stopWordsArray;
    }

    private static ArrayList<Double> getVector(int id) {
        String line;
        try (Stream<String> lines = Files.lines(Paths.get("python\\emb.txt"))) {
            line = lines.skip(id).findFirst().get();
            return lineToArray2(line);
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    private static HashMap<String, Integer> readTokens2ID() throws FileNotFoundException {
        File token2ID = new File("python\\token2id.txt");
        HashMap<String, Integer> tokens2ID = new HashMap<>();
        Scanner scannerToken2ID = new Scanner(token2ID);
        String line = scannerToken2ID.nextLine();
        String[] items = line.split(", ");
        for (String s : items) {
            String[] item = s.split(": ");
            String key = item[0].replace("{", "").substring(1, item[0].length() - 1);
            int value = Integer.parseInt(item[1].replace("}", ""));
            tokens2ID.put(key, value);
        }
        return tokens2ID;

    }

    private static ArrayList<Double> calcStatistics(ArrayList<ArrayList<Double>> vectors) {
        ArrayList<Double> mean = getMeanVector(vectors);
        ArrayList<Double> secondMoment = getMoment(vectors, mean, 2);
        ArrayList<Double> thirdMoment = getMoment(vectors, mean, 3);
        ArrayList<Double> res = new ArrayList<>();
        res.addAll(mean);
        res.addAll(secondMoment);
        res.addAll(thirdMoment);
        return res;

    }

    private static ArrayList<Double> getMeanVector(ArrayList<ArrayList<Double>> vectors) {
        ArrayList<Double> mean = new ArrayList<>();
        int vecSize = vectors.get(0).size();
        for (int i = 0; i < vecSize; i++) {
            double d = 0;
            for (ArrayList<Double> vector : vectors) {
                d += vector.get(i);
            }
            mean.add(d / vectors.size());
        }
        return mean;
    }

    private static ArrayList<Double> getMoment(ArrayList<ArrayList<Double>> vectors, ArrayList<Double> mean, int k) {
        ArrayList<Double> moment = new ArrayList<>();
        int vecSize = vectors.get(0).size();
        for (int i = 0; i < vecSize; i++) {
            double d = 0;
            for (ArrayList<Double> vector : vectors) {
                d += Math.pow(vector.get(i) - mean.get(i), k);
            }
            moment.add(d / vectors.size());
        }
        return moment;
    }


    private static ArrayList<Double> getFeatures() throws FileNotFoundException {
        File featuresFile = new File("python\\features.txt");
        Scanner scannerFeatures = new Scanner(featuresFile);
        return lineToArray(scannerFeatures.nextLine());
    }

    private static ArrayList<ArrayList<Double>> getBiases() throws FileNotFoundException {
        File biasesFile = new File("python\\biases.txt");
        Scanner scannerBiases = new Scanner(biasesFile);

        ArrayList<Double> b1 = lineToArray(scannerBiases.nextLine());
        ArrayList<Double> b2 = lineToArray(scannerBiases.nextLine());

        ArrayList<ArrayList<Double>> res = new ArrayList<>();
        res.add(b1);
        res.add(b2);
        return res;
    }

    private static ArrayList<ArrayList<ArrayList<Double>>> getWeights() throws FileNotFoundException {
        File weightsFile = new File("python\\weights.txt");
        Scanner scannerWeights = new Scanner(weightsFile);
        ArrayList<ArrayList<ArrayList<Double>>> res = new ArrayList<>();
        String line;
        ArrayList<ArrayList<Double>> weightsOfLayer = new ArrayList<>();
        while (scannerWeights.hasNextLine()) {
            line = scannerWeights.nextLine();
            if (line.equals("end")) {
                res.add(weightsOfLayer);
                weightsOfLayer = new ArrayList<>();
            } else {
                weightsOfLayer.add(lineToArray(line));
            }
        }
        return res;
    }

    private static ArrayList<Double> lineToArray(String line) {
        String[] s = line.split(", ");
        ArrayList<Double> array = new ArrayList<>();
        for (String s1 : s) {
            array.add(Double.parseDouble(s1));
        }
        return array;
    }

    private static ArrayList<Double> lineToArray2(String line) {
        String[] s = line.split(",");
        ArrayList<Double> array = new ArrayList<>();
        for (String s1 : s) {
            array.add(Double.parseDouble(s1));
        }
        return array;
    }

}
