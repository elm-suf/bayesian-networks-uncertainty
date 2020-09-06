package parser;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;

import java.util.List;

/**
 *
 * @author torta
 */
public class    BNParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BayesianNetwork bn = BifReader.readBIF("earthquake.xml");
        List<RandomVariable> rvs = bn.getVariablesInTopologicalOrder();
        for (RandomVariable rv :rvs) {
            System.out.println(rv.getName());
        }
        }   
    
}
