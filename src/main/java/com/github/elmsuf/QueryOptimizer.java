package com.github.elmsuf;

import aima.core.probability.CategoricalDistribution;
import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesInference;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.exact.EliminationAsk;
import aima.core.probability.bayes.exact.EnumerationAsk;
import aima.core.probability.proposition.AssignmentProposition;
import aima.core.probability.util.RandVar;

import java.util.*;
import java.util.stream.Collectors;

public class QueryOptimizer {
    private final BayesianNetwork bn;
    private Map<String, RandomVariable> map = new HashMap<>();

    public QueryOptimizer(BayesianNetwork bn) {
        this.bn = bn;
        bn.getVariablesInTopologicalOrder().forEach(el -> this.map.put(el.getName(), el));
    }

    public void executeSimpleQuery(RandomVariable[] vars, AssignmentProposition[] query) {
        var bi = new EliminationAsk();
        long startTime = System.nanoTime();
        CategoricalDistribution res = bi.ask(vars, query, this.bn);
        long endTime = System.nanoTime();
        System.out.println(res);
        System.out.println(bi.getClass().getSimpleName() + " EXEC TOOK: " + (endTime - startTime));
    }

    public void executeQueryWithVariableElimination(BayesInference bi, RandomVariable[] vars, AssignmentProposition[] query) {
        this.eliminateVariables(Arrays.asList(vars), query);
        long startTime = System.nanoTime();
        CategoricalDistribution res = bi.ask(vars, query, bn);
        long endTime = System.nanoTime();
        System.out.println(res);
        System.out.println(bi.getClass().getSimpleName() + " EXEC TOOK: " + (endTime - startTime));
    }


    private void eliminateVariables(List<RandomVariable> vars, AssignmentProposition[] query) {
        var relevant = new HashSet<Node>();
        Arrays.stream(query)
                .map(el -> el.getTermVariable().getName())
                .forEach(el -> relevant.addAll(getNode(el).getParents()));

        vars.forEach(el -> {
            relevant.addAll(getNode(el).getParents());
            relevant.add(getNode(el));
        });
        pruneNodes(relevant);
    }



    private void pruneNodes(Set<Node> relevant) {
        this.bn.getVariablesInTopologicalOrder().stream()
                .map(this::getNode)
                .filter(el -> !relevant.contains(el))
                .forEach(this.bn::removeNode);
    }

    public Node getNode(String node) {
        return this.getNode(this.map.get(node));
    }

    public Node getNode(RandomVariable var) {
        return this.bn.getNode(var);
    }

}
