package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;

import org.web3j.abi.datatypes.Ufixed;

/**
 * <p>Auto generated code.<br>
 * <strong>Do not modifiy!</strong><br>
 * Please use {@link org.web3j.codegen.AbiTypesGenerator} to update.</p>
 */
public class Ufixed32x120 extends Ufixed {
  public static final Ufixed32x120 DEFAULT = new Ufixed32x120(BigInteger.ZERO);

  public Ufixed32x120(BigInteger value) {
    super(32, 120, value);
  }

  public Ufixed32x120(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
    super(32, 120, m, n);
  }
}
