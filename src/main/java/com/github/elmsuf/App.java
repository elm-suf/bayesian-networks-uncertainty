package com.github.elmsuf;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.exact.EliminationAsk;
import aima.core.probability.bayes.exact.EnumerationAsk;
import aima.core.probability.example.BayesNetExampleFactory;
import aima.core.probability.proposition.AssignmentProposition;
import bnparser.BifReader;

import java.util.Arrays;

public class App {
    public static void main(String... args) {
//        BayesianNetwork bn = BifReader.readBIF("src/main/resources/networks/alarm.xml");
        BayesianNetwork bn = BayesNetExampleFactory.constructBurglaryAlarmNetwork();
        QueryOptimizer q = new QueryOptimizer(bn);

        /* Query 1:
         * P(JohnCalls | Alarm=true)
         */
        RandomVariable[] vars = {q.getNode("JohnCalls").getRandomVariable()};
        var Alarm = q.getNode("Alarm").getRandomVariable();
        AssignmentProposition[] ass = {new AssignmentProposition(Alarm, Boolean.TRUE)};

        q.executeSimpleQuery(vars, ass);
        q.executeQueryWithVariableElimination(new EliminationAsk(), vars, ass);

    }
}
