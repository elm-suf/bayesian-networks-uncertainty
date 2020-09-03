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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryOptimizer {
    private final BayesianNetwork bn;
    private BayesInference bi;
    private Map<String, RandomVariable> map = new HashMap<>();

    public QueryOptimizer(BayesianNetwork bn) {
        this.bn = bn;
        bn.getVariablesInTopologicalOrder().forEach(el -> this.map.put(el.getName(), el));
    }

    public void executeSimpleQuery(RandomVariable[] vars, AssignmentProposition[] query) {
        this.bi = new EnumerationAsk();
        CategoricalDistribution res = this.bi.ask(vars, query, this.bn);
        System.out.println(res);
    }

    public Node getNode(String node) {
        return this.getNode(this.map.get(node));
    }

    public Node getNode(RandomVariable var) {
        return this.bn.getNode(var);
    }


    public void optimizeForQuery(List<RandomVariable> vars, List<AssignmentProposition> ass) {
        //var q1 = vars.get(0);
        ass.forEach(el -> {
            var tmp = getNode(el.getTermVariable());
            System.out.println("-----getChildren");
            System.out.println(tmp.getChildren());
            System.out.println("-----getCPD");
            System.out.println(tmp.getCPD());
            System.out.println("-----getParents");
            System.out.println(tmp.getParents());
            System.out.println("-----");

        });

    }
}
