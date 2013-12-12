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

import static org.mathIT.genes.AminoAcid.*;

/** This enum provides the 4<sup>3</sup> = 64 DNA codons made of the alphabet
 *  T, C, A, G; associated to each cocon is the amino acid to which it is
 *  transformed by the genetic code during the process of genetic expression.
 *  
 *  The genetic code is adapted from Mortimer, Müller: <i>Chemie.</i> 
 *  Thieme, Stuttgart 2007, S.627.
 *  <pre>
   TTT (Phe), TCT (Ser), TAT (Tyr), TGT (Cys),
   TTC (Phe), TCC (Ser), TAC (Tyr), TGC (Cys),
   TTA (Leu), TCA (Ser), TAA (Stop),TGA (Sec),
   TTG (Leu), TCG (Ser), TAG (Pyl), TGG (Trp),

   CTT (Leu), CCT (Pro), CAT (His), CGT (Arg),
   CTC (Leu), CCC (Pro), CAC (His), CGC (Arg),
   CTA (Leu), CCA (Pro), CAA (Gln), CGA (Arg),
   CTG (Leu), CCG (Pro), CAG (Gln), CGG (Arg),

   ATT (Ile), ACT (Thr), AAT (Asn), AGT (Ser),
   ATC (Ile), ACC (Thr), AAC (Asn), AGC (Ser),
   ATA (Ile), ACA (Thr), AAA (Lys), AGA (Arg),
   ATG (Met), ACG (Thr), AAG (Lys), AGG (Arg),

   GTT (Val), GCT (Ala), GAT (Asp), GGT (Gly),
   GTC (Val), GCC (Ala), GAC (Asp), GGC (Gly),
   GTA (Val), GCA (Ala), GAA (Glu), GGA (Gly),
   GTG (Val, 61), GCG (Ala, 62), GAG (Glu, 63), GGG (Gly, 64),
   NNN (null, 65);
 *  </pre>
 * @author Andreas de Vries
 * @version 1.1
 */
public enum Codon {
   /** Genetic code according to Mortimer, Müller: Chemie. Thieme, Stuttgart 2007, S.627.*/
   TTT (Phe,  1), TCT (Ser,  2), TAT (Tyr,  3), TGT (Cys,  4),
   TTC (Phe,  5), TCC (Ser,  6), TAC (Tyr,  7), TGC (Cys,  8),
   TTA (Leu,  9), TCA (Ser, 10), TAA (Stop,11), TGA (Sec, 12),
   TTG (Leu, 13), TCG (Ser, 14), TAG (Pyl, 15), TGG (Trp, 16),

   CTT (Leu, 17), CCT (Pro, 18), CAT (His, 19), CGT (Arg, 20),
   CTC (Leu, 21), CCC (Pro, 22), CAC (His, 23), CGC (Arg, 24),
   CTA (Leu, 25), CCA (Pro, 26), CAA (Gln, 27), CGA (Arg, 28),
   CTG (Leu, 29), CCG (Pro, 30), CAG (Gln, 31), CGG (Arg, 32),

   ATT (Ile, 33), ACT (Thr, 34), AAT (Asn, 35), AGT (Ser, 36),
   ATC (Ile, 37), ACC (Thr, 38), AAC (Asn, 39), AGC (Ser, 40),
   ATA (Ile, 41), ACA (Thr, 42), AAA (Lys, 43), AGA (Arg, 44),
   ATG (Met, 45), ACG (Thr, 46), AAG (Lys, 47), AGG (Arg, 48),

   GTT (Val, 49), GCT (Ala, 50), GAT (Asp, 51), GGT (Gly, 52),
   GTC (Val, 53), GCC (Ala, 54), GAC (Asp, 55), GGC (Gly, 56),
   GTA (Val, 57), GCA (Ala, 58), GAA (Glu, 59), GGA (Gly, 60),
   GTG (Val, 61), GCG (Ala, 62), GAG (Glu, 63), GGG (Gly, 64),
   NNN (null, 65);
   
   /** Code number for bitmask coding. */
   private byte bitmaskCode;
   
   /** Amino acid which is related to this codon by the genetic code. */
   private AminoAcid aminoAcid;
   
   /** Constructor of this codon. */
   private Codon(AminoAcid aminoAcid, int bitmaskCode) {
      this.bitmaskCode = (byte) bitmaskCode;
      this.aminoAcid   = aminoAcid;
   }
   
   /** Returns the bitmask code referring to this codon.
    *  It is (arbitrarily chosen to be) as follows.
    *  <pre>
       1 -> TTT,  2 -> TCT,  3 -> TAT,  4 -> TGT,
       5 -> TTC,  6 -> TCC,  7 -> TAC,  8 -> TGC,
       9 -> TTA, 10 -> TCA, 11 -> TAA, 12 -> TGA,
      13 -> TTG, 14 -> TCG, 15 -> TAG, 16 -> TGG,
   
      17 -> CTT, 18 -> CCT, 19 -> CAT, 20 -> CGT,
      21 -> CTC, 22 -> CCC, 23 -> CAC, 24 -> CGC,
      25 -> CTA, 26 -> CCA, 27 -> CAA, 28 -> CGA,
      29 -> CTG, 30 -> CCG, 31 -> CAG, 32 -> CGG,
   
      33 -> ATT, 34 -> ACT, 35 -> AAT, 36 -> AGT,
      37 -> ATC, 38 -> ACC, 39 -> AAC, 40 -> AGC,
      41 -> ATA, 42 -> ACA, 43 -> AAA, 44 -> AGA,
      45 -> ATG, 46 -> ACG, 47 -> AAG, 48 -> AGG,
   
      49 -> GTT, 50 -> GCT, 51 -> GAT, 52 -> GGT,
      53 -> GTC, 54 -> GCC, 55 -> GAC, 56 -> GGC,
      57 -> GTA, 58 -> GCA, 59 -> GAA, 60 -> GGA,
      61 -> GTG, 62 -> GCG, 63 -> GAG, 64 -> GGG
      
      65 -> NNN (unknown codon)
    *  </pre>
    *  @return the bitmask code of this codon
    */
   public byte getBitmaskCode() {
      return bitmaskCode;
   }
   
   /** Returns the amino acid which is related to this codon by the genetic code.
    *  Note that the stop codon TAA returns "Stop" instead of an amino acid.
    *  @return the amino acid coded by this codon
    */
   public AminoAcid getAminoAcid() {
      return aminoAcid;
   }
   
   /** Returns the amino acid which is related to this codon by the genetic code.
    *  Note that the stop codon TAA returns "Stop" instead of an amino acid.
    *  @return the amino acid coded by this codon
    */
   public AminoAcid encode() {
      return aminoAcid;
   }
   
   /** Returns the amino acid which is related to this codon by the genetic code.
    *  Note that the stop codon TAA returns "Stop" instead of an amino acid.
    *  @return the amino acid coded by this codon
    */
   public static Codon decode(AminoAcid aminoAcid) {
      for (Codon codon : values()) {
         if (aminoAcid.equals(codon.aminoAcid)) {
            return codon;
         }
      }
      //throw new IllegalArgumentException("Unknown aminoAcid " + aminoAcid);
      return NNN;
   }
   
   /** Returns the DNA codon which is represented by the specified bitmask code,
    *  an integer <i>x</i> satisfying 0 &le; <i>x</i> &lt; 64.
    *  It is (arbitrarily) chosen to be as follows.
    *  <pre>
       1 -> TTT,  2 -> TCT,  3 -> TAT,  4 -> TGT,
       5 -> TTC,  6 -> TCC,  7 -> TAC,  8 -> TGC,
       9 -> TTA, 10 -> TCA, 11 -> TAA, 12 -> TGA,
      13 -> TTG, 14 -> TCG, 15 -> TAG, 16 -> TGG,
   
      17 -> CTT, 18 -> CCT, 19 -> CAT, 20 -> CGT,
      21 -> CTC, 22 -> CCC, 23 -> CAC, 24 -> CGC,
      25 -> CTA, 26 -> CCA, 27 -> CAA, 28 -> CGA,
      29 -> CTG, 30 -> CCG, 31 -> CAG, 32 -> CGG,
   
      33 -> ATT, 34 -> ACT, 35 -> AAT, 36 -> AGT,
      37 -> ATC, 38 -> ACC, 39 -> AAC, 40 -> AGC,
      41 -> ATA, 42 -> ACA, 43 -> AAA, 44 -> AGA,
      45 -> ATG, 46 -> ACG, 47 -> AAG, 48 -> AGG,
   
      49 -> GTT, 50 -> GCT, 51 -> GAT, 52 -> GGT,
      53 -> GTC, 54 -> GCC, 55 -> GAC, 56 -> GGC,
      57 -> GTA, 58 -> GCA, 59 -> GAA, 60 -> GGA,
      61 -> GTG, 62 -> GCG, 63 -> GAG, 64 -> GGG
      
      65 -> NNN (unknown codon)
    *  </pre>
    *  @param bitmaskCode the bitmask code representing a codon.
    *  @return the codon represented by the specified bitmask code
    *  @throws IllegalArgumentException if the string is not a codon triplet
    */
   public static Codon fromBitmaskCode(byte bitmaskCode) {
      for (Codon codon : values()) {
         if (bitmaskCode == codon.bitmaskCode) {
            return codon;
         }
      }
      throw new IllegalArgumentException(
         bitmaskCode + " is not a bitmask code representing a DNA codon triplet"
      );
   }
   
   /** Returns the DNA codon which is represented by the specified three 
    *  nucleobases, a triplet, consisting of the DNA letters T, C, A, G.
    *  @param x the first of the three-letter string representing a codon.
    *  @param y the second of the three-letter string representing a codon.
    *  @param z the third of the three-letter string representing a codon.
    *  @return the codon represented by the specified triplet
    *  @throws IllegalArgumentException if the string is not a codon triplet
    */
   public static Codon fromTriplet(char x, char y, char z) {
      String triplet = "" + x + y + z;
      for (Codon codon : values()) {
         if (triplet.equals(codon.toString())) {
            return codon;
         }
      }
      throw new IllegalArgumentException(triplet + " is not a DNA codon triplet");
   }
   
   /** Returns the DNA codon which is represented by the specified three-letter 
    *  string, a triplet, consisting of the DNA letters T, C, A, G.
    *  @param  triplet three-letter string representing a codon.
    *  @return the codon represented by the specified triplet
    *  @throws IllegalArgumentException if the string is not a codon triplet
    */
   public static Codon fromString(CharSequence triplet) {
      if (triplet.length() != 3) {
         throw new IllegalArgumentException(triplet + " is not a triplet");
      }
      for (Codon codon : values()) {
         if (triplet.toString().equals(codon.toString())) {
            return codon;
         }
      }
      //throw new IllegalArgumentException(triplet + " is not a codon triplet");
      return NNN;
   }
   
   public static void main(String... args) {
      String out = "";
      
      //javax.swing.JOptionPane.showMessageDialog(null, out);
      
      StringBuilder string = new StringBuilder("AGA");
      System.out.println(string + " = " + Codon.fromString(string));
      AminoAcid acid = Trp;
      System.out.println(acid + " <- " + Codon.decode(acid));
      
      // /*
      int count = 0;
      for(Codon c: values()) {
         if (count %  4 == 0) {
            System.out.println();
         }
         if (count % 16 == 0) {
            System.out.println();
         }
         System.out.print("" + c.getBitmaskCode() +" -> " + c + ", ");
         count++;
      }
      // */
   }
}
