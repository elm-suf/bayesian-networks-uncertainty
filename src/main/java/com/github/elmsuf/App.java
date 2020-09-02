package com.github.elmsuf;

import aima.core.probability.bayes.BayesianNetwork;
import bnparser.BifReader;

public class App {
    public static void main(String... args) {
        BayesianNetwork bn = BifReader.readBIF("src/main/resources/networks/alarm.xml");
        System.out.println(bn);
    }
}
