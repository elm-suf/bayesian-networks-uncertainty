package com.github.elmsuf;

import aima.core.probability.CategoricalDistribution;
import aima.core.probability.Factor;
import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesInference;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.exact.EliminationAsk;
import aima.core.probability.bayes.exact.EnumerationAsk;
import aima.core.probability.bayes.impl.BayesNet;
import aima.core.probability.proposition.AssignmentProposition;
import aima.core.probability.util.RandVar;

import java.util.*;
import java.util.stream.Collectors;

public class QueryOptimizer {
    private BayesianNetwork bn;
    private Map<String, RandomVariable> map = new HashMap<>();

    public QueryOptimizer(BayesianNetwork bn) {
        this.bn = bn;
        bn.getVariablesInTopologicalOrder().forEach(el -> this.map.put(el.getName(), el));
    }

    public void executeQuery(BayesInference bi, RandomVariable[] vars, AssignmentProposition[] query) {
        long startTime = System.nanoTime();
        CategoricalDistribution res = bi.ask(vars, query, this.bn);
        long endTime = System.nanoTime();
//        System.out.println(res);
        System.out.println(bi.getClass().getSimpleName() + " EXEC TOOK: " + (endTime - startTime));
    }


    public void executeQueryWithVariableElimination(BayesInference bi, RandomVariable[] vars, AssignmentProposition[] query) {
        var network = this.eliminateVariables(vars, query);
        long startTime = System.nanoTime();
        CategoricalDistribution res = bi.ask(vars, query, network);
        long endTime = System.nanoTime();
//        System.out.println(res);
        System.out.println(bi.getClass().getSimpleName() + " EXEC TOOK: " + (endTime - startTime));
    }

    private BayesianNetwork eliminateVariables(RandomVariable[] vars, AssignmentProposition[] query) {
        var relevant = new HashSet<Node>();
        Arrays.stream(query).map(el -> el.getTermVariable().getName()).forEach(el -> relevant.addAll(getNode(el).getParents()));
        Arrays.stream(vars).forEach(el -> relevant.addAll(getNode(el).getParents()));
        Node[] nodes = this.bn.getVariablesInTopologicalOrder().stream()
                .map(this::getNode)
                .filter(el -> relevant.contains(el))
                .toArray(size -> new Node[size]);
        return new BayesNet(nodes);
//        long startTime = System.nanoTime();
//        long endTime = System.nanoTime();
//        System.out.println("VARIABLE ELIMINATION TOOK: " + (endTime - startTime));
//        System.out.println("-Size" + this.bn.getVariablesInTopologicalOrder().size());
//        System.out.println("+Size" + nodes.length);
    }

    //Helper Methods
    public Node getNode(String node) {
        return this.getNode(this.map.get(node));
    }

    public Node getNode(RandomVariable var) {
        return this.bn.getNode(var);
    }

}