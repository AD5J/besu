package tech.pegasys.pantheon.ethereum.jsonrpc.internal.processor;

import tech.pegasys.pantheon.ethereum.core.Transaction;
import tech.pegasys.pantheon.ethereum.mainnet.TransactionProcessor.Result;
import tech.pegasys.pantheon.ethereum.mainnet.TransactionValidator.TransactionInvalidReason;
import tech.pegasys.pantheon.ethereum.mainnet.ValidationResult;
import tech.pegasys.pantheon.util.bytes.BytesValue;

import com.google.common.base.Objects;

public class TransientTransactionProcessingResult {

  private final Transaction transaction;
  private final Result result;

  TransientTransactionProcessingResult(final Transaction transaction, final Result result) {
    this.transaction = transaction;
    this.result = result;
  }

  public boolean isSuccessful() {
    return result.isSuccessful();
  }

  public long getGasEstimate() {
    return transaction.getGasLimit() - result.getGasRemaining();
  }

  public BytesValue getOutput() {
    return result.getOutput();
  }

  public ValidationResult<TransactionInvalidReason> getValidationResult() {
    return result.getValidationResult();
  }

  public Result getResult() {
    return result;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final TransientTransactionProcessingResult that = (TransientTransactionProcessingResult) o;
    return Objects.equal(transaction, that.transaction) && Objects.equal(result, that.result);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(transaction, result);
  }
}
