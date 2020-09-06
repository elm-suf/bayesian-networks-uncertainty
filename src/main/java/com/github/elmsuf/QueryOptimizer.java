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
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

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
        keepNodes(relevant);
    }


    public void executeQueryWithVariableElimination1(
            BayesInference bi,
            RandomVariable[] queryVars,
            AssignmentProposition[] evidence) {

        Graph<String, DefaultEdge> graph = this.constructGraph(queryVars, evidence);

        var evidenceList = Arrays.stream(evidence)
                .map(el -> el.getTermVariable().getName())
                .collect(Collectors.toList());

        var relevant = new ArrayList<Node>();
        var irrelevant = new HashSet<Node>();

        for (String e : evidenceList) {
            relevant.add(getNode(e));
            graph.removeVertex(e);
        }

        ConnectivityInspector<String, DefaultEdge> path =
                new ConnectivityInspector<>(graph);

        for (RandomVariable randVar : this.bn.getVariablesInTopologicalOrder()) {
            for (var e : queryVars) {
                if (!evidenceList.contains(randVar.getName())) {
                    boolean pathExists = path.pathExists(randVar.getName(), e.getName());
//                    System.out.printf("[%s -> %s] : %b %n", randVar.getName(), e.getName(), pathExists);
                    if (pathExists) {
                        relevant.add(getNode(randVar));
                    } else {
                        if (!relevant.contains(getNode(randVar)))
                            irrelevant.add(getNode(randVar));
                    }
                }
            }
        }
        System.out.println("***************++" + irrelevant);
        System.out.println(this.bn.getVariablesInTopologicalOrder());
        pruneNodes(irrelevant);
        System.out.println(this.bn.getVariablesInTopologicalOrder());

        long startTime = System.nanoTime();
        CategoricalDistribution res = bi.ask(queryVars, evidence, bn);
        long endTime = System.nanoTime();
        System.out.println(res);
        System.out.println(bi.getClass().getSimpleName() + " EXEC TOOK: " + (endTime - startTime));
    }

    private Graph<String, DefaultEdge> constructGraph(RandomVariable[] vars, AssignmentProposition[] evidence) {
        Graph<String, DefaultEdge> g = new DefaultUndirectedGraph<>(DefaultEdge.class);

        this.bn.getVariablesInTopologicalOrder().forEach(el -> g.addVertex(el.getName()));

        this.bn.getVariablesInTopologicalOrder().stream().map(this::getNode).forEach(node -> {
            final String curr = node.getRandomVariable().getName();
            marryParents(g, node.getParents());
            connectWithNeighbors(g, curr, node.getParents());
        });

        return g;
    }

    private void marryParents(Graph<String, DefaultEdge> g, Set<Node> parents) {
        parents.forEach(parent -> {
            parents.stream()
                    .filter(sibling -> sibling.getRandomVariable() != parent.getRandomVariable())
                    .forEach(el -> g.addEdge(parent.getRandomVariable().getName(), el.getRandomVariable().getName()));
        });
    }

    private void connectWithNeighbors(Graph<String, DefaultEdge> g, String curr, Set<Node> neighbors) {
        neighbors.stream().map(el -> el.getRandomVariable().getName())
                .forEach(nb -> g.addEdge(curr, nb));
    }



    private void pruneNodes(Set<Node> irelevant) {
        irelevant.forEach(this.bn::removeNode);
    }
    private void keepNodes(Set<Node> relevant) {
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
