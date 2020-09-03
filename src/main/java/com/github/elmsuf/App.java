package com.github.elmsuf;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.example.BayesNetExampleFactory;
import aima.core.probability.proposition.AssignmentProposition;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;

public class App {
    public static void main(String... args) {
//        BayesianNetwork bn = BifReader.readBIF("src/main/resources/networks/alarm.xml");
        BayesianNetwork bn = BayesNetExampleFactory.constructBurglaryAlarmNetwork();
        QueryOptimizer q = new QueryOptimizer(bn);
//        bn.getVariablesInTopologicalOrder().forEach(System.out::println);

        /* Query 1:
         * P(JohnCalls | Alarm=true)
         */
        RandomVariable[] vars = {q.getNode("JohnCalls").getRandomVariable()};
        var Alarm = q.getNode("Alarm").getRandomVariable();
        var MaryCalls = q.getNode("MaryCalls").getRandomVariable();
        AssignmentProposition[] ass = {
                new AssignmentProposition(Alarm, Boolean.FALSE),
        };

//        q.executeQuery(new EliminationAsk(), vars, ass);
//        q.executeQuery(new EnumerationAsk(), vars, ass);
//        System.out.println("________");
//        System.out.println("________");
//        System.out.println("________");
//        q.executeQueryWithVariableElimination(new EliminationAsk(), vars, ass);
//        q.executeQueryWithVariableElimination(new EnumerationAsk(), vars, ass);


        Graph<String, DefaultEdge> g = new DefaultUndirectedGraph<>(DefaultEdge.class);




        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";
        String v5 = "v5";

        // add some sample data (graph manipulated via JGraphX)
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);

        g.addEdge(v1, v2);
        g.addEdge(v1, v3);
        g.addEdge(v3, v4);
        g.addEdge(v4, v3);

        DijkstraShortestPath<String, DefaultEdge> dijkstraAlg =
                new DijkstraShortestPath<>(g);


        System.out.println(dijkstraAlg.getPath(v1, v5));
    }
}