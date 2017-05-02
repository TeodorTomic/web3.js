package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;

import org.web3j.abi.datatypes.Ufixed;

/**
 * <p>Auto generated code.<br>
 * <strong>Do not modifiy!</strong><br>
 * Please use {@link org.web3j.codegen.AbiTypesGenerator} to update.</p>
 */
public class Ufixed64x160 extends Ufixed {
  public static final Ufixed64x160 DEFAULT = new Ufixed64x160(BigInteger.ZERO);

  public Ufixed64x160(BigInteger value) {
    super(64, 160, value);
  }

  public Ufixed64x160(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
    super(64, 160, m, n);
  }
}
