package tech.pegasys.pantheon.ethereum.vm.operations;

import tech.pegasys.pantheon.ethereum.core.Gas;
import tech.pegasys.pantheon.ethereum.vm.AbstractOperation;
import tech.pegasys.pantheon.ethereum.vm.GasCalculator;
import tech.pegasys.pantheon.ethereum.vm.MessageFrame;
import tech.pegasys.pantheon.util.bytes.Bytes32;
import tech.pegasys.pantheon.util.bytes.BytesValue;
import tech.pegasys.pantheon.util.bytes.MutableBytes32;
import tech.pegasys.pantheon.util.uint.UInt256;

public class CallDataLoadOperation extends AbstractOperation {

  public CallDataLoadOperation(final GasCalculator gasCalculator) {
    super(0x35, "CALLDATALOAD", 1, 1, false, 1, gasCalculator);
  }

  @Override
  public Gas cost(final MessageFrame frame) {
    return gasCalculator().getVeryLowTierGasCost();
  }

  @Override
  public void execute(final MessageFrame frame) {
    final UInt256 startWord = frame.popStackItem().asUInt256();

    // If the start index doesn't fit a int, it comes after anything in data, and so the returned
    // word should be zero.
    if (!startWord.fitsInt()) {
      frame.pushStackItem(Bytes32.ZERO);
      return;
    }

    final int offset = startWord.toInt();
    final BytesValue data = frame.getInputData();
    final MutableBytes32 res = MutableBytes32.create();
    if (offset < data.size()) {
      final BytesValue toCopy = data.slice(offset, Math.min(Bytes32.SIZE, data.size() - offset));
      toCopy.copyTo(res, 0);
    }
    frame.pushStackItem(res);
  }
}
