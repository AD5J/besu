package tech.pegasys.pantheon.ethereum.vm.operations;

import tech.pegasys.pantheon.ethereum.core.Gas;
import tech.pegasys.pantheon.ethereum.vm.AbstractOperation;
import tech.pegasys.pantheon.ethereum.vm.GasCalculator;
import tech.pegasys.pantheon.ethereum.vm.MessageFrame;
import tech.pegasys.pantheon.util.uint.Counter;
import tech.pegasys.pantheon.util.uint.UInt256;
import tech.pegasys.pantheon.util.uint.UInt256Value;

public class ByteOperation extends AbstractOperation {

  public ByteOperation(final GasCalculator gasCalculator) {
    super(0x1A, "BYTE", 2, 1, false, 1, gasCalculator);
  }

  @Override
  public Gas cost(final MessageFrame frame) {
    return gasCalculator().getVeryLowTierGasCost();
  }

  private UInt256 getByte(final UInt256 seq, final UInt256 offset) {
    if (!offset.fitsInt()) {
      return UInt256.ZERO;
    }

    final int index = offset.toInt();
    if (index >= 32) {
      return UInt256.ZERO;
    }

    final byte b = seq.getBytes().get(index);
    final Counter<UInt256> res = UInt256.newCounter();
    res.getBytes().set(UInt256Value.SIZE - 1, b);
    return res.get();
  }

  @Override
  public void execute(final MessageFrame frame) {

    final UInt256 value0 = frame.popStackItem().asUInt256();
    final UInt256 value1 = frame.popStackItem().asUInt256();

    // Stack items are reversed for the BYTE operation.
    final UInt256 result = getByte(value1, value0);

    frame.pushStackItem(result.getBytes());
  }
}
