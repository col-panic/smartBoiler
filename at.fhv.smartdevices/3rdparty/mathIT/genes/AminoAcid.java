/*
 * Copyleft 2011 Andreas de Vries
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301  USA
 */
package org.mathIT.genes;

/** This enum represents the 22 natural amino acids forming proteins.
 * <pre> 
   Gly - glycine,        'G'
   Ala - alanine,        'A'
   Val - valine,         'V'
   Leu - leucine,        'L'
   Ile - isoleucine,     'I'
   Phe - phenylalanine,  'F'
   Ser - serine,         'S'
   Thr - threonine,      'T'
   Tyr - tyrosine,       'Y'
   Cys - cysteine,       'C'
   Sec - selenocysteine, 'U' // 21st amino acid
   Met - methionine,     'M'
   Pro - proline,        'P'
   Lys - lysine,         'K'
   His - histidine,      'H'
   Trp - tryptophan,     'W'
   Arg - arginine,       'R'
   Asn - asparagine,     'N'
   Gln - glutamine,      'Q'
   Pyl - pyrrolysine,    'O' // 22nd amino acid
   Asp - aspartic acid,  'D'
   Glu - glutamic acid,  'E'
   Stop- -,              '*'
   </pre>
   The order of the amino acids is taken from 
   C.E. Mortimer & U. Müller: <i>Chemie</i>. 10th edition, Thieme, Stuttgart 2007.
 * @author Andreas de Vries
 * @version 1.1
 */
public enum AminoAcid {
   Gly  ("glycine",        'G'),
   Ala  ("alanine",        'A'),
   Val  ("valine",         'V'),
   Leu  ("leucine",        'L'),
   Ile  ("isoleucine",     'I'),
   Phe  ("phenylalanine",  'F'),
   Ser  ("serine",         'S'),
   Thr  ("threonine",      'T'),
   Tyr  ("tyrosine",       'Y'),
   Cys  ("cysteine",       'C'),
   Sec  ("selenocysteine", 'U'), // 21st amino acid
   Met  ("methionine",     'M'),
   Pro  ("proline",        'P'),
   Lys  ("lysine",         'K'),
   His  ("histidine",      'H'),
   Trp  ("tryptophan",     'W'),
   Arg  ("arginine",       'R'),
   Asn  ("asparagine",     'N'),
   Gln  ("glutamine",      'Q'),
   Pyl  ("pyrrolysine",    'O'), // 22nd amino acid
   Asp  ("aspartic acid",  'D'),
   Glu  ("glutamic acid",  'E'),
   Stop ("--",             '*');
      
   /** Amino acid which is related to this codon by the genetic code. */
   private String name;
   
   /** Amino acid which is related to this codon by the genetic code. */
   private char symbol;
   
   /** Constructor of this amino acid. */
   private AminoAcid(String name, char symbol) {
      this.name   = name;
      this.symbol = symbol;
   }
   
   /** Returns the name of this amino acid.
    *  @return the name of this amino acid
    */
   public String getName() {
      return name;
   }
   
   /** Returns the one-letter symbol of this amino acid.
    *  @return the one-letter symbol of this amino acid
    */
   public char getSymbol() {
      return symbol;
   }
   
   /** Returns the amino acid represented by the specified name.
    *  @param name three-symbol string representing an amino acid
    *  @return the amino acid represented by the specified name
    *  @throws IllegalArgumentException if the string is not an amino acid name
    */
   public static AminoAcid decode(String name) {
      for (AminoAcid a : values()) {
         if (name.equalsIgnoreCase(a.name)) {
            return a;
         }
      }
      throw new IllegalArgumentException(name + " is not an amino acid");
   }
   
   /** Returns the amino acid represented by the specified one-letter symbol.
    *  @param  symbol one-letter symbol representing an amino acid.
    *  @return the amino acid represented by symbol
    *  @throws IllegalArgumentException if the string does not represent an amino acid
    */
   public static AminoAcid decode(char symbol) {
      for (AminoAcid a : values()) {
         if (symbol == a.symbol) {
            return a;
         }
      }
      throw new IllegalArgumentException(symbol + " is not a symbol for an amino acid");
   }
   
   /*
   public static void main(String... args) {      
      String out = "";
      
      //javax.swing.JOptionPane.showMessageDialog(null, out);
      
      String string = "Alanine";
      char c = 'Y';
      System.out.println(c + " = " + decode(c));
      System.out.println(string + " = " + decode(string));
   }
   */
}
