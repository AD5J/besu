package tech.pegasys.pantheon.ethereum.mainnet;

import tech.pegasys.pantheon.ethereum.core.BlockHashFunction;
import tech.pegasys.pantheon.ethereum.core.BlockImporter;
import tech.pegasys.pantheon.ethereum.core.Wei;
import tech.pegasys.pantheon.ethereum.mainnet.MainnetBlockProcessor.TransactionReceiptFactory;
import tech.pegasys.pantheon.ethereum.vm.EVM;

/** A protocol specification. */
public class ProtocolSpec<C> {

  private final String name;
  private final EVM evm;

  private final TransactionValidator transactionValidator;

  private final TransactionProcessor transactionProcessor;

  private final BlockHeaderValidator<C> blockHeaderValidator;

  private final BlockBodyValidator<C> blockBodyValidator;

  private final BlockImporter<C> blockImporter;

  private final BlockProcessor blockProcessor;

  private final BlockHashFunction blockHashFunction;

  private final TransactionReceiptFactory transactionReceiptFactory;

  private final DifficultyCalculator<C> difficultyCalculator;

  private final Wei blockReward;

  private final MiningBeneficiaryCalculator miningBeneficiaryCalculator;

  /**
   * Creates a new protocol specification instance.
   *
   * @param name the protocol specification name
   * @param evm the EVM supporting the appropriate operations for this specification
   * @param transactionValidator the transaction validator to use
   * @param transactionProcessor the transaction processor to use
   * @param blockHeaderValidator the block header validator to use
   * @param blockBodyValidator the block body validator to use
   * @param blockProcessor the block processor to use
   * @param blockImporter the block importer to use
   * @param blockHashFunction the block hash function to use
   * @param transactionReceiptFactory the transactionReceiptFactory to use
   * @param difficultyCalculator the difficultyCalculator to use
   * @param blockReward the blockReward to use.
   * @param transactionReceiptType the type of transaction receipt to use, one of
   * @param miningBeneficiaryCalculator determines to whom mining proceeds are paid
   */
  public ProtocolSpec(
      final String name,
      final EVM evm,
      final TransactionValidator transactionValidator,
      final TransactionProcessor transactionProcessor,
      final BlockHeaderValidator<C> blockHeaderValidator,
      final BlockBodyValidator<C> blockBodyValidator,
      final BlockProcessor blockProcessor,
      final BlockImporter<C> blockImporter,
      final BlockHashFunction blockHashFunction,
      final TransactionReceiptFactory transactionReceiptFactory,
      final DifficultyCalculator<C> difficultyCalculator,
      final Wei blockReward,
      final TransactionReceiptType transactionReceiptType,
      final MiningBeneficiaryCalculator miningBeneficiaryCalculator) {
    this.name = name;
    this.evm = evm;
    this.transactionValidator = transactionValidator;
    this.transactionProcessor = transactionProcessor;
    this.blockHeaderValidator = blockHeaderValidator;
    this.blockBodyValidator = blockBodyValidator;
    this.blockProcessor = blockProcessor;
    this.blockImporter = blockImporter;
    this.blockHashFunction = blockHashFunction;
    this.transactionReceiptFactory = transactionReceiptFactory;
    this.difficultyCalculator = difficultyCalculator;
    this.blockReward = blockReward;
    this.miningBeneficiaryCalculator = miningBeneficiaryCalculator;
  }

  /**
   * Returns the protocol specification name.
   *
   * @return the protocol specification name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the transaction validator used in this specification.
   *
   * @return the transaction validator
   */
  public TransactionValidator getTransactionValidator() {
    return transactionValidator;
  }

  /**
   * Returns the transaction processor used in this specification.
   *
   * @return the transaction processor
   */
  public TransactionProcessor getTransactionProcessor() {
    return transactionProcessor;
  }

  /**
   * Returns the block processor used in this specification.
   *
   * @return the block processor
   */
  public BlockProcessor getBlockProcessor() {
    return blockProcessor;
  }

  /**
   * Returns the block importer used in this specification.
   *
   * @return the block importer
   */
  public BlockImporter<C> getBlockImporter() {
    return blockImporter;
  }

  /**
   * Returns the block header validator used in this specification.
   *
   * @return the block header validator
   */
  public BlockHeaderValidator<C> getBlockHeaderValidator() {
    return blockHeaderValidator;
  }

  /**
   * Returns the block body validator used in this specification.
   *
   * @return the block body validator
   */
  public BlockBodyValidator<C> getBlockBodyValidator() {
    return blockBodyValidator;
  }

  /**
   * Returns the block hash function used in this specification.
   *
   * @return the block hash function
   */
  public BlockHashFunction getBlockHashFunction() {
    return blockHashFunction;
  }

  /**
   * Returns the EVM for this specification.
   *
   * @return the EVM
   */
  public EVM getEvm() {
    return evm;
  }

  /**
   * Returns the TransctionReceiptFactory used in this specification
   *
   * @return the transaction receipt factory
   */
  public TransactionReceiptFactory getTransactionReceiptFactory() {
    return transactionReceiptFactory;
  }

  /**
   * Returns the DifficultyCalculator used in this specification.
   *
   * @return the difficulty calculator.
   */
  public DifficultyCalculator<C> getDifficultyCalculator() {
    return difficultyCalculator;
  }

  /**
   * Returns the blockReward used in this specification.
   *
   * @return the amount to be rewarded for block mining.
   */
  public Wei getBlockReward() {
    return blockReward;
  }

  public MiningBeneficiaryCalculator getMiningBeneficiaryCalculator() {
    return miningBeneficiaryCalculator;
  }
}
