package openerp.openerpresourceserver.strategy;

import lombok.Getter;
import openerp.openerpresourceserver.entity.Employee;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.Sender;
import openerp.openerpresourceserver.utils.DistanceCalculator.GraphHopperCalculator;

import java.util.*;

public class Population {
    @Getter
    private List<Individual> individuals;
    private int populationSize;
    private double crossoverRate;
    private double mutationRate;
    private Map<UUID, Sender> senderMap;
    private final GraphHopperCalculator graphHopperCalculator;
    private double[][] distanceMatrix; // Mảng 2 chiều lưu trữ khoảng cách

    public Population(Map<UUID, Sender> senderMap, int populationSize, double crossoverRate, double mutationRate, List<Order> orderList, List<Employee> employees, GraphHopperCalculator graphHopperCalculator, double[][] distanceMatrix) {
        this.populationSize = populationSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.senderMap = senderMap;
        this.individuals = new ArrayList<>();
        this.graphHopperCalculator = graphHopperCalculator;
        this.distanceMatrix = distanceMatrix; // Nhận mảng khoảng cách

        for (int i = 0; i < populationSize; i++) {
            Individual individual = new Individual(senderMap, orderList.size(), employees.size(), orderList, graphHopperCalculator, distanceMatrix);
            individual.randomInit();
            this.individuals.add(individual);
        }
    }

    public Population(List<Individual> offspring, int populationSize, double crossoverRate, double mutationRate, GraphHopperCalculator graphHopperCalculator, double[][] distanceMatrix) {
        this.individuals = offspring;
        this.populationSize = populationSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.graphHopperCalculator = graphHopperCalculator;
        this.distanceMatrix = distanceMatrix; // Nhận mảng khoảng cách
    }

    public void evaluateFitness() {
        for (Individual individual : individuals) {
            individual.calculateFitness();
        }
    }

    public List<openerp.openerpresourceserver.strategy.Individual> selectParents() {
        List<Individual> selectedParents = new ArrayList<>();
        individuals.sort(Comparator.comparingDouble(Individual::getFitness).reversed());
        for (int i = 0; i < individuals.size() / 2; i++) {
            selectedParents.add(individuals.get(i));
        }
        return selectedParents;
    }

    public List<Individual> reproduct(List<Individual> parents) {
        List<Individual> offspring = new ArrayList<>();
        Random random = new Random();
        int parentSize = parents.size();

        while(offspring.size()< populationSize){
            int p1 = random.nextInt(parentSize);
            int p2 = random.nextInt(parentSize);
            while(p1 == p2) p2 = random.nextInt(parentSize);

            Individual parent1 = parents.get(p1);
            Individual parent2 = parents.get(p2);
            if ( random.nextDouble() < crossoverRate) {
                Individual child = parent1.crossoverWith(parent2);
                offspring.add(child);
            }
        }


        return offspring;
    }

    public void mutate(List<Individual> offspring) {
        Random random = new Random();
        for (Individual individual : offspring) {
            if (random.nextDouble() < mutationRate) {
                individual.mutate();
            }
        }
    }

    public Individual findBestIndividual() {
       Individual bestIndividual = individuals.stream().max(Comparator.comparingDouble(Individual::getFitness)).orElse(null);
//        System.out.println(bestIndividual.getFitness());
        return bestIndividual;
    }

}
