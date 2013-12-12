/*
 * Clustering.java
 *
 * Copyright (C) 2013 Andreas de Vries
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.mathIT.graphs;

import java.util.ArrayList;
import org.mathIT.algebra.MathSet;

/**
 * This class enables to store and manipulate clusters of a graph.
 * @see Graph
 * @author Andreas de Vries
 */
public class Clustering { //extends ArrayList<MathSet<Integer>> {
   //private static final long serialVersionUID = 9223372034793564103L; // "Clustering".hashCode() + Long.MAX_VALUE
   /** The distribution of the vertices over the clusters of this clustering.
    *  Here <code>vertexDistribution[i] = j</code> means that vertex <i>i</i> is 
    *  in cluster <i>j</i>.
    */
   private int[] vertexDistribution;
   /** The number of clusters of this clustering.*/
   private int numberOfClusters;

   /**
    * Generates an empty clustering
    */
   public Clustering() {}
   
   /**
    * Generates a clustering with the specified vertex distribution to clusters.
    * Here <code>vertexDistribution[i] = j</code> means that vertex <i>i</i> is 
    * in cluster <i>j</i>.
    * @param vertexDistribution encodes the distribution of the vertices to the clusters
    */
   public Clustering(int[] vertexDistribution) {
      //super();
      this.vertexDistribution = vertexDistribution;
      int max = -1;
      for (int i = 0; i < this.vertexDistribution.length; i++) {
         if (max < this.vertexDistribution[i]) max = this.vertexDistribution[i];
      }
      this.numberOfClusters = max + 1;
   }
   
   /**
    * Generates a clustering with the specified set of clusters.
    * @param clusters disjoint sets of vertices representing the clusters
    */
   public Clustering(ArrayList<MathSet<Integer>> clusters) {
      //super(clusters);
      int n = 0;
      for (MathSet<Integer> c : clusters) {
         n += c.size();
      }
      this.vertexDistribution = new int[n];
      int j;
      for (int i = 0; i < this.vertexDistribution.length; i++) {
         for (j = 0; j < clusters.size(); j++) {
            if (clusters.get(j).contains(i)) {
               this.vertexDistribution[i] = j;
               continue;
            }
         }
      }
      this.numberOfClusters = clusters.size();
   }
   
   /**
    * Returns the number of clusters of this clustering.
    * @return the number of clusters of this clustering
    */
   public int getNumberOfClusters() {
      return numberOfClusters;
   }
   
   /**
    * Returns the cluster with the specified index of this clustering,
    * represented as the set consisting of the indices
    * of its vertices.
    * The index numbering starts with 0.
    * If there exists no cluster of the specified index, the empty set is returned.
    * @param index index specifying the cluster
    * @return the cluster with the specified index of this clustering.
    */
   public MathSet<Integer> get(int index) {
      MathSet<Integer> cluster = new MathSet<>();
      if (this.vertexDistribution == null || this.vertexDistribution.length == 0) return MathSet.emptySet();
      for (int j = 0; j < this.vertexDistribution.length; j++) {
         if (this.vertexDistribution[j] == index) {
            cluster.add(j);
         }
      }
      return cluster;
   }
   
   /**
    * Returns a list of the clusters of this clustering.
    * Here a cluster is represented as the set consisting of the indices
    * of its vertices.
    * @return a list of the clusters of this clustering
    */
   public ArrayList<MathSet<Integer>> getClusters() {
      ArrayList<MathSet<Integer>> clusters = new ArrayList<>(this.numberOfClusters);
      //for (int i = 0; i < this.numberOfClusters; i++) {
      //   clusters.add(get(i));
      //}
      for (int i = 0; i < this.numberOfClusters; i++) {
         clusters.add(new MathSet<Integer>());
      }
      for (int j = 0; j < this.vertexDistribution.length; j++) {
         clusters.get(this.vertexDistribution[j]).add(j);
      }
      return clusters;
   }
   
   /**
    * Computes and returns the modularity of this clustering in a directed graph.
    * The modularity <i>Q</i> of a graph measures the quality of a division of its
    * vertices into clusters where a clustering is assumed to be good if the number
    * of edges between clusters is smaller than is expected for a random graph
    * with the same clustering. To be precise,
    * <p style="text-align:center">
    *  <i>Q</i>
    *  = (2<i>m</i>)<sup>-1</sup> &sum;<sub><i>i,j</i></sub> 
    *  <span style="font-size:large">(</span>
    *    <i>A<sub>ij</sub></i> - (2<i>m</i>)<sup>-1</sup> 
    *    <i>k</i><sub><i>i</i></sub><sup>out</sup><i>k</i><sub><i>j</i></sub><sup>in</sup>
    *  <span style="font-size:large">)</span>
    * </p>
    * where <i>k</i><sub><i>i</i></sub><sup>out</sup> and 
    * <i>k</i><sub><i>i</i></sub><sup>in</sup>
    * are the outdegree and the indegree of vertex <i>i</i>, respectively.
    * M. E. J. Newman (2010): <i>Networks. An Introduction.</i> 
    * Oxford University Press, Oxford New York, Eq. (7.69).
    * @param adjacency the adjacency matrix of the directed graph
    * @param edges the number of edges of the directed graph
    * @param indeg array of the the indegrees
    * @param outdeg array of the outdegrees
    * @return the modularity of this clustering
    */
   public double modularity(int[][] adjacency, int edges, int[] indeg, int[] outdeg) {
      double m = 2*edges;
      int n = adjacency.length;
      int i, j;
      double Q = 0;
      for (i = 0; i < n; i++) {
         for (j = 0; j < n; j++) {
            if (vertexDistribution[i] == vertexDistribution[j]) {
               Q += adjacency[i][j] - outdeg[i]*indeg[j]/m;
            }
         }
      }
      Q /= m;
      //System.out.println("### clusters: "+numberOfClusters);
      return Q;
   }
   
   /**
    * Computes and returns the modularity of this clustering in an undirected graph.
    * The modularity <i>Q</i> of a graph measures the quality of a division of its
    * vertices into clusters where a clustering is assumed to be good if the number
    * of edges between clusters is smaller than is expected for a random graph
    * with the same clustering. To be precise,
    * <p style="text-align:center">
    *  <i>Q</i>
    *  = (2<i>m</i>)<sup>-1</sup> &sum;<sub><i>i,j</i></sub>
    *  <span style="font-size:large">(</span>
    *    <i>A<sub>ij</sub></i> - (2<i>m</i>)<sup>-1</sup> 
    *    <i>k</i><sub><i>i</i></sub><i>k</i><sub><i>j</i></sub>
    *  <span style="font-size:large">)</span>
    * </p>
    * where <i>k</i><sub><i>i</i></sub> is the degree of vertex <i>i</i>.
    * For more details see 
    * M. E. J. Newman (2010): <i>Networks. An Introduction.</i> 
    * Oxford University Press, Oxford New York, Eq. (7.69).
    * @param adjacency the adjacency matrix of the directed graph
    * @param edges the number of edges of the directed graph
    * @param deg array of the the degrees
    * @return the modularity of this clustering
    */
   public double modularity(int[][] adjacency, int edges, int[] deg) {
      return modularity(adjacency, edges, deg, deg);
   }
   
   /**
    * Merges the clusters i and j of this clustering and returns the resulting clustering. 
    * In fact the cluster of
    * the greater index is dissolved and its elements are added to the other cluster.
    * @param i index of the first cluster
    * @param j index of the second cluster
    * @return the clustering resulting from merging clusters <i>i</i> and <i>j</i>
    */
   public Clustering merge(int i, int j) {
      int min, max;
      if (i <= j) {
         min = i;
         max = j;
      } else {
         min = j;
         max = i;
      }
      int[] sd = java.util.Arrays.copyOf(this.vertexDistribution, this.vertexDistribution.length);
      //int[] sd; System.arraycopy(this.vertexDistribution, 0, sd, 0, this.vertexDistribution.length);
      for (int k = 0; k < this.vertexDistribution.length; k++) {
         if (this.vertexDistribution[k] == max) sd[k] = min;
         if (vertexDistribution[k] >  max) sd[k]--;
      }
      return new Clustering(sd);
   }
   
   /**
    * Returns a string representing this clustering of vertices.
    * This method is useful if the names of the vertices instead of their indices
    * should be displayed.
    * @param vertices an array of the vertices of this clustering
    * @return a string representing this clustering of vertices
    */
   public String toString(Vertible[] vertices) {
      if (this.vertexDistribution == null || this.vertexDistribution.length == 0) return "[]";
      StringBuilder out = new StringBuilder("{");
      MathSet<Integer> cluster;
      for (int i = 0; i < this.numberOfClusters; i++) {
         cluster = get(i);
         out.append("{");
         if (!cluster.isEmpty()) {
            for (Integer j : cluster) {
               out.append(vertices[j].getName());
               out.append(", ");
            }
         }
         out.replace(out.length() - 2, out.length(), "}");
         out.append(", ");
      }
      out.replace(out.length() - 2, out.length(), "}");
      //out.append("}");
      return out.toString();
   }
   
   @Override
   public String toString() {
      if (this.vertexDistribution == null || this.vertexDistribution.length == 0) return "[]";
      StringBuilder out = new StringBuilder("{");
      MathSet<Integer> cluster;
      for (int i = 0; i < this.numberOfClusters; i++) {
         cluster = get(i);
         if (!cluster.isEmpty()) {
            out.append(cluster.toString());
            out.append(", ");
         }
      }
      out.replace(out.length() - 2, out.length(), "}");
      return out.toString();
   }
   
   /** For test purposes ... */
   /*
   public static void main(String... args) {
      int n = 3, k = 2;
      ArrayList<int[]> surjections = org.mathIT.numbers.Combinatorics.surjections(n, k);
      for(int[] p : surjections) {
         System.out.println(java.util.Arrays.toString(p));
      }
   }
   // */
}
