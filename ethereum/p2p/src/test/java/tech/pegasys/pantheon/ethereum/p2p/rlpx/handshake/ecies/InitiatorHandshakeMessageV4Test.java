package tech.pegasys.pantheon.ethereum.p2p.rlpx.handshake.ecies;

import tech.pegasys.pantheon.crypto.SECP256K1;
import tech.pegasys.pantheon.util.bytes.BytesValue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/** Tests for {@link InitiatorHandshakeMessageV4}. */
public final class InitiatorHandshakeMessageV4Test {

  private static final BytesValue EXAMPLE_MESSAGE;

  private static final SECP256K1.KeyPair EXAMPLE_KEYPAIR;

  static {
    try {
      EXAMPLE_KEYPAIR =
          SECP256K1.KeyPair.load(
              new File(InitiatorHandshakeMessageV4.class.getResource("test.keypair").toURI()));
    } catch (final IOException | URISyntaxException ex) {
      throw new IllegalStateException(ex);
    }
    try {
      EXAMPLE_MESSAGE =
          BytesValue.fromHexString(
              Resources.readLines(
                      InitiatorHandshakeMessageV4Test.class.getResource("test.initiatormessage"),
                      StandardCharsets.UTF_8)
                  .get(0));
    } catch (final IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Test
  public void encodeDecodeRoundtrip() {
    final InitiatorHandshakeMessageV4 initial =
        InitiatorHandshakeMessageV4.decode(EXAMPLE_MESSAGE, EXAMPLE_KEYPAIR);
    final BytesValue encoded = initial.encode();
    Assertions.assertThat(encoded).isEqualTo(EXAMPLE_MESSAGE.slice(0, encoded.size()));
  }
}
