package main.java;
public class ActivationFunction {
    public double activation(double input){
        if (input > 0){
            return input;
        }
        else
            return 0;
    }
}
