/*
 * Matrix.java - Class to compute matrix operations
 *
 * Copyright (C) 2007-2013 Andreas de Vries
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
package org.mathIT.algebra;
import java.util.ArrayList;
import org.mathIT.numbers.Numbers;
/** 
 * This class enables to contruct a matrix which can be manipulated by various
 * matrix operations.
 * @author  Andreas de Vries
 * @version 1.2
 */
public class Matrix {
   private static final java.text.DecimalFormat numberFormat = 
           org.mathIT.util.Formats.O_DOT_A3;
   private int rows;
   private int columns;
   private double matrix[][];
   
   /** Constructs a zero matrix with the given rows and columns.
    *  @param rows the number of rows of this matrix
    *  @param columns the number of columns of this matrix
    */
   public Matrix(int rows, int columns) {
      this.rows    = rows;
      this.columns = columns;
      matrix = new double[rows][columns];
   }
   
   /** Constructs a matrix from the given two-dimensional array.
    *  @param matrix the matrix entries as an array
    */
   public Matrix(double[][] matrix) {
      this.rows    = matrix.length;
      this.columns = matrix[0].length;
      this.matrix  = matrix;
   }
   
   /** Constructs a matrix from the given two-dimensional array.
    *  @param matrix the matrix entries as an array
    * @param rows number of rows
    * @param columns  number of columns
    */
   public Matrix(double[][] matrix, int rows, int columns) {
      this.rows    = rows;
      this.columns = columns;
      this.matrix  = matrix;
   }
   
   /** Constructs a matrix (1 &times; <i>n</i>) matrix from the given one-dimensional array.
    *  @param vector the matrix entries as an array
    */
   public Matrix(double[] vector) {
      this.rows    = 1;
      this.columns = vector.length;
      this.matrix  = new double[rows][columns];
      System.arraycopy(vector, 0, this.matrix[0], 0, vector.length);
   }
   
   /** Returns the number of rows of this matrix.
    *  @return the number of rows of this matrix
    */
   public int getRows() {
      return rows;
   }
   
   /** Returns the number of columns of this matrix.
    *  @return the number of columns of this matrix
    */
   public int getColumns() {
      return columns;
   }
   
   /** Returns this matrix as a two-dimensional array.
    *  The array matrix is an attribute of this matrix, but it is returned as
    *  a copy.
    *  @return the double array with the entries of this matrix
    */
   public double[][] getMatrix() {
      double[][] a = new double[rows][columns];
      for (int i = 0; i < rows; i++) {
         System.arraycopy(matrix[i], 0, a[i], 0, columns);
      }
      return a;
   }
  
   /** Returns the value of the matrix position given by the parameters row and col
    *  in the usual mathematical convention, that is, both indices are
    *  counted from 1 and terminate at the maximum number of rows or columns, respectively.
    *  @param row the row index (counting from 1) to be set
    *  @param col the column index (counting from 1) to be set
    *  @return the value <i>A<sub>ij</sub></i> with <i>i</i> = row, <i>j</i> = column
    */
   public double getValue(int row, int col) {
       return matrix[row - 1][col - 1];
   }
   
   /** Sets the value into the matrix position given by the parameters row and col
    *  in the usual mathematical convention, that is, both indices are
    *  counted from 1 and terminate at the maximum number of rows or columns, respectively.
    *  @param row the row index (counting from 1) to be set
    *  @param col the column index (counting from 1) to be set
    *  @param value the value to be set
    */
   public void setValue(int row, int col, double value) {
       matrix[row - 1][col - 1] = value;
   }
   
   /** Returns whether this matrix is square.
    *  @return true if and only if this matrix is square
    */
   public boolean isSquare() {
      return (rows == columns);
   }
   
   /** Returns whether this matrix is symmetric.
    *  @return true if and only if this matrix is symmetric
    */
   public boolean isSymmetric() {
      if (!isSquare()) return false;
      int i, j;
      for (i = 1; i < rows; i++) {
         for (j = 0; j < i; j++) {
            if (matrix[i][j] != matrix[j][i]) return false;
         }
      }
      return true;
   }
   
   /** Returns a copy of this matrix. Thus this method can be used to clone
    *  this matrix.
    *  @return a copy of this matrix
    */
   public Matrix copy() {
      Matrix A = new Matrix(rows, columns);
      int j;
      for (int i = 0; i < rows; i++) {
         /*
         for (j = 0; j < columns; j++) {
            A.matrix[i][j] = this.matrix[i][j];
         }
         */
         System.arraycopy(matrix[i], 0, A.matrix[i], 0, columns);
      }
      return A;
   }
   
   /**
    * Returns the two norm ||<i>A</i>||<sub>2</sub> of this matrix. 
    * The two norm ||<i>A</i>||<sub>2</sub> of a matrix <i>A</i> is defined as the value
    * <p style="text-align:center;">
    *  ||<i>A</i>||<sub>2</sub> 
    *  = max<sub>||<i>x</i>||=1</sub> ||<i>Ax</i>||
    * </p>
    * where ||<i>x</i>|| is the usual Euclidean norm for <i>vectors</i>.
    * In fact, ||<i>A</i>||<sub>2</sub> is the maximum singular value of <i>A</i>.
    * This method obtains the norm
    * from the {@link SingularValueDecomposition singular value decomposition} 
    * (SVD) of this matrix. This method is convenient to be invoked 
    * if only the norm of the matrix is desired. Since, however, 
    * in particular for large matrices it requires
    * substantial computational effort, you may be considering to apply more efficiently
    * the class {@link SingularValueDecomposition} and its methods.
    *
    * @return the effective numerical rank of this matrix
    * @see SingularValueDecomposition
    * @see SingularValueDecomposition#norm2()
    */
   public double norm2() {
      return new SingularValueDecomposition(this).norm2();
   }
   
   /**
    * Returns the rank of this matrix. It is the effective numerical rank as
    * is obtained from the {@link SingularValueDecomposition Singular value decomposition} 
    * (SVD) of this matrix. 
    * This method is convenient to be invoked 
    * if only the rank of the matrix is desired. Since, however, 
    * in particular for large matrices it requires
    * substantial computational effort, you may be considering to apply more efficiently
    * the class {@link SingularValueDecomposition} and its methods.
    *
    * @return the effective numerical rank of this matrix
    * @see SingularValueDecomposition
    * @see SingularValueDecomposition#rank()
    */
   public int rank() {
      return new SingularValueDecomposition(this).rank();
   }
   
   /**
    * Returns the two norm condition number defined as the ratio
    * <i>s</i><sub>max</sub>/<i>s</i><sub>min</sub> 
    * of the maximum and the minimum singular value of this matrix.
    * This method obtains its result from the 
    * {@link SingularValueDecomposition Singular value decomposition} (SVD) of this matrix.
    * This method is convenient to be invoked 
    * if only the condition number of the matrix is desired. Since, however, 
    * in particular for large matrices it requires
    * substantial computational effort, you may be considering to apply more efficiently
    * the class {@link SingularValueDecomposition} and its methods.
    * @return ratio of largest to smallest singular value 
    * (<i>s</i><sub>max</sub>/<i>s</i><sub>min</sub>).
    * @see SingularValueDecomposition#cond()
    */
   public double cond() {
      return new SingularValueDecomposition(this).cond();
   }

   /** Computes the determinant of this matrix. 
    *  An exception is thrown if this matrix is not square, since then
    *  the determinant is not defined mathematically.
    *  @return the determinant of this matrix+
    *  @throws IllegalArgumentException if this matrix is not square
    */
   public double det() {
      if (rows != columns) {
         throw new IllegalArgumentException("Not a square matrix: ("+rows+"x"+columns+")");
      }
      double[][] a = new double[rows][columns]; 
      for (int i = 0; i < rows; i++) {
         System.arraycopy(matrix[i], 0, a[i], 0, columns);
      }
      int indx [] = new int[rows];
      double d_ = luDecompose(a, indx);
      for (int j = 0; j < rows; j++)  d_ *= a[j][j];
      return d_;
   }

   /**
    * Returns an array consisting of the real parts of the eigenvalues of this
    * matrix.
    * This method obtains its result from the 
    * {@link EigenvalueDecomposition eigenvalue decomposition} of this matrix.
    * This method is convenient to be invoked 
    * if only the real parts of the eigenvalues of this matrix are desired. 
    * Since, however, in particular for large matrices it requires
    * substantial computational effort, you may be considering to apply more efficiently
    * the class {@link EigenvalueDecomposition} and its methods.
    * This is especially recommended if other related informations of this matrix
    * such as the {@link EigenvalueDecomposition#getImagEigenvalues() imaginary parts}
    * or the {@link EigenvalueDecomposition#getV() eigenvectors} are to be computed
    * as well.
    * @return an array of the real parts of the eigenvalues of this matrix 
    * @see EigenvalueDecomposition#getRealEigenvalues()
    * @throws IllegalArgumentException if this matrix is not square
    */
   public double[] getRealEigenvalues() {
      if (!isSquare()) {
         throw new IllegalArgumentException("This matrix is not square.");
      }
      return new EigenvalueDecomposition(this).getRealEigenvalues();
   }
   
   /**
    * Returns an array consisting of the real parts of the eigenvalues of this
    * matrix.
    * This method obtains its result from the 
    * {@link EigenvalueDecomposition eigenvalue decomposition} of this matrix.
    * This method is convenient to be invoked 
    * if only the imaginary parts of the eigenvalues of this matrix are desired. 
    * Since, however, in particular for large matrices it requires
    * substantial computational effort, you may be considering to apply more efficiently
    * the class {@link EigenvalueDecomposition} and its methods.
    * This is especially recommended if other related informations of this matrix
    * such as the {@link EigenvalueDecomposition#getRealEigenvalues() real parts}
    * or the {@link EigenvalueDecomposition#getV() eigenvectors} are to be computed
    * as well.
    * @return an array of the real parts of the eigenvalues of this matrix 
    * @see EigenvalueDecomposition#getImagEigenvalues()
    * @throws IllegalArgumentException if this matrix is not square
    */
   public double[] getImagEigenvalues() {
      if (!isSquare()) {
         throw new IllegalArgumentException("This matrix is not square.");
      }
      return new EigenvalueDecomposition(this).getImagEigenvalues();
   }
   
   /**
    * Returns a matrix whose columns are the eigenvectors of this matrix.
    * This method obtains its result from the 
    * {@link EigenvalueDecomposition eigenvalue decomposition} of this matrix.
    * This method is convenient to be invoked 
    * if only the eigenvectors of the matrix are desired. Since, however, 
    * in particular for large matrices it requires
    * substantial computational effort, you may be considering to apply more efficiently
    * the class {@link EigenvalueDecomposition} and its methods.
    * This is especially recommended if other related informations of this matrix
    * such as the {@link EigenvalueDecomposition#getRealEigenvalues() real parts}
    * or the {@link EigenvalueDecomposition#getImagEigenvalues()  imaginary parts} are to be computed
    * as well.
    * @return an array of the real parts of the eigenvalues of this matrix 
    * @see EigenvalueDecomposition#getV()
    * @throws IllegalArgumentException if this matrix is not square
    */
   public Matrix getEigenvectors() {
      if (!isSquare()) {
         throw new IllegalArgumentException("This matrix is not square.");
      }
      return new EigenvalueDecomposition(this).getV();
   }
   
   /** Returns the adjugate of this matrix.
    *  An exception is thrown if this matrix is not square, since then
    *  the adjugate is not defined mathematically.
    *  For a definition of the adjugate see 
    *  <a href="http://en.wikipedia.org/wiki/Adjugate_matrix" target="_top">http://en.wikipedia.org/wiki/Adjugate_matrix</a>
    *  @return the adjugate of this matrix
    *  @throws IllegalArgumentException if the matrix is not square
    */
   public Matrix adjugate() {
      if (rows != columns) {
         throw new IllegalArgumentException("Not a square matrix: ("+rows+"x"+columns+")");
      }
      Matrix adj = new Matrix(rows, columns);
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < columns; j++) {
            adj.matrix [i][j] = cofactor(j,i);
         }
      }
      return adj;
   }
    
   /** Returns the inverse of this matrix.
    *  An exception is thrown if this matrix is not square, since then
    *  the inverse is not defined mathematically.
    *  @return the inverse of this matrix
    *  @throws IllegalArgumentException if the matrix is not square
    */
   public Matrix inverse() {
      if (rows != columns) {
         throw new IllegalArgumentException("Not a square matrix: ("+rows+"x"+columns+")");
      }
      //double det = det();
      Matrix inv = new Matrix(rows, columns);
      /*
      Matrix adj = adjoint(); //new Matrix(rows, columns);
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < columns; j++) {
            inv.matrix[i][j] = adj.matrix [i][j] / det;
         }
      }
      */
      double[][] a = new double[rows][columns];
      for (int i = 0; i < rows; i++) // copy by value
         //for (int j = 0; j < columns; j++)  a[i][j] = matrix[i][j];
         System.arraycopy(matrix[i], 0, a[i], 0, columns);
      int indx [] = new int[rows];
      double [] col = new double[rows];
      
      luDecompose(a, indx);                           // LU-decompose matrix a
      for (int j = 0; j < rows; j++) {                // find inverse by columns
         for (int i = 0; i < rows; i++)  col[i] = 0.0;
         col[j] = 1.0;
         col = luBacksubst(a, indx, col);
         for (int i = 0; i < rows; i++)  inv.matrix[i][j] = col[i];
      }
      return inv;
   }
  
   /** Returns the cofactor <i>C<sub>ij</sub></i> = (-1)<sup><i>i</i> + <i>j</i></sup> <i>M<sub>ij</sub></i>
    *  of this matrix, where <i>M<sub>ij</sub></i> denotes the minor.
    *  For a definition of the cofactor and the minor of a matrix, see
    *  <a href="http://en.wikipedia.org/wiki/Minor_%28linear_algebra%29" target="_top">
    *  http://en.wikipedia.org/wiki/Minor (linear algebra)</a>
    *  @param i index of cofactor <i>C<sub>ij</sub></i>
    *  @param j index of cofactor <i>C<sub>ij</sub></i>
    *  @return the cofactor <i>C<sub>ij</sub></i>
    *  @throws IllegalArgumentException if the matrix is not square
    */
   public double cofactor(int i, int j) {
      int kCorrect = 0, lCorrect;
      Matrix minorMatrix = new Matrix (rows - 1, columns - 1);
      for (int k = 0; k < rows - 1; k++) {
         if (k == i) kCorrect = 1;
         lCorrect = 0;
         for (int l = 0; l < columns - 1; l++) {
            if (l == j) lCorrect = 1;
            minorMatrix.matrix[k][l] = matrix[k + kCorrect][l + lCorrect];
         }
      }
      return Math.pow(-1, i+j) * minorMatrix.det();
   }
  
   /** Returns the transpose of this matrix.
    *  @return the transpose this matrix
    */
   public Matrix transpose() {
      Matrix transpose = new Matrix(columns, rows);
      for (int i = 0; i < columns; i++) {
         for (int j = 0; j < rows; j++) {
            transpose.matrix[i][j] = matrix[j][i];
         }
      }
      return transpose;
   }
    
   /** Transposes this matrix in place, that is, without using additional space 
    *  to create a new matrix. Evidently, the matrix has to be square in this case.
    *  @throws IllegalArgumentException if this matrix is not square
    */
   public void transposeInPlace() {
      if (!isSquare()) {
         throw new IllegalArgumentException("This matrix is not square.");
      }
      double tmp;
      int j;
      for (int i = 0; i < columns; i++) {
         for (j = 0; j < rows; j++) {
            tmp = matrix[i][j];
            matrix[i][j] = matrix[j][i];
            matrix[j][i] = tmp;
         }
      }
   }
    
   /** Returns the trace of this matrix.
    *  @return the trace of this matrix
    *  @throws IllegalArgumentException if this matrix is not square
    */
   public double trace() {
      if (!isSquare()) {
         throw new IllegalArgumentException("This matrix is not square.");
      }
      double tr = 0;
      for (int i = 0; i < rows; i++) {
         tr += matrix[i][i];
      }
      return tr;
   }
  
   /** Returns the sum of this matrix with the given matrix b.
    *  @param b the matrix to be added to this matrix
    *  @return the sum of this matrix and the input matrix b
    */
   public Matrix add(Matrix b) {
      int rowsMax    = (rows >= b.rows) ? rows : b.rows;
      int columnsMax = (columns >= b.columns) ? columns : b.columns;
      int i, j;
      Matrix c = new Matrix(rowsMax, columnsMax);
      for (i = 0; i < rows; i++) {
         for (j = 0; j < columns; j++) {
            c.matrix[i][j] = matrix[i][j];
         }
      }
      for (i = 0; i < b.rows; i++) {
         for (j = 0; j < b.columns; j++) {
            c.matrix[i][j] += b.matrix[i][j];
         }
      }
      return c;
   } 
  
   /** Returns the negative of this matrix.
    *  @return the negative of this matrix
    */
   public Matrix negative() {
      Matrix c = new Matrix(rows, columns);
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < columns; j++) {
            c.matrix[i][j] = -matrix[i][j];
         }
      }
      return c;
   }
  
   /** Returns the difference of this matrix with the given matrix b.
    *  @param b the matrix to be subtracted from this matrix
    *  @return the difference of this matrix and the input matrix b
    */
   public Matrix minus(Matrix b) {
      int rowsMax    = (rows >= b.rows) ? rows : b.rows;
      int columnsMax = (columns >= b.columns) ? columns : b.columns;
      int i, j;
      Matrix c = new Matrix(rowsMax, columnsMax);
      for (i = 0; i < rows; i++) {
         for (j = 0; j < columns; j++) {
            c.matrix[i][j] = matrix[i][j];
         }
      }
      for (i = 0; i < b.rows; i++) {
         for (j = 0; j < b.columns; j++) {
            c.matrix[i][j] -= b.matrix[i][j];
         }
      }
      return c;
   }
   
   /** Returns the subtraction of the vectors v and w.
    *  @param v a vector
    *  @param w a vector
    *  @return the difference vector v - w
    *  @throws IllegalArgumentException if number of entries of v and w differ
    */
   public static double[] minus(double[] v, double[] w) {
      if (v.length != w.length) {
         throw new IllegalArgumentException(
            "Dimension of vectors are different: " + v.length + " != " + w.length
        );
      }
      double[] y = new double[v.length];
      for (int i = 0; i < v.length; i++) {
         y[i] = v[i] - w[i];
      }
      return y;
   }
  
   /** Returns the (right) product of this matrix with the given matrix b.
    *  @param b the matrix to multiply this matrix from right
    *  @return the right product of this matrix and the input matrix b
    *  @throws IllegalArgumentException if number of columns of this matrix is
    *  different from the number of rows of <i>b</i>
    */
   public Matrix times(Matrix b) {
      if (columns != b.getRows()) {
         throw new IllegalArgumentException(
            "Column and row numbers not appropriate for multiplication: " +
            columns + "!=" + b.getRows()
         );
      }
      Matrix c = new Matrix(rows, b.getColumns());
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < b.getColumns(); j++) {
            for (int k = 0; k < columns; k++) {
               c.matrix[i][j] += matrix[i][k] * b.getMatrix()[k][j];
            }
         }
      }
      return c;
   }
  
   /** Returns the (right) product of this matrix with the given column vector v.
    *  @param v the column vector to multiply this matrix from right
    *  @return the right product of this matrix and the input vector v
    *  @throws IllegalArgumentException if number of columns of this matrix is
    *  different from the number of rows of <i>v</i>
    */
   public double[] times(double[] v) {
      if (columns != v.length) {
         throw new IllegalArgumentException(
            "Column and row numbers not appropriate for multiplication: " +
            columns + "!=" + v.length
        );
      }
      double[] y = new double[v.length];
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < v.length; j++) {
            y[i] += matrix[i][j] * v[j];
         }
      }
      return y;
   }
  
   /** Returns the product a*v.
    *  @param a a constant
    *  @param v a vector
    *  @return the product a*v
    */
   public static double[] multiply(double a, double[] v) {
      double[] y = new double[v.length];
      for (int i = 0; i < v.length; i++) {
         y[i] = a * v[i];
      }
      return y;
   }
  
   /** Returns the scalar product of the vectors v and w.
    *  @param v a row vector
    *  @param w a column vector
    *  @return the scalar product of the vectors v and w
    *  @throws IllegalArgumentException if number of entries of v and w differ
    */
   public static double multiply(double[] v, double[] w) {
      if (v.length != w.length) {
         throw new IllegalArgumentException(
            "Dimension of vectors are different: " + v.length + "!=" + w.length
         );
      }
      double y = 0;
      for (int i = 0; i < v.length; i++) {
         y += v[i] * w[i];
      }
      return y;
   }
  
   /** Returns the tensor product of the matrices a[0], a[1], ..., a[n-1],
    *  i.e., a[0] &otimes; a[1] ... a[n-2] &otimes; a[n-1].
    *  @param a array of several complex matrices a[0], a[1], ..., a[n-1]
    *  @return the tensor product 
    *  a[n-1] &otimes; a[n-2] &otimes; ... &otimes; a[1] &otimes; a[0]
    *  @throws IllegalArgumentException if one of the matrices is empty
    */
   public static Matrix tensor(Matrix[] a) {
      ArrayList<double[][]> matrices = new ArrayList<>(a.length);
      for (Matrix m : a) {
         matrices.add(m.getMatrix());
      }
      return new Matrix(tensor(matrices.toArray(new double[0][0][0])));
   }
   
   /** Returns the tensor product of the matrices a[0], a[1], ..., a[n-1],
    *  i.e., a[0] &otimes; a[1] ... a[n-2] &otimes; a[n-1].
    *  @param a array of several complex matrices a[0], a[1], ..., a[n-1]
    *  @return the tensor product 
    *  a[n-1] &otimes; a[n-2] &otimes; ... &otimes; a[1] &otimes; a[0]
    *  @throws IllegalArgumentException if one of the matrices is empty
    */
   public static double[][] tensor(double[][][] a) {
      for (int i = 0; i < a.length; i++) {
         if (a[i].length == 0 || a[i][0].length == 0) {
            throw new IllegalArgumentException("Empty matrix");
         }
      }
      
      int[] ia = new int[a.length];  // auxiliary indices
      int[] ja = new int[a.length];  // auxiliary indices
      int rows = 1, cols = 1; // rows and columns of the resultig matrix
      
      for (int i = 0; i < a.length; i++) {
         rows *= a[i].length;
         cols *= a[i][0].length;
      }
      
      double[][] c = new double[rows][cols];
      
      for (int i = 0; i < c.length; i++) {
         for (int j = 0; j < c[0].length; j++) {
            rows = c.length;
            cols = c.length;
            for (int k = 0; k < a.length; k++) { // vorwärts multiplizieren
               rows /= a[k].length;
               cols /= a[k][0].length;
               ia[k] = (i / rows) % a[k].length;
               ja[k] = (j / cols) % a[k][0].length;
            }
            
            c[i][j] = a[0][ia[0]][ja[0]];
            
            for (int k = 1; k < a.length; k++) {
               c[i][j] = a[k][ia[k]][ja[k]] * c[i][j];
            }
         }
      }
      return c;
   }
   
   /**
    *  This method solves the <i>n</i> linear equation <i>Ax = b</i>,
    *  where <i>A</i> is this matrix and <i>b</i> the given vector.
    *  The method applies the Gaussian algorithm.
    *  @param b vector
    *  @return the solution vector <i>x</i> of <i>Ax = b</i>
    *  @throws IllegalArgumentException if b is not an appropriate column vector
    */
   public Matrix solve(Matrix b) {
      if (b.getColumns() != 1) {
         throw new IllegalArgumentException("b is not a column vector: columns="+b.getColumns());
      }
      if (b.getRows() != rows) {
         throw new IllegalArgumentException("b is not an n-dimensional vector: rows="+b.getRows());
      }
      Matrix x = new Matrix(rows, 1);
      double[][] a = this.getMatrix();
      int indx [] = new int[rows];
      double[] bv = new double[rows];
      double[] v;
      for (int i = 0; i < rows; i++)  bv[i] = b.matrix[i][0]; // copy by value
//     x = inverse().times(b);
      luDecompose(a, indx); // return value ignored, used as a void method to change a and indx
      v = luBacksubst(a, indx, bv);
      for (int i = 0; i < rows; i++)  x.matrix[i][0] = v[i];
      return x;
   }
  
   /**
    *  Returns the tridiagonal matrix of this matrix according to the Lanczos algorithm.
    *  @return the tridiagonal matrix of this matrix according to the Lanczos algorithm
    *  @throws IllegalArgumentException if this matrix is not symmetric.
    */
   /* --- is obsolete with class EigenvalueDecomposition ...
   public Matrix lanczos() {
      if (!isSymmetric()) {
         throw new IllegalArgumentException("This matrix is not symmetric. (Lanczos algorithm only works for symmetric matrices)");
      }
      Matrix T = new Matrix(new double[matrix.length][matrix.length]); // tridiagonal matrix
      Matrix Q = new Matrix(new double[matrix.length][matrix.length]); // orthogonal Q-matrix
      // -> At the end we have T = QAQ^*, where A = this matrix
      double[] r; // temporary vector
      int i, j; // indices
      double l; // temporary variable to compute the length of vectors
      double[] q0 = new double[matrix.length]; // temporary vector
      for (i = 0; i < matrix.length; i++) {
         Q.matrix[0][i] = 1.; //Math.random();
      }
      //Q.matrix[0][0] = 1.;
      l = getNorm(Q.matrix[0]);
      for (i = 0; i < matrix.length; i++) {
         Q.matrix[0][i] /= l;
      }
      
      T.matrix[0][1] = l;
      T.matrix[1][0] = l;
            
      // Lanczos loop:
      l = 0;
      for (i = 0; i < matrix.length; i++) {
         r = minus(this.times(Q.matrix[i]), multiply(l, q0)); // A*q_i - l q_{i-1}
         T.matrix[i][i] = multiply(r, Q.matrix[i]);
         r = minus(r, multiply(T.matrix[i][i], Q.matrix[i]));
         r = minus(r, Q.transpose().times(Q).times(r)); // reorthogonalization (expansive for large matrices!)
         l = getNorm(r);
         if (i < matrix.length - 1) {
            T.matrix[i+1][i] = l;
            T.matrix[i][i+1] = l;
         }
         System.arraycopy(Q.matrix[i], 0, q0, 0, q0.length); // v0 = V[i]
         if (l != 0.0) {
            r = multiply(1/l, r);
         } else { // only the diagonal matters!
            r = new double[matrix.length];
            r[i] = 1;
         }
         if (i < matrix.length - 1) {
            if (l != 0.0) {
               System.arraycopy(r, 0, Q.matrix[i+1], 0, matrix.length);
            } else { // only the diagonal matters!
               Q.matrix[i+1][i+1] = 1; // ???
            }
         }
      }
      
      Matrix[] QR = T.decomposeQR();
      
      // -- Eigenvector test: ---
      //Matrix v = Q.transpose().times(w);
      //System.out.println("Eigenvectors v:\n"+v);
      //System.out.println("(AQ^* - Q^* T) w :\n"+this.times(v).minus(Q.transpose().times(T).times(w)));
      System.out.println(T);
      //System.out.println(QR[0]);
      //System.out.println(QR[1]);
      //System.out.println(QR[1].det() - T.det());
      //return Q.times(this.times(Q.transpose())).minus(T);
      //return QR[0].transpose().times(this);
      //return QR[0].times(QR[0].transpose());
      //return QR[0].times(QR[1]);
      return QR[1]; //.times(QR[1]);
   }
   */
   
   /**
    *  Returns the tridiagonal matrix of this matrix according to the Lanczos algorithm
    *  for non-symmetric matrices.
    *  @return the tridiagonal matrix of this matrix according to the Lanczos algorithm
    *  @throws IllegalArgumentException if this matrix is not square.
    */
   /* --- is obsolete with class EigenvalueDecomposition ...
   public Matrix lanczosNonSymmetric() {
      if (!isSquare()) {
         throw new IllegalArgumentException("This matrix is not square. (Lanczos algorithm only works for symmetric matrices)");
      }
      Matrix T = new Matrix(new double[rows][rows]); // tridiagonal matrix
      Matrix V = new Matrix(new double[rows][rows]); // orthogonal V-matrix
      Matrix W = new Matrix(new double[rows][rows]); // orthogonal W-matrix
      // -> At the end we have A = V^*TW <=> T = WAV^*, where A = this matrix
      int i, j; // indices
      double l; // temporary variable to compute the length of vectors

      V.matrix[0][0] = 1.;
      W.matrix[0][0] = 1.;
      l = getNorm(V.matrix[0]);
      for (i = 0; i < rows; i++) {
         V.matrix[0][i] /= l;
      }
      
      l = getNorm(W.matrix[0]);
      for (i = 0; i < rows; i++) {
         W.matrix[0][i] /= l;
      }
      
      Matrix A_star = this.transpose();
      // Lanczos loop:
      for (i = 0; i < rows; i++) {
         T.matrix[i][i] = multiply(this.times(V.matrix[i]), W.matrix[i]); // alpha_i
         if (i == rows - 1) break;
         V.matrix[i+1] = this.times(V.matrix[i]);
         V.matrix[i+1] = minus(V.matrix[i+1], multiply(T.matrix[i][i], V.matrix[i]));
         if (i > 0) {
            V.matrix[i+1] = minus(V.matrix[i+1], multiply(T.matrix[i-1][i], V.matrix[i-1]));
         }
         
         W.matrix[i+1] = A_star.times(W.matrix[i]);
         W.matrix[i+1] = minus(W.matrix[i+1], multiply(T.matrix[i][i], W.matrix[i]));
         if (i > 0) {
            W.matrix[i+1] = minus(W.matrix[i+1], multiply(T.matrix[i][i-1], W.matrix[i-1]));
         }
         
         l = multiply(V.matrix[i+1], W.matrix[i+1]);
         if (l == 0) {
            T.matrix[i+1][i] = 0; // = delta_{j+1}
            T.matrix[i][i+1] = 0; // = beta_{j+1}
            W.matrix[i+1][i+1] = 1;
            V.matrix[i+1][i+1] = 1;            
         } else {
            //System.out.println("### i=" + i + ", l=" + l);
            T.matrix[i+1][i] = Math.sqrt(Math.abs(l));  // = delta_{j+1}
            T.matrix[i][i+1] = l / T.matrix[i+1][i];    // = beta_{j+1}
            W.matrix[i+1] = multiply(1 / T.matrix[i][i+1], W.matrix[i+1]);
            V.matrix[i+1] = multiply(1 / T.matrix[i+1][i], V.matrix[i+1]);
         }
      }
      
      
      Matrix[] QR = T.decomposeQR();
      
      //System.out.println(V); System.out.println(W);
      //System.out.println(T);
      //System.out.println(this);
      //System.out.println(QR[0]);
      //System.out.println(QR[1]);
      //System.out.println(QR[1].det() - T.det());
      //return QR[0].transpose().times(this);
      //return QR[0].times(QR[0].transpose());
      //return QR[0].times(QR[1]);
      return QR[1];
      //return V.transpose().times(T).times(W);
      //return W.times(this).times(V.transpose());
   }
   // */
   
   /** Returns the QR decomposition of this matrix as a pair of matrices.
    *  Here <i>A = QR</i> where <i>A</i> is this matrix, <i>Q</i> is an
    *  orthogonal matrix and <i>R</i> an upper triangular matrix.
    *  @return an array [<i>Q,R</i>] such that <i>A = QR</i>
    *  @throws IllegalArgumentException if this matrix has more columns than rows.
    */
   public Matrix[] decomposeQR() {
      if (rows < columns) {
         throw new IllegalArgumentException(
           "This matrix is not decomposable: "+rows+" rows < "+columns+" columns"
         );
      }
      int m = rows, n = columns;
      int i, j, k;
      double norm, s;
      double[][] QR = new double[rows][columns]; // Array for internal storage of decomposition
      double[] Rdiag = new double[n];
      Matrix Q = new Matrix(rows,columns);
      Matrix R = new Matrix(columns,columns);      
      
      for (i = 0; i < rows; i++) {
         for (j = 0; j < columns; j++) {
            QR[i][j] = matrix[i][j];
         }
      }

      for (k = 0; k < n; k++) {
         // Compute 2-norm of k-th column without under/overflow.
         norm = 0;
         for (i = k; i < m; i++) {
            norm = Numbers.hypotenuse(norm, QR[i][k]);
         }

         if (norm != 0.0) {
            // Form k-th Householder vector.
            if (QR[k][k] < 0) {
               norm = -norm;
            }
            for (i = k; i < m; i++) {
               QR[i][k] /= norm;
            }
            QR[k][k] += 1.0;

            // Apply transformation to remaining columns.
            for (j = k+1; j < n; j++) {
               s = 0.0;
               for (i = k; i < m; i++) {
                  s += QR[i][k] * QR[i][j];
               }
               s = -s / QR[k][k];
               for (i = k; i < m; i++) {
                  QR[i][j] += s * QR[i][k];
               }
            }
         }
         Rdiag[k] = norm;
      }
      
      // Compute Q:
      for (k = n-1; k >= 0; k--) {
         Q.matrix[k][k] = -1.0;
         for (j = k; j < n; j++) {
            if (QR[k][k] != 0) {
               s = 0.0;
               for (i = k; i < m; i++) {
                  s -= QR[i][k]*Q.matrix[i][j];
               }
               s = -s/QR[k][k]; // ??
               for (i = k; i < m; i++) {
                  Q.matrix[i][j] -= s*QR[i][k];
               }
            }
         }
      }
      
      // Compute R:
      for (i = 0; i < columns; i++) {
         for (j = 0; j < columns; j++) {
            if (i < j) {
               R.matrix[i][j] = -QR[i][j];
            } else if (i == j) {
               R.matrix[i][j] = Rdiag[i];
            }
         }
      }
      return new Matrix[]{Q, R};
   }

   /** Returns a String representation of this matrix as an HTML table where each
    *  entry is aligned according to the specified value. The possible values are
    *  "left", "center", "right" (in case insensitive manner). If the specified string
    *  has another value, it is set internally to the default value "center".
    *  @param align the alignment of each matrix entry, either "left", "center", or "right"
    *  @param showZeros flag whether zeros in the matrix are to be displayed
    *  @return a String representation of this matrix as an HTML table.
    */
   public String toHTML(String align, boolean showZeros) {
      if (
         !align.equalsIgnoreCase("left")   &&
         !align.equalsIgnoreCase("center") &&
         !align.equalsIgnoreCase("right")
      ) {
         align="center";
      }
      int i, j;
      double value;
      String output = "<table border=\"0\">";
      for (i = 0; i < matrix.length; i++) {
         output += "<tr>";
         for (j = 0; j < matrix[0].length; j++) {
            value = (Math.abs(matrix[i][j]) < 1e-12) ? 0 : matrix[i][j];
            output += "<td align=\""+align+"\">";
            if (showZeros || value != 0) {
               output += numberFormat.format(value);
            } 
            output += "</td>";
         }
         output += "</tr>";
      }
      output += "</table>";
      return output;
   }
  
   /** Returns a String representation of this matrix as an HTML table where each
    *  entry is aligned according to the specified value. The possible values are
    *  "left", "center", "right" (in case insensitive manner). If the specified string
    *  has another value, it is set internally to the default value "center".
    *  @param align the alignment of each matrix entry, either "left", "center", or "right"
    *  @return a String representation of this matrix as an HTML table.
    */
   public String toHTML(String align) {
      return toHTML(align, true);
   }
  
   /** Returns a String representation of this matrix as an HTML table.
    *  @return a String representation of this matrix as an HTML table.
    */
   public String toHTML() {
      return toHTML("center", true);
   }

   /** Returns a String representation of this matrix.
    */
   @Override
   public String toString() {
      int i, j;
      String output = "{";
      for (i = 0; i < matrix.length - 1; i++) {
         output += "{";
         for (j = 0; j < matrix[0].length - 1; j++) {
            output += numberFormat.format(matrix[i][j]) + ", ";
         }
         output += numberFormat.format(matrix[i][j]) + "}\n ";
      }
      output += "{";
      for (j = 0; j < matrix[0].length - 1; j++) {
         output += numberFormat.format(matrix[i][j]) + ", ";
      }
      output += numberFormat.format(matrix[i][j]) + "}}";
      return output;
   }

   /* ------------------------
    + Private Methods
    * ------------------------ */

   /** 
    *  This method replaces the input matrix <code>a</code> by the LU decomposition,
    *  as given by Crout's algorithm, of a rowwise permutation of itself and returns +1 or -1 
    *  depending on whether the number of row interchanges was even or odd, respectively
    *. The input array <code>indx</code> is a vector recording 
    *  the row permutations effected by partial pivoting. 
    *  This method is used in combination with <code>luBacksubst()</code> to solve 
    *  linear equations or invert a matrix.
    *  <br/>
    *  <b>Complexity:</b> The method performs <i>n</i><sup>3</sup>/3 executions 
    *  where <i>n</i> is the number of rows of this matrix.
    *  @param a the matrix to be replaced by the LU decomposition (call by reference!)
    *  @param indx a vector recording the row permutations (call by reference!)
    *  @return +1 or -1 depending on whether the number of row interchanges <i>a</i> was even or odd,
    *  @throws IllegalArgumentException if the specified matrix is singular
    */
   private static byte luDecompose(double[][] a, int[] indx) {
      byte d = 1;                   // no row interchanges yet
      int imax = 0, n = a.length;   //rows;
      double big, dum, sum, temp;
      double vv[] = new double[n];  // stores the implicit scaling of each row
      
      for (int i = 0; i < n; i++) {
         big = 0.0;
         for (int j = 0; j < n; j++) {
            if ((temp = Math.abs(a[i][j])) > big) big = temp;
         }
         if (big == 0.0) throw new IllegalArgumentException("Singular matrix!");
         vv[i] = 1 / big;   // save the scaling
      }
      for (int j = 0; j < n; j++) {  // this is the loop over columns of Crout's method
         for (int i = 0; i < j; i++) {
            sum = a[i][j];
            for (int k = 0; k < i; k++) sum -= a[i][k] * a[k][j];
            a[i][j] = sum;
         }
         big = 0.0;      // initialize for the search for largest pivot element
         for (int i = j; i < n; i++) {
            sum = a[i][j];
            for (int k = 0; k < j; k++)
               sum -= a[i][k] * a[k][j];
            a[i][j] = sum;
            if ((dum = vv[i] * Math.abs(sum)) >= big) { // is the figure of merit for the
               big = dum;                                     // pivot better than the best so far?
               imax = i;
            }
         }
         if (j != imax) {                  // Do we need to interchange rows?
            for (int k = 0; k < n; k++) {   // Yes, do so ...
               dum = a[imax][k];
               a[imax][k] = a[j][k];
               a[j][k] = dum;
            }
            d = (byte) -d;                    // ... change the parity of d ...
            vv[imax] = vv[j];                 // ... and interchange the scale factor
         }
         indx[j] = imax;
         if (j != n) { // Finally, divide the pivot element
            dum = 1 / a[j][j];
            for (int i = j+1; i < n; i++)  a[i][j] *= dum;
         }
      } // go back for the next column j in the Crout reduction
      return d;
   }
   
   /**
    *  This method solves the <i>n</i> linear equation <code>ax = b</code>, given the 
    *  LU-decomposed (n x n)-matrix <code>a</code> and the n-vector <code>b</code> as 
    *  input, and returns the solution <code>x</code>.
    *  Here the LU decomposition is understood as given by Crout's algorithm, as 
    *  performed by the method <code>luDecompose()</code>. <code>indx</code> is the
    *  permutation vector as evaluated by <code>luDecompose()</code>. 
    *  All input parameters are left unchanged.
    *  This method takes into account the possibility that <code>b</code> will
    *  begin with many zero elements, so it is efficient for use in matrix inversion.
    *  <br/>
    *  <b>Complexity:</b> The method performs <i>n</i><sup>3</sup>/2 executions.
    *  @param a the matrix of the linear equation <code>ax = b</code>
    *  @param b the matrix of the linear equation <code>ax = b</code>
    */
   private static double[] luBacksubst(double[][] a, int [] indx, double[] b) {
      double x[] = new double[b.length]; //[rows];
      System.arraycopy(b, 0, x, 0, b.length);
      int n = a.length, ii = -1, ip;
      double sum;
      
      for (int i = 0; i < n; i++) {
         ip = indx[i];
         sum = x[ip];
         x[ip] = x[i];
         if (ii != -1)
            for (int j = ii; j <= i-1; j++)  sum -= a[i][j] * x[j];
         else if (sum != 0) ii = i;  // a nonzero element was encountered, so from now on we will 
         x[i] = sum;                  // have to do the sums in the loop above
      }
      for (int i = n-1; i >= 0; i--) {  //now we do the backsubstitution
         sum = x[i];
         for (int j = i+1; j < n; j++)  sum -= a[i][j] * x[j];
         x[i] = sum / a[i][i];
      }
      return x;
   }
   
   /**
    * Returns the norm |<i>v</i>| of the vector <i>v</i>.
    * This method computes the norm without unnecessary overflow or underflow.
    * @param v a vector
    * @return the norm |<i>v</i>| of the vector <i>v</i>
    */
   public static double getNorm(double[] v) {
      double norm = 0;
      for (int i = 0; i < v.length; i++) {
         norm = Numbers.hypotenuse(norm, v[i]);
      }
      return norm;
   }
   
   /**
    *  This method creates an (<i>n</i> x <i>n</i>) unity matrix.
    *  I.e., a square matrix whose diagonal entries are 1's, and all other
    *  entries are 0.
    *  @param n the number of rows and columns
    *  @return the (<i>n</i> x <i>n</i>) unity matrix
    */
   public static Matrix createIdentity(int n) {
      Matrix m = new Matrix(n,n);
      for (int i = 0; i < n; i++) {
         m.matrix[i][i] = 1.;
      }
      return m;
   }
  
   /** For test purposes...*/
   /*
   public static void main(String[] args) {
      Matrix A = new Matrix(new double[][] {
         { 0, 1, 0, 1},
         { 0, 0, 1, 1},
         { 1, 0, 0, 0},
         { 0, 0, 1, 0},
      });
      //Matrix T = A.lanczosNonSymmetric();
      Matrix T = A.getEigenvectors();
      Matrix eigen = new Matrix(new double[][] {A.getRealEigenvalues(), A.getImagEigenvalues()});
      System.out.println(eigen);
      javax.swing.JOptionPane.showMessageDialog(null, "<html>" + T.toHTML("right"));
   }
   // */
}
