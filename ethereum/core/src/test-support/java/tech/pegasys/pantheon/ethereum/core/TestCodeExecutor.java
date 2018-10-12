package tech.pegasys.pantheon.ethereum.core;

import tech.pegasys.pantheon.crypto.SECP256K1.Signature;
import tech.pegasys.pantheon.ethereum.db.WorldStateArchive;
import tech.pegasys.pantheon.ethereum.mainnet.MainnetMessageCallProcessor;
import tech.pegasys.pantheon.ethereum.mainnet.PrecompileContractRegistry;
import tech.pegasys.pantheon.ethereum.mainnet.ProtocolSchedule;
import tech.pegasys.pantheon.ethereum.mainnet.ProtocolSpec;
import tech.pegasys.pantheon.ethereum.vm.BlockHashLookup;
import tech.pegasys.pantheon.ethereum.vm.Code;
import tech.pegasys.pantheon.ethereum.vm.MessageFrame;
import tech.pegasys.pantheon.ethereum.vm.MessageFrame.Type;
import tech.pegasys.pantheon.ethereum.vm.OperationTracer;
import tech.pegasys.pantheon.util.bytes.BytesValue;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

public class TestCodeExecutor {

  private final ExecutionContextTestFixture fixture;
  private final BlockHeader blockHeader = new BlockHeaderTestFixture().number(13).buildHeader();
  private static final Address SENDER_ADDRESS = AddressHelpers.ofValue(244259721);

  public TestCodeExecutor(final ProtocolSchedule<Void> protocolSchedule) {
    fixture = new ExecutionContextTestFixture(protocolSchedule);
  }

  public MessageFrame executeCode(
      final String code, final long gasLimit, final Consumer<MutableAccount> accountSetup) {
    final ProtocolSpec<Void> protocolSpec = fixture.getProtocolSchedule().getByBlockNumber(0);
    final WorldUpdater worldState =
        createInitialWorldState(accountSetup, fixture.getStateArchive());
    final Deque<MessageFrame> messageFrameStack = new ArrayDeque<>();

    final MainnetMessageCallProcessor messageCallProcessor =
        new MainnetMessageCallProcessor(protocolSpec.getEvm(), new PrecompileContractRegistry());

    final Transaction transaction =
        Transaction.builder()
            .value(Wei.ZERO)
            .sender(SENDER_ADDRESS)
            .signature(Signature.create(BigInteger.ONE, BigInteger.TEN, (byte) 1))
            .gasLimit(gasLimit)
            .to(SENDER_ADDRESS)
            .payload(BytesValue.EMPTY)
            .gasPrice(Wei.ZERO)
            .nonce(0)
            .build();
    final MessageFrame initialFrame =
        MessageFrame.builder()
            .type(Type.MESSAGE_CALL)
            .messageFrameStack(messageFrameStack)
            .blockchain(fixture.getBlockchain())
            .worldState(worldState)
            .initialGas(Gas.of(gasLimit))
            .address(SENDER_ADDRESS)
            .originator(SENDER_ADDRESS)
            .contract(SENDER_ADDRESS)
            .gasPrice(transaction.getGasPrice())
            .inputData(transaction.getPayload())
            .sender(SENDER_ADDRESS)
            .value(transaction.getValue())
            .apparentValue(transaction.getValue())
            .code(new Code(BytesValue.fromHexString(code)))
            .blockHeader(blockHeader)
            .depth(0)
            .completer(c -> {})
            .miningBeneficiary(blockHeader.coinbase)
            .blockHashLookup(new BlockHashLookup(blockHeader, fixture.getBlockchain()))
            .build();
    messageFrameStack.addFirst(initialFrame);

    while (!messageFrameStack.isEmpty()) {
      messageCallProcessor.process(messageFrameStack.peekFirst(), OperationTracer.NO_TRACING);
    }
    return initialFrame;
  }

  private WorldUpdater createInitialWorldState(
      final Consumer<MutableAccount> accountSetup, final WorldStateArchive stateArchive) {
    final MutableWorldState initialWorldState = stateArchive.getMutable();

    final WorldUpdater worldState = initialWorldState.updater();
    final MutableAccount senderAccount = worldState.getOrCreate(TestCodeExecutor.SENDER_ADDRESS);
    accountSetup.accept(senderAccount);
    worldState.commit();
    initialWorldState.persist();
    return stateArchive.getMutable(initialWorldState.rootHash()).updater();
  }
}
